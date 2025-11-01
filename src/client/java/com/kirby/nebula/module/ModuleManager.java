package com.kirby.nebula.module;

import com.kirby.nebula.module.modules.combat.*;
//import com.kirby.nebula.module.modules.rendering.*;
//import com.kirby.nebula.module.modules.world.*;
import com.kirby.nebula.module.modules.movement.*;
//import com.kirby.nebula.module.modules.player.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages all modules in the client
 * To add a new module:
 * 1. Create a new Module class extending Module
 * 2. Add it to the appropriate register method
 * 3. That's it! The UI will automatically display it in the correct category
 */
public class ModuleManager {
	private static ModuleManager INSTANCE;
	private final List<Module> modules = new ArrayList<>();

	private ModuleManager() {
		// Register all modules
		registerCombatModules();
		registerRenderingModules();
		registerWorldModules();
		registerMovementModules();
		registerPlayerModules();
		// Add more register methods as needed
	}

	public static ModuleManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ModuleManager();
		}
		return INSTANCE;
	}

	// === MODULE REGISTRATION ===
	// Simply add new modules to these methods to include them in the client

	private void registerCombatModules() {
		modules.add(new Velocity());
	}

	private void registerRenderingModules() {

	}

	private void registerWorldModules() {

	}

	private void registerMovementModules() {
		modules.add(new ToggleSprint());
	}

	private void registerPlayerModules() {

	}

	// === PUBLIC API ===

	/**
	 * Get all registered modules
	 */
	public List<Module> getModules() {
		return modules;
	}

	/**
	 * Get modules by category
	 */
	public List<Module> getModulesByCategory(Category category) {
		return modules.stream()
				.filter(m -> m.getCategory() == category)
				.collect(Collectors.toList());
	}

	/**
	 * Get a module by name (case insensitive)
	 */
	public Module getModuleByName(String name) {
		return modules.stream()
				.filter(m -> m.getName().equalsIgnoreCase(name))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Get all enabled modules
	 */
	public List<Module> getEnabledModules() {
		return modules.stream()
				.filter(Module::isEnabled)
				.collect(Collectors.toList());
	}

	/**
	 * Enable a module by name
	 */
	public void enableModule(String name) {
		Module module = getModuleByName(name);
		if (module != null) {
			module.setEnabled(true);
		}
	}

	/**
	 * Disable a module by name
	 */
	public void disableModule(String name) {
		Module module = getModuleByName(name);
		if (module != null) {
			module.setEnabled(false);
		}
	}

	/**
	 * Toggle a module by name
	 */
	public void toggleModule(String name) {
		Module module = getModuleByName(name);
		if (module != null) {
			module.toggle();
		}
	}

	/**
	 * Called every tick - updates all enabled modules
	 */
	public void onTick() {
		for (Module module : getEnabledModules()) {
			try {
				module.onTick();
			} catch (Exception e) {
				// Log but don't crash
				System.err.println("Error in module " + module.getName() + ": " + e.getMessage());
			}
		}
	}

	/**
	 * Called every render tick - updates all enabled modules
	 */
	public void onRender() {
		for (Module module : getEnabledModules()) {
			try {
				module.onRender();
			} catch (Exception e) {
				// Log but don't crash
				System.err.println(
						"Error rendering module " + module.getName() + ": " + e.getMessage());
			}
		}
	}

	/**
	 * Handle key press for module keybinds
	 */
	public void onKeyPress(int keyCode) {
		for (Module module : modules) {
			if (module.getKeyBind() == keyCode && keyCode != 0) {
				module.toggle();
			}
		}
	}

	/**
	 * Disable all modules (useful for cleanup)
	 */
	public void disableAll() {
		for (Module module : modules) {
			if (module.isEnabled()) {
				module.setEnabled(false);
			}
		}
	}
}
