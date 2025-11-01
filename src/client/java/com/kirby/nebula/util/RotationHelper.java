package com.kirby.nebula.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * Helper class for rotation calculations
 */
public class RotationHelper {
    private static final Minecraft mc = Minecraft.getInstance();

    /**
     * Calculate rotation to look at a position
     */
    public static float[] getRotationTo(Vec3 target) {
        if (mc.player == null) return new float[]{0, 0};
        
        Vec3 eyePos = mc.player.getEyePosition();
        Vec3 diff = target.subtract(eyePos);
        
        double distance = Math.sqrt(diff.x * diff.x + diff.z * diff.z);
        
        float yaw = (float) Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(diff.y, distance));
        
        return new float[]{yaw, pitch};
    }

    /**
     * Calculate rotation to look at an entity
     */
    public static float[] getRotationToEntity(Entity entity) {
        return getRotationTo(entity.getEyePosition());
    }

    /**
     * Calculate rotation to look at a block position
     */
    public static float[] getRotationToBlock(BlockPos pos) {
        return getRotationTo(Vec3.atCenterOf(pos));
    }

    /**
     * Get the angle difference between two rotations
     */
    public static float getAngleDifference(float angle1, float angle2) {
        float diff = Math.abs(angle1 - angle2) % 360.0f;
        if (diff > 180.0f) {
            diff = 360.0f - diff;
        }
        return diff;
    }

    /**
     * Get rotation difference between player and target
     */
    public static float[] getRotationDifference(float[] target) {
        if (mc.player == null) return new float[]{0, 0};
        
        float yawDiff = getAngleDifference(mc.player.getYRot(), target[0]);
        float pitchDiff = getAngleDifference(mc.player.getXRot(), target[1]);
        
        return new float[]{yawDiff, pitchDiff};
    }

    /**
     * Smoothly rotate towards target rotation
     */
    public static float[] smoothRotation(float[] current, float[] target, float smoothness) {
        float yawDiff = wrapAngleTo180(target[0] - current[0]);
        float pitchDiff = wrapAngleTo180(target[1] - current[1]);
        
        float newYaw = current[0] + yawDiff * smoothness;
        float newPitch = current[1] + pitchDiff * smoothness;
        
        newPitch = Math.max(-90.0f, Math.min(90.0f, newPitch));
        
        return new float[]{newYaw, newPitch};
    }

    /**
     * Wrap angle to -180 to 180 range
     */
    public static float wrapAngleTo180(float angle) {
        angle = angle % 360.0f;
        if (angle >= 180.0f) {
            angle -= 360.0f;
        }
        if (angle < -180.0f) {
            angle += 360.0f;
        }
        return angle;
    }

    /**
     * Check if player is looking at entity
     */
    public static boolean isLookingAt(Entity entity, float maxAngle) {
        if (mc.player == null) return false;
        
        float[] targetRot = getRotationToEntity(entity);
        float[] diff = getRotationDifference(targetRot);
        
        return diff[0] <= maxAngle && diff[1] <= maxAngle;
    }

    /**
     * Check if player is looking at position
     */
    public static boolean isLookingAt(Vec3 pos, float maxAngle) {
        if (mc.player == null) return false;
        
        float[] targetRot = getRotationTo(pos);
        float[] diff = getRotationDifference(targetRot);
        
        return diff[0] <= maxAngle && diff[1] <= maxAngle;
    }

    /**
     * Get the vector from rotation angles
     */
    public static Vec3 getVectorFromRotation(float yaw, float pitch) {
        float f = pitch * 0.017453292f;
        float f1 = -yaw * 0.017453292f;
        float f2 = (float) Math.cos(f1);
        float f3 = (float) Math.sin(f1);
        float f4 = (float) Math.cos(f);
        float f5 = (float) Math.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }

    /**
     * Get look vector for current rotation
     */
    public static Vec3 getLookVector() {
        if (mc.player == null) return Vec3.ZERO;
        return getVectorFromRotation(mc.player.getYRot(), mc.player.getXRot());
    }

    /**
     * Rotate player to look at position
     */
    public static void rotateToPosition(Vec3 pos) {
        if (mc.player == null) return;
        float[] rot = getRotationTo(pos);
        mc.player.setYRot(rot[0]);
        mc.player.setXRot(rot[1]);
    }

    /**
     * Rotate player to look at entity
     */
    public static void rotateToEntity(Entity entity) {
        rotateToPosition(entity.getEyePosition());
    }
}
