package com.kirby.nebula.ui;

import com.kirby.nebula.module.settings.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class SettingWidget {
	private final int relX, relY, width, height;
	private final Setting<?> setting;
	private boolean dragging = false;

	// Colors
	private static final int BG_DEFAULT = 0xFF2A1B3D;
	private static final int BG_HOVER = 0xFF3D2862;
	private static final int ACCENT_PRIMARY = 0xFF6B46C1;
	private static final int ACCENT_SECONDARY = 0xFF8B5CF6;
	private static final int TEXT_PRIMARY = 0xFFFFFFFF;
	private static final int TEXT_SECONDARY = 0xFFC4B5FD;
	private static final int SLIDER_BG = 0xFF1A1A2E;
	private static final int SLIDER_FILL = 0xFF8B5CF6;

	public SettingWidget(int relX, int relY, int width, int height, Setting<?> setting) {
		this.relX = relX;
		this.relY = relY;
		this.width = width;
		this.height = height;
		this.setting = setting;
	}

	public void render(GuiGraphics g, int panelX, int animatedY, int mouseX, int mouseY,
			Font defaultFont, CustomFontRenderer customFont) {
		int x = panelX + relX;
		int y = animatedY + relY;
		boolean hovered = isMouseOver(panelX, animatedY, mouseX, mouseY);

		// Background
		int bg = hovered ? BG_HOVER : BG_DEFAULT;
		UIHelper.drawRoundedRect(g, x, y, width, height, 6, bg);

		// Setting name
		String name = setting.getName();
		if (customFont != null) {
			customFont.drawString(g, name, x + 12, y + 8, TEXT_PRIMARY);
		} else {
			g.drawString(defaultFont, name, x + 12, y + 8, TEXT_PRIMARY);
		}

		// Setting description (smaller text)
		g.drawString(defaultFont, setting.getDescription(), x + 12, y + 22, TEXT_SECONDARY);

		// Render control based on type
		switch (setting.getType()) {
			case BOOLEAN:
				renderBooleanControl(g, x, y, (BooleanSetting) setting);
				break;
			case NUMBER:
				renderNumberControl(g, panelX, animatedY, x, y, mouseX, mouseY, defaultFont,
						(NumberSetting) setting);
				break;
			case MODE:
				renderModeControl(g, x, y, defaultFont, customFont, (ModeSetting) setting);
				break;
		}
	}

	private void renderBooleanControl(GuiGraphics g, int x, int y, BooleanSetting setting) {
		int toggleX = x + width - 50;
		int toggleY = y + height / 2 - 8;
		boolean enabled = setting.getValue();

		// Toggle background
		int toggleBg = enabled ? ACCENT_PRIMARY : 0xFF4B5563;
		UIHelper.drawRoundedRect(g, toggleX, toggleY, 36, 16, 8, toggleBg);

		// Toggle circle
		int circleX = enabled ? toggleX + 20 : toggleX + 4;
		int circleY = toggleY + 4;

		// Make circle rounded
		UIHelper.drawRoundedRect(g, circleX - 1, circleY - 1, 10, 10, 5, 0xFFFFFFFF);
	}

	private void renderNumberControl(GuiGraphics g, int panelX, int animatedY, int x, int y, int mouseX, int mouseY,
			Font font, NumberSetting setting) {
		int sliderX = x + 12;
		int sliderY = y + 32;
		int sliderWidth = width - 80;
		int sliderHeight = 4;

		// Slider background
		UIHelper.drawRoundedRect(g, sliderX, sliderY, sliderWidth, sliderHeight, 2, SLIDER_BG);

		// Slider fill
		double progress = (setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin());
		int fillWidth = (int) (sliderWidth * progress);
		UIHelper.drawRoundedRect(g, sliderX, sliderY, fillWidth, sliderHeight, 2, SLIDER_FILL);

		// Slider handle
		int handleX = sliderX + fillWidth - 4;
		int handleY = sliderY - 4;
		UIHelper.drawRoundedRect(g, handleX, handleY, 8, 12, 4, ACCENT_SECONDARY);

		// Value display
		String valueText = String.format("%.1f", setting.getValue());
		g.drawString(font, valueText, x + width - 60, y + 28, TEXT_PRIMARY);

		// Handle dragging - only if THIS widget is dragging
		if (dragging) {
			int absoluteSliderX = panelX + relX + 12;
			int relMouseX = mouseX - absoluteSliderX;
			double newProgress = Math.max(0, Math.min(1, (double) relMouseX / sliderWidth));
			double newValue = setting.getMin() + (setting.getMax() - setting.getMin()) * newProgress;

			// Round to increment
			newValue = Math.round(newValue / setting.getIncrement()) * setting.getIncrement();
			setting.setValue(newValue);
		}
	}

	private void renderModeControl(GuiGraphics g, int x, int y, Font defaultFont,
			CustomFontRenderer customFont, ModeSetting setting) {
		int buttonX = x + width - 120;
		int buttonY = y + height / 2 - 10;
		int buttonWidth = 100;
		int buttonHeight = 20;

		// Button background
		UIHelper.drawRoundedRect(g, buttonX, buttonY, buttonWidth, buttonHeight, 4, ACCENT_PRIMARY);

		// Current mode text
		String modeText = setting.getValue();
		if (customFont != null) {
			customFont.drawCenteredString(g, modeText, buttonX + buttonWidth / 2,
					buttonY + (buttonHeight - customFont.getFontHeight()) / 2, TEXT_PRIMARY);
		} else {
			g.drawCenteredString(defaultFont, modeText, buttonX + buttonWidth / 2,
					buttonY + (buttonHeight - 8) / 2, TEXT_PRIMARY);
		}

		// Arrows
		g.drawString(defaultFont, "<", buttonX + 8, buttonY + 6, TEXT_SECONDARY);
		g.drawString(defaultFont, ">", buttonX + buttonWidth - 16, buttonY + 6, TEXT_SECONDARY);
	}

	public boolean mouseClicked(int panelX, int animatedY, int mouseX, int mouseY) {
		int x = panelX + relX;
		int y = animatedY + relY;

		if (!isMouseOver(panelX, animatedY, mouseX, mouseY)) {
			// If clicking outside, stop dragging
			dragging = false;
			return false;
		}

		switch (setting.getType()) {
			case BOOLEAN:
				int toggleX = x + width - 50;
				int toggleY = y + height / 2 - 8;
				if (mouseX >= toggleX && mouseX <= toggleX + 36 &&
						mouseY >= toggleY && mouseY <= toggleY + 16) {
					((BooleanSetting) setting).toggle();
					return true;
				}
				break;
			case NUMBER:
				int sliderX = x + 12;
				int sliderY = y + 32;
				int sliderWidth = width - 80;
				if (mouseX >= sliderX && mouseX <= sliderX + sliderWidth &&
						mouseY >= sliderY - 8 && mouseY <= sliderY + 12) {
					dragging = true;
					// Update value immediately on click
					int relMouseX = mouseX - sliderX;
					double newProgress = Math.max(0, Math.min(1, (double) relMouseX / sliderWidth));
					NumberSetting numSetting = (NumberSetting) setting;
					double newValue = numSetting.getMin()
							+ (numSetting.getMax() - numSetting.getMin()) * newProgress;
					newValue = Math.round(newValue / numSetting.getIncrement())
							* numSetting.getIncrement();
					numSetting.setValue(newValue);
					return true;
				}
				break;
			case MODE:
				int buttonX = x + width - 120;
				int buttonY = y + height / 2 - 10;
				int buttonWidth = 100;
				int buttonHeight = 20;
				if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
						mouseY >= buttonY && mouseY <= buttonY + buttonHeight) {
					((ModeSetting) setting).cycle();
					return true;
				}
				break;
		}

		return false;
	}

	public boolean mouseDragged(int panelX, int animatedY, int mouseX, int mouseY) {
		if (dragging && setting.getType() == SettingType.NUMBER) {
			return true;
		}
		return false;
	}

	public void mouseReleased() {
		dragging = false;
	}

	private boolean isMouseOver(int panelX, int animatedY, int mouseX, int mouseY) {
		int x = panelX + relX;
		int y = animatedY + relY;
		return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
	}
}
