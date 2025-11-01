package com.kirby.nebula.ui;

import net.minecraft.client.gui.GuiGraphics;

public class UIHelper {
	
	public static void drawRoundedRect(GuiGraphics graphics, int x, int y, int width, int height, int radius, int color) {
		graphics.fill(x + radius, y, x + width - radius, y + height, color);
		graphics.fill(x, y + radius, x + radius, y + height - radius, color);
		graphics.fill(x + width - radius, y + radius, x + width, y + height - radius, color);

		drawCorner(graphics, x, y, radius, color, true, true);
		drawCorner(graphics, x + width - radius, y, radius, color, false, true);
		drawCorner(graphics, x, y + height - radius, radius, color, true, false);
		drawCorner(graphics, x + width - radius, y + height - radius, radius, color, false, false);
	}

	private static void drawCorner(GuiGraphics graphics, int cx, int cy, int radius, int color, boolean left, boolean top) {
		for (int i = 0; i < radius; i++) {
			for (int j = 0; j < radius; j++) {
				double dist = Math.sqrt(i * i + j * j);
				if (dist <= radius) {
					int px = left ? cx + (radius - i - 1) : cx + i;
					int py = top ? cy + (radius - j - 1) : cy + j;
					graphics.fill(px, py, px + 1, py + 1, color);
				}
			}
		}
	}
}
