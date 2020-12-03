package net.roguelogix.biggerreactors.client.old;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.gui.old.client.GuiPartBase;
import net.roguelogix.phosphophyllite.gui.old.client.GuiRenderHelper;
import net.roguelogix.phosphophyllite.gui.old.client.api.IHasTooltip;

public class GuiFluidTank<T extends Container> extends GuiPartBase<T> implements IHasTooltip {
    
    private final ResourceLocation texture = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/gui_bars.png");
    private Fluid fluidType;
    private long fluidStored;
    private long fluidCapacity;
    
    /**
     * @param screen The screen this instance belongs to.
     * @param xPos   The X position of the part.
     * @param yPos   The Y position of the part.
     */
    public GuiFluidTank(ContainerScreen<T> screen, int xPos, int yPos) {
        super(screen, xPos, yPos, 18, 64);
    }
    
    /**
     * Update the fluid values this part uses.
     *
     * @param fluidType     The type of fluid stored.
     * @param fluidStored   The amount of fluids currently stored.
     * @param fluidCapacity The max capacity of fluids storable.
     */
    public void updateFluid(Fluid fluidType, long fluidStored, long fluidCapacity) {
        this.fluidType = fluidType;
        this.fluidStored = fluidStored;
        this.fluidCapacity = fluidCapacity;
    }
    
    /**
     * Render this element.
     */
    @Override
    public void drawPart(MatrixStack mStack) {
        // Reset and bind texture.
        super.drawPart(mStack);
        GuiRenderHelper.setTexture(this.texture);
        GuiRenderHelper.setTextureOffset(0, 0);
        
        // Draw background.
        GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
        
        // Draw foreground.
        if (this.fluidCapacity != 0) {
            // Determine amount to draw.
            int renderSize = (int) (this.ySize * this.fluidStored / this.fluidCapacity);
            
            // Gather fluid texture.
            FluidAttributes fluidAttributes = this.fluidType.getAttributes();
            TextureAtlasSprite fluidSprite = Minecraft.getInstance()
                    .getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(fluidAttributes.getStillTexture());
            
            // Draw fluid.
            GuiRenderHelper.setTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
            GuiRenderHelper.setRenderColor(fluidAttributes.getColor());
            GuiRenderHelper.drawSpriteGrid(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, 16, fluidSprite, 1, 4);
            GuiRenderHelper.clearRenderColor();
            
            // Mask away empty bit.
            GuiRenderHelper.setTexture(this.texture);
            GuiRenderHelper.setTextureOffset(0, 0);
            GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize - renderSize);
        }
        // Draw frame.
        GuiRenderHelper.setTextureOffset(18, 0);
        GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
        
        // Draw level gauge.
        GuiRenderHelper.setTextureOffset(36, 0);
        GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
    }
    
    /**
     * Perform an action on click.
     *
     * @param mouseX The cursor's X position.
     * @param mouseY The cursor's Y position.
     */
    @Override
    public void drawTooltip(MatrixStack mStack, int mouseX, int mouseY) {
        if (this.isMouseOver(mouseX, mouseY)) {
            this.screen.renderTooltip(mStack, new StringTextComponent(String.format("%d/%d mB", this.fluidStored, this.fluidCapacity)), mouseX, mouseY);
        }
    }
}
