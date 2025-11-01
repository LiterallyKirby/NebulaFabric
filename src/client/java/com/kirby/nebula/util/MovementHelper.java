package com.kirby.nebula.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * Helper class for movement operations
 */
public class MovementHelper {
    private static final Minecraft mc = Minecraft.getInstance();

    /**
     * Check if player is moving
     */
    public static boolean isMoving() {
        if (mc.player == null) return false;
        return mc.player.input.hasForwardImpulse() || 
               mc.player.input.left || 
               mc.player.input.right;
    }

    /**
     * Get player's horizontal speed
     */
    public static double getSpeed() {
        if (mc.player == null) return 0;
        Vec3 motion = mc.player.getDeltaMovement();
        return Math.sqrt(motion.x * motion.x + motion.z * motion.z);
    }

    /**
     * Get player's total speed (including vertical)
     */
    public static double getTotalSpeed() {
        if (mc.player == null) return 0;
        Vec3 motion = mc.player.getDeltaMovement();
        return motion.length();
    }

    /**
     * Set player's horizontal speed
     */
    public static void setSpeed(double speed) {
        if (mc.player == null || !isMoving()) return;
        
        double yaw = getDirection();
        mc.player.setDeltaMovement(
            -Math.sin(yaw) * speed,
            mc.player.getDeltaMovement().y,
            Math.cos(yaw) * speed
        );
    }

    /**
     * Get movement direction in radians
     */
    public static double getDirection() {
        if (mc.player == null) return 0;
        
        float yaw = mc.player.getYRot();
        
        if (mc.player.input.left) {
            yaw += 90;
            if (mc.player.input.hasForwardImpulse()) {
                yaw -= 45;
            }
        } else if (mc.player.input.right) {
            yaw -= 90;
            if (mc.player.input.hasForwardImpulse()) {
                yaw += 45;
            }
        }
        
        if (mc.player.input.hasForwardImpulse()) {
            // Forward is already handled by yaw
        }
        
        return Math.toRadians(yaw);
    }

    /**
     * Strafe towards a position
     */
    public static void strafeTowards(Vec3 target, double speed) {
        if (mc.player == null) return;
        
        Vec3 playerPos = mc.player.position();
        double diffX = target.x - playerPos.x;
        double diffZ = target.z - playerPos.z;
        
        double yaw = Math.atan2(diffZ, diffX) - Math.PI / 2;
        
        mc.player.setDeltaMovement(
            -Math.sin(yaw) * speed,
            mc.player.getDeltaMovement().y,
            Math.cos(yaw) * speed
        );
    }

    /**
     * Strafe away from a position
     */
    public static void strafeAway(Vec3 target, double speed) {
        if (mc.player == null) return;
        
        Vec3 playerPos = mc.player.position();
        double diffX = playerPos.x - target.x;
        double diffZ = playerPos.z - target.z;
        
        double yaw = Math.atan2(diffZ, diffX) - Math.PI / 2;
        
        mc.player.setDeltaMovement(
            -Math.sin(yaw) * speed,
            mc.player.getDeltaMovement().y,
            Math.cos(yaw) * speed
        );
    }

    /**
     * Stop all movement
     */
    public static void stop() {
        if (mc.player == null) return;
        mc.player.setDeltaMovement(0, mc.player.getDeltaMovement().y, 0);
    }

    /**
     * Check if player is on ground
     */
    public static boolean isOnGround() {
        if (mc.player == null) return false;
        return mc.player.onGround();
    }

    /**
     * Check if player is in air
     */
    public static boolean isInAir() {
        return !isOnGround();
    }

    /**
     * Check if player is falling
     */
    public static boolean isFalling() {
        if (mc.player == null) return false;
        return mc.player.getDeltaMovement().y < -0.1;
    }

    /**
     * Check if player is ascending
     */
    public static boolean isAscending() {
        if (mc.player == null) return false;
        return mc.player.getDeltaMovement().y > 0.1;
    }

    /**
     * Get distance to ground
     */
    public static double getDistanceToGround() {
        if (mc.player == null || mc.level == null) return 0;
        
        BlockPos playerPos = mc.player.blockPosition();
        for (int i = 0; i < 256; i++) {
            BlockPos checkPos = playerPos.below(i);
            if (!BlockHelper.isAir(checkPos)) {
                return i;
            }
        }
        return 256;
    }

    /**
     * Check if player is moving forward
     */
    public static boolean isMovingForward() {
        if (mc.player == null) return false;
        return mc.player.input.hasForwardImpulse();
    }

    /**
     * Check if player is moving backward
     */
    public static boolean isMovingBackward() {
        if (mc.player == null) return false;
        return mc.player.input.hasBackwardImpulse();
    }

    /**
     * Check if player is strafing left
     */
    public static boolean isStrafingLeft() {
        if (mc.player == null) return false;
        return mc.player.input.left;
    }

    /**
     * Check if player is strafing right
     */
    public static boolean isStrafingRight() {
        if (mc.player == null) return false;
        return mc.player.input.right;
    }

    /**
     * Get vertical velocity
     */
    public static double getVerticalSpeed() {
        if (mc.player == null) return 0;
        return mc.player.getDeltaMovement().y;
    }

    /**
     * Set vertical velocity
     */
    public static void setVerticalSpeed(double speed) {
        if (mc.player == null) return;
        Vec3 motion = mc.player.getDeltaMovement();
        mc.player.setDeltaMovement(motion.x, speed, motion.z);
    }

    /**
     * Jump
     */
    public static void jump() {
        if (mc.player == null || !isOnGround()) return;
        mc.player.jumpFromGround();
    }

    /**
     * Calculate distance to position
     */
    public static double getDistanceTo(Vec3 pos) {
        if (mc.player == null) return Double.MAX_VALUE;
        return mc.player.position().distanceTo(pos);
    }

    /**
     * Calculate distance to block position
     */
    public static double getDistanceTo(BlockPos pos) {
        return getDistanceTo(Vec3.atCenterOf(pos));
    }

    /**
     * Get blocks per second
     */
    public static double getBlocksPerSecond() {
        return getSpeed() * 20.0; // 20 ticks per second
    }

    /**
     * Check if player can step up (1 block)
     */
    public static boolean canStepUp() {
        if (mc.player == null) return false;
        BlockPos pos = mc.player.blockPosition().above();
        return BlockHelper.isAir(pos);
    }

    /**
     * Predict player position after ticks
     */
    public static Vec3 predictPosition(int ticks) {
        if (mc.player == null) return Vec3.ZERO;
        
        Vec3 currentPos = mc.player.position();
        Vec3 motion = mc.player.getDeltaMovement();
        
        return currentPos.add(motion.scale(ticks));
    }
}
