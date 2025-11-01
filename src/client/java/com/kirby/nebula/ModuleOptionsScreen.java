package com.kirby.nebula;

import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.settings.*;
import com.kirby.nebula.ui.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ModuleOptionsScreen extends Screen {
	private static final int PANEL_WIDTH = 500;
	private static final int PANEL_HEIGHT = 400;
	private static final int CORNER_RADIUS = 10;
	
	// Deep Purple Color Scheme
	private static final int BG_MAIN = 0xE0181025;
	private static final int BG_HEADER = 0xE00F0820;
	private static final int ACCENT_PRIMARY = 0xFF6B46C1;
	private static final int ACCENT_SECONDARY = 0xFF8B5CF6;
	private static final int TEXT_PRIMARY = 0xFFFFFFFF;
	private static final int TEXT_SECONDARY = 0xFFC4B5FD;
	private static final int TEXT_MUTED = 0xFF9F7AEA;
	
	private final Screen parent;
	private final Module module;
	private float animationProgress = 0.0f;
	private final List<SettingWidget> settingWidgets = new ArrayList<>();
	
	private CustomFontRenderer titleFont;
	private CustomFontRenderer settingFont;

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
		int startY = 80;
		int spacing = 55;
		
		for (int i = 0; i < module.getSettings().size(); i++) {
			Setting<?> setting = module.getSettings().get(i);
			settingWidgets.add(new SettingWidget(
				25,
				startY + (i * spacing),
				PANEL_WIDTH - 50,
				45,
				setting
			));
		}
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
		renderSettings(graphics, panelX, animatedY, mouseX, mouseY);
		renderFooter(graphics, panelX, animatedY);
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

	private void renderSettings(GuiGraphics graphics, int panelX, int animatedY, int mouseX, int mouseY) {
		for (SettingWidget widget : settingWidgets) {
			widget.render(graphics, panelX, animatedY, mouseX, mouseY, this.font, settingFont);
		}
	}

	private void renderFooter(GuiGraphics graphics, int panelX, int animatedY) {
		String footerText = "Press ESC to go back";
		graphics.drawCenteredString(this.font, footerText, 
			panelX + PANEL_WIDTH / 2, animatedY + PANEL_HEIGHT - 25, TEXT_MUTED);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			int panelX = (this.width - PANEL_WIDTH) / 2;
			int panelY = (this.height - PANEL_HEIGHT) / 2;
			int animatedY = (int) (panelY - (60 * (1.0f - animationProgress)));

			for (SettingWidget widget : settingWidgets) {
				if (widget.mouseClicked(panelX, animatedY, (int) mouseX, (int) mouseY)) {
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

		for (SettingWidget widget : settingWidgets) {
			if (widget.mouseDragged(panelX, animatedY, (int) mouseX, (int) mouseY)) {
				return true;
			}
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
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
		if (titleFont != null) titleFont.cleanup();
		if (settingFont != null) settingFont.cleanup();
	}
}
