package com.kirby.nebula.mixin.client;

import com.kirby.nebula.module.ModuleManager;
import com.kirby.nebula.module.modules.combat.Hitboxes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ProjectileUtil.class)
public class ProjectileMixin {
    
    /**
     * Modify entity bounding boxes for projectile/raycast checks
     * This makes the hitbox expansion work for all targeting
     */
    @ModifyVariable(
        method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;",
        at = @At("HEAD"),
        ordinal = 0,
        argsOnly = true
    )
    private static AABB modifyBoundingBox(AABB box, Entity entity, Vec3 start, Vec3 end, AABB searchBox) {
        Hitboxes hitboxes = (Hitboxes) ModuleManager.getInstance().getModuleByName("Hitboxes");
        if (hitboxes != null && hitboxes.shouldExpand()) {
            // This will expand the search area for entity hits
            double expansion = hitboxes.getExpansion();
            return box.inflate(expansion);
        }
        return box;
    }
}
