package net.roguelogix.phosphophyllite.gui.client;

import com.google.common.collect.PeekingIterator;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.roguelogix.phosphophyllite.Phosphophyllite;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

import static com.google.common.collect.Iterators.peekingIterator;

public class RenderHelper {
    /**
     * The current/active texture (probably).
     */
    private static ResourceLocation currentResource;

    /**
     * Size prefixes (milli-, base, kilo-, mega-, giga-, tera-, peta-, exa-, zetta-, yotta-, hogi-).
     */
    public static String[] baseSizePrefixes = {"m", "", "Ki", "Me", "Gi", "Te", "Pe", "Ex", "Ze", "Yo", "Ho"};

    /**
     * Get a blank resource location texture.
     *
     * @return A completely empty texture atlas.
     */
    public static ResourceLocation getBlankResource() {
        return new ResourceLocation(Phosphophyllite.modid, "textures/blank.png");
    }

    /**
     * Return the current/active texture.
     *
     * @return The current texture, as far as RenderHelper is aware of.
     * @implNote This will only return the last texture used with RenderHelper, and may not be the ACTUAL current texture.
     */
    public static ResourceLocation getCurrentResource() {
        return RenderHelper.currentResource;
    }

    /**
     * Reset the current texture color.
     */
    public static void clearRenderColor() {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Sets the current shading color to the specified value via decimal values.
     *
     * @param color The color to shade with.
     */
    public static void setRenderColor(int color) {
        float alpha = ((color >> 24) & 0xFF) / 255F;
        float red = ((color >> 16) & 0xFF) / 255F;
        float green = ((color >> 8) & 0xFF) / 255F;
        float blue = ((color) & 0xFF) / 255F;
        RenderHelper.setRenderColor(red, green, blue, alpha);
    }

    /**
     * Sets the current shading color to the specified value via RGBA values.
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
     * Set the current texture/resource to draw.
     *
     * @param resourceLocation The texture/resource to draw.
     */
    public static void bindTexture(ResourceLocation resourceLocation) {
        Minecraft.getInstance().getTextureManager().bindTexture(resourceLocation);
        RenderHelper.currentResource = resourceLocation;
    }

    /**
     * Draw the provided texture.
     *
     * @param mStack     The matrix stack.
     * @param x          The X position to draw at.
     * @param y          The Y position to draw at.
     * @param blitOffset The blit offset to use.
     * @param width      The width of the texture.
     * @param height     The height of the texture.
     * @param sprite     The sprite to draw.
     */
    public static void drawTexture(@Nonnull MatrixStack mStack, int x, int y, int blitOffset, int width, int height, TextureAtlasSprite sprite) {
        AbstractGui.blit(mStack, x, y, blitOffset, width, height, sprite);
    }

    /**
     * Draw the provided fluid texture.
     *
     * @param mStack     The matrix stack.
     * @param x          The X position to draw at.
     * @param y          The Y position to draw at.
     * @param blitOffset The blit offset to use.
     * @param width      The width of the texture.
     * @param height     The height of the texture.
     * @param fluid      The fluid to draw.
     */
    public static void drawFluid(@Nonnull MatrixStack mStack, int x, int y, int blitOffset, int width, int height, Fluid fluid) {
        // Preserve the previously selected texture.
        ResourceLocation preservedResource = RenderHelper.getCurrentResource();
        // Bind the new texture, set the color, and draw.
        RenderHelper.bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
        RenderHelper.setRenderColor(fluid.getAttributes().getColor());
        RenderHelper.drawTexture(mStack, x, y, blitOffset, width, height,
                Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE)
                        .apply(fluid.getAttributes().getStillTexture()));
        // Reset color and restore the previously bound texture.
        RenderHelper.clearRenderColor();
        RenderHelper.bindTexture(preservedResource);
    }

    /**
     * Draw the provided texture in a repeated grid.
     *
     * @param mStack           The matrix stack.
     * @param x                The x position to draw at.
     * @param y                The y position to draw at.
     * @param blitOffset       The blit offset to use.
     * @param width            The width of the texture.
     * @param height           The height of the texture.
     * @param sprite           The sprite to draw.
     * @param horizontalRepeat How many times to repeat right, drawing in chunks of xSize.
     * @param verticalRepeat   How many times to repeat down, drawing in chunks of ySize.
     * @implNote If you need to fill an area that is NOT a multiple of xSize or ySize, it is recommended you use another draw call to mask away the extra part.
     */
    public static void drawTextureGrid(@Nonnull MatrixStack mStack, int x, int y, int blitOffset, int width, int height, TextureAtlasSprite sprite, int horizontalRepeat, int verticalRepeat) {
        for (int iX = 0; iX < horizontalRepeat; iX++) {
            for (int iY = 0; iY < verticalRepeat; iY++) {
                RenderHelper.drawTexture(mStack, x + (width * iX), y + (height * iY), blitOffset, width, height, sprite);
            }
        }
    }

    /**
     * Draw the provided fluid texture in a repeated grid.
     *
     * @param mStack     The matrix stack.
     * @param x          The x position to draw at.
     * @param y          The y position to draw at.
     * @param blitOffset The blit offset to use.
     * @param width      The width of the texture.
     * @param height     The height of the texture.
     * @param fluid      The fluid to draw.
     * @param xRepeat    How many times to repeat right, drawing in chunks of xSize.
     * @param yRepeat    How many times to repeat down, drawing in chunks of ySize.
     * @implNote If you need to fill an area that is NOT a multiple of xSize or ySize, it is recommended you use another draw call to mask away the extra part.
     */
    public static void drawFluidGrid(@Nonnull MatrixStack mStack, int x, int y, int blitOffset, int width, int height, Fluid fluid, int xRepeat, int yRepeat) {
        for (int iX = 0; iX < xRepeat; iX++) {
            for (int iY = 0; iY < yRepeat; iY++) {
                RenderHelper.drawFluid(mStack, x + (width * iX), y + (height * iY), blitOffset, width, height, fluid);
            }
        }
    }

    /**
     * Draw the provided fluid texture.
     *
     * @param mStack     The matrix stack.
     * @param x          The X position to draw at.
     * @param y          The Y position to draw at.
     * @param blitOffset The blit offset to use.
     * @param width      The width of the texture.
     * @param height     The height of the texture.
     * @param u          The u offset in the current texture to use as a mask/draw on top.
     * @param v          The v offset in the current texture to use as a mask/draw on top.
     * @param fluid      The fluid to draw.
     */
    public static void drawMaskedFluid(@Nonnull MatrixStack mStack, int x, int y, int blitOffset, int width, int height, int u, int v, Fluid fluid) {
        // Draw the fluid.
        RenderHelper.drawFluid(mStack, x, y, blitOffset, width, height, fluid);
        // Draw frame/mask, or the lightning bolt icon.
        // I have now noticed that Mojang went (x, y, u, v, w, h), while I did (x, y, w, h, u, v). And no, I won't change mine, because it'll be a pain to change every call.
        AbstractGui.blit(mStack, x, y, u, v, width, height, 256, 256);
    }

    /**
     * Draw the provided fluid texture in a repeated grid.
     *
     * @param mStack     The matrix stack.
     * @param x          The x position to draw at.
     * @param y          The y position to draw at.
     * @param blitOffset The blit offset to use.
     * @param width      The width of the texture.
     * @param height     The height of the texture.
     * @param u          The u offset in the current texture to use as a mask/draw on top.
     * @param v          The v offset in the current texture to use as a mask/draw on top.
     * @param fluid      The fluid to draw.
     * @param xRepeat    How many times to repeat right, drawing in chunks of xSize.
     * @param yRepeat    How many times to repeat down, drawing in chunks of ySize.
     * @implNote If you need to fill an area that is NOT a multiple of xSize or ySize, it is recommended you use another draw call to mask away the extra part.
     */
    public static void drawMaskedFluidGrid(@Nonnull MatrixStack mStack, int x, int y, int blitOffset, int width, int height, int u, int v, Fluid fluid, int xRepeat, int yRepeat) {
        for (int iX = 0; iX < xRepeat; iX++) {
            for (int iY = 0; iY < yRepeat; iY++) {
                RenderHelper.drawMaskedFluid(mStack, x + (width * iX), y + (height * iY), blitOffset, width, height, u, v, fluid);
            }
        }
    }

    /**
     * Format a value to be human-readable.
     * See https://stackoverflow.com/a/3758880.
     *
     * @param value  The value to humanize. This should be in base units.
     * @param suffix The suffix to use/modify. If null, no suffix will appear.
     * @return A string containing the shortened value and the appropriate suffix.
     */
    public static String formatValue(double value, @Nullable String suffix) {
        return RenderHelper.formatValue(value, suffix, false);
    }

    /**
     * Format a value to be human-readable.
     * See https://stackoverflow.com/a/3758880.
     *
     * @param value            The value to humanize. This should be in base units.
     * @param suffix           The suffix to use/modify. If null, no suffix will appear.
     * @param allowMilliSuffix Which unit prefix to treat as "lowest." True uses milli-, false uses base.
     * @return A string containing the shortened value and the appropriate suffix.
     */
    public static String formatValue(double value, @Nullable String suffix, boolean allowMilliSuffix) {
        return RenderHelper.formatValue(value, 1, suffix, allowMilliSuffix);
    }

    /**
     * Format a value to be human-readable.
     * See https://stackoverflow.com/a/3758880.
     *
     * @param value            The value to humanize. This should be in base units.
     * @param precision        How many decimals to use.
     * @param suffix           The suffix to use/modify. If null, no suffix will appear.
     * @param allowMilliSuffix Which unit prefix to treat as "lowest." True uses milli-, false uses base.
     * @return A string containing the shortened value and the appropriate suffix.
     */
    public static String formatValue(double value, int precision, @Nullable String suffix, boolean allowMilliSuffix) {
        // Get the suffix iterator.
        PeekingIterator<String> suffixIter = peekingIterator(Arrays.stream(RenderHelper.baseSizePrefixes).iterator());

        // If the value is less than one, we can use the first suffix (milli-, must be enabled via allowMilliSuffix).
        if (Math.abs(value) < 1 && allowMilliSuffix) {
            if (suffix != null) {
                return String.format("%." + precision + "f %s", (value * 1000.0), (suffixIter.peek() + suffix));
            } else {
                return String.format("%." + precision + "f", (value * 1000.0));
            }
        }

        // If the value is less than a thousand, we can use the second suffix (base).
        suffixIter.next();
        if (Math.abs(value) < 1000) {
            if (suffix != null) {
                return String.format("%." + precision + "f %s", value, (suffixIter.peek() + suffix));
            } else {
                return String.format("%." + precision + "f", value);
            }
        }

        // If the value is larger, then we iterate and use the rest of the suffixes.
        suffixIter.next();
        while (Math.abs(value) >= 999_950 && suffixIter.hasNext()) {
            value /= 1000;
            suffixIter.next();
        }

        // Return the value and new suffix.
        if (suffix != null) {
            // Append the suffix.
            return String.format("%." + precision + "f %s", (value / 1000.0), (suffixIter.peek() + suffix));
        } else {
            // Do not append the suffix.
            return String.format("%." + precision + "f", (value / 1000.0));
        }
    }
}
