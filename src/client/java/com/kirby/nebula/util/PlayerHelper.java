package com.kirby.nebula.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Helper class for player-related operations
 */
public class PlayerHelper {
    private static final Minecraft mc = Minecraft.getInstance();

    /**
     * Get the local player
     */
    public static LocalPlayer getPlayer() {
        return mc.player;
    }

    /**
     * Check if player exists and is valid
     */
    public static boolean isPlayerValid() {
        return mc.player != null && mc.level != null;
    }

    /**
     * Get player's current position
     */
    public static Vec3 getPlayerPos() {
        if (!isPlayerValid()) return Vec3.ZERO;
        return mc.player.position();
    }

    /**
     * Get player's eye position
     */
    public static Vec3 getEyePos() {
        if (!isPlayerValid()) return Vec3.ZERO;
        return mc.player.getEyePosition();
    }

    /**
     * Get player's current health
     */
    public static float getHealth() {
        if (!isPlayerValid()) return 0;
        return mc.player.getHealth();
    }

    /**
     * Get player's max health
     */
    public static float getMaxHealth() {
        if (!isPlayerValid()) return 0;
        return mc.player.getMaxHealth();
    }

    /**
     * Get player's health percentage (0.0 - 1.0)
     */
    public static float getHealthPercentage() {
        if (!isPlayerValid()) return 0;
        return getHealth() / getMaxHealth();
    }

    /**
     * Check if player is on ground
     */
    public static boolean isOnGround() {
        if (!isPlayerValid()) return false;
        return mc.player.onGround();
    }

    /**
     * Check if player is in water
     */
    public static boolean isInWater() {
        if (!isPlayerValid()) return false;
        return mc.player.isInWater();
    }

    /**
     * Check if player is in lava
     */
    public static boolean isInLava() {
        if (!isPlayerValid()) return false;
        return mc.player.isInLava();
    }

    /**
     * Check if player is moving
     */
    public static boolean isMoving() {
        if (!isPlayerValid()) return false;
        return mc.player.input.hasForwardImpulse() || 
               mc.player.input.left || 
               mc.player.input.right;
    }

    /**
     * Get player's movement speed
     */
    public static double getSpeed() {
        if (!isPlayerValid()) return 0;
        Vec3 motion = mc.player.getDeltaMovement();
        return Math.sqrt(motion.x * motion.x + motion.z * motion.z);
    }

    /**
     * Get all entities within range
     */
    public static List<Entity> getEntitiesInRange(double range) {
        List<Entity> entities = new ArrayList<>();
        if (!isPlayerValid()) return entities;
        
        Vec3 playerPos = getPlayerPos();
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity == mc.player) continue;
            if (entity.distanceToSqr(playerPos) <= range * range) {
                entities.add(entity);
            }
        }
        return entities;
    }

    /**
     * Get all living entities within range
     */
    public static List<LivingEntity> getLivingEntitiesInRange(double range) {
        List<LivingEntity> entities = new ArrayList<>();
        if (!isPlayerValid()) return entities;
        
        Vec3 playerPos = getPlayerPos();
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity == mc.player) continue;
            if (entity instanceof LivingEntity living) {
                if (entity.distanceToSqr(playerPos) <= range * range) {
                    entities.add(living);
                }
            }
        }
        return entities;
    }

    /**
     * Get all players within range
     */
    public static List<Player> getPlayersInRange(double range) {
        List<Player> players = new ArrayList<>();
        if (!isPlayerValid()) return players;
        
        Vec3 playerPos = getPlayerPos();
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity == mc.player) continue;
            if (entity instanceof Player player) {
                if (entity.distanceToSqr(playerPos) <= range * range) {
                    players.add(player);
                }
            }
        }
        return players;
    }

    /**
     * Get the closest entity to player
     */
    public static Entity getClosestEntity(double range) {
        if (!isPlayerValid()) return null;
        
        return getEntitiesInRange(range).stream()
            .min(Comparator.comparingDouble(e -> e.distanceToSqr(getPlayerPos())))
            .orElse(null);
    }

    /**
     * Get the closest living entity to player
     */
    public static LivingEntity getClosestLivingEntity(double range) {
        if (!isPlayerValid()) return null;
        
        return getLivingEntitiesInRange(range).stream()
            .min(Comparator.comparingDouble(e -> e.distanceToSqr(getPlayerPos())))
            .orElse(null);
    }

    /**
     * Get the closest player to player
     */
    public static Player getClosestPlayer(double range) {
        if (!isPlayerValid()) return null;
        
        return getPlayersInRange(range).stream()
            .min(Comparator.comparingDouble(e -> e.distanceToSqr(getPlayerPos())))
            .orElse(null);
    }

    /**
     * Check if entity is a friend (on same team)
     */
    public static boolean isFriend(Entity entity) {
        if (!isPlayerValid() || !(entity instanceof Player)) return false;
        return mc.player.isAlliedTo(entity);
    }

    /**
     * Check if player can see entity
     */
    public static boolean canSeeEntity(Entity entity) {
        if (!isPlayerValid()) return false;
        return mc.player.hasLineOfSight(entity);
    }

    /**
     * Get distance to entity
     */
    public static double getDistanceTo(Entity entity) {
        if (!isPlayerValid()) return Double.MAX_VALUE;
        return mc.player.distanceTo(entity);
    }

    /**
     * Get squared distance to entity (faster)
     */
    public static double getDistanceToSq(Entity entity) {
        if (!isPlayerValid()) return Double.MAX_VALUE;
        return mc.player.distanceToSqr(entity);
    }

    /**
     * Check if player is holding an item of a specific class
     */
    public static boolean isHolding(Class<?> itemClass) {
        if (!isPlayerValid()) return false;
        return itemClass.isInstance(mc.player.getMainHandItem().getItem()) ||
               itemClass.isInstance(mc.player.getOffhandItem().getItem());
    }

    /**
     * Get player's rotation (yaw, pitch)
     */
    public static float[] getRotation() {
        if (!isPlayerValid()) return new float[]{0, 0};
        return new float[]{mc.player.getYRot(), mc.player.getXRot()};
    }

    /**
     * Get player's yaw
     */
    public static float getYaw() {
        if (!isPlayerValid()) return 0;
        return mc.player.getYRot();
    }

    /**
     * Get player's pitch
     */
    public static float getPitch() {
        if (!isPlayerValid()) return 0;
        return mc.player.getXRot();
    }
}
