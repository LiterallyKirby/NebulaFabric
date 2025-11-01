package com.kirby.nebula.module.modules.combat;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class KillauraModule extends Module {
	private final Minecraft mc = Minecraft.getInstance();
	private final double reach = 4.0;

	public KillauraModule() {
		super("Killaura", "Automatically attacks nearby entities", Category.COMBAT);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		// Setup code here
	}

	@Override
	public void onDisable() {
		super.onDisable();
		// Cleanup code here
	}

	@Override
	public void onTick() {
		if (mc.player == null || mc.level == null) return;

		// Find nearest entity within reach
		Entity target = findTarget();
		if (target != null) {
			// Attack logic would go here
			// mc.gameMode.attack(mc.player, target);
		}
	}

	private Entity findTarget() {
		return mc.level.getEntities(mc.player, mc.player.getBoundingBox().inflate(reach))
			.stream()
			.filter(e -> e instanceof LivingEntity)
			.filter(e -> e != mc.player)
			.filter(e -> !e.isRemoved())
			.filter(e -> mc.player.distanceTo(e) <= reach)
			.min((e1, e2) -> Double.compare(
				mc.player.distanceTo(e1),
				mc.player.distanceTo(e2)
			))
			.orElse(null);
	}
}
