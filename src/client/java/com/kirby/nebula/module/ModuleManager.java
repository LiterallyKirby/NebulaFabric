package com.kirby.nebula.module;

import com.kirby.nebula.module.modules.combat.*;
import com.kirby.nebula.module.modules.rendering.*;
import com.kirby.nebula.module.modules.world.*;
import com.kirby.nebula.module.modules.movement.*;
import com.kirby.nebula.module.modules.player.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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
	}

	public static ModuleManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ModuleManager();
		}
		return INSTANCE;
	}

	// === MODULE REGISTRATION ===

	private void registerCombatModules() {
		modules.add(new Velocity());
		modules.add(new AutoCrystal());
		modules.add(new LeftClicker());
		
		// Ghost combat modules
		modules.add(new Autoclicker());
		modules.add(new AimAssist());
		modules.add(new Reach());
		modules.add(new Hitboxes());
	}

	private void registerPlayerModules() {
		modules.add(new RightClicker());
		
		// Ghost player modules
		modules.add(new FastPlace());
	}

	private void registerRenderingModules() {
		modules.add(new Fullbright());
	}

	private void registerWorldModules() {
		modules.add(new NoFall());
	}

	private void registerMovementModules() {
		modules.add(new ToggleSprint());
		modules.add(new Speed());
		modules.add(new Fly());
		
		// Ghost movement modules
		modules.add(new SafeWalk());
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
