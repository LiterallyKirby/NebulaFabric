package com.kirby.nebula.module.modules.movement;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import net.minecraft.client.Minecraft;

public class ToggleSprint extends Module {
	private final Minecraft mc = Minecraft.getInstance();

	// REQUIRED: Constructor that calls super with name, description, and category
	public ToggleSprint() {
		super("Toggle Sprint", "Automatically sprints when moving forward", Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		// Setup code here
	}

	@Override
	public void onDisable() {
		super.onDisable();
		// Cleanup code here
	}

	@Override
	public void onTick() {
		// Only sprint if player exists and is moving forward
		if (mc.player != null && mc.player.input.hasForwardImpulse()) {
			mc.player.setSprinting(true);
		}
	}
}
