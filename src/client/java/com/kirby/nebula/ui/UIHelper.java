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

	/**
	 * Draw a vertical gradient rectangle
	 */
	public static void drawGradientRect(GuiGraphics graphics, int x, int y, int width, int height, int colorTop, int colorBottom) {
		for (int i = 0; i < height; i++) {
			float progress = (float) i / height;
			int color = blendColors(colorTop, colorBottom, progress);
			graphics.fill(x, y + i, x + width, y + i + 1, color);
		}
	}

	/**
	 * Blend two ARGB colors
	 */
	private static int blendColors(int color1, int color2, float ratio) {
		int a1 = (color1 >> 24) & 0xFF;
		int r1 = (color1 >> 16) & 0xFF;
		int g1 = (color1 >> 8) & 0xFF;
		int b1 = color1 & 0xFF;

		int a2 = (color2 >> 24) & 0xFF;
		int r2 = (color2 >> 16) & 0xFF;
		int g2 = (color2 >> 8) & 0xFF;
		int b2 = color2 & 0xFF;

		int a = (int) (a1 + (a2 - a1) * ratio);
		int r = (int) (r1 + (r2 - r1) * ratio);
		int g = (int) (g1 + (g2 - g1) * ratio);
		int b = (int) (b1 + (b2 - b1) * ratio);

		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	/**
	 * Draw a shadow effect around a rectangle
	 */
	public static void drawShadow(GuiGraphics graphics, int x, int y, int width, int height, int shadowSize, int shadowColor) {
		for (int i = 0; i < shadowSize; i++) {
			int alpha = (shadowColor >> 24) & 0xFF;
			int adjustedAlpha = (int) (alpha * (1.0f - (float) i / shadowSize));
			int color = (adjustedAlpha << 24) | (shadowColor & 0x00FFFFFF);
			
			graphics.fill(x - i, y - i, x + width + i, y - i + 1, color);
			graphics.fill(x - i, y + height + i - 1, x + width + i, y + height + i, color);
			graphics.fill(x - i, y - i, x - i + 1, y + height + i, color);
			graphics.fill(x + width + i - 1, y - i, x + width + i, y + height + i, color);
		}
	}
}
