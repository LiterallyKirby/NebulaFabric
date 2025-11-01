package com.kirby.nebula.module.settings;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {
	private final List<String> modes;
	private int currentIndex;

	public ModeSetting(String name, String description, String defaultValue, String... modes) {
		super(name, description, defaultValue);
		this.modes = Arrays.asList(modes);
		this.currentIndex = this.modes.indexOf(defaultValue);
	}

	public void cycle() {
		currentIndex = (currentIndex + 1) % modes.size();
		setValue(modes.get(currentIndex));
	}

	public List<String> getModes() {
		return modes;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setMode(String mode) {
		if (modes.contains(mode)) {
			currentIndex = modes.indexOf(mode);
			setValue(mode);
		}
	}

	@Override
	public SettingType getType() {
		return SettingType.MODE;
	}
}
