package com.kirby.nebula.module.modules.combat;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.settings.BooleanSetting;
import com.kirby.nebula.module.settings.NumberSetting;
import net.minecraft.client.Minecraft;


public class Hitboxes extends Module {
	private final Minecraft mc = Minecraft.getInstance();
	
	private final NumberSetting expand;
	private final BooleanSetting playersOnly;
	private final BooleanSetting combatOnly;
	private final BooleanSetting randomize;
	
	public Hitboxes() {
		super("Hitboxes", "Slightly expands entity hitboxes", Category.COMBAT);
		
		this.expand = new NumberSetting(
			"Expand",
			"How much to expand hitboxes",
			0.1,
			0.0,
			0.5,
			0.01
		);
		addSetting(expand);
		
		this.playersOnly = new BooleanSetting(
			"Players Only",
			"Only expand player hitboxes",
			true
		);
		addSetting(playersOnly);
		
		this.combatOnly = new BooleanSetting(
			"Combat Only",
			"Only expand during combat",
			true
		);
		addSetting(combatOnly);
		
		this.randomize = new BooleanSetting(
			"Randomize",
			"Randomize expansion slightly",
			true
		);
		addSetting(randomize);
	}
	
	public double getExpansion() {
		double expansion = expand.getValue();
		
		if (randomize.getValue()) {
			expansion += (Math.random() - 0.5) * 0.05;
		}
		
		return Math.max(0, expansion);
	}
	
	public boolean shouldExpand() {
		if (!isEnabled()) return false;
		
		if (combatOnly.getValue() && mc.player != null) {
			return mc.player.getAttackStrengthScale(0.5f) < 1.0f;
		}
		
		return true;
	}
}
