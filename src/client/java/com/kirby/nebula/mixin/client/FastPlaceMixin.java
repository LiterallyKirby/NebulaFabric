package com.kirby.nebula.mixin.client;

import com.kirby.nebula.module.ModuleManager;
import com.kirby.nebula.module.modules.player.FastPlace;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class FastPlaceMixin {
    
    @Shadow
    private int rightClickDelay;
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        FastPlace fastPlace = (FastPlace) ModuleManager.getInstance().getModuleByName("Fast Place");
        if (fastPlace != null && fastPlace.shouldApply()) {
            int desiredDelay = fastPlace.getPlaceDelay();
            if (this.rightClickDelay > desiredDelay) {
                this.rightClickDelay = desiredDelay;
            }
        }
    }
}
