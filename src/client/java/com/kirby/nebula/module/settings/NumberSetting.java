package com.kirby.nebula.module.settings;

public class NumberSetting extends Setting<Double> {
	private final double min;
	private final double max;
	private final double increment;

	public NumberSetting(String name, String description, double defaultValue, double min, double max, double increment) {
		super(name, description, defaultValue);
		this.min = min;
		this.max = max;
		this.increment = increment;
	}

	@Override
	public void setValue(Double value) {
		super.setValue(Math.max(min, Math.min(max, value)));
	}

	public void increment() {
		setValue(getValue() + increment);
	}

	public void decrement() {
		setValue(getValue() - increment);
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public double getIncrement() {
		return increment;
	}

	@Override
	public SettingType getType() {
		return SettingType.NUMBER;
	}
}
