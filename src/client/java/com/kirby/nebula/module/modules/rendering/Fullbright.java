package com.kirby.nebula.module.modules.rendering;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.settings.ModeSetting;
import com.kirby.nebula.module.settings.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class Fullbright extends Module {
	private final Minecraft mc = Minecraft.getInstance();
	private final ModeSetting mode;
	private final NumberSetting gamma;
	private double oldGamma;

	public Fullbright() {
		super("Fullbright", "See in the dark", Category.RENDERING);
		
		this.mode = new ModeSetting(
			"Mode",
			"Fullbright mode",
			"Gamma",
			"Gamma", "NightVision"
		);
		addSetting(mode);
		
		this.gamma = new NumberSetting(
			"Gamma",
			"Gamma level (Gamma mode only)",
			10.0,
			1.0,
			15.0,
			0.5
		);
		addSetting(gamma);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		oldGamma = mc.options.gamma().get();
		
		if (mode.getValue().equals("Gamma")) {
			mc.options.gamma().set(gamma.getValue());
		}
	}

	@Override
	public void onDisable() {
		super.onDisable();
		mc.options.gamma().set(oldGamma);
		
		if (mc.player != null && mode.getValue().equals("NightVision")) {
			mc.player.removeEffect(MobEffects.NIGHT_VISION);
		}
	}

	@Override
	public void onTick() {
		if (mc.player == null) return;
		
		String currentMode = mode.getValue();
		
		if (currentMode.equals("Gamma")) {
			mc.options.gamma().set(gamma.getValue());
		} else if (currentMode.equals("NightVision")) {
			mc.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 999999, 0, false, false));
		}
	}
}
