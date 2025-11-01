package com.kirby.nebula.mixin.client;

import com.kirby.nebula.module.ModuleManager;
import com.kirby.nebula.module.modules.combat.Reach;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class ReachMixin {
    
    @Shadow @Final
    private Minecraft minecraft;
    
    /**
     * Modify the reach distance used for raycasting
     * This affects both block and entity interaction range
     */
    @ModifyVariable(
        method = "pick",
        at = @At("STORE"),
        ordinal = 0
    )
    private double modifyReachDistance(double distance) {
        Reach reach = (Reach) ModuleManager.getInstance().getModuleByName("Reach");
        if (reach != null && reach.shouldApply()) {
            return distance + reach.getReachDistance();
        }
        return distance;
    }
}
