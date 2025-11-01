package com.kirby.nebula.mixin.client;

import com.kirby.nebula.module.ModuleManager;
import com.kirby.nebula.module.modules.combat.Reach;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class ReachMixin {
    
    @Inject(method = "getBlockReach", at = @At("RETURN"), cancellable = true)
    private void onGetBlockReach(CallbackInfoReturnable<Double> cir) {
        Reach reach = (Reach) ModuleManager.getInstance().getModuleByName("Reach");
        if (reach != null && reach.shouldApply()) {
            double original = cir.getReturnValue();
            double extended = original + reach.getReachDistance();
            cir.setReturnValue(extended);
        }
    }
    
    @Inject(method = "getEntityReach", at = @At("RETURN"), cancellable = true)
    private void onGetEntityReach(CallbackInfoReturnable<Double> cir) {
        Reach reach = (Reach) ModuleManager.getInstance().getModuleByName("Reach");
        if (reach != null && reach.shouldApply()) {
            double original = cir.getReturnValue();
            double extended = original + reach.getReachDistance();
            cir.setReturnValue(extended);
        }
    }
}
