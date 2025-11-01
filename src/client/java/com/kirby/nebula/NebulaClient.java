package com.kirby.nebula;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

public class NebulaClient implements ClientModInitializer {
	private static KeyMapping menuKey;

	@Override
	public void onInitializeClient() {
		// Register the menu keybinding (Right Shift)
		menuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.nebula.menu",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_RIGHT_SHIFT,
			"category.nebula"
		));

		// Register a tick event to check if the key is pressed
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (menuKey.consumeClick()) {
				// Open the menu screen
				Minecraft.getInstance().setScreen(new NebulaMenuScreen());
			}
		});
	}
}
