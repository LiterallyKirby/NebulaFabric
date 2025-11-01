package com.kirby.nebula.module.modules.movement;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.settings.BooleanSetting;
import com.kirby.nebula.module.settings.ModeSetting;
import net.minecraft.client.Minecraft;

public class ToggleSprint extends Module {
	private final Minecraft mc = Minecraft.getInstance();
	
	private final BooleanSetting omnidirectional;
	private final BooleanSetting keepSprint;
	private final ModeSetting mode;

	public ToggleSprint() {
		super("Toggle Sprint", "Automatically sprints when moving forward", Category.MOVEMENT);
		
		// Add settings
		this.omnidirectional = new BooleanSetting(
			"Omnidirectional",
			"Sprint in all directions",
			false
		);
		addSetting(omnidirectional);
		
		this.keepSprint = new BooleanSetting(
			"Keep Sprint",
			"Don't stop sprinting when hitting entities",
			true
		);
		addSetting(keepSprint);
		
		this.mode = new ModeSetting(
			"Mode",
			"Sprint activation mode",
			"Always",
			"Always", "OnMove", "Manual"
		);
		addSetting(mode);
	}

	@Override
	public void onEnable() {
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
		if (mc.player != null) {
			mc.player.setSprinting(false);
		}
	}

	@Override
	public void onTick() {
		if (mc.player == null) return;
		
		String currentMode = mode.getValue();
		
		switch (currentMode) {
			case "Always":
				mc.player.setSprinting(true);
				break;
			case "OnMove":
				if (omnidirectional.getValue()) {
					boolean moving = mc.player.input.hasForwardImpulse() || 
									mc.player.input.left || 
									mc.player.input.right;
					mc.player.setSprinting(moving);
				} else {
					mc.player.setSprinting(mc.player.input.hasForwardImpulse());
				}
				break;
			case "Manual":
				// Only sprint when player holds sprint key
				break;
		}
	}
}
