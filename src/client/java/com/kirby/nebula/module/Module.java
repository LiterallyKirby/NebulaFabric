package com.kirby.nebula.module;

import com.kirby.nebula.Nebula;
import com.kirby.nebula.module.settings.Setting;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
	private final String name;
	private final String description;
	private final Category category;
	private boolean enabled;
	private int keyBind;
	private final List<Setting<?>> settings = new ArrayList<>();

	public Module(String name, String description, Category category) {
		this.name = name;
		this.description = description;
		this.category = category;
		this.enabled = false;
		this.keyBind = 0;
	}

	/**
	 * Called when the module is enabled
	 */
	public void onEnable() {
		Nebula.LOGGER.info(name + " enabled");
	}

	/**
	 * Called when the module is disabled
	 */
	public void onDisable() {
		Nebula.LOGGER.info(name + " disabled");
	}

	/**
	 * Called every tick when the module is enabled
	 */
	public void onTick() {
		// Override in subclasses if needed
	}

	/**
	 * Called every render tick when the module is enabled
	 */
	public void onRender() {
		// Override in subclasses if needed
	}

	/**
	 * Toggle the module on/off
	 */
	public void toggle() {
		setEnabled(!enabled);
	}

	/**
	 * Set the enabled state
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (enabled) {
			onEnable();
		} else {
			onDisable();
		}
	}

	/**
	 * Add a setting to this module
	 */
	protected void addSetting(Setting<?> setting) {
		settings.add(setting);
	}

	/**
	 * Get all settings for this module
	 */
	public List<Setting<?>> getSettings() {
		return settings;
	}

	/**
	 * Check if this module has any settings
	 */
	public boolean hasSettings() {
		return !settings.isEmpty();
	}

	// Getters
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Category getCategory() {
		return category;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public int getKeyBind() {
		return keyBind;
	}

	public void setKeyBind(int keyBind) {
		this.keyBind = keyBind;
	}
}
