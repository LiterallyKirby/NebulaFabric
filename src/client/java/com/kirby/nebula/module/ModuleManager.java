package com.kirby.nebula.module;

import com.kirby.nebula.module.modules.combat.*;
import com.kirby.nebula.module.modules.rendering.*;
import com.kirby.nebula.module.modules.world.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
	private static ModuleManager INSTANCE;
	private final List<Module> modules = new ArrayList<>();

	private ModuleManager() {
		// Register all modules here
		registerCombatModules();
		registerRenderingModules();
		registerWorldModules();
	}

	public static ModuleManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ModuleManager();
		}
		return INSTANCE;
	}

	private void registerCombatModules() {
		modules.add(new KillauraModule());
		modules.add(new VelocityModule());
		modules.add(new AutoTotemModule());
		modules.add(new CriticalsModule());
	}

	private void registerRenderingModules() {
		modules.add(new ESPModule());
		modules.add(new TracersModule());
		modules.add(new NametagsModule());
		modules.add(new FullbrightModule());
	}

	private void registerWorldModules() {
		modules.add(new XrayModule());
		modules.add(new NukerModule());
		modules.add(new AutoMineModule());
		modules.add(new TimerModule());
	}

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
	 * Get a module by name
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
	 * Called every tick - updates all enabled modules
	 */
	public void onTick() {
		for (Module module : getEnabledModules()) {
			module.onTick();
		}
	}

	/**
	 * Called every render tick - updates all enabled modules
	 */
	public void onRender() {
		for (Module module : getEnabledModules()) {
			module.onRender();
		}
	}

	/**
	 * Handle key press for module keybinds
	 */
	public void onKeyPress(int keyCode) {
		for (Module module : modules) {
			if (module.getKeyBind() == keyCode) {
				module.toggle();
			}
		}
	}
}
