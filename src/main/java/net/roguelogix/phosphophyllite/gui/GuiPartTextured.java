package net.roguelogix.phosphophyllite.gui;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.phosphophyllite.gui.api.IHasGuiTexture;

@OnlyIn(Dist.CLIENT)
public class GuiPartTextured<T extends Container> extends GuiPartBase<T> implements IHasGuiTexture {
    
    protected ResourceLocation texture;
    protected int offsetX;
    protected int offsetY;
    
    /**
     * @param screen  The screen this instance belongs to.
     * @param xPos    The X position of the part.
     * @param yPos    The Y position of the part.
     * @param xSize   The width of the part.
     * @param ySize   The height of the part.
     * @param texture The texture (or texture map) this part should use.
     * @param offsetX The texture offset to use (x-axis).
     * @param offsetY The texture offset to use (y-axis).
     */
    public GuiPartTextured(ContainerScreen<T> screen, int xPos, int yPos, int xSize, int ySize, ResourceLocation texture, int offsetX, int offsetY) {
        super(screen, xPos, yPos, xSize, ySize);
        this.updateTexture(texture, offsetX, offsetY);
    }
    
    /**
     * Update the texture this part uses.
     *
     * @param texture The texture resource to use.
     * @param offsetX The texture offset to use (x-axis).
     * @param offsetY The texture offset to use (y-axis).
     */
    @Override
    public void updateTexture(ResourceLocation texture, int offsetX, int offsetY) {
        this.texture = texture;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
    
    /**
     * Render this element.
     */
    @Override
    public void drawPart() {
        this.screen.getMinecraft().getTextureManager().bindTexture(this.texture);
        this.screen.blit(this.xPos, this.yPos, this.offsetX, this.offsetY, this.xSize, this.ySize);
    }
}
