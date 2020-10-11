package net.roguelogix.biggerreactors.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.gui.client.GuiPartBase;
import net.roguelogix.phosphophyllite.gui.client.GuiRenderHelper;

public class GuiProgressBar<T extends Container> extends GuiPartBase<T> {
    
    private final ResourceLocation texture = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/gui_bars.png");
    private int workTime;
    private int workTimeTotal;
    
    /**
     * @param screen The screen this instance belongs to.
     * @param xPos   The X position of the part.
     * @param yPos   The Y position of the part.
     */
    public GuiProgressBar(ContainerScreen<T> screen, int xPos, int yPos) {
        super(screen, xPos, yPos, 24, 18);
    }
    
    /**
     * Update the energy values this part uses.
     *
     * @param workTime      The amount of energy currently stored.
     * @param workTimeTotal The max capacity of energy storable.
     */
    public void updateWorkTime(int workTime, int workTimeTotal) {
        this.workTime = workTime;
        this.workTimeTotal = workTimeTotal;
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
        GuiRenderHelper.setTextureOffset(0, 64);
        GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
        
        // Draw foreground.
        if (this.workTimeTotal != 0) {
            // Determine amount to draw.
            int renderSize = this.xSize * this.workTime / this.workTimeTotal;
            
            // Draw work.
            GuiRenderHelper.setTextureOffset(25, 64);
            GuiRenderHelper.draw(mStack, this.xPos + 1, this.yPos, this.screen.getBlitOffset(), renderSize, this.ySize);
        }
    }
}
