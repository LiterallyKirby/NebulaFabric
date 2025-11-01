package com.kirby.nebula.module.modules.combat;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.settings.BooleanSetting;
import com.kirby.nebula.module.settings.ModeSetting;
import com.kirby.nebula.module.settings.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

public class Velocity extends Module {
	private final Minecraft mc = Minecraft.getInstance();
	
	private final ModeSetting mode;
	private final NumberSetting horizontal;
	private final NumberSetting vertical;
	private final NumberSetting jumpDelay;
	private final BooleanSetting onlyWhileHurt;
	private final BooleanSetting onlyWhileSprint;
	private final BooleanSetting discretize;
	private final NumberSetting stepSize;
	
	private int lastHurtTime = 0;
	private int jumpTicks = 0;
	private boolean shouldJump = false;

	public Velocity() {
		super("Velocity", "Reduces the knockback you take", Category.COMBAT);
		
		this.mode = new ModeSetting(
			"Mode",
			"Velocity reduction mode",
			"Cancel",
			"Cancel", "Jump"
		);
		addSetting(mode);
		
		this.horizontal = new NumberSetting(
			"Horizontal",
			"Horizontal knockback multiplier",
			0.0,
			0.0,
			1.0,
			0.1
		);
		addSetting(horizontal);
		
		this.vertical = new NumberSetting(
			"Vertical",
			"Vertical knockback multiplier",
			0.0,
			0.0,
			1.0,
			0.1
		);
		addSetting(vertical);
		
		this.jumpDelay = new NumberSetting(
			"Jump Delay",
			"Delay in ticks before jumping (Jump mode only)",
			0.0,
			0.0,
			10.0,
			1.0
		);
		addSetting(jumpDelay);
		
		this.onlyWhileHurt = new BooleanSetting(
			"Only While Hurt",
			"Only reduce velocity when taking damage",
			true
		);
		addSetting(onlyWhileHurt);
		
		this.onlyWhileSprint = new BooleanSetting(
			"Only While Sprint",
			"Only reduce velocity when sprinting",
			false
		);
		addSetting(onlyWhileSprint);

		this.discretize = new BooleanSetting(
			"Ghost Mode",
			"Quantize motion to fixed steps to make velocity discrete",
			false
		);
		addSetting(discretize);

		this.stepSize = new NumberSetting(
			"Step Size",
			"Step size for quantization. Only used when Ghost Mode is true.",
			0.05,
			0.01,
			0.5,
			0.01
		);
		addSetting(stepSize);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		lastHurtTime = 0;
		jumpTicks = 0;
		shouldJump = false;
	}

	@Override
	public void onDisable() {
		super.onDisable();
		lastHurtTime = 0;
		jumpTicks = 0;
		shouldJump = false;
	}

	@Override
	public void onTick() {
		if (mc.player == null) return;
		LocalPlayer player = mc.player;

		// Detect a fresh hit (hurtTime counts down)
		boolean justGotHit = player.hurtTime > lastHurtTime && player.hurtTime > 0;

		if (justGotHit && shouldProcessHit(player)) {
			String currentMode = mode.getValue();

			if (currentMode.equals("Cancel")) {
				// Cancel mode - directly reduce velocity using multipliers
				double h = horizontal.getValue().doubleValue();
				double v = vertical.getValue().doubleValue();
				player.setDeltaMovement(applyReduction(player.getDeltaMovement(), h, v));
			} else if (currentMode.equals("Jump")) {
				// Jump mode - schedule a jump after delay
				shouldJump = true;
				jumpTicks = (int) jumpDelay.getValue().doubleValue();
			}
		}

		// Handle jump mode
		if (shouldJump && mode.getValue().equals("Jump")) {
			if (jumpTicks > 0) {
				jumpTicks--;
			} else {
				// Time to jump - only if on ground
				if (player.onGround()) {
					player.jumpFromGround();
				}
				shouldJump = false;
			}
		}

		// Store for next tick comparison
		lastHurtTime = player.hurtTime;
	}

	private boolean shouldProcessHit(LocalPlayer player) {
		boolean hurtCondition = !onlyWhileHurt.getValue() || player.hurtTime > 0;
		boolean sprintCondition = !onlyWhileSprint.getValue() || player.isSprinting();
		return hurtCondition && sprintCondition;
	}

	private Vec3 applyReduction(Vec3 current, double horizontalMul, double verticalMul) {
		double nx = current.x * horizontalMul;
		double ny = current.y * verticalMul;
		double nz = current.z * horizontalMul;

		Vec3 reduced = new Vec3(nx, ny, nz);

		if (discretize.getValue()) {
			double step = stepSize.getValue().doubleValue();
			if (step > 0) {
				reduced = quantize(reduced, step);
			}
		}

		return reduced;
	}

	private Vec3 quantize(Vec3 vec, double step) {
		if (step <= 0) return vec;
		double qx = roundToStep(vec.x, step);
		double qy = roundToStep(vec.y, step);
		double qz = roundToStep(vec.z, step);
		return new Vec3(qx, qy, qz);
	}

	private double roundToStep(double value, double step) {
		return Math.round(value / step) * step;
	}
}
