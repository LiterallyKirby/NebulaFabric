package com.kirby.nebula.module;

public enum Category {
	COMBAT("Combat"),
	RENDERING("Rendering"),
	WORLD("World"),
	MOVEMENT("Movement"),
	PLAYER("Player"),
	MISC("Misc");

	private final String displayName;

	Category(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
