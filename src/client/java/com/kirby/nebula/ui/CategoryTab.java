package com.kirby.nebula.ui;

import com.kirby.nebula.module.Category;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class CategoryTab {
	private final int relX, relY, width, height;
	private final Component label;
	private final Category category;

	// Purple theme colors
	private static final int BG_SELECTED = 0xFF6B46C1;
	private static final int BG_HOVER = 0xFF4C2F91;
	private static final int BG_DEFAULT = 0xFF2D1B4E;
	private static final int TEXT_SELECTED = 0xFFFFFFFF;
	private static final int TEXT_DEFAULT = 0xFFC4B5FD;
	private static final int ACCENT = 0xFFA78BFA;

	public CategoryTab(int relX, int relY, int width, int height, Component label, Category category) {
		this.relX = relX;
		this.relY = relY;
		this.width = width;
		this.height = height;
		this.label = label;
		this.category = category;
	}

	public void render(GuiGraphics g, int panelX, int animatedY, int mouseX, int mouseY, 
			Category currentCategory, Font defaultFont, CustomFontRenderer customFont) {
		int x = panelX + relX;
		int y = animatedY + relY;
		boolean hovered = isMouseOver(panelX, animatedY, mouseX, mouseY);
		boolean selected = this.category == currentCategory;

		// Background with smooth transitions
		int bg = selected ? BG_SELECTED : (hovered ? BG_HOVER : BG_DEFAULT);
		UIHelper.drawRoundedRect(g, x, y, width, height, 6, bg);

		// Selection indicator (left border)
		if (selected) {
			g.fill(x, y + 8, x + 3, y + height - 8, ACCENT);
		}

		// Text rendering
		String text = label.getString().toUpperCase();
		int textColor = selected ? TEXT_SELECTED : TEXT_DEFAULT;

		if (customFont != null) {
			int textY = y + (height - customFont.getFontHeight()) / 2;
			customFont.drawCenteredString(g, text, x + width / 2, textY, textColor);
		} else {
			String styledText = selected ? "§l" + text : text;
			g.drawCenteredString(defaultFont, styledText, x + width / 2, y + (height - 8) / 2, textColor);
		}

		// Hover glow effect
		if (hovered && !selected) {
			UIHelper.drawRoundedRect(g, x, y, width, 1, 0, 0x60A78BFA);
			UIHelper.drawRoundedRect(g, x, y + height - 1, width, 1, 0, 0x60A78BFA);
		}
	}

	public boolean isMouseOver(int panelX, int animatedY, int mouseX, int mouseY) {
		int x = panelX + relX;
		int y = animatedY + relY;
		return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
	}

	public Category getCategory() {
		return category;
	}
}
