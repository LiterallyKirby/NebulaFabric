package com.kirby.nebula.module.modules.combat;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.settings.BooleanSetting;
import com.kirby.nebula.module.settings.NumberSetting;
import com.kirby.nebula.util.InventoryHelper;
import net.minecraft.client.Minecraft;

/**
 * Ghost module that extends reach distance subtly
 * Note: This requires mixins to actually work - this is just the configuration
 */
public class Reach extends Module {
	private final Minecraft mc = Minecraft.getInstance();
	
	private final NumberSetting reach;
	private final BooleanSetting randomize;
	private final NumberSetting randomAmount;
	private final BooleanSetting weaponsOnly;
	private final BooleanSetting combatOnly;
	
	public Reach() {
		super("Reach", "Extends attack reach distance", Category.COMBAT);
		
		this.reach = new NumberSetting(
			"Reach",
			"Additional reach distance",
			0.2,
			0.0,
			1.5,
			0.05
		);
		addSetting(reach);
		
		this.randomize = new BooleanSetting(
			"Randomize",
			"Randomize reach slightly each hit",
			true
		);
		addSetting(randomize);
		
		this.randomAmount = new NumberSetting(
			"Random Amount",
			"Maximum random variation",
			0.1,
			0.0,
			0.5,
			0.01
		);
		addSetting(randomAmount);
		
		this.weaponsOnly = new BooleanSetting(
			"Weapons Only",
			"Only extend reach when holding weapons",
			true
		);
		addSetting(weaponsOnly);
		
		this.combatOnly = new BooleanSetting(
			"Combat Only",
			"Only extend reach during combat",
			true
		);
		addSetting(combatOnly);
	}
	
	public double getReachDistance() {
		double baseReach = reach.getValue();
		
		if (randomize.getValue()) {
			double random = (Math.random() - 0.5) * 2 * randomAmount.getValue();
			baseReach += random;
		}
		
		return Math.max(0, baseReach);
	}
	
	public boolean shouldApply() {
		if (!isEnabled()) return false;
		if (mc.player == null) return false;
		
		if (weaponsOnly.getValue()) {
			if (!InventoryHelper.isHoldingType(net.minecraft.world.item.SwordItem.class) &&
				!InventoryHelper.isHoldingType(net.minecraft.world.item.AxeItem.class)) {
				return false;
			}
		}
		
		if (combatOnly.getValue()) {
			// Check if player recently attacked (within last 2 seconds)
			return mc.player.getAttackStrengthScale(0.5f) < 1.0f;
		}
		
		return true;
	}
}
