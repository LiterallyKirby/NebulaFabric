package com.kirby.nebula.mixin.client;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class VelocityMixin {
    
    /**
     * Velocity reduction is handled in the Velocity module's onTick method
     * This mixin provides a potential injection point if packet-based velocity is needed later
     */
    @Inject(method = "aiStep", at = @At("HEAD"))
    private void onAiStep(CallbackInfo ci) {
        // Velocity logic is in module
    }
}
