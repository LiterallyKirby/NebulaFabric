package com.kirby.nebula.mixin.client;

import com.kirby.nebula.module.ModuleManager;
import com.kirby.nebula.module.modules.combat.Hitboxes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    
    @Shadow
    public abstract AABB getBoundingBox();
    
    /**
     * Modify the entity's bounding box for collision/targeting
     * This affects raycasting and entity selection
     */
    @Inject(method = "getBoundingBoxForCulling", at = @At("RETURN"), cancellable = true)
    private void onGetBoundingBoxForCulling(CallbackInfoReturnable<AABB> cir) {
        Entity entity = (Entity) (Object) this;
        Hitboxes hitboxes = (Hitboxes) ModuleManager.getInstance().getModuleByName("Hitboxes");
        
        if (hitboxes != null && hitboxes.shouldExpand() && entity instanceof Player) {
            double expansion = hitboxes.getExpansion();
            AABB original = cir.getReturnValue();
            AABB expanded = original.inflate(expansion);
            cir.setReturnValue(expanded);
        }
    }
}
