package com.kirby.nebula.mixin.client;

import com.kirby.nebula.module.ModuleManager;
import com.kirby.nebula.module.modules.movement.SafeWalk;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class SafeWalkMixin {
    
    /**
     * Inject into isShiftKeyDown to make entity think player is sneaking
     * This prevents falling off edges
     */
    @Inject(method = "isShiftKeyDown", at = @At("HEAD"), cancellable = true)
    private void onIsShiftKeyDown(CallbackInfoReturnable<Boolean> cir) {
        SafeWalk safeWalk = (SafeWalk) ModuleManager.getInstance().getModuleByName("Safe Walk");
        if (safeWalk != null && safeWalk.shouldPreventMovement()) {
            cir.setReturnValue(true);
        }
    }
}
