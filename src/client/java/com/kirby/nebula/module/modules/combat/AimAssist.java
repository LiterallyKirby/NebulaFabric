package com.kirby.nebula.module.modules.combat;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.settings.BooleanSetting;
import com.kirby.nebula.module.settings.ModeSetting;
import com.kirby.nebula.module.settings.NumberSetting;
import com.kirby.nebula.util.InventoryHelper;
import com.kirby.nebula.util.PlayerHelper;
import com.kirby.nebula.util.RotationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * Ghost module that subtly assists aim towards nearby targets
 */
public class AimAssist extends Module {
	private final Minecraft mc = Minecraft.getInstance();
	
	private final NumberSetting range;
	private final NumberSetting strength;
	private final NumberSetting fov;
	private final BooleanSetting clickOnly;
	private final BooleanSetting weaponsOnly;
	private final BooleanSetting ignoreTeam;
	private final BooleanSetting vertical;
	private final ModeSetting mode;
	private final BooleanSetting smoothing;
	private final NumberSetting smoothSpeed;
	
	private LivingEntity currentTarget;
	private long lastClickTime = 0;
	
	public AimAssist() {
		super("Aim Assist", "Subtly helps aim at nearby targets", Category.COMBAT);
		
		this.range = new NumberSetting(
			"Range",
			"Maximum range to assist",
			4.5,
			1.0,
			6.0,
			0.5
		);
		addSetting(range);
		
		this.strength = new NumberSetting(
			"Strength",
			"How much to assist (lower is more legit)",
			15.0,
			1.0,
			100.0,
			1.0
		);
		addSetting(strength);
		
		this.fov = new NumberSetting(
			"FOV",
			"Field of view to check for targets",
			90.0,
			30.0,
			180.0,
			5.0
		);
		addSetting(fov);
		
		this.clickOnly = new BooleanSetting(
			"Click Only",
			"Only assist when attacking",
			true
		);
		addSetting(clickOnly);
		
		this.weaponsOnly = new BooleanSetting(
			"Weapons Only",
			"Only assist when holding weapons",
			true
		);
		addSetting(weaponsOnly);
		
		this.ignoreTeam = new BooleanSetting(
			"Ignore Team",
			"Don't target teammates",
			true
		);
		addSetting(ignoreTeam);
		
		this.vertical = new BooleanSetting(
			"Vertical",
			"Assist vertical aim (less legit)",
			false
		);
		addSetting(vertical);
		
		this.mode = new ModeSetting(
			"Mode",
			"Aim assist mode",
			"Snap",
			"Snap", "Smooth", "Sticky"
		);
		addSetting(mode);
		
		this.smoothing = new BooleanSetting(
			"Smoothing",
			"Smooth aim movements",
			true
		);
		addSetting(smoothing);
		
		this.smoothSpeed = new NumberSetting(
			"Smooth Speed",
			"Speed of smooth aim",
			0.3,
			0.1,
			1.0,
			0.05
		);
		addSetting(smoothSpeed);
	}
	
	@Override
	public void onTick() {
		if (mc.player == null || mc.level == null) return;
		
		// Check if we should assist
		if (clickOnly.getValue()) {
			if (!mc.options.keyAttack.isDown()) {
				currentTarget = null;
				return;
			}
			lastClickTime = System.currentTimeMillis();
		}
		
		// Weapons only check
		if (weaponsOnly.getValue()) {
			if (!InventoryHelper.isHoldingType(net.minecraft.world.item.SwordItem.class) &&
				!InventoryHelper.isHoldingType(net.minecraft.world.item.AxeItem.class)) {
				return;
			}
		}
		
		// Find target
		findTarget();
		
		if (currentTarget == null || currentTarget.isDeadOrDying()) {
			currentTarget = null;
			return;
		}
		
		// Apply aim assist
		applyAimAssist();
	}
	
	private void findTarget() {
		List<LivingEntity> entities = PlayerHelper.getLivingEntitiesInRange(range.getValue());
		
		if (entities.isEmpty()) {
			currentTarget = null;
			return;
		}
		
		// Filter entities
		entities.removeIf(entity -> {
			if (entity.isDeadOrDying()) return true;
			
			// Ignore teammates
			if (ignoreTeam.getValue() && entity instanceof Player) {
				if (PlayerHelper.isFriend(entity)) return true;
			}
			
			// Check FOV
			float[] rotation = RotationHelper.getRotationToEntity(entity);
			float[] diff = RotationHelper.getRotationDifference(rotation);
			float totalDiff = (float) Math.sqrt(diff[0] * diff[0] + diff[1] * diff[1]);
			
			return totalDiff > fov.getValue() / 2;
		});
		
		if (entities.isEmpty()) {
			currentTarget = null;
			return;
		}
		
		// Find closest to crosshair
		currentTarget = entities.stream()
			.min((e1, e2) -> {
				float[] rot1 = RotationHelper.getRotationToEntity(e1);
				float[] rot2 = RotationHelper.getRotationToEntity(e2);
				float[] diff1 = RotationHelper.getRotationDifference(rot1);
				float[] diff2 = RotationHelper.getRotationDifference(rot2);
				
				float dist1 = (float) Math.sqrt(diff1[0] * diff1[0] + diff1[1] * diff1[1]);
				float dist2 = (float) Math.sqrt(diff2[0] * diff2[0] + diff2[1] * diff2[1]);
				
				return Float.compare(dist1, dist2);
			})
			.orElse(null);
	}
	
	private void applyAimAssist() {
		if (currentTarget == null) return;
		
		float[] targetRotation = RotationHelper.getRotationToEntity(currentTarget);
		float[] currentRotation = new float[]{mc.player.getYRot(), mc.player.getXRot()};
		
		String currentMode = mode.getValue();
		float assistStrength = strength.getValue().floatValue() / 100.0f;
		
		switch (currentMode) {
			case "Snap":
				applySnapAim(currentRotation, targetRotation, assistStrength);
				break;
			case "Smooth":
				applySmoothAim(currentRotation, targetRotation, assistStrength);
				break;
			case "Sticky":
				applyStickyAim(currentRotation, targetRotation, assistStrength);
				break;
		}
	}
	
	private void applySnapAim(float[] current, float[] target, float strength) {
		float yawDiff = RotationHelper.wrapAngleTo180(target[0] - current[0]);
		float pitchDiff = RotationHelper.wrapAngleTo180(target[1] - current[1]);
		
		float newYaw = current[0] + yawDiff * strength;
		float newPitch = current[1] + (vertical.getValue() ? pitchDiff * strength : 0);
		
		mc.player.setYRot(newYaw);
		mc.player.setXRot(newPitch);
	}
	
	private void applySmoothAim(float[] current, float[] target, float strength) {
		if (!smoothing.getValue()) {
			applySnapAim(current, target, strength);
			return;
		}
		
		float smoothFactor = smoothSpeed.getValue().floatValue();
		float[] smoothed = RotationHelper.smoothRotation(current, target, smoothFactor * strength);
		
		mc.player.setYRot(smoothed[0]);
		if (vertical.getValue()) {
			mc.player.setXRot(smoothed[1]);
		}
	}
	
	private void applyStickyAim(float[] current, float[] target, float strength) {
		// Sticky aim slows mouse movement when close to target
		float[] diff = RotationHelper.getRotationDifference(target);
		float distance = (float) Math.sqrt(diff[0] * diff[0] + diff[1] * diff[1]);
		
		if (distance < 10.0f) {
			// Within sticky range - slow down movement
			float slowFactor = 1.0f - (strength * (1.0f - distance / 10.0f));
			
			// This would require mouse input interception
			// For now, just apply gentle pull
			applySnapAim(current, target, strength * 0.3f);
		}
	}
}
