package com.kirby.nebula.module.modules.movement;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.settings.ModeSetting;
import com.kirby.nebula.module.settings.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public class Speed extends Module {
	private final Minecraft mc = Minecraft.getInstance();
	private final ModeSetting mode;
	private final NumberSetting speedValue;

	public Speed() {
		super("Speed", "Move faster", Category.MOVEMENT);
		
		this.mode = new ModeSetting(
			"Mode",
			"Speed mode",
			"Vanilla",
			"Vanilla", "Strafe"
		);
		addSetting(mode);
		
		this.speedValue = new NumberSetting(
			"Speed",
			"Speed multiplier",
			1.5,
			1.0,
			3.0,
			0.1
		);
		addSetting(speedValue);
	}

	@Override
	public void onTick() {
		if (mc.player == null) return;
		
		String currentMode = mode.getValue();
		double speed = speedValue.getValue();
		
		if (currentMode.equals("Vanilla")) {
			if (mc.player.onGround() && isMoving()) {
				Vec3 motion = mc.player.getDeltaMovement();
				mc.player.setDeltaMovement(motion.x * speed, motion.y, motion.z * speed);
			}
		} else if (currentMode.equals("Strafe")) {
			if (mc.player.onGround() && isMoving()) {
				double yaw = Math.toRadians(mc.player.getYRot());
				double forward = mc.player.input.hasForwardImpulse() ? 1 : 0;
				double strafe = 0;
				
				if (mc.player.input.left) strafe += 1;
				if (mc.player.input.right) strafe -= 1;
				
				if (forward == 0 && strafe == 0) return;
				
				double angle = Math.atan2(strafe, forward);
				double x = -Math.sin(yaw + angle) * speed * 0.3;
				double z = Math.cos(yaw + angle) * speed * 0.3;
				
				mc.player.setDeltaMovement(x, mc.player.getDeltaMovement().y, z);
			}
		}
	}
	
	private boolean isMoving() {
		return mc.player.input.hasForwardImpulse() || mc.player.input.left || mc.player.input.right;
	}
}
