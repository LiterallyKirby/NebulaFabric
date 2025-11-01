package com.kirby.nebula.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Helper class for End Crystal operations (Auto Crystal)
 */
public class CrystalHelper {
    private static final Minecraft mc = Minecraft.getInstance();

    /**
     * Check if a position is valid for crystal placement
     */
    public static boolean isValidCrystalPos(BlockPos pos) {
        if (mc.level == null) return false;

        // Check if block is bedrock or obsidian
        if (!BlockHelper.isBlock(pos, Blocks.BEDROCK) && !BlockHelper.isBlock(pos, Blocks.OBSIDIAN)) {
            return false;
        }

        // Check if there's space above (2 blocks for 1.13+, 1 block for 1.12)
        BlockPos above = pos.above();
        BlockPos above2 = above.above();

        if (!BlockHelper.isAir(above) || !BlockHelper.isAir(above2)) {
            return false;
        }

        // Check if there are entities blocking
        AABB box = new AABB(above).expandTowards(0, 1, 0);
        List<Entity> entities = mc.level.getEntities(null, box);

        for (Entity entity : entities) {
            if (entity instanceof ItemEntity || entity instanceof ArmorStand) continue;
            return false;
        }

        return true;
    }

    /**
     * Get all valid crystal positions around player
     */
    public static List<BlockPos> getValidCrystalPositions(double range) {
        List<BlockPos> positions = new ArrayList<>();
        if (mc.player == null) return positions;

        BlockPos playerPos = mc.player.blockPosition();
        List<BlockPos> blocks = BlockHelper.getBlocksInSphere(playerPos, range);

        for (BlockPos pos : blocks) {
            if (isValidCrystalPos(pos)) {
                positions.add(pos);
            }
        }

        return positions;
    }

    /**
     * Calculate damage a crystal would deal at position to target
     */
    public static float calculateCrystalDamage(BlockPos crystalPos, Entity target) {
        if (mc.level == null) return 0;

        Vec3 crystalVec = Vec3.atCenterOf(crystalPos.above());
        Vec3 targetPos = target.position();

        double distance = crystalVec.distanceTo(targetPos);

        // Basic explosion damage calculation
        if (distance > 12.0) return 0;

        double impact = (1.0 - distance / 12.0) * 
                       getExposure(crystalVec, target);

        float damage = (float) ((impact * impact + impact) / 2.0 * 7.0 * 12.0 + 1.0);

        // Apply armor reduction (simplified)
        if (target instanceof Player player) {
            damage = applyArmorReduction(damage, player);
        }

        return damage;
    }

    /**
     * Calculate exposure for explosion damage
     */
    private static double getExposure(Vec3 crystal, Entity target) {
        AABB box = target.getBoundingBox();
        double xStep = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
        double yStep = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
        double zStep = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);

        double xOffset = (1.0 - Math.floor(1.0 / xStep) * xStep) / 2.0;
        double zOffset = (1.0 - Math.floor(1.0 / zStep) * zStep) / 2.0;

        int visible = 0;
        int total = 0;

        for (double x = 0.0; x <= 1.0; x += xStep) {
            for (double y = 0.0; y <= 1.0; y += yStep) {
                for (double z = 0.0; z <= 1.0; z += zStep) {
                    Vec3 point = new Vec3(
                        box.minX + (box.maxX - box.minX) * x + xOffset,
                        box.minY + (box.maxY - box.minY) * y,
                        box.minZ + (box.maxZ - box.minZ) * z + zOffset
                    );

                    if (BlockHelper.hasLineOfSight(crystal, point)) {
                        visible++;
                    }
                    total++;
                }
            }
        }

        return (double) visible / total;
    }

    /**
     * Apply armor reduction to damage (simplified)
     */
    private static float applyArmorReduction(float damage, Player player) {
        int armor = player.getArmorValue();
        float reduction = armor * 0.04f; // 4% per armor point
        return damage * (1.0f - Math.min(reduction, 0.8f)); // Max 80% reduction
    }

    /**
     * Get best crystal position to place against target
     */
    public static BlockPos getBestCrystalPos(Entity target, double range) {
        List<BlockPos> positions = getValidCrystalPositions(range);
        if (positions.isEmpty()) return null;

        return positions.stream()
            .max(Comparator.comparingDouble(pos -> calculateCrystalDamage(pos, target)))
            .orElse(null);
    }

    /**
     * Get all End Crystals in range
     */
    public static List<Entity> getCrystalsInRange(double range) {
        List<Entity> crystals = new ArrayList<>();
        if (mc.level == null || mc.player == null) return crystals;

        Vec3 playerPos = mc.player.position();
        for (Entity entity : mc.level.entitiesForRendering()) {
            // Check if entity is End Crystal
            if (entity.getType().toString().contains("end_crystal")) {
                if (entity.distanceToSqr(playerPos) <= range * range) {
                    crystals.add(entity);
                }
            }
        }

        return crystals;
    }

    /**
     * Get closest crystal to position
     */
    public static Entity getClosestCrystal(Vec3 pos, double range) {
        List<Entity> crystals = getCrystalsInRange(range);
        if (crystals.isEmpty()) return null;

        return crystals.stream()
            .min(Comparator.comparingDouble(c -> c.distanceToSqr(pos)))
            .orElse(null);
    }

    /**
     * Check if player has crystals in inventory
     */
    public static boolean hasCrystals() {
        return InventoryHelper.hasItem(Items.END_CRYSTAL);
    }

    /**
     * Get crystal count in inventory
     */
    public static int getCrystalCount() {
        return InventoryHelper.countItem(Items.END_CRYSTAL);
    }

    /**
     * Check if crystal at position would damage self
     */
    public static boolean wouldDamageSelf(BlockPos pos, float minDamage) {
        if (mc.player == null) return false;
        float damage = calculateCrystalDamage(pos, mc.player);
        return damage >= minDamage;
    }

    /**
     * Calculate damage to all players in range
     */
    public static float getTotalDamageToPlayers(BlockPos pos, double range) {
        List<Player> players = PlayerHelper.getPlayersInRange(range);
        float totalDamage = 0;

        for (Player player : players) {
            totalDamage += calculateCrystalDamage(pos, player);
        }

        return totalDamage;
    }

    /**
     * Get best crystal to break based on damage to target
     */
    public static Entity getBestCrystalToBreak(Entity target, double range) {
        List<Entity> crystals = getCrystalsInRange(range);
        if (crystals.isEmpty()) return null;

        Entity bestCrystal = null;
        float bestDamage = 0;

        for (Entity crystal : crystals) {
            BlockPos pos = crystal.blockPosition();
            float damage = calculateCrystalDamage(pos, target);
            
            if (damage > bestDamage) {
                bestDamage = damage;
                bestCrystal = crystal;
            }
        }

        return bestCrystal;
    }

    /**
     * Check if position is in blast radius of any existing crystal
     */
    public static boolean isInCrystalRange(BlockPos pos, double range) {
        List<Entity> crystals = getCrystalsInRange(range);
        Vec3 posVec = Vec3.atCenterOf(pos);

        for (Entity crystal : crystals) {
            if (crystal.position().distanceTo(posVec) <= 6.0) {
                return true;
            }
        }

        return false;
    }
}
