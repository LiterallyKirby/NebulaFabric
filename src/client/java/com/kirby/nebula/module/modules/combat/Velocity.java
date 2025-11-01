package com.kirby.nebula.module.modules.combat;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.settings.BooleanSetting;
import com.kirby.nebula.module.settings.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class Velocity extends Module {
	private final Minecraft mc = Minecraft.getInstance();
	
	private final NumberSetting horizontal;
	private final NumberSetting vertical;
	private final BooleanSetting onlyWhileHurt;

	public Velocity() {
		super("Velocity", "Reduces the knockback you take", Category.COMBAT);
		
		// Add settings
		this.horizontal = new NumberSetting(
			"Horizontal",
			"Horizontal knockback multiplier",
			0.0,
			0.0,
			1.0,
			0.1
		);
		addSetting(horizontal);
		
		this.vertical = new NumberSetting(
			"Vertical",
			"Vertical knockback multiplier",
			0.0,
			0.0,
			1.0,
			0.1
		);
		addSetting(vertical);
		
		this.onlyWhileHurt = new BooleanSetting(
			"Only While Hurt",
			"Only reduce velocity when taking damage",
			true
		);
		addSetting(onlyWhileHurt);
	}

	@Override
	public void onEnable() {
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@Override
	public void onTick() {
		if (mc.player != null) {
			LocalPlayer player = mc.player;

			if (!onlyWhileHurt.getValue() || player.hurtTime > 0) {
				double h = horizontal.getValue();
				double v = vertical.getValue();
				player.setDeltaMovement(
					player.getDeltaMovement().multiply(h, v, h)
				);
			}
		}
	}
}
