package com.kirby.nebula.module.modules.combat;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.settings.BooleanSetting;
import com.kirby.nebula.module.settings.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Random;

public class LeftClicker extends Module {
	private final Minecraft mc = Minecraft.getInstance();
	private final Random random = new Random();
	
	private final NumberSetting minCPS;
	private final NumberSetting maxCPS;
	private final BooleanSetting weaponsOnly;
	private final BooleanSetting waitForCooldown;
	private final BooleanSetting allowBlockMining;
	
	private int clickDelay = 0;

	public LeftClicker() {
		super("Left Clicker", "Automatically left clicks", Category.COMBAT);
		
		this.minCPS = new NumberSetting(
			"Min CPS",
			"Minimum clicks per second",
			10.0,
			1.0,
			20.0,
			1.0
		);
		addSetting(minCPS);
		
		this.maxCPS = new NumberSetting(
			"Max CPS",
			"Maximum clicks per second",
			14.0,
			1.0,
			20.0,
			1.0
		);
		addSetting(maxCPS);
		
		this.weaponsOnly = new BooleanSetting(
			"Weapons Only",
			"Only click when holding sword or axe",
			true
		);
		addSetting(weaponsOnly);
		
		this.waitForCooldown = new BooleanSetting(
			"Wait For Cooldown",
			"Only attack when cooldown is ready",
			true
		);
		addSetting(waitForCooldown);
		
		this.allowBlockMining = new BooleanSetting(
			"Allow Block Mining",
			"Allow clicking to mine blocks",
			false
		);
		addSetting(allowBlockMining);
	}

	@Override
	public void onTick() {
		if (mc.player == null || mc.screen != null) return;
		
		// Check if attack key is held down
		if (!mc.options.keyAttack.isDown()) {
			return;
		}
		
		// Check if holding weapon (if enabled)
		if (weaponsOnly.getValue()) {
			ItemStack heldItem = mc.player.getMainHandItem();
			if (!(heldItem.getItem() instanceof SwordItem) && !(heldItem.getItem() instanceof AxeItem)) {
				return;
			}
		}
		
		// Check cooldown (if enabled)
		if (waitForCooldown.getValue()) {
			if (mc.player.getAttackStrengthScale(0.0f) < 1.0f) {
				return;
			}
		}
		
		// Check if we're looking at a block and block mining is disabled
		if (!allowBlockMining.getValue()) {
			HitResult hitResult = mc.hitResult;
			if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
				return;
			}
		}
		
		// Handle click delay
		if (clickDelay <= 0) {
			performLeftClick();
			clickDelay = getRandomDelay();
		} else {
			clickDelay--;
		}
	}
	
	private void performLeftClick() {
		HitResult hitResult = mc.hitResult;
		
		if (hitResult != null) {
			switch (hitResult.getType()) {
				case ENTITY:
					EntityHitResult entityHit = (EntityHitResult) hitResult;
					Entity target = entityHit.getEntity();
					mc.gameMode.attack(mc.player, target);
					mc.player.swing(InteractionHand.MAIN_HAND);
					break;
					
				case BLOCK:
					if (allowBlockMining.getValue()) {
						BlockHitResult blockHit = (BlockHitResult) hitResult;
						if (mc.gameMode.continueDestroyBlock(blockHit.getBlockPos(), blockHit.getDirection())) {
							mc.player.swing(InteractionHand.MAIN_HAND);
						}
					}
					break;
					
				case MISS:
					// Swing in air
					mc.player.swing(InteractionHand.MAIN_HAND);
					break;
			}
		}
	}
	
	private int getRandomDelay() {
		double min = minCPS.getValue();
		double max = maxCPS.getValue();
		
		// Ensure min is not greater than max
		if (min > max) {
			double temp = min;
			min = max;
			max = temp;
		}
		
		double cps = min + (max - min) * random.nextDouble();
		return (int) (20.0 / cps); // Convert CPS to ticks
	}
}
