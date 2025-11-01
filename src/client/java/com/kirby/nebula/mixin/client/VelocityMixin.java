package com.kirby.nebula.mixin.client;

import com.kirby.nebula.module.ModuleManager;
import com.kirby.nebula.module.modules.combat.Velocity;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LocalPlayer.class)
public class VelocityMixin {
    
    /**
     * This mixin intercepts velocity changes from knockback
     * Note: The Velocity module itself handles the logic in onTick,
     * but this mixin could be used for packet-based velocity cancellation
     */
    @ModifyVariable(
        method = "hurtTo",
        at = @At("HEAD"),
        ordinal = 0,
        argsOnly = true
    )
    private float modifyHurtAmount(float amount) {
        Velocity velocity = (Velocity) ModuleManager.getInstance().getModuleByName("Velocity");
        if (velocity != null && velocity.isEnabled()) {
            // The module handles velocity reduction in its onTick method
            // This is just here as a backup/alternative implementation point
        }
        return amount;
    }
}
