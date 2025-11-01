package com.kirby.nebula.module.modules.world;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.settings.ModeSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class NoFall extends Module {
	private final Minecraft mc = Minecraft.getInstance();
	private final ModeSetting mode;

	public NoFall() {
		super("NoFall", "Prevents fall damage", Category.WORLD);
		
		this.mode = new ModeSetting(
			"Mode",
			"NoFall mode",
			"Packet",
			"Packet", "NoGround"
		);
		addSetting(mode);
	}

	@Override
	public void onTick() {
		if (mc.player == null || mc.getConnection() == null) return;
		
		String currentMode = mode.getValue();
		
		if (currentMode.equals("Packet")) {
			// Send ground packet when falling
			if (mc.player.fallDistance > 2.0f) {
				mc.getConnection().send(new ServerboundMovePlayerPacket.StatusOnly(true));
			}
		} else if (currentMode.equals("NoGround")) {
			// Set fallDistance to 0
			if (mc.player.fallDistance > 0) {
				mc.player.fallDistance = 0;
			}
		}
	}
}
