package com.kirby.nebula;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;

public class NebulaClient implements ClientModInitializer {
	private static KeyMapping helloKey;

	@Override
	public void onInitializeClient() {
		// Register the keybinding
		helloKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.nebula.hello", // Translation key
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_Y, // The Y key
			"category.nebula" // Category in controls menu
		));

		// Register a tick event to check if the key is pressed
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (helloKey.consumeClick()) {
				Nebula.LOGGER.info("hello");
			}
		});
	}
}
