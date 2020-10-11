package net.roguelogix.biggerreactors.client.controlrod;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.gui.client.GuiPartBase;
import net.roguelogix.phosphophyllite.gui.client.GuiRenderHelper;
import net.roguelogix.phosphophyllite.gui.client.api.IHasTooltip;

public class GuiRodLevelBar<T extends Container> extends GuiPartBase<T> implements IHasTooltip {
    
    private final ResourceLocation texture = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/gui_bars.png");
    private double insertionLevel;
    
    /**
     * @param screen The screen this instance belongs to.
     * @param xPos   The X position of the part.
     * @param yPos   The Y position of the part.
     */
    public GuiRodLevelBar(ContainerScreen<T> screen, int xPos, int yPos) {
        super(screen, xPos, yPos, 18, 64);
    }
    
    /**
     * Update the insertion level this part uses.
     *
     * @param insertionLevel The current insertion level.
     */
    public void updateInsertion(double insertionLevel) {
        this.insertionLevel = insertionLevel;
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
        GuiRenderHelper.setTextureOffset(0, 82);
        GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
        
        // Draw foreground.
        // Determine amount to draw.
        int renderSize = (int) (this.ySize * this.insertionLevel / 100D);
    
        // Draw energy.
        GuiRenderHelper.setTextureOffset(36, 82);
        GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
    
        // Mask away empty bit.
        GuiRenderHelper.setTextureOffset(0, 82);
        GuiRenderHelper.draw(mStack, this.xPos, this.yPos + renderSize, this.screen.getBlitOffset(), this.xSize, this.ySize - renderSize);
        
        // Draw frame.
        GuiRenderHelper.setTextureOffset(18, 82);
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
            this.screen.renderTooltip(mStack, new StringTextComponent(String.format("%.1f%%", this.insertionLevel)), mouseX, mouseY);
        }
    }
}