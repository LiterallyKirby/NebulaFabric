package com.kirby.nebula;

import com.kirby.nebula.module.Category;
import com.kirby.nebula.module.Module;
import com.kirby.nebula.module.ModuleManager;
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
	// UI Constants - Deep Purple Theme
	private static final int PANEL_WIDTH = 650;
	private static final int PANEL_HEIGHT = 450;
	private static final int SIDEBAR_WIDTH = 180;
	private static final int CORNER_RADIUS = 10;

	// Deep Purple Color Scheme
	private static final int BG_MAIN = 0xE0181025; // Dark purple background
	private static final int BG_SIDEBAR = 0xE00F0820; // Darker purple sidebar
	private static final int ACCENT_PRIMARY = 0xFF6B46C1; // Medium purple
	private static final int ACCENT_SECONDARY = 0xFF8B5CF6; // Light purple
	private static final int ACCENT_HIGHLIGHT = 0xFFA78BFA; // Lighter purple
	private static final int TEXT_PRIMARY = 0xFFFFFFFF;
	private static final int TEXT_SECONDARY = 0xFFC4B5FD;
	private static final int TEXT_MUTED = 0xFF9F7AEA;

	private float animationProgress = 0.0f;
	private final List<ModButton> modButtons = new ArrayList<>();
	private final List<CategoryTab> categoryTabs = new ArrayList<>();
	private Category currentCategory = Category.COMBAT;

	private CustomFontRenderer titleFont;
	private CustomFontRenderer buttonFont;

	public NebulaMenuScreen() {
		super(Component.literal("Nebula Client"));
	}

	@Override
	protected void init() {
		super.init();
		loadFonts();
		initializeCategoryTabs();
		refreshModButtons();
	}

	private void loadFonts() {
		try {
			titleFont = CustomFontRenderer.create("/assets/nebula/fonts/title.ttf", 24f);
		} catch (Exception e) {
			Nebula.LOGGER.warn("Failed to load title font, using default");
			titleFont = null;
		}

		try {
			buttonFont = CustomFontRenderer.create("/assets/nebula/fonts/button.ttf", 15f);
		} catch (Exception e) {
			Nebula.LOGGER.warn("Failed to load button font, using default");
			buttonFont = null;
		}
	}

	private void initializeCategoryTabs() {
		categoryTabs.clear();
		int startY = 80;
		int spacing = 45;
		int index = 0;

		// Automatically create tabs for all categories
		for (Category category : Category.values()) {
			categoryTabs.add(new CategoryTab(
					15,
					startY + (index * spacing),
					SIDEBAR_WIDTH - 30,
					38,
					Component.literal(category.getDisplayName()),
					category));
			index++;
		}
	}

	private void refreshModButtons() {
		modButtons.clear();
		int startX = SIDEBAR_WIDTH + 25;
		int startY = 70;
		int buttonWidth = PANEL_WIDTH - SIDEBAR_WIDTH - 50;
		int buttonHeight = 35;
		int spacing = 8;

		// Get modules for current category from ModuleManager
		List<Module> modules = ModuleManager.getInstance().getModulesByCategory(currentCategory);

		for (int i = 0; i < modules.size(); i++) {
			Module module = modules.get(i);
			modButtons.add(new ModButton(
					startX,
					startY + (i * (buttonHeight + spacing)),
					buttonWidth,
					buttonHeight,
					Component.literal(module.getName()),
					module.getDescription(),
					module.isEnabled(),
					() -> module.toggle()));
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(graphics, mouseX, mouseY, partialTick);

		// Smooth animation
		if (animationProgress < 1.0f) {
			animationProgress = Math.min(1.0f, animationProgress + partialTick * 0.08f);
		}

		int panelX = (this.width - PANEL_WIDTH) / 2;
		int panelY = (this.height - PANEL_HEIGHT) / 2;

		// Slide and fade animation
		int animatedY = (int) (panelY - (60 * (1.0f - animationProgress)));
		int alpha = (int) (224 * animationProgress);

		// Draw main UI
		renderPanel(graphics, panelX, animatedY, alpha);
		renderHeader(graphics, panelX, animatedY);
		renderCategoryTitle(graphics, panelX, animatedY);
		renderFooter(graphics, panelX, animatedY);
		renderButtons(graphics, panelX, animatedY, mouseX, mouseY);
	}

	private void renderPanel(GuiGraphics graphics, int panelX, int animatedY, int alpha) {
		// Main panel with adjusted alpha
		int bgMain = (alpha << 24) | (BG_MAIN & 0x00FFFFFF);
		int bgSidebar = (alpha << 24) | (BG_SIDEBAR & 0x00FFFFFF);

		UIHelper.drawRoundedRect(graphics, panelX, animatedY, PANEL_WIDTH, PANEL_HEIGHT,
				CORNER_RADIUS, bgMain);

		// Sidebar
		UIHelper.drawRoundedRect(graphics, panelX, animatedY, SIDEBAR_WIDTH, PANEL_HEIGHT,
				CORNER_RADIUS, bgSidebar);

		// Accent borders
		UIHelper.drawRoundedRect(graphics, panelX, animatedY, PANEL_WIDTH, 3,
				CORNER_RADIUS, ACCENT_SECONDARY);

		graphics.fill(panelX + SIDEBAR_WIDTH, animatedY + 10,
				panelX + SIDEBAR_WIDTH + 2, animatedY + PANEL_HEIGHT - 10, ACCENT_PRIMARY);

		// Glow effect on top
		UIHelper.drawGradientRect(graphics, panelX, animatedY + 3, PANEL_WIDTH, 30,
				0x40A78BFA, 0x00A78BFA);
	}

	private void renderHeader(GuiGraphics graphics, int panelX, int animatedY) {
		if (titleFont != null) {
			titleFont.drawCenteredString(graphics, "NEBULA",
					panelX + SIDEBAR_WIDTH / 2, animatedY + 22, ACCENT_HIGHLIGHT);
			titleFont.drawCenteredString(graphics, "CLIENT",
					panelX + SIDEBAR_WIDTH / 2, animatedY + 46, TEXT_SECONDARY);
		} else {
			graphics.drawCenteredString(this.font, "§l§nNEBULA",
					panelX + SIDEBAR_WIDTH / 2, animatedY + 22, ACCENT_HIGHLIGHT);
			graphics.drawCenteredString(this.font, "§lCLIENT",
					panelX + SIDEBAR_WIDTH / 2, animatedY + 40, TEXT_SECONDARY);
		}
	}

	private void renderCategoryTitle(GuiGraphics graphics, int panelX, int animatedY) {
		String title = currentCategory.getDisplayName().toUpperCase();
		int moduleCount = ModuleManager.getInstance().getModulesByCategory(currentCategory).size();
		String subtitle = moduleCount + " module" + (moduleCount != 1 ? "s" : "");

		if (buttonFont != null) {
			buttonFont.drawString(graphics, title,
					panelX + SIDEBAR_WIDTH + 25, animatedY + 25, TEXT_PRIMARY);
			buttonFont.drawString(graphics, subtitle,
					panelX + SIDEBAR_WIDTH + 25, animatedY + 45, TEXT_MUTED);
		} else {
			graphics.drawString(this.font, "§l" + title,
					panelX + SIDEBAR_WIDTH + 25, animatedY + 25, TEXT_PRIMARY);
			graphics.drawString(this.font, subtitle,
					panelX + SIDEBAR_WIDTH + 25, animatedY + 40, TEXT_MUTED);
		}
	}

	private void renderFooter(GuiGraphics graphics, int panelX, int animatedY) {
		String footerText = "Press ESC or Right Shift to close";
		graphics.drawCenteredString(this.font, footerText,
				panelX + SIDEBAR_WIDTH / 2, animatedY + PANEL_HEIGHT - 25, TEXT_MUTED);
	}

	private void renderButtons(GuiGraphics graphics, int panelX, int animatedY, int mouseX, int mouseY) {
		// Render category tabs
		for (CategoryTab tab : categoryTabs) {
			tab.render(graphics, panelX, animatedY, mouseX, mouseY,
					currentCategory, this.font, buttonFont);
		}

		// Render module buttons
		for (ModButton btn : modButtons) {
			btn.render(graphics, panelX, animatedY, mouseX, mouseY, this.font, buttonFont);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			int panelX = (this.width - PANEL_WIDTH) / 2;
			int panelY = (this.height - PANEL_HEIGHT) / 2;
			int animatedY = (int) (panelY - (60 * (1.0f - animationProgress)));

			// Check category tabs
			for (CategoryTab tab : categoryTabs) {
				if (tab.isMouseOver(panelX, animatedY, (int) mouseX, (int) mouseY)) {
					if (currentCategory != tab.getCategory()) {
						currentCategory = tab.getCategory();
						refreshModButtons();
					}
					return true;
				}
			}

			// Check module buttons
			for (ModButton btn : modButtons) {
				ModButton.ClickResult result = btn.mouseClicked(panelX, animatedY, (int) mouseX,
						(int) mouseY);
				if (result == ModButton.ClickResult.OPEN_OPTIONS) {
					// Open the options screen for this module
					this.minecraft.setScreen(new ModuleOptionsScreen(this, btn.getModule()));
					return true;
				} else if (result == ModButton.ClickResult.TOGGLE) {
					// Just toggled, nothing else to do
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
		if (titleFont != null)
			titleFont.cleanup();
		if (buttonFont != null)
			buttonFont.cleanup();
	}
}
