package net.roguelogix.biggerreactors.client.reactor;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.gui.old.client.GuiPartBase;
import net.roguelogix.phosphophyllite.gui.old.client.GuiRenderHelper;
import net.roguelogix.phosphophyllite.gui.old.client.api.IHasTooltip;

public class GuiReactorHeatBar<T extends Container> extends GuiPartBase<T> implements IHasTooltip {
    
    private final ResourceLocation texture = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/gui_bars.png");
    private double heatStored;
    private double heatCapacity;
    
    /**
     * @param screen The screen this instance belongs to.
     * @param xPos   The X position of the part.
     * @param yPos   The Y position of the part.
     */
    public GuiReactorHeatBar(ContainerScreen<T> screen, int xPos, int yPos) {
        super(screen, xPos, yPos, 18, 64);
    }
    
    /**
     * Update the case heat values this part uses.
     *
     * @param heatStored   The amount of heat currently stored.
     * @param heatCapacity The amount of heat currently stored.
     */
    public void updateHeat(double heatStored, double heatCapacity) {
        this.heatStored = heatStored;
        this.heatCapacity = heatCapacity;
    }
    
    /**
     * Render this element.
     */
    @Override
    public void drawPart(MatrixStack mStack) {
        // Reset and bind texture.
        super.drawPart(mStack);
        GuiRenderHelper.setTexture(this.texture);
        
        // Draw background.
        GuiRenderHelper.setTextureOffset(54, 0);
        GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
        
        // Draw foreground.
        if (this.heatCapacity != 0) {
            // Determine amount to draw.
            int renderSize = (int) (this.ySize * this.heatStored / this.heatCapacity);
            
            // Draw case heat.
            GuiRenderHelper.setTextureOffset(126, 0);
            GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
            
            // Mask away empty bit.
            GuiRenderHelper.setTextureOffset(54, 0);
            GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize - renderSize);
        }
        // Draw frame.
        GuiRenderHelper.setTextureOffset(18, 0);
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
            this.screen.renderTooltip(mStack, new StringTextComponent(String.format("%.1f/%.1f \u00B0C", this.heatStored, this.heatCapacity)), mouseX, mouseY);
        }
    }
}
