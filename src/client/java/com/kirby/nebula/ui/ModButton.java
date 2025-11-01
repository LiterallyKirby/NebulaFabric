package com.kirby.nebula.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ModButton {
	private final int relX, relY, width, height;
	private final Component label;
	private final String description;
	private final Runnable onPress;
	private boolean enabled;

	// Purple theme colors
	private static final int BG_ENABLED = 0xFF6B46C1;
	private static final int BG_HOVER = 0xFF3D2862;
	private static final int BG_DEFAULT = 0xFF2A1B3D;
	private static final int BORDER_ENABLED = 0xFFA78BFA;
	private static final int BORDER_HOVER = 0xFF6B46C1;
	private static final int BORDER_DEFAULT = 0xFF4C2F91;
	private static final int TEXT_ENABLED = 0xFFFFFFFF;
	private static final int TEXT_DEFAULT = 0xFFDDD6FE;
	private static final int TEXT_DESC = 0xFF9F7AEA;
	private static final int INDICATOR_ON = 0xFF10B981;

	public ModButton(int relX, int relY, int width, int height, Component label, 
			String description, boolean enabled, Runnable onPress) {
		this.relX = relX;
		this.relY = relY;
		this.width = width;
		this.height = height;
		this.label = label;
		this.description = description;
		this.enabled = enabled;
		this.onPress = onPress;
	}

	public void render(GuiGraphics g, int panelX, int animatedY, int mouseX, int mouseY,
			Font defaultFont, CustomFontRenderer customFont) {
		int x = panelX + relX;
		int y = animatedY + relY;
		boolean hovered = isMouseOver(panelX, animatedY, mouseX, mouseY);

		// Background
		int bg = enabled ? BG_ENABLED : (hovered ? BG_HOVER : BG_DEFAULT);
		UIHelper.drawRoundedRect(g, x, y, width, height, 6, bg);

		// Border
		int borderColor = enabled ? BORDER_ENABLED : (hovered ? BORDER_HOVER : BORDER_DEFAULT);
		UIHelper.drawRoundedRect(g, x, y, width, 1, 0, borderColor);
		UIHelper.drawRoundedRect(g, x, y + height - 1, width, 1, 0, borderColor);

		// Glow effect when enabled
		if (enabled) {
			UIHelper.drawGradientRect(g, x, y + 1, width, 3, 0x40A78BFA, 0x00A78BFA);
		}

		// Text rendering
		String text = label.getString();
		int textColor = enabled ? TEXT_ENABLED : TEXT_DEFAULT;

		if (customFont != null) {
			int textY = y + 8;
			customFont.drawString(g, text, x + 12, textY, textColor);
			
			// Description text (smaller)
			if (description != null && !description.isEmpty()) {
				// Use default font for description since we need smaller text
				g.drawString(defaultFont, description, x + 12, y + height - 14, TEXT_DESC);
			}
		} else {
			String styledText = enabled ? "§l" + text : text;
			g.drawString(defaultFont, styledText, x + 12, y + 8, textColor);
			
			if (description != null && !description.isEmpty()) {
				g.drawString(defaultFont, description, x + 12, y + height - 14, TEXT_DESC);
			}
		}

		// Status indicator
		if (enabled) {
			int indicatorX = x + width - 25;
			int indicatorY = y + height / 2 - 4;
			UIHelper.drawRoundedRect(g, indicatorX, indicatorY, 16, 8, 4, INDICATOR_ON);
			g.fill(indicatorX + 9, indicatorY + 1, indicatorX + 15, indicatorY + 7, 0xFFFFFFFF);
		} else {
			int indicatorX = x + width - 25;
			int indicatorY = y + height / 2 - 4;
			UIHelper.drawRoundedRect(g, indicatorX, indicatorY, 16, 8, 4, 0xFF4B5563);
			g.fill(indicatorX + 1, indicatorY + 1, indicatorX + 7, indicatorY + 7, 0xFF9CA3AF);
		}

		// Hover effect
		if (hovered && !enabled) {
			UIHelper.drawRoundedRect(g, x + 2, y + 2, width - 4, height - 4, 4, 0x20A78BFA);
		}
	}

	public boolean isMouseOver(int panelX, int animatedY, int mouseX, int mouseY) {
		int x = panelX + relX;
		int y = animatedY + relY;
		return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
	}

	public void toggle() {
		enabled = !enabled;
		onPress.run();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
