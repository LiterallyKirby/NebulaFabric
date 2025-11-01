package com.kirby.nebula.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for inventory operations
 */
public class InventoryHelper {
    private static final Minecraft mc = Minecraft.getInstance();

    /**
     * Get player's inventory
     */
    public static Inventory getInventory() {
        if (mc.player == null) return null;
        return mc.player.getInventory();
    }

    /**
     * Get item in main hand
     */
    public static ItemStack getMainHandItem() {
        if (mc.player == null) return ItemStack.EMPTY;
        return mc.player.getMainHandItem();
    }

    /**
     * Get item in offhand
     */
    public static ItemStack getOffhandItem() {
        if (mc.player == null) return ItemStack.EMPTY;
        return mc.player.getOffhandItem();
    }

    /**
     * Check if player is holding item
     */
    public static boolean isHolding(Item item) {
        ItemStack mainHand = getMainHandItem();
        ItemStack offhand = getOffhandItem();
        return mainHand.getItem() == item || offhand.getItem() == item;
    }

    /**
     * Check if player is holding item of specific class
     */
    public static boolean isHoldingType(Class<?> itemClass) {
        ItemStack mainHand = getMainHandItem();
        ItemStack offhand = getOffhandItem();
        return itemClass.isInstance(mainHand.getItem()) || itemClass.isInstance(offhand.getItem());
    }

    /**
     * Find item in inventory (returns slot index, -1 if not found)
     */
    public static int findItem(Item item) {
        Inventory inv = getInventory();
        if (inv == null) return -1;
        
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find item in hotbar (returns hotbar slot 0-8, -1 if not found)
     */
    public static int findItemInHotbar(Item item) {
        Inventory inv = getInventory();
        if (inv == null) return -1;
        
        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find all slots containing item
     */
    public static List<Integer> findAllItems(Item item) {
        List<Integer> slots = new ArrayList<>();
        Inventory inv = getInventory();
        if (inv == null) return slots;
        
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).getItem() == item) {
                slots.add(i);
            }
        }
        return slots;
    }

    /**
     * Count items in inventory
     */
    public static int countItem(Item item) {
        Inventory inv = getInventory();
        if (inv == null) return 0;
        
        int count = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }

    /**
     * Get item in specific slot
     */
    public static ItemStack getItemInSlot(int slot) {
        Inventory inv = getInventory();
        if (inv == null || slot < 0 || slot >= inv.getContainerSize()) {
            return ItemStack.EMPTY;
        }
        return inv.getItem(slot);
    }

    /**
     * Check if inventory has item
     */
    public static boolean hasItem(Item item) {
        return findItem(item) != -1;
    }

    /**
     * Get current selected hotbar slot
     */
    public static int getSelectedSlot() {
        if (mc.player == null) return -1;
        return mc.player.getInventory().selected;
    }

    /**
     * Switch to hotbar slot
     */
    public static void switchToSlot(int slot) {
        if (mc.player == null || slot < 0 || slot > 8) return;
        mc.player.getInventory().selected = slot;
    }

    /**
     * Switch to item in hotbar (returns true if switched)
     */
    public static boolean switchToItem(Item item) {
        int slot = findItemInHotbar(item);
        if (slot == -1) return false;
        switchToSlot(slot);
        return true;
    }

    /**
     * Get empty slots count
     */
    public static int getEmptySlots() {
        Inventory inv = getInventory();
        if (inv == null) return 0;
        
        int empty = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).isEmpty()) {
                empty++;
            }
        }
        return empty;
    }

    /**
     * Check if inventory is full
     */
    public static boolean isInventoryFull() {
        return getEmptySlots() == 0;
    }

    /**
     * Get hotbar items
     */
    public static List<ItemStack> getHotbarItems() {
        List<ItemStack> items = new ArrayList<>();
        Inventory inv = getInventory();
        if (inv == null) return items;
        
        for (int i = 0; i < 9; i++) {
            items.add(inv.getItem(i));
        }
        return items;
    }

    /**
     * Check if slot is in hotbar
     */
    public static boolean isHotbarSlot(int slot) {
        return slot >= 0 && slot < 9;
    }

    /**
     * Get armor in specific slot (0=feet, 1=legs, 2=chest, 3=head)
     */
    public static ItemStack getArmor(int slot) {
        if (mc.player == null || slot < 0 || slot > 3) return ItemStack.EMPTY;
        return mc.player.getInventory().getArmor(slot);
    }

    /**
     * Check if wearing full armor
     */
    public static boolean hasFullArmor() {
        for (int i = 0; i < 4; i++) {
            if (getArmor(i).isEmpty()) return false;
        }
        return true;
    }

    /**
     * Get total armor durability percentage
     */
    public static float getArmorDurability() {
        if (!hasFullArmor()) return 0;
        
        float total = 0;
        for (int i = 0; i < 4; i++) {
            ItemStack armor = getArmor(i);
            if (!armor.isEmpty() && armor.isDamageableItem()) {
                total += (float) (armor.getMaxDamage() - armor.getDamageValue()) / armor.getMaxDamage();
            }
        }
        return total / 4.0f;
    }
}
