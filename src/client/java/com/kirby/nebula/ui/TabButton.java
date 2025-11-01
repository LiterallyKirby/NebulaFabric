package com.kirby.nebula.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TabButton {
	private final int relX, relY, width, height;
	private final Component label;
	private final TabType type;

	public TabButton(int relX, int relY, int width, int height, Component label, TabType type) {
		this.relX = relX;
		this.relY = relY;
		this.width = width;
		this.height = height;
		this.label = label;
		this.type = type;
	}

	public void render(GuiGraphics g, int panelX, int animatedY, int mouseX, int mouseY, 
			TabType currentTab, Font defaultFont, CustomFontRenderer customFont) {
		int x = panelX + relX;
		int y = animatedY + relY;
		boolean hovered = isMouseOver(panelX, animatedY, mouseX, mouseY);
		boolean selected = this.type == currentTab;

		int bg = selected ? 0xFF00AAFF : (hovered ? 0xFF2A2A2A : 0xFF1A1A1A);

		UIHelper.drawRoundedRect(g, x, y, width, height, 4, bg);

		String text = label.getString();
		int textColor = selected ? 0xFFFFFFFF : 0xFFAAAAAA;

		if (customFont != null) {
			int textY = y + (height - customFont.getFontHeight()) / 2;
			customFont.drawCenteredString(g, text, x + width / 2, textY, textColor);
		} else {
			g.drawCenteredString(defaultFont, text, x + width / 2, y + (height - 8) / 2, textColor);
		}
	}

	public boolean isMouseOver(int panelX, int animatedY, int mouseX, int mouseY) {
		int x = panelX + relX;
		int y = animatedY + relY;
		return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
	}

	public TabType getType() {
		return type;
	}

	// Getters for subclasses
	protected int getRelX() {
		return relX;
	}

	protected int getRelY() {
		return relY;
	}

	protected int getWidth() {
		return width;
	}

	protected int getHeight() {
		return height;
	}

	protected Component getLabel() {
		return label;
	}
}
