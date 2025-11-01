package com.kirby.nebula.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ModButton {
	private final int relX, relY, width, height;
	private final Component label;
	private final Runnable onPress;
	private boolean enabled;

	public ModButton(int relX, int relY, int width, int height, Component label, boolean enabled, Runnable onPress) {
		this.relX = relX;
		this.relY = relY;
		this.width = width;
		this.height = height;
		this.label = label;
		this.enabled = enabled;
		this.onPress = onPress;
	}

	public void render(GuiGraphics g, int panelX, int animatedY, int mouseX, int mouseY,
			Font defaultFont, CustomFontRenderer customFont) {
		int x = panelX + relX;
		int y = animatedY + relY;
		boolean hovered = isMouseOver(panelX, animatedY, mouseX, mouseY);

		int bg = enabled ? 0xFF1A6AA8 : (hovered ? 0xFF2A2A2A : 0xFF222222);

		UIHelper.drawRoundedRect(g, x, y, width, height, 4, bg);

		int borderColor = enabled ? 0xFF00AAFF : (hovered ? 0xFF555555 : 0xFF333333);
		UIHelper.drawRoundedRect(g, x, y, width, 1, 0, borderColor);
		UIHelper.drawRoundedRect(g, x, y + height - 1, width, 1, 0, borderColor);

		String text = label.getString();
		int textColor = enabled ? 0xFFFFFFFF : 0xFFCCCCCC;

		if (customFont != null) {
			int textY = y + (height - customFont.getFontHeight()) / 2;
			customFont.drawString(g, text, x + 10, textY, textColor);
		} else {
			g.drawString(defaultFont, text, x + 10, y + (height - 8) / 2, textColor);
		}

		if (enabled) {
			g.fill(x + width - 20, y + height / 2 - 3, x + width - 10, y + height / 2 + 3, 0xFF00FF00);
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
