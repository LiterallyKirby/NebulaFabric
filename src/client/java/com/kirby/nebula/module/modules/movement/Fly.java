package com.kirby.nebula.module.modules.movement;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.settings.ModeSetting;
import com.kirby.nebula.module.settings.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public class Fly extends Module {
	private final Minecraft mc = Minecraft.getInstance();
	private final ModeSetting mode;
	private final NumberSetting speed;

	public Fly() {
		super("Fly", "Allows you to fly", Category.MOVEMENT);
		
		this.mode = new ModeSetting(
			"Mode",
			"Fly mode",
			"Vanilla",
			"Vanilla", "Motion"
		);
		addSetting(mode);
		
		this.speed = new NumberSetting(
			"Speed",
			"Fly speed",
			1.0,
			0.1,
			5.0,
			0.1
		);
		addSetting(speed);
	}

	@Override
	public void onDisable() {
		super.onDisable();
		if (mc.player != null && mc.player.getAbilities().mayfly) {
			mc.player.getAbilities().flying = false;
			mc.player.getAbilities().mayfly = false;
		}
	}

	@Override
	public void onTick() {
		if (mc.player == null) return;
		
		String currentMode = mode.getValue();
		double flySpeed = speed.getValue();
		
		if (currentMode.equals("Vanilla")) {
			mc.player.getAbilities().mayfly = true;
			mc.player.getAbilities().flying = true;
			mc.player.getAbilities().setFlyingSpeed((float) (flySpeed * 0.05f));
		} else if (currentMode.equals("Motion")) {
			mc.player.getAbilities().flying = false;
			mc.player.setDeltaMovement(0, 0, 0);
			
			double motionX = 0;
			double motionY = 0;
			double motionZ = 0;
			
			if (mc.player.input.hasForwardImpulse()) {
				double yaw = Math.toRadians(mc.player.getYRot());
				motionX -= Math.sin(yaw) * flySpeed * 0.2;
				motionZ += Math.cos(yaw) * flySpeed * 0.2;
			}
			
			if (mc.player.input.jumping) {
				motionY = flySpeed * 0.2;
			}
			
			if (mc.player.input.shiftKeyDown) {
				motionY = -flySpeed * 0.2;
			}
			
			mc.player.setDeltaMovement(motionX, motionY, motionZ);
		}
	}
}
