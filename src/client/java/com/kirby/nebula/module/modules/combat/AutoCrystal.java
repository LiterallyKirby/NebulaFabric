package com.kirby.nebula.module.modules.combat;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.settings.BooleanSetting;
import com.kirby.nebula.module.settings.ModeSetting;
import com.kirby.nebula.module.settings.NumberSetting;
import com.kirby.nebula.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AutoCrystal extends Module {
	private final Minecraft mc = Minecraft.getInstance();
	
	// Target settings
	private final NumberSetting targetRange;
	private final BooleanSetting autoTarget;
	private final BooleanSetting targetPlayers;
	private final BooleanSetting targetMobs;
	private final BooleanSetting ignoreFriends;
	private final BooleanSetting smartTarget;
	
	// Place settings
	private final BooleanSetting autoPlace;
	private final NumberSetting placeRange;
	private final NumberSetting placeDelay;
	private final NumberSetting minPlaceDamage;
	private final BooleanSetting placeRaytrace;
	private final ModeSetting placeMode;
	private final BooleanSetting multiPlace;
	private final NumberSetting multiPlaceCount;
	
	// Break settings
	private final BooleanSetting autoBreak;
	private final NumberSetting breakRange;
	private final NumberSetting breakDelay;
	private final BooleanSetting breakRaytrace;
	private final BooleanSetting inhibit;
	private final BooleanSetting antiWeakness;
	
	// Safety settings
	private final NumberSetting maxSelfDamage;
	private final NumberSetting minDamageRatio;
	private final BooleanSetting antiSuicide;
	private final BooleanSetting pauseOnEat;
	private final BooleanSetting pauseOnMine;
	private final BooleanSetting holeCheck;
	
	// Misc settings
	private final BooleanSetting autoSwitch;
	private final BooleanSetting switchBack;
	private final ModeSetting rotationMode;
	private final BooleanSetting predictMovement;
	private final NumberSetting predictTicks;
	private final BooleanSetting facePlace;
	private final NumberSetting facePlaceHealth;
	
	// Ghost settings
	private final BooleanSetting ghostMode;
	private final NumberSetting randomDelay;
	private final BooleanSetting humanize;
	
	// State tracking
	private final TimerHelper placeTimer = new TimerHelper();
	private final TimerHelper breakTimer = new TimerHelper();
	private final TimerHelper switchBackTimer = new TimerHelper();
	private LivingEntity currentTarget;
	private BlockPos lastPlacePos;
	private Entity lastCrystal;
	private int previousSlot = -1;
	private int crystalsPlaced = 0;
	private long lastActionTime = 0;

	public AutoCrystal() {
		super("Auto Crystal", "Automatically places and breaks End Crystals", Category.COMBAT);
		
		// Target settings
		this.targetRange = new NumberSetting(
			"Target Range",
			"Range to search for targets",
			12.0, 1.0, 20.0, 0.5
		);
		addSetting(targetRange);
		
		this.autoTarget = new BooleanSetting(
			"Auto Target",
			"Automatically select target",
			true
		);
		addSetting(autoTarget);
		
		this.targetPlayers = new BooleanSetting(
			"Target Players",
			"Target other players",
			true
		);
		addSetting(targetPlayers);
		
		this.targetMobs = new BooleanSetting(
			"Target Mobs",
			"Target mobs (for testing)",
			false
		);
		addSetting(targetMobs);
		
		this.ignoreFriends = new BooleanSetting(
			"Ignore Friends",
			"Don't target friends/team members",
			true
		);
		addSetting(ignoreFriends);
		
		this.smartTarget = new BooleanSetting(
			"Smart Target",
			"Prioritize low health & armor targets",
			true
		);
		addSetting(smartTarget);
		
		// Place settings
		this.autoPlace = new BooleanSetting(
			"Auto Place",
			"Automatically place crystals",
			true
		);
		addSetting(autoPlace);
		
		this.placeRange = new NumberSetting(
			"Place Range",
			"Range to place crystals",
			5.0, 1.0, 6.0, 0.5
		);
		addSetting(placeRange);
		
		this.placeDelay = new NumberSetting(
			"Place Delay",
			"Delay between crystal placements (ms)",
			50.0, 0.0, 500.0, 10.0
		);
		addSetting(placeDelay);
		
		this.minPlaceDamage = new NumberSetting(
			"Min Place Damage",
			"Minimum damage to place crystal",
			4.0, 0.0, 20.0, 0.5
		);
		addSetting(minPlaceDamage);
		
		this.placeRaytrace = new BooleanSetting(
			"Place Raytrace",
			"Only place if raytrace is clear",
			false
		);
		addSetting(placeRaytrace);
		
		this.placeMode = new ModeSetting(
			"Place Mode",
			"Crystal placement mode",
			"Normal",
			"Normal", "Damage", "Safe", "Smart"
		);
		addSetting(placeMode);
		
		this.multiPlace = new BooleanSetting(
			"Multi Place",
			"Place multiple crystals per tick",
			false
		);
		addSetting(multiPlace);
		
		this.multiPlaceCount = new NumberSetting(
			"Multi Count",
			"Max crystals to place per tick",
			2.0, 1.0, 5.0, 1.0
		);
		addSetting(multiPlaceCount);
		
		// Break settings
		this.autoBreak = new BooleanSetting(
			"Auto Break",
			"Automatically break crystals",
			true
		);
		addSetting(autoBreak);
		
		this.breakRange = new NumberSetting(
			"Break Range",
			"Range to break crystals",
			5.0, 1.0, 6.0, 0.5
		);
		addSetting(breakRange);
		
		this.breakDelay = new NumberSetting(
			"Break Delay",
			"Delay between crystal breaks (ms)",
			50.0, 0.0, 500.0, 10.0
		);
		addSetting(breakDelay);
		
		this.breakRaytrace = new BooleanSetting(
			"Break Raytrace",
			"Only break if raytrace is clear",
			false
		);
		addSetting(breakRaytrace);
		
		this.inhibit = new BooleanSetting(
			"Inhibit",
			"Don't place new crystals if one exists near target",
			true
		);
		addSetting(inhibit);
		
		this.antiWeakness = new BooleanSetting(
			"Anti Weakness",
			"Switch to tool when breaking with weakness effect",
			true
		);
		addSetting(antiWeakness);
		
		// Safety settings
		this.maxSelfDamage = new NumberSetting(
			"Max Self Damage",
			"Maximum damage to self",
			8.0, 0.0, 20.0, 0.5
		);
		addSetting(maxSelfDamage);
		
		this.minDamageRatio = new NumberSetting(
			"Min Damage Ratio",
			"Minimum damage ratio (target/self)",
			1.5, 0.0, 5.0, 0.1
		);
		addSetting(minDamageRatio);
		
		this.antiSuicide = new BooleanSetting(
			"Anti Suicide",
			"Don't place if it would kill you",
			true
		);
		addSetting(antiSuicide);
		
		this.pauseOnEat = new BooleanSetting(
			"Pause On Eat",
			"Pause when eating",
			true
		);
		addSetting(pauseOnEat);
		
		this.pauseOnMine = new BooleanSetting(
			"Pause On Mine",
			"Pause when mining",
			false
		);
		addSetting(pauseOnMine);
		
		this.holeCheck = new BooleanSetting(
			"Hole Check",
			"Don't target players in safe holes",
			false
		);
		addSetting(holeCheck);
		
		// Misc settings
		this.autoSwitch = new BooleanSetting(
			"Auto Switch",
			"Automatically switch to crystals",
			true
		);
		addSetting(autoSwitch);
		
		this.switchBack = new BooleanSetting(
			"Switch Back",
			"Switch back to previous item",
			true
		);
		addSetting(switchBack);
		
		this.rotationMode = new ModeSetting(
			"Rotation",
			"Rotation mode",
			"None",
			"None", "Packet", "Client"
		);
		addSetting(rotationMode);
		
		this.predictMovement = new BooleanSetting(
			"Predict Movement",
			"Predict target movement",
			true
		);
		addSetting(predictMovement);
		
		this.predictTicks = new NumberSetting(
			"Predict Ticks",
			"How many ticks to predict",
			3.0, 1.0, 10.0, 1.0
		);
		addSetting(predictTicks);
		
		this.facePlace = new BooleanSetting(
			"Face Place",
			"Always place at target's feet when low health",
			true
		);
		addSetting(facePlace);
		
		this.facePlaceHealth = new NumberSetting(
			"FP Health",
			"Health threshold for face placing",
			8.0, 0.0, 20.0, 1.0
		);
		addSetting(facePlaceHealth);
		
		// Ghost settings
		this.ghostMode = new BooleanSetting(
			"Ghost Mode",
			"Make actions look more human-like",
			false
		);
		addSetting(ghostMode);
		
		this.randomDelay = new NumberSetting(
			"Random Delay",
			"Random delay variation (ms)",
			20.0, 0.0, 100.0, 5.0
		);
		addSetting(randomDelay);
		
		this.humanize = new BooleanSetting(
			"Humanize",
			"Add human-like imperfections",
			false
		);
		addSetting(humanize);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		currentTarget = null;
		lastPlacePos = null;
		lastCrystal = null;
		previousSlot = -1;
		crystalsPlaced = 0;
	}

	@Override
	public void onDisable() {
		super.onDisable();
		currentTarget = null;
		if (switchBack.getValue() && previousSlot != -1) {
			InventoryHelper.switchToSlot(previousSlot);
			previousSlot = -1;
		}
	}

	@Override
	public void onTick() {
		if (!PlayerHelper.isPlayerValid()) return;
		
		// Check pause conditions
		if (shouldPause()) return;
		
		// Update target
		if (autoTarget.getValue()) {
			updateTarget();
		}
		
		if (currentTarget == null) return;
		
		// Apply ghost mode delays
		if (ghostMode.getValue() && !canPerformAction()) return;
		
		// Auto break crystals (higher priority)
		if (autoBreak.getValue() && breakTimer.hasReached(getEffectiveDelay(breakDelay.getValue()))) {
			breakCrystals();
		}
		
		// Auto place crystals
		if (autoPlace.getValue() && placeTimer.hasReached(getEffectiveDelay(placeDelay.getValue()))) {
			if (multiPlace.getValue()) {
				int count = (int) multiPlaceCount.getValue().doubleValue();
				for (int i = 0; i < count; i++) {
					if (!placeCrystal()) break;
				}
			} else {
				placeCrystal();
			}
		}
		
		// Handle switch back
		if (switchBack.getValue() && previousSlot != -1 && switchBackTimer.hasReached(100)) {
			if (!InventoryHelper.isHolding(Items.END_CRYSTAL)) {
				InventoryHelper.switchToSlot(previousSlot);
				previousSlot = -1;
			}
		}
	}

	private boolean shouldPause() {
		if (pauseOnEat.getValue() && mc.player.isUsingItem()) {
			if (mc.player.getUseItem().has(net.minecraft.core.component.DataComponents.FOOD)) {
				return true;
			}
		}
		
		if (pauseOnMine.getValue() && mc.options.keyAttack.isDown()) {
			return true;
		}
		
		return false;
	}

	private long getEffectiveDelay(double baseDelay) {
		if (!ghostMode.getValue()) return (long) baseDelay;
		
		double random = Math.random() * randomDelay.getValue();
		return (long) (baseDelay + random);
	}

	private boolean canPerformAction() {
		if (!ghostMode.getValue()) return true;
		
		long currentTime = System.currentTimeMillis();
		long timeSinceLastAction = currentTime - lastActionTime;
		
		// Minimum 50ms between actions in ghost mode
		if (timeSinceLastAction < 50) return false;
		
		lastActionTime = currentTime;
		return true;
	}

	private void updateTarget() {
		List<LivingEntity> targets = new ArrayList<>();
		
		if (targetPlayers.getValue()) {
			targets.addAll(PlayerHelper.getPlayersInRange(targetRange.getValue()));
		}
		
		if (targetMobs.getValue()) {
			List<LivingEntity> mobs = PlayerHelper.getLivingEntitiesInRange(targetRange.getValue());
			for (LivingEntity mob : mobs) {
				if (!targets.contains(mob) && !(mob instanceof Player)) {
					targets.add(mob);
				}
			}
		}
		
		if (targets.isEmpty()) {
			currentTarget = null;
			return;
		}
		
		// Filter targets
		targets.removeIf(entity -> {
			if (entity instanceof Player player) {
				if (ignoreFriends.getValue() && PlayerHelper.isFriend(player)) return true;
				if (holeCheck.getValue() && isInSafeHole(player)) return true;
			}
			if (entity.isDeadOrDying()) return true;
			return false;
		});
		
		if (targets.isEmpty()) {
			currentTarget = null;
			return;
		}
		
		// Smart target selection
		currentTarget = targets.stream()
			.min(Comparator.comparingDouble(this::getTargetPriority))
			.orElse(null);
	}

	private double getTargetPriority(LivingEntity entity) {
		double distWeight = PlayerHelper.getDistanceTo(entity) * 0.1;
		double healthWeight = entity.getHealth() * 0.3;
		
		if (smartTarget.getValue() && entity instanceof Player player) {
			// Prioritize low armor targets
			int armor = player.getArmorValue();
			double armorWeight = armor * 0.2;
			return distWeight + healthWeight + armorWeight;
		}
		
		return distWeight + healthWeight;
	}

	private boolean isInSafeHole(Player player) {
		BlockPos pos = player.blockPosition();
		
		// Check if player is in a hole (bedrock/obsidian on all sides)
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			BlockPos checkPos = pos.relative(dir);
			if (!BlockHelper.isBlock(checkPos, net.minecraft.world.level.block.Blocks.BEDROCK) &&
				!BlockHelper.isBlock(checkPos, net.minecraft.world.level.block.Blocks.OBSIDIAN)) {
				return false;
			}
		}
		
		return true;
	}

	private void breakCrystals() {
		List<Entity> crystals = CrystalHelper.getCrystalsInRange(breakRange.getValue());
		if (crystals.isEmpty()) return;
		
		Entity bestCrystal = null;
		float bestDamage = 0;
		
		for (Entity crystal : crystals) {
			if (breakRaytrace.getValue() && !PlayerHelper.canSeeEntity(crystal)) {
				continue;
			}
			
			float damage = CrystalHelper.calculateCrystalDamage(crystal.blockPosition(), currentTarget);
			
			if (damage > bestDamage) {
				bestDamage = damage;
				bestCrystal = crystal;
			}
		}
		
		if (bestCrystal != null) {
			attackCrystal(bestCrystal);
			breakTimer.reset();
			lastCrystal = bestCrystal;
		}
	}

	private boolean placeCrystal() {
		if (!CrystalHelper.hasCrystals()) return false;
		
		// Handle auto switch
		if (autoSwitch.getValue() && !InventoryHelper.isHolding(Items.END_CRYSTAL)) {
			int crystalSlot = InventoryHelper.findItemInHotbar(Items.END_CRYSTAL);
			if (crystalSlot == -1) return false;
			
			if (previousSlot == -1) {
				previousSlot = InventoryHelper.getSelectedSlot();
			}
			InventoryHelper.switchToSlot(crystalSlot);
		}
		
		if (!InventoryHelper.isHolding(Items.END_CRYSTAL)) return false;
		
		// Check inhibit
		if (inhibit.getValue() && lastCrystal != null && !lastCrystal.isRemoved()) {
			double dist = lastCrystal.distanceTo(currentTarget);
			if (dist < 4.0) return false;
		}
		
		// Find best position
		BlockPos bestPos = findBestCrystalPosition();
		if (bestPos == null) return false;
		
		// Place crystal
		if (placeCrystalAt(bestPos)) {
			placeTimer.reset();
			lastPlacePos = bestPos;
			crystalsPlaced++;
			
			// Switch back if enabled
			if (switchBack.getValue() && previousSlot != -1) {
				switchBackTimer.reset();
			}
			
			return true;
		}
		
		return false;
	}

	private BlockPos findBestCrystalPosition() {
		List<BlockPos> validPositions = CrystalHelper.getValidCrystalPositions(placeRange.getValue());
		if (validPositions.isEmpty()) return null;
		
		Vec3 targetPos = currentTarget.position();
		
		// Face place check
		boolean shouldFacePlace = facePlace.getValue() && 
			currentTarget.getHealth() <= facePlaceHealth.getValue();
		
		if (shouldFacePlace) {
			BlockPos feetPos = currentTarget.blockPosition().below();
			if (validPositions.contains(feetPos)) {
				return feetPos;
			}
		}
		
		// Predict movement
		if (predictMovement.getValue()) {
			Vec3 velocity = currentTarget.getDeltaMovement();
			int ticks = (int) predictTicks.getValue().doubleValue();
			targetPos = targetPos.add(velocity.scale(ticks));
		}
		
		BlockPos bestPos = null;
		float bestScore = 0;
		
		for (BlockPos pos : validPositions) {
			if (placeRaytrace.getValue() && !BlockHelper.hasLineOfSight(
				PlayerHelper.getEyePos(), Vec3.atCenterOf(pos.above()))) {
				continue;
			}
			
			float targetDamage = CrystalHelper.calculateCrystalDamage(pos, currentTarget);
			float selfDamage = CrystalHelper.calculateCrystalDamage(pos, mc.player);
			
			// Safety checks
			if (selfDamage > maxSelfDamage.getValue()) continue;
			if (targetDamage < minPlaceDamage.getValue()) continue;
			
			if (antiSuicide.getValue() && selfDamage >= PlayerHelper.getHealth()) {
				continue;
			}
			
			float ratio = targetDamage / Math.max(1, selfDamage);
			if (ratio < minDamageRatio.getValue()) continue;
			
			// Calculate score based on mode
			float score = calculatePlaceScore(pos, targetPos, targetDamage, selfDamage);
			
			// Add humanization randomness
			if (humanize.getValue()) {
				score += (float) (Math.random() * 0.5 - 0.25);
			}
			
			if (score > bestScore) {
				bestScore = score;
				bestPos = pos;
			}
		}
		
		return bestPos;
	}

	private float calculatePlaceScore(BlockPos pos, Vec3 targetPos, float targetDamage, float selfDamage) {
		String mode = placeMode.getValue();
		double distance = Math.sqrt(pos.distToCenterSqr(targetPos));
		
		switch (mode) {
			case "Damage":
				return targetDamage;
			case "Safe":
				return targetDamage - (selfDamage * 2);
			case "Smart":
				// Balance between damage and safety
				float damageScore = targetDamage * 2;
				float safetyScore = Math.max(0, maxSelfDamage.getValue().floatValue() - selfDamage);
				float distScore = (float) (10.0 / Math.max(1, distance));
				return damageScore + safetyScore + distScore;
			default: // Normal
				return targetDamage - selfDamage + (float) (10.0 / Math.max(1, distance));
		}
	}

	private boolean placeCrystalAt(BlockPos pos) {
		if (!CrystalHelper.isValidCrystalPos(pos)) return false;
		
		// Apply rotation
		if (!rotationMode.getValue().equals("None")) {
			float[] rotation = RotationHelper.getRotationToBlock(pos.above());
			
			// Add humanization to rotations
			if (humanize.getValue()) {
				rotation[0] += (float) (Math.random() * 2 - 1);
				rotation[1] += (float) (Math.random() * 2 - 1);
			}
			
			if (rotationMode.getValue().equals("Client")) {
				mc.player.setYRot(rotation[0]);
				mc.player.setXRot(rotation[1]);
			}
		}
		
		// Send place packet
		BlockHitResult hitResult = new BlockHitResult(
			Vec3.atCenterOf(pos),
			Direction.UP,
			pos,
			false
		);
		
		mc.getConnection().send(new ServerboundUseItemOnPacket(
			InteractionHand.MAIN_HAND,
			hitResult,
			0
		));
		
		if (!ghostMode.getValue() || Math.random() > 0.3) {
			mc.player.swing(InteractionHand.MAIN_HAND);
		}
		
		return true;
	}

	private void attackCrystal(Entity crystal) {
		if (crystal == null || crystal.isRemoved()) return;
		
		// Apply rotation
		if (!rotationMode.getValue().equals("None")) {
			float[] rotation = RotationHelper.getRotationToEntity(crystal);
			
			if (humanize.getValue()) {
				rotation[0] += (float) (Math.random() * 1.5 - 0.75);
				rotation[1] += (float) (Math.random() * 1.5 - 0.75);
			}
			
			if (rotationMode.getValue().equals("Client")) {
				mc.player.setYRot(rotation[0]);
				mc.player.setXRot(rotation[1]);
			}
		}
		
		// Send attack packet
		mc.getConnection().send(ServerboundInteractPacket.createAttackPacket(
			crystal,
			mc.player.isShiftKeyDown()
		));
		
		if (!ghostMode.getValue() || Math.random() > 0.2) {
			mc.player.swing(InteractionHand.MAIN_HAND);
		}
	}
}
