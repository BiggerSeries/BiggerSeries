package net.roguelogix.phosphophyllite.gui.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public class GuiRenderHelper extends AbstractGui {
    
    private static int xOffset = 0;
    private static int yOffset = 0;
    
    /**
     * Reset the current shade color.
     */
    public static void clearRenderColor() {
        GuiRenderHelper.setRenderColor(1, 1, 1, 1);
    }
    
    /**
     * Sets the current shading color to the specified value.
     *
     * @param color The color to shade with.
     */
    public static void setRenderColor(int color) {
        float alpha = ((color >> 24) & 0xFF) / 255F;
        float red = ((color >> 16) & 0xFF) / 255F;
        float green = ((color >> 8) & 0xFF) / 255F;
        float blue = ((color) & 0xFF) / 255F;
        GuiRenderHelper.setRenderColor(red, green, blue, alpha);
    }
    
    /**
     * Sets the current shading color to the specified value.
     *
     * @param red   The amount of red value to shade.
     * @param blue  The amount of blue value to shade.
     * @param green The amount of green value to shade.
     * @param alpha The amount of alpha/transparency value to shade.
     */
    public static void setRenderColor(float red, float green, float blue, float alpha) {
        RenderSystem.color4f(red, green, blue, alpha);
    }
    
    /**
     * Set the texture/resource to draw.
     *
     * @param resourceLocation The texture/resource to draw.
     */
    public static void setTexture(ResourceLocation resourceLocation) {
        Minecraft.getInstance().getTextureManager().bindTexture(resourceLocation);
    }
    
    /**
     * Set the texture position within the main texture atlas. Useful if you have multiple textures in one file.
     *
     * @param xOffset The upper-left X position of the texture.
     * @param yOffset The upper-left Y position of the texture.
     */
    public static void setTextureOffset(int xOffset, int yOffset) {
        GuiRenderHelper.xOffset = xOffset;
        GuiRenderHelper.yOffset = yOffset;
    }
    
    /**
     * Draw a texture. Atlas size is assumed to be 256x256.
     *
     * @param mStack     The matrix stack.
     * @param xPos       The X position to draw at.
     * @param yPos       The Y position to draw at.
     * @param blitOffset The blit offset to use.
     * @param xSize      The width of the texture.
     * @param ySize      The height of the texture.
     */
    public static void draw(MatrixStack mStack, int xPos, int yPos, int blitOffset, int xSize, int ySize) {
        GuiRenderHelper.draw(mStack, xPos, yPos, blitOffset, xSize, ySize, 256, 256);
    }
    
    /**
     * Draw a texture with a custom atlas size.
     *
     * @param mStack     The matrix stack.
     * @param xPos       The X position to draw at.
     * @param yPos       The Y position to draw at.
     * @param blitOffset The blit offset to use.
     * @param xSize      The width of the texture.
     * @param ySize      The height of the texture.
     * @param xAtlasSize The width of the atlas.
     * @param yAtlasSize The height of the atlas.
     */
    public static void draw(MatrixStack mStack, int xPos, int yPos, int blitOffset, int xSize, int ySize, int xAtlasSize, int yAtlasSize) {
        AbstractGui.blit(mStack, xPos, yPos, blitOffset, xOffset, yOffset, xSize, ySize, xAtlasSize, yAtlasSize);
    }
    
    /**
     * Draw a sprite from a texture atlas.
     *
     * @param mStack     The matrix stack.
     * @param xPos       The X position to draw at.
     * @param yPos       The Y position to draw at.
     * @param blitOffset The blit offset to use.
     * @param xSize      The width of the texture.
     * @param ySize      The height of the texture.
     * @param sprite     The sprite to draw.
     */
    public static void drawSprite(MatrixStack mStack, int xPos, int yPos, int blitOffset, int xSize, int ySize, TextureAtlasSprite sprite) {
        AbstractGui.blit(mStack, xPos, yPos, blitOffset, xSize, ySize, sprite);
    }
    
    /**
     * Draw a sprite from a texture atlas in a grid.
     *
     * @param xPos       The X position to draw at.
     * @param yPos       The Y position to draw at.
     * @param blitOffset The blit offset to use.
     * @param xSize      The width of the texture.
     * @param ySize      The height of the texture.
     * @param sprite     The sprite to draw.
     * @param repeatX    How many times to repeat right, drawing in chunks of xSize.
     * @param repeatY    How many times to repeat down, drawing in chunks of ySize.
     */
    public static void drawSpriteGrid(MatrixStack mStack, int xPos, int yPos, int blitOffset, int xSize, int ySize, TextureAtlasSprite sprite, int repeatX, int repeatY) {
        for (int iX = 0; iX < repeatX; iX++) {
            for (int iY = 0; iY < repeatY; iY++) {
                GuiRenderHelper.drawSprite(mStack, xPos + (xSize * iX), yPos + (ySize * iY), blitOffset, xSize, ySize, sprite);
            }
        }
    }
}
