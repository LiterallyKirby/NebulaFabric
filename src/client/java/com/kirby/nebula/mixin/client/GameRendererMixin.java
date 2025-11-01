package com.kirby.nebula.mixin.client;

import com.kirby.nebula.module.ModuleManager;
import com.kirby.nebula.module.modules.combat.Reach;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    
    /**
     * Modify the reach distance for raycasting
     * This affects what the player can interact with
     */
    @ModifyVariable(
        method = "pick",
        at = @At("HEAD"),
        ordinal = 0,
        argsOnly = true
    )
    private double modifyReachDistance(double distance) {
        Reach reach = (Reach) ModuleManager.getInstance().getModuleByName("Reach");
        if (reach != null && reach.shouldApply()) {
            return distance + reach.getReachDistance();
        }
        return distance;
    }
}
