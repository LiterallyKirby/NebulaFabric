package com.kirby.nebula.module.modules.player;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.settings.BooleanSetting;
import com.kirby.nebula.module.settings.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;

/**
 * Ghost module for faster block placement
 * Note: Requires reflection/mixin to modify place delay
 */
public class FastPlace extends Module {
	private final Minecraft mc = Minecraft.getInstance();
	
	private final NumberSetting delay;
	private final BooleanSetting blocksOnly;
	private final BooleanSetting randomize;
	private final NumberSetting randomAmount;
	
	public FastPlace() {
		super("Fast Place", "Place blocks faster", Category.PLAYER);
		
		this.delay = new NumberSetting(
			"Delay",
			"Block place delay in ticks",
			2.0,
			0.0,
			4.0,
			1.0
		);
		addSetting(delay);
		
		this.blocksOnly = new BooleanSetting(
			"Blocks Only",
			"Only speed up block placement",
			true
		);
		addSetting(blocksOnly);
		
		this.randomize = new BooleanSetting(
			"Randomize",
			"Randomize delay slightly",
			true
		);
		addSetting(randomize);
		
		this.randomAmount = new NumberSetting(
			"Random Amount",
			"Maximum random variation",
			1.0,
			0.0,
			2.0,
			0.5
		);
		addSetting(randomAmount);
	}
	
	public int getPlaceDelay() {
		if (!isEnabled()) return 4; // Default Minecraft delay
		
		int baseDelay = delay.getValue().intValue();
		
		if (randomize.getValue()) {
			int random = (int) (Math.random() * randomAmount.getValue());
			baseDelay += random;
		}
		
		return Math.max(0, baseDelay);
	}
	
	public boolean shouldApply() {
		if (!isEnabled()) return false;
		if (mc.player == null) return false;
		
		if (blocksOnly.getValue()) {
			return mc.player.getMainHandItem().getItem() instanceof BlockItem ||
				   mc.player.getOffhandItem().getItem() instanceof BlockItem;
		}
		
		return true;
	}
}
