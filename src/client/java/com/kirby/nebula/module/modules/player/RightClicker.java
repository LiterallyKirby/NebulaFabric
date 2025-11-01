package com.kirby.nebula.module.modules.player;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.settings.BooleanSetting;
import com.kirby.nebula.module.settings.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Random;

public class RightClicker extends Module {
	private final Minecraft mc = Minecraft.getInstance();
	private final Random random = new Random();
	
	private final NumberSetting minCPS;
	private final NumberSetting maxCPS;
	private final BooleanSetting blocksOnly;
	
	private int clickDelay = 0;

	public RightClicker() {
		super("Right Clicker", "Automatically right clicks", Category.PLAYER);
		
		this.minCPS = new NumberSetting(
			"Min CPS",
			"Minimum clicks per second",
			8.0,
			1.0,
			20.0,
			1.0
		);
		addSetting(minCPS);
		
		this.maxCPS = new NumberSetting(
			"Max CPS",
			"Maximum clicks per second",
			12.0,
			1.0,
			20.0,
			1.0
		);
		addSetting(maxCPS);
		
		this.blocksOnly = new BooleanSetting(
			"Blocks Only",
			"Only click when holding blocks",
			true
		);
		addSetting(blocksOnly);
	}

	@Override
	public void onTick() {
		if (mc.player == null || mc.screen != null) return;
		
		// Check if use key is held down
		if (!mc.options.keyUse.isDown()) {
			return;
		}
		
		// Check if holding a block (if enabled)
		if (blocksOnly.getValue()) {
			ItemStack heldItem = mc.player.getMainHandItem();
			if (!(heldItem.getItem() instanceof BlockItem)) {
				// Also check offhand
				ItemStack offhandItem = mc.player.getOffhandItem();
				if (!(offhandItem.getItem() instanceof BlockItem)) {
					return;
				}
			}
		}
		
		// Handle click delay
		if (clickDelay <= 0) {
			performRightClick();
			clickDelay = getRandomDelay();
		} else {
			clickDelay--;
		}
	}
	
	private void performRightClick() {
		HitResult hitResult = mc.hitResult;
		
		if (hitResult != null) {
			switch (hitResult.getType()) {
				case BLOCK:
					BlockHitResult blockHit = (BlockHitResult) hitResult;
					// Use item on block
					mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, blockHit);
					mc.player.swing(InteractionHand.MAIN_HAND);
					break;
					
				case ENTITY:
					// Interact with entity
					mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
					break;
					
				case MISS:
					// Use item in air (e.g., throwing items, eating)
					if (!blocksOnly.getValue()) {
						mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
					}
					break;
			}
		}
	}
	
	private int getRandomDelay() {
		double min = minCPS.getValue();
		double max = maxCPS.getValue();
		
		// Ensure min is not greater than max
		if (min > max) {
			double temp = min;
			min = max;
			max = temp;
		}
		
		double cps = min + (max - min) * random.nextDouble();
		return (int) (20.0 / cps); // Convert CPS to ticks
	}
}
