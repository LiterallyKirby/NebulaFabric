package com.kirby.nebula.module.modules.combat;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.client.player.LocalPlayer;

public class Velocity extends Module {
    private final Minecraft mc = Minecraft.getInstance();

    public Velocity() {
        super("Velocity", "Reduces the knockback you take", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        // Could reset any stored data here if needed
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // Reset any modifications back to normal
    }

    @Override
    public void onTick() {
        if (mc.player != null) {
            LocalPlayer player = mc.player;


            if (player.hurtTime > 0) {
                player.setDeltaMovement(player.getDeltaMovement().multiply(0, 0, 0));

            }
        }
    }
}
