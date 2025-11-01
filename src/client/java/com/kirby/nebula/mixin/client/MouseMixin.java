package com.kirby.nebula.mixin.client;

import com.kirby.nebula.module.ModuleManager;
import com.kirby.nebula.module.modules.combat.AimAssist;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MouseHandler.class)
public class MouseMixin {
    
    /**
     * Modify horizontal mouse movement for aim assist
     */
    @ModifyVariable(
        method = "turnPlayer",
        at = @At("HEAD"),
        ordinal = 2,
        argsOnly = true
    )
    private double modifyMouseX(double mouseX) {
        AimAssist aimAssist = (AimAssist) ModuleManager.getInstance().getModuleByName("Aim Assist");
        if (aimAssist != null && aimAssist.isEnabled()) {
            // The module handles aim assist in its own onTick method
            // This mixin could be used for mouse input modification if needed
        }
        return mouseX;
    }
    
    /**
     * Modify vertical mouse movement for aim assist
     */
    @ModifyVariable(
        method = "turnPlayer",
        at = @At("HEAD"),
        ordinal = 3,
        argsOnly = true
    )
    private double modifyMouseY(double mouseY) {
        AimAssist aimAssist = (AimAssist) ModuleManager.getInstance().getModuleByName("Aim Assist");
        if (aimAssist != null && aimAssist.isEnabled()) {
            // The module handles aim assist in its own onTick method
        }
        return mouseY;
    }
}
