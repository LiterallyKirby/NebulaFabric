package com.kirby.nebula;

import com.kirby.nebula.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

public class NebulaClient implements ClientModInitializer {
	private static KeyMapping menuKey;

	@Override
	public void onInitializeClient() {
		Nebula.LOGGER.info("Initializing Nebula Client...");

		// Initialize the module manager
		ModuleManager.getInstance();
		Nebula.LOGGER.info("Module system initialized");

		// Register the menu keybinding (Right Shift)
		menuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.nebula.menu",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_RIGHT_SHIFT,
			"category.nebula"
		));

		// Register tick event for module updates
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// Check menu key
			while (menuKey.consumeClick()) {
				Minecraft.getInstance().setScreen(new NebulaMenuScreen());
			}

			// Update all enabled modules
			ModuleManager.getInstance().onTick();
		});

		// Register render event for module rendering
		WorldRenderEvents.AFTER_ENTITIES.register(context -> {
			ModuleManager.getInstance().onRender();
		});

		// Register key press handler for module keybinds
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// You can add keybind handling here
			// For now, modules are toggled through the menu
		});

		Nebula.LOGGER.info("Nebula Client initialized successfully!");
	}
}
