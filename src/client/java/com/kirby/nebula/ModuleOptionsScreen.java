package com.kirby.nebula;

import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.settings.*;
import com.kirby.nebula.ui.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ModuleOptionsScreen extends Screen {
	private static final int PANEL_WIDTH = 500;
	private static final int PANEL_HEIGHT = 400;
	private static final int CORNER_RADIUS = 10;
	private static final int HEADER_HEIGHT = 60;
	private static final int FOOTER_HEIGHT = 40;
	private static final int CONTENT_HEIGHT = PANEL_HEIGHT - HEADER_HEIGHT - FOOTER_HEIGHT;

	private static final int BG_MAIN = 0xF00A0A0F;
	private static final int BG_HEADER = 0xF0050508;
	private static final int ACCENT_PRIMARY = 0xFF5B21B6;
	private static final int ACCENT_SECONDARY = 0xFF7C3AED;
	private static final int TEXT_PRIMARY = 0xFFFFFFFF;
	private static final int TEXT_SECONDARY = 0xFFC4B5FD;
	private static final int TEXT_MUTED = 0xFF9F7AEA;

	private final Screen parent;
	private final Module module;
	private float animationProgress = 0.0f;
	private final List<SettingWidget> settingWidgets = new ArrayList<>();

	private CustomFontRenderer titleFont;
	private CustomFontRenderer settingFont;

	// Scrolling variables
	private float scrollOffset = 0.0f;
	private float maxScroll = 0.0f;
	private boolean isDragging = false;

	public ModuleOptionsScreen(Screen parent, Module module) {
		super(Component.literal(module.getName() + " Options"));
		this.parent = parent;
		this.module = module;
	}

	@Override
	protected void init() {
		super.init();
		loadFonts();
		createSettingWidgets();
		calculateMaxScroll();
	}

	private void loadFonts() {
		try {
			titleFont = CustomFontRenderer.create("/assets/nebula/fonts/title.ttf", 20f);
		} catch (Exception e) {
			titleFont = null;
		}

		try {
			settingFont = CustomFontRenderer.create("/assets/nebula/fonts/button.ttf", 14f);
		} catch (Exception e) {
			settingFont = null;
		}
	}

	private void createSettingWidgets() {
		settingWidgets.clear();
		int startY = 0; // Start from 0 since we'll offset by scroll
		int spacing = 55;

		for (int i = 0; i < module.getSettings().size(); i++) {
			Setting<?> setting = module.getSettings().get(i);
			settingWidgets.add(new SettingWidget(
					25,
					startY + (i * spacing),
					PANEL_WIDTH - 50,
					45,
					setting));
		}
	}

	private void calculateMaxScroll() {
		if (settingWidgets.isEmpty()) {
			maxScroll = 0;
			return;
		}

		// Calculate total content height
		int lastWidgetIndex = settingWidgets.size() - 1;
		SettingWidget lastWidget = settingWidgets.get(lastWidgetIndex);
		int totalContentHeight = lastWidget.getRelY() + 45 + 20; // Last widget Y + height + padding

		// Max scroll is the amount content exceeds visible area
		maxScroll = Math.max(0, totalContentHeight - CONTENT_HEIGHT);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(graphics, mouseX, mouseY, partialTick);

		if (animationProgress < 1.0f) {
			animationProgress = Math.min(1.0f, animationProgress + partialTick * 0.08f);
		}

		int panelX = (this.width - PANEL_WIDTH) / 2;
		int panelY = (this.height - PANEL_HEIGHT) / 2;

		int animatedY = (int) (panelY - (60 * (1.0f - animationProgress)));
		int alpha = (int) (224 * animationProgress);

		renderPanel(graphics, panelX, animatedY, alpha);
		renderHeader(graphics, panelX, animatedY);
		
		// Enable scissor test for content area
		enableScissor(panelX, animatedY + HEADER_HEIGHT, PANEL_WIDTH, CONTENT_HEIGHT);
		renderSettings(graphics, panelX, animatedY, mouseX, mouseY);
		disableScissor();
		
		renderScrollbar(graphics, panelX, animatedY);
		renderFooter(graphics, panelX, animatedY);
	}

	private void enableScissor(int x, int y, int width, int height) {
		double scale = minecraft.getWindow().getGuiScale();
		int scaledX = (int) (x * scale);
		int scaledY = (int) (minecraft.getWindow().getHeight() - (y + height) * scale);
		int scaledWidth = (int) (width * scale);
		int scaledHeight = (int) (height * scale);
		
		com.mojang.blaze3d.systems.RenderSystem.enableScissor(scaledX, scaledY, scaledWidth, scaledHeight);
	}

	private void disableScissor() {
		com.mojang.blaze3d.systems.RenderSystem.disableScissor();
	}

	private void renderPanel(GuiGraphics graphics, int panelX, int animatedY, int alpha) {
		int bgMain = (alpha << 24) | (BG_MAIN & 0x00FFFFFF);
		int bgHeader = (alpha << 24) | (BG_HEADER & 0x00FFFFFF);

		UIHelper.drawRoundedRect(graphics, panelX, animatedY, PANEL_WIDTH, PANEL_HEIGHT,
				CORNER_RADIUS, bgMain);

		UIHelper.drawRoundedRect(graphics, panelX, animatedY, PANEL_WIDTH, 60,
				CORNER_RADIUS, bgHeader);

		UIHelper.drawRoundedRect(graphics, panelX, animatedY, PANEL_WIDTH, 3,
				CORNER_RADIUS, ACCENT_SECONDARY);

		UIHelper.drawGradientRect(graphics, panelX, animatedY + 3, PANEL_WIDTH, 30,
				0x40A78BFA, 0x00A78BFA);
	}

	private void renderHeader(GuiGraphics graphics, int panelX, int animatedY) {
		String title = module.getName().toUpperCase();
		String subtitle = "SETTINGS";

		if (titleFont != null) {
			titleFont.drawCenteredString(graphics, title,
					panelX + PANEL_WIDTH / 2, animatedY + 15, TEXT_PRIMARY);
			graphics.drawCenteredString(this.font, subtitle,
					panelX + PANEL_WIDTH / 2, animatedY + 40, TEXT_MUTED);
		} else {
			graphics.drawCenteredString(this.font, "§l" + title,
					panelX + PANEL_WIDTH / 2, animatedY + 15, TEXT_PRIMARY);
			graphics.drawCenteredString(this.font, subtitle,
					panelX + PANEL_WIDTH / 2, animatedY + 35, TEXT_MUTED);
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) {
			isDragging = false;
			for (SettingWidget widget : settingWidgets) {
				widget.mouseReleased();
			}
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	private void renderSettings(GuiGraphics graphics, int panelX, int animatedY, int mouseX, int mouseY) {
		int contentY = animatedY + HEADER_HEIGHT;
		int scrolledY = (int) (contentY - scrollOffset);

		for (SettingWidget widget : settingWidgets) {
			widget.render(graphics, panelX, scrolledY, mouseX, mouseY, this.font, settingFont);
		}
	}

	private void renderScrollbar(GuiGraphics graphics, int panelX, int animatedY) {
		if (maxScroll <= 0) return; // No scrollbar needed

		int scrollbarX = panelX + PANEL_WIDTH - 8;
		int scrollbarY = animatedY + HEADER_HEIGHT;
		int scrollbarHeight = CONTENT_HEIGHT;
		int scrollbarWidth = 4;

		// Scrollbar background
		graphics.fill(scrollbarX, scrollbarY, scrollbarX + scrollbarWidth, 
				scrollbarY + scrollbarHeight, 0x40FFFFFF);

		// Scrollbar handle
		float scrollPercentage = scrollOffset / maxScroll;
		int handleHeight = Math.max(20, (int) (scrollbarHeight * (CONTENT_HEIGHT / (float) (CONTENT_HEIGHT + maxScroll))));
		int handleY = scrollbarY + (int) ((scrollbarHeight - handleHeight) * scrollPercentage);

		graphics.fill(scrollbarX, handleY, scrollbarX + scrollbarWidth, 
				handleY + handleHeight, ACCENT_PRIMARY);
	}

	private void renderFooter(GuiGraphics graphics, int panelX, int animatedY) {
		String footerText = "Press ESC to go back";
		if (maxScroll > 0) {
			footerText += " | Scroll to see more";
		}
		graphics.drawCenteredString(this.font, footerText,
				panelX + PANEL_WIDTH / 2, animatedY + PANEL_HEIGHT - 25, TEXT_MUTED);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			int panelX = (this.width - PANEL_WIDTH) / 2;
			int panelY = (this.height - PANEL_HEIGHT) / 2;
			int animatedY = (int) (panelY - (60 * (1.0f - animationProgress)));
			int contentY = animatedY + HEADER_HEIGHT;
			int scrolledY = (int) (contentY - scrollOffset);

			// Check if clicking on scrollbar
			int scrollbarX = panelX + PANEL_WIDTH - 8;
			int scrollbarY = animatedY + HEADER_HEIGHT;
			if (mouseX >= scrollbarX && mouseX <= scrollbarX + 4 &&
				mouseY >= scrollbarY && mouseY <= scrollbarY + CONTENT_HEIGHT) {
				isDragging = true;
				return true;
			}

			for (SettingWidget widget : settingWidgets) {
				if (widget.mouseClicked(panelX, scrolledY, (int) mouseX, (int) mouseY)) {
					return true;
				}
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		int panelX = (this.width - PANEL_WIDTH) / 2;
		int panelY = (this.height - PANEL_HEIGHT) / 2;
		int animatedY = (int) (panelY - (60 * (1.0f - animationProgress)));

		// Handle scrollbar dragging
		if (isDragging && maxScroll > 0) {
			int scrollbarY = animatedY + HEADER_HEIGHT;
			float scrollPercentage = (float) (mouseY - scrollbarY) / CONTENT_HEIGHT;
			scrollOffset = Mth.clamp(scrollPercentage * maxScroll, 0, maxScroll);
			return true;
		}

		int contentY = animatedY + HEADER_HEIGHT;
		int scrolledY = (int) (contentY - scrollOffset);

		for (SettingWidget widget : settingWidgets) {
			if (widget.mouseDragged(panelX, scrolledY, (int) mouseX, (int) mouseY)) {
				return true;
			}
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (maxScroll > 0) {
			// Scroll by 30 pixels per scroll notch
			scrollOffset = Mth.clamp(scrollOffset - (float) scrollY * 30, 0, maxScroll);
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) { // ESC
			this.minecraft.setScreen(parent);
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void removed() {
		super.removed();
		if (titleFont != null)
			titleFont.cleanup();
		if (settingFont != null)
			settingFont.cleanup();
	}
}
