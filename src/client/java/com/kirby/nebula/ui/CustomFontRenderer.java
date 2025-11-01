package com.kirby.nebula.ui;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CustomFontRenderer {
	private final Font awtFont;
	private final Map<Character, CharData> charCache = new HashMap<>();
	private final int fontHeight;

	private CustomFontRenderer(Font awtFont) {
		this.awtFont = awtFont;
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setFont(awtFont);
		FontMetrics metrics = g2d.getFontMetrics();
		this.fontHeight = metrics.getHeight();
		g2d.dispose();
	}

	public static CustomFontRenderer create(String path, float size) throws Exception {
		try (InputStream is = CustomFontRenderer.class.getResourceAsStream(path)) {
			if (is == null) {
				throw new Exception("Font not found: " + path);
			}
			Font awtFont = Font.createFont(Font.TRUETYPE_FONT, is);
			awtFont = awtFont.deriveFont(Font.PLAIN, size);
			return new CustomFontRenderer(awtFont);
		}
	}

	private CharData getCharData(char c) {
		if (!charCache.containsKey(c)) {
			BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = img.createGraphics();
			g2d.setFont(awtFont);
			FontMetrics metrics = g2d.getFontMetrics();
			int width = metrics.charWidth(c);
			int height = metrics.getHeight();
			g2d.dispose();

			if (width == 0)
				width = 8; // fallback for space

			img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			g2d = img.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setFont(awtFont);
			g2d.setColor(Color.WHITE);
			g2d.drawString(String.valueOf(c), 0, metrics.getAscent());
			g2d.dispose();

			CharData data = new CharData(img, width, height);
			charCache.put(c, data);
		}
		return charCache.get(c);
	}

	public int getStringWidth(String text) {
		int width = 0;
		for (char c : text.toCharArray()) {
			CharData data = getCharData(c);
			width += data.width;
		}
		return width;
	}

	public int getFontHeight() {
		return fontHeight;
	}

	public void drawString(GuiGraphics graphics, String text, int x, int y, int color) {
		float red = ((color >> 16) & 0xFF) / 255f;
		float green = ((color >> 8) & 0xFF) / 255f;
		float blue = (color & 0xFF) / 255f;
		float alpha = ((color >> 24) & 0xFF) / 255f;
		if (alpha == 0)
			alpha = 1.0f;

		PoseStack poseStack = graphics.pose();
		poseStack.pushPose();

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderColor(red, green, blue, alpha);

		int currentX = x;
		for (char c : text.toCharArray()) {
			CharData data = getCharData(c);
			if (data.texture != null) {
				renderChar(poseStack, data, currentX, y);
			}
			currentX += data.width;
		}

		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		poseStack.popPose();
	}

	private void renderChar(PoseStack poseStack, CharData data, int x, int y) {
		if (data.texture == null)
			return;

		RenderSystem.setShaderTexture(0, data.texture.getId());

		Matrix4f matrix = poseStack.last().pose();
		BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,
				DefaultVertexFormat.POSITION_TEX_COLOR);

		float x0 = x;
		float y0 = y;
		float x1 = x + data.width;
		float y1 = y + data.height;

		buffer.addVertex(matrix, x0, y1, 0).setUv(0, 1).setColor(255, 255, 255, 255);
		buffer.addVertex(matrix, x1, y1, 0).setUv(1, 1).setColor(255, 255, 255, 255);
		buffer.addVertex(matrix, x1, y0, 0).setUv(1, 0).setColor(255, 255, 255, 255);
		buffer.addVertex(matrix, x0, y0, 0).setUv(0, 0).setColor(255, 255, 255, 255);

		BufferUploader.drawWithShader(buffer.buildOrThrow());
	}

	public void drawCenteredString(GuiGraphics graphics, String text, int x, int y, int color) {
		int width = getStringWidth(text);
		drawString(graphics, text, x - width / 2, y, color);
	}

	public void cleanup() {
		for (CharData data : charCache.values()) {
			if (data.texture != null) {
				data.texture.close();
			}
		}
		charCache.clear();
	}

	private static class CharData {
		final DynamicTexture texture;
		final int width;
		final int height;

		CharData(BufferedImage img, int width, int height) {
			this.width = width;
			this.height = height;

			NativeImage nativeImage = new NativeImage(img.getWidth(), img.getHeight(), true);
			for (int y = 0; y < img.getHeight(); y++) {
				for (int x = 0; x < img.getWidth(); x++) {
					int rgb = img.getRGB(x, y);
					nativeImage.setPixelRGBA(x, y, rgb);
				}
			}

			this.texture = new DynamicTexture(nativeImage);
			net.minecraft.client.Minecraft.getInstance().getTextureManager().register(
					ResourceLocation.fromNamespaceAndPath("nebula", "font/char_" + System.nanoTime()),
					this.texture);
		}
	}
}
