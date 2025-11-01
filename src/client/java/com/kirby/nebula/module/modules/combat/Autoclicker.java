package com.kirby.nebula.module.modules.combat;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.settings.BooleanSetting;
import com.kirby.nebula.module.settings.NumberSetting;
import com.kirby.nebula.util.InventoryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Ghost auto clicker with realistic human-like patterns
 */
public class Autoclicker extends Module {
	private final Minecraft mc = Minecraft.getInstance();
	private final Random random = new Random();
	
	private final NumberSetting minCPS;
	private final NumberSetting maxCPS;
	private final BooleanSetting weaponsOnly;
	private final BooleanSetting jitterSimulation;
	private final BooleanSetting burstMode;
	private final NumberSetting burstChance;
	private final BooleanSetting cooldownCheck;
	private final BooleanSetting breakBlocks;
	private final BooleanSetting entityOnly;
	
	// Ghost features
	private final BooleanSetting randomizePattern;
	private final BooleanSetting spikesEnabled;
	private final BooleanSetting dropsEnabled;
	private final NumberSetting spikeChance;
	private final NumberSetting dropChance;
	
	private int clickDelay = 0;
	private final List<Integer> recentDelays = new ArrayList<>();
	private int burstClicks = 0;
	private long lastClickTime = 0;
	
	public Autoclicker() {
		super("Ghost Clicker", "Human-like auto clicker", Category.COMBAT);
		
		this.minCPS = new NumberSetting(
			"Min CPS",
			"Minimum clicks per second",
			9.0,
			1.0,
			20.0,
			0.5
		);
		addSetting(minCPS);
		
		this.maxCPS = new NumberSetting(
			"Max CPS",
			"Maximum clicks per second",
			13.0,
			1.0,
			20.0,
			0.5
		);
		addSetting(maxCPS);
		
		this.weaponsOnly = new BooleanSetting(
			"Weapons Only",
			"Only click when holding weapons",
			true
		);
		addSetting(weaponsOnly);
		
		this.jitterSimulation = new BooleanSetting(
			"Jitter Sim",
			"Simulate jitter clicking patterns",
			false
		);
		addSetting(jitterSimulation);
		
		this.burstMode = new BooleanSetting(
			"Burst Mode",
			"Occasionally burst click faster",
			true
		);
		addSetting(burstMode);
		
		this.burstChance = new NumberSetting(
			"Burst Chance",
			"Chance of burst per second (%)",
			15.0,
			0.0,
			100.0,
			5.0
		);
		addSetting(burstChance);
		
		this.cooldownCheck = new BooleanSetting(
			"Cooldown Check",
			"Only attack when weapon is ready",
			true
		);
		addSetting(cooldownCheck);
		
		this.breakBlocks = new BooleanSetting(
			"Break Blocks",
			"Allow breaking blocks",
			false
		);
		addSetting(breakBlocks);
		
		this.entityOnly = new BooleanSetting(
			"Entity Only",
			"Only click when looking at entities",
			true
		);
		addSetting(entityOnly);
		
		// Ghost features
		this.randomizePattern = new BooleanSetting(
			"Randomize Pattern",
			"Randomize clicking patterns",
			true
		);
		addSetting(randomizePattern);
		
		this.spikesEnabled = new BooleanSetting(
			"CPS Spikes",
			"Occasional CPS spikes (human-like)",
			true
		);
		addSetting(spikesEnabled);
		
		this.dropsEnabled = new BooleanSetting(
			"CPS Drops",
			"Occasional CPS drops (human-like)",
			true
		);
		addSetting(dropsEnabled);
		
		this.spikeChance = new NumberSetting(
			"Spike Chance",
			"Chance of CPS spike (%)",
			5.0,
			0.0,
			25.0,
			1.0
		);
		addSetting(spikeChance);
		
		this.dropChance = new NumberSetting(
			"Drop Chance",
			"Chance of CPS drop (%)",
			8.0,
			0.0,
			25.0,
			1.0
		);
		addSetting(dropChance);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		recentDelays.clear();
		burstClicks = 0;
	}
	
	@Override
	public void onTick() {
		if (mc.player == null || mc.screen != null) return;
		
		// Check if attack key is held
		if (!mc.options.keyAttack.isDown()) {
			return;
		}
		
		// Weapons only check
		if (weaponsOnly.getValue()) {
			if (!InventoryHelper.isHoldingType(SwordItem.class) && 
				!InventoryHelper.isHoldingType(AxeItem.class)) {
				return;
			}
		}
		
		// Cooldown check
		if (cooldownCheck.getValue()) {
			if (mc.player.getAttackStrengthScale(0.5f) < 0.9f) {
				return;
			}
		}
		
		// Entity only check
		if (entityOnly.getValue()) {
			HitResult hitResult = mc.hitResult;
			if (hitResult == null || hitResult.getType() != HitResult.Type.ENTITY) {
				return;
			}
		}
		
		// Break blocks check
		if (!breakBlocks.getValue()) {
			HitResult hitResult = mc.hitResult;
			if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
				return;
			}
		}
		
		// Handle clicking
		if (clickDelay <= 0) {
			performClick();
			clickDelay = getNextDelay();
		} else {
			clickDelay--;
		}
	}
	
	private void performClick() {
		HitResult hitResult = mc.hitResult;
		
		if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
			EntityHitResult entityHit = (EntityHitResult) hitResult;
			Entity target = entityHit.getEntity();
			mc.gameMode.attack(mc.player, target);
			mc.player.swing(InteractionHand.MAIN_HAND);
		} else if (breakBlocks.getValue() && hitResult != null && 
				   hitResult.getType() == HitResult.Type.BLOCK) {
			mc.player.swing(InteractionHand.MAIN_HAND);
		} else {
			mc.player.swing(InteractionHand.MAIN_HAND);
		}
		
		lastClickTime = System.currentTimeMillis();
	}
	
	private int getNextDelay() {
		double min = minCPS.getValue();
		double max = maxCPS.getValue();
		
		// Ensure min <= max
		if (min > max) {
			double temp = min;
			min = max;
			max = temp;
		}
		
		// Check for burst mode
		if (burstMode.getValue() && random.nextDouble() * 100 < burstChance.getValue()) {
			burstClicks = random.nextInt(3) + 2; // 2-4 burst clicks
		}
		
		double targetCPS;
		
		if (burstClicks > 0) {
			// Burst clicking - increase CPS
			targetCPS = max + random.nextDouble() * 3;
			burstClicks--;
		} else {
			// Normal clicking
			targetCPS = min + (max - min) * random.nextDouble();
			
			// Apply CPS spikes
			if (spikesEnabled.getValue() && random.nextDouble() * 100 < spikeChance.getValue()) {
				targetCPS += random.nextDouble() * 2;
			}
			
			// Apply CPS drops
			if (dropsEnabled.getValue() && random.nextDouble() * 100 < dropChance.getValue()) {
				targetCPS -= random.nextDouble() * 2;
				targetCPS = Math.max(min, targetCPS);
			}
		}
		
		// Jitter simulation
		if (jitterSimulation.getValue()) {
			targetCPS += (random.nextGaussian() * 0.5);
		}
		
		int delay = (int) (20.0 / targetCPS);
		
		// Randomize pattern
		if (randomizePattern.getValue()) {
			// Use Gaussian distribution for more human-like randomness
			double variance = delay * 0.15; // 15% variance
			delay += (int) (random.nextGaussian() * variance);
		}
		
		// Store recent delays for pattern analysis
		recentDelays.add(delay);
		if (recentDelays.size() > 20) {
			recentDelays.remove(0);
		}
		
		// Ensure delay is at least 1
		return Math.max(1, delay);
	}
	
	/**
	 * Get average CPS over recent clicks
	 */
	public double getAverageCPS() {
		if (recentDelays.isEmpty()) return 0;
		
		double avgDelay = recentDelays.stream()
			.mapToInt(Integer::intValue)
			.average()
			.orElse(0);
		
		return 20.0 / avgDelay;
	}
}
