package com.kirby.nebula.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for block-related operations
 */
public class BlockHelper {
    private static final Minecraft mc = Minecraft.getInstance();

    /**
     * Get block at position
     */
    public static Block getBlock(BlockPos pos) {
        if (mc.level == null) return Blocks.AIR;
        return mc.level.getBlockState(pos).getBlock();
    }

    /**
     * Get block state at position
     */
    public static BlockState getBlockState(BlockPos pos) {
        if (mc.level == null) return Blocks.AIR.defaultBlockState();
        return mc.level.getBlockState(pos);
    }

    /**
     * Check if block at position is air
     */
    public static boolean isAir(BlockPos pos) {
        return getBlock(pos) == Blocks.AIR;
    }

    /**
     * Check if block at position is replaceable
     */
    public static boolean isReplaceable(BlockPos pos) {
        if (mc.level == null) return false;
        return mc.level.getBlockState(pos).canBeReplaced();
    }

    /**
     * Check if block at position is solid
     */
    public static boolean isSolid(BlockPos pos) {
        if (mc.level == null) return false;
        return mc.level.getBlockState(pos).isSolid();
    }

    /**
     * Check if block is specific type
     */
    public static boolean isBlock(BlockPos pos, Block block) {
        return getBlock(pos) == block;
    }

    /**
     * Get all blocks in a sphere around position
     */
    public static List<BlockPos> getBlocksInSphere(BlockPos center, double radius) {
        List<BlockPos> blocks = new ArrayList<>();
        int radiusCeil = (int) Math.ceil(radius);
        
        for (int x = -radiusCeil; x <= radiusCeil; x++) {
            for (int y = -radiusCeil; y <= radiusCeil; y++) {
                for (int z = -radiusCeil; z <= radiusCeil; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    if (center.distSqr(pos) <= radius * radius) {
                        blocks.add(pos);
                    }
                }
            }
        }
        
        return blocks;
    }

    /**
     * Get all blocks in a box
     */
    public static List<BlockPos> getBlocksInBox(BlockPos pos1, BlockPos pos2) {
        List<BlockPos> blocks = new ArrayList<>();
        
        int minX = Math.min(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        
        return blocks;
    }

    /**
     * Get blocks of specific type in range
     */
    public static List<BlockPos> findBlocks(Block block, BlockPos center, double radius) {
        List<BlockPos> blocks = new ArrayList<>();
        
        for (BlockPos pos : getBlocksInSphere(center, radius)) {
            if (isBlock(pos, block)) {
                blocks.add(pos);
            }
        }
        
        return blocks;
    }

    /**
     * Get the closest block of specific type
     */
    public static BlockPos findClosestBlock(Block block, Vec3 center, double radius) {
        BlockPos centerPos = BlockPos.containing(center);
        BlockPos closest = null;
        double closestDist = Double.MAX_VALUE;
        
        for (BlockPos pos : getBlocksInSphere(centerPos, radius)) {
            if (isBlock(pos, block)) {
                double dist = Math.sqrt(centerPos.distSqr(pos));
                if (dist < closestDist) {
                    closest = pos;
                    closestDist = dist;
                }
            }
        }
        
        return closest;
    }

    /**
     * Check if there's a clear line of sight between two positions
     */
    public static boolean hasLineOfSight(Vec3 from, Vec3 to) {
        if (mc.level == null) return false;
        return mc.level.clip(new net.minecraft.world.level.ClipContext(
            from, to,
            net.minecraft.world.level.ClipContext.Block.COLLIDER,
            net.minecraft.world.level.ClipContext.Fluid.NONE,
            mc.player
        )).getType() == net.minecraft.world.phys.HitResult.Type.MISS;
    }

    /**
     * Get all surrounding blocks (6 directions)
     */
    public static List<BlockPos> getSurroundingBlocks(BlockPos pos) {
        List<BlockPos> blocks = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            blocks.add(pos.relative(dir));
        }
        return blocks;
    }

    /**
     * Check if position is valid (within world bounds)
     */
    public static boolean isValidPos(BlockPos pos) {
        if (mc.level == null) return false;
        return mc.level.isInWorldBounds(pos);
    }

    /**
     * Get block bounding box
     */
    public static AABB getBlockBox(BlockPos pos) {
        if (mc.level == null) return new AABB(pos);
        VoxelShape shape = mc.level.getBlockState(pos).getShape(mc.level, pos);
        return shape.isEmpty() ? new AABB(pos) : shape.bounds().move(pos);
    }

    /**
     * Check if player can reach block
     */
    public static boolean canReach(BlockPos pos, double reachDistance) {
        if (mc.player == null) return false;
        Vec3 playerPos = mc.player.getEyePosition();
        Vec3 blockCenter = Vec3.atCenterOf(pos);
        return playerPos.distanceTo(blockCenter) <= reachDistance;
    }

    /**
     * Get the face to click on a block
     */
    public static Direction getFacing(BlockPos pos) {
        if (mc.player == null) return Direction.UP;
        Vec3 playerPos = mc.player.getEyePosition();
        Vec3 blockCenter = Vec3.atCenterOf(pos);
        Vec3 diff = blockCenter.subtract(playerPos);
        
        Direction facing = Direction.getNearest(
            (float) diff.x,
            (float) diff.y,
            (float) diff.z
        );
        
        return facing.getOpposite();
    }

    /**
     * Check if block can be placed at position
     */
    public static boolean canPlace(BlockPos pos) {
        if (mc.level == null) return false;
        
        // Check if position is replaceable
        if (!isReplaceable(pos)) return false;
        
        // Check if there's a solid block to place against
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.relative(dir);
            if (isSolid(neighbor)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Get distance between player and block
     */
    public static double getDistanceToBlock(BlockPos pos) {
        if (mc.player == null) return Double.MAX_VALUE;
        return mc.player.position().distanceTo(Vec3.atCenterOf(pos));
    }
}
