package com.kirby.nebula.mixin.client;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to track player attacks for ghost modules
 * This helps modules know when combat is happening
 */
@Mixin(LocalPlayer.class)
public class AttackCooldownMixin {
    
    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttack(CallbackInfo ci) {
        // This gets called whenever the player attacks
        // Ghost modules can use this to track combat state
    }
    
    @Inject(method = "swing", at = @At("HEAD"))
    private void onSwing(CallbackInfo ci) {
        // This gets called whenever the player swings
        // Useful for tracking click timing
    }
}
