package com.kirby.nebula;

import com.kirby.nebula.ui.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class NebulaMenuScreen extends Screen {
	private static final int PANEL_WIDTH = 600;
	private static final int PANEL_HEIGHT = 400;
	private static final int SIDEBAR_WIDTH = 150;
	private static final int CORNER_RADIUS = 8;
	
	private float animationProgress = 0.0f;
	private final List<ModButton> modButtons = new ArrayList<>();
	private final List<TabButton> tabButtons = new ArrayList<>();
	private TabType currentTab = TabType.COMBAT;

	// Custom fonts
	private CustomFontRenderer titleFont;
	private CustomFontRenderer buttonFont;

	public NebulaMenuScreen() {
		super(Component.literal("Nebula Mod Menu"));
	}

	@Override
	protected void init() {
		super.init();

		// Load custom fonts (fallback to default if loading fails)
		loadFonts();
		
		// Initialize UI components
		initializeTabs();
		refreshModButtons();
	}

	private void loadFonts() {
		try {
			titleFont = CustomFontRenderer.create("/assets/nebula/fonts/title.ttf", 20f);
		} catch (Exception e) {
			Nebula.LOGGER.warn("Failed to load title font: " + e.getMessage());
			titleFont = null;
		}

		try {
			buttonFont = CustomFontRenderer.create("/assets/nebula/fonts/button.ttf", 14f);
		} catch (Exception e) {
			Nebula.LOGGER.warn("Failed to load button font: " + e.getMessage());
			buttonFont = null;
		}
	}

	private void initializeTabs() {
		tabButtons.clear();
		tabButtons.add(new TabButton(10, 60, SIDEBAR_WIDTH - 20, 35, 
			Component.literal("Combat"), TabType.COMBAT));
		tabButtons.add(new TabButton(10, 100, SIDEBAR_WIDTH - 20, 35, 
			Component.literal("Rendering"), TabType.RENDERING));
		tabButtons.add(new TabButton(10, 140, SIDEBAR_WIDTH - 20, 35, 
			Component.literal("World"), TabType.WORLD));
	}

	private void refreshModButtons() {
		modButtons.clear();
		int startX = SIDEBAR_WIDTH + 20;
		int startY = 60;
		int buttonWidth = PANEL_WIDTH - SIDEBAR_WIDTH - 40;

		switch (currentTab) {
			case COMBAT:
				addCombatMods(startX, startY, buttonWidth);
				break;
			case RENDERING:
				addRenderingMods(startX, startY, buttonWidth);
				break;
			case WORLD:
				addWorldMods(startX, startY, buttonWidth);
				break;
		}
	}

	private void addCombatMods(int startX, int startY, int buttonWidth) {
		modButtons.add(new ModButton(startX, startY, buttonWidth, 30,
			Component.literal("Killaura"), false,
			() -> Nebula.LOGGER.info("Killaura toggled!")));
		modButtons.add(new ModButton(startX, startY + 38, buttonWidth, 30,
			Component.literal("Velocity"), false,
			() -> Nebula.LOGGER.info("Velocity toggled!")));
		modButtons.add(new ModButton(startX, startY + 76, buttonWidth, 30,
			Component.literal("Auto Totem"), false,
			() -> Nebula.LOGGER.info("Auto Totem toggled!")));
		modButtons.add(new ModButton(startX, startY + 114, buttonWidth, 30,
			Component.literal("Criticals"), false,
			() -> Nebula.LOGGER.info("Criticals toggled!")));
	}

	private void addRenderingMods(int startX, int startY, int buttonWidth) {
		modButtons.add(new ModButton(startX, startY, buttonWidth, 30,
			Component.literal("ESP"), false,
			() -> Nebula.LOGGER.info("ESP toggled!")));
		modButtons.add(new ModButton(startX, startY + 38, buttonWidth, 30,
			Component.literal("Tracers"), false,
			() -> Nebula.LOGGER.info("Tracers toggled!")));
		modButtons.add(new ModButton(startX, startY + 76, buttonWidth, 30,
			Component.literal("Nametags"), false,
			() -> Nebula.LOGGER.info("Nametags toggled!")));
		modButtons.add(new ModButton(startX, startY + 114, buttonWidth, 30,
			Component.literal("Fullbright"), false,
			() -> Nebula.LOGGER.info("Fullbright toggled!")));
	}

	private void addWorldMods(int startX, int startY, int buttonWidth) {
		modButtons.add(new ModButton(startX, startY, buttonWidth, 30,
			Component.literal("Xray"), false,
			() -> Nebula.LOGGER.info("Xray toggled!")));
		modButtons.add(new ModButton(startX, startY + 38, buttonWidth, 30,
			Component.literal("Nuker"), false,
			() -> Nebula.LOGGER.info("Nuker toggled!")));
		modButtons.add(new ModButton(startX, startY + 76, buttonWidth, 30,
			Component.literal("Auto Mine"), false,
			() -> Nebula.LOGGER.info("Auto Mine toggled!")));
		modButtons.add(new ModButton(startX, startY + 114, buttonWidth, 30,
			Component.literal("Timer"), false,
			() -> Nebula.LOGGER.info("Timer toggled!")));
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(graphics, 0, 0, partialTick);

		// Animate panel sliding in
		if (animationProgress < 1.0f) {
			animationProgress = Math.min(1.0f, animationProgress + partialTick * 0.1f);
		}

		int panelX = (this.width - PANEL_WIDTH) / 2;
		int panelY = (this.height - PANEL_HEIGHT) / 2;
		int animatedY = (int) (panelY - (50 * (1.0f - animationProgress)));

		// Draw panel backgrounds
		renderPanelBackground(graphics, panelX, animatedY);
		
		// Draw title
		renderTitle(graphics, panelX, animatedY);
		
		// Draw tab title
		renderTabTitle(graphics, panelX, animatedY);
		
		// Draw footer text
		graphics.drawCenteredString(this.font, "ESC to close", panelX + SIDEBAR_WIDTH / 2,
			animatedY + PANEL_HEIGHT - 20, 0x808080);

		// Render all buttons
		renderButtons(graphics, panelX, animatedY, mouseX, mouseY);
	}

	private void renderPanelBackground(GuiGraphics graphics, int panelX, int animatedY) {
		// Main panel
		UIHelper.drawRoundedRect(graphics, panelX, animatedY, PANEL_WIDTH, PANEL_HEIGHT, 
			CORNER_RADIUS, 0xE0202020);
		
		// Sidebar
		UIHelper.drawRoundedRect(graphics, panelX, animatedY, SIDEBAR_WIDTH, PANEL_HEIGHT, 
			CORNER_RADIUS, 0xE0181818);
		
		// Sidebar border
		graphics.fill(panelX + SIDEBAR_WIDTH, animatedY + CORNER_RADIUS,
			panelX + SIDEBAR_WIDTH + 2, animatedY + PANEL_HEIGHT - CORNER_RADIUS, 0xFF00AAFF);

		// Top accent
		UIHelper.drawRoundedRect(graphics, panelX, animatedY, PANEL_WIDTH, 3, CORNER_RADIUS, 0xFF00AAFF);
	}

	private void renderTitle(GuiGraphics graphics, int panelX, int animatedY) {
		if (titleFont != null) {
			int titleY = animatedY + 18;
			titleFont.drawCenteredString(graphics, "NEBULA", panelX + SIDEBAR_WIDTH / 2, titleY, 0xFF00AAFF);
			titleFont.drawCenteredString(graphics, "CLIENT", panelX + SIDEBAR_WIDTH / 2, titleY + 20, 0xFF00AAFF);
		} else {
			graphics.drawCenteredString(this.font, "NEBULA", panelX + SIDEBAR_WIDTH / 2, animatedY + 20, 0x00AAFF);
			graphics.drawCenteredString(this.font, "CLIENT", panelX + SIDEBAR_WIDTH / 2, animatedY + 32, 0x00AAFF);
		}
	}

	private void renderTabTitle(GuiGraphics graphics, int panelX, int animatedY) {
		String tabTitle = currentTab.toString() + " MODS";
		if (buttonFont != null) {
			buttonFont.drawString(graphics, tabTitle, panelX + SIDEBAR_WIDTH + 20, 
				animatedY + 25, 0xFFFFFFFF);
		} else {
			graphics.drawString(this.font, tabTitle, panelX + SIDEBAR_WIDTH + 20, 
				animatedY + 25, 0xFFFFFF);
		}
	}

	private void renderButtons(GuiGraphics graphics, int panelX, int animatedY, int mouseX, int mouseY) {
		// Render tab buttons
		for (TabButton tab : tabButtons) {
			tab.render(graphics, panelX, animatedY, mouseX, mouseY, currentTab, this.font, buttonFont);
		}

		// Render mod buttons
		for (ModButton btn : modButtons) {
			btn.render(graphics, panelX, animatedY, mouseX, mouseY, this.font, buttonFont);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			int panelX = (this.width - PANEL_WIDTH) / 2;
			int panelY = (this.height - PANEL_HEIGHT) / 2;
			int animatedY = (int) (panelY - (50 * (1.0f - animationProgress)));

			// Check tab buttons
			for (TabButton tab : tabButtons) {
				if (tab.isMouseOver(panelX, animatedY, (int) mouseX, (int) mouseY)) {
					if (currentTab != tab.getType()) {
						currentTab = tab.getType();
						refreshModButtons();
					}
					return true;
				}
			}

			// Check mod buttons
			for (ModButton btn : modButtons) {
				if (btn.isMouseOver(panelX, animatedY, (int) mouseX, (int) mouseY)) {
					btn.toggle();
					return true;
				}
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256 || keyCode == 344) { // ESC or Right Shift
			this.onClose();
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
		// Clean up font textures
		if (titleFont != null)
			titleFont.cleanup();
		if (buttonFont != null)
			buttonFont.cleanup();
	}
}
