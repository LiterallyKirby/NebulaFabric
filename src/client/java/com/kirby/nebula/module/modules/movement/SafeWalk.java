package com.kirby.nebula.module.modules.movement;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.settings.BooleanSetting;
import com.kirby.nebula.module.settings.ModeSetting;
import com.kirby.nebula.util.BlockHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

/**
 * Ghost module that prevents walking off edges
 */
public class SafeWalk extends Module {
	private final Minecraft mc = Minecraft.getInstance();
	
	private final ModeSetting mode;
	private final BooleanSetting sneakOnly;
	private final BooleanSetting voidOnly;
	private final BooleanSetting edgesOnly;
	
	public SafeWalk() {
		super("Safe Walk", "Prevents falling off edges", Category.MOVEMENT);
		
		this.mode = new ModeSetting(
			"Mode",
			"Safe walk mode",
			"Edge",
			"Edge", "Void", "Both"
		);
		addSetting(mode);
		
		this.sneakOnly = new BooleanSetting(
			"Sneak Only",
			"Only prevent when sneaking",
			true
		);
		addSetting(sneakOnly);
		
		this.voidOnly = new BooleanSetting(
			"Void Only",
			"Only prevent void falls",
			false
		);
		addSetting(voidOnly);
		
		this.edgesOnly = new BooleanSetting(
			"Edges Only",
			"Only prevent at block edges",
			true
		);
		addSetting(edgesOnly);
	}
	
	@Override
	public void onTick() {
		if (mc.player == null) return;
		
		// Check sneak requirement
		if (sneakOnly.getValue() && !mc.player.isShiftKeyDown()) {
			return;
		}
		
		// Check if player is at edge
		if (!isAtEdge()) return;
		
		// Prevent movement off edge
		// This would require movement input interception via mixin
	}
	
	private boolean isAtEdge() {
		if (mc.player == null) return false;
		
		BlockPos playerPos = mc.player.blockPosition();
		BlockPos below = playerPos.below();
		
		// Check if block below is air
		if (!BlockHelper.isAir(below)) {
			return false;
		}
		
		String currentMode = mode.getValue();
		
		// Check void
		if (currentMode.equals("Void") || currentMode.equals("Both")) {
			if (voidOnly.getValue()) {
				// Check if there's void below
				for (int i = 1; i < 64; i++) {
					BlockPos checkPos = below.below(i);
					if (!BlockHelper.isAir(checkPos)) {
						return false; // Not void
					}
					if (checkPos.getY() < -64) {
						return true; // Void detected
					}
				}
			}
		}
		
		// Check edge
		if (currentMode.equals("Edge") || currentMode.equals("Both")) {
			return true;
		}
		
		return false;
	}
	
	public boolean shouldPreventMovement() {
		if (!isEnabled()) return false;
		
		if (sneakOnly.getValue() && mc.player != null && !mc.player.isShiftKeyDown()) {
			return false;
		}
		
		return isAtEdge();
	}
}
