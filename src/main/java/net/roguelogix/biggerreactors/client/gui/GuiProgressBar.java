package net.roguelogix.biggerreactors.client.gui;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.gui.GuiPartTextured;

public class GuiProgressBar<T extends Container> extends GuiPartTextured<T> {
    
    private int workTime;
    private int workTimeTotal;
    
    /**
     * @param screen The screen this instance belongs to.
     * @param xPos   The X position of the part.
     * @param yPos   The Y position of the part.
     */
    public GuiProgressBar(ContainerScreen<T> screen, int xPos, int yPos) {
        super(screen, xPos, yPos, 25, 16,
                new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/progress_bar.png"),
                0, 0);
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
    public void drawPart() {
        // Bind texture.
        this.screen.getMinecraft().getTextureManager().bindTexture(this.texture);
        // Draw background.
        this.updateTexture(this.texture, 0, 0);
        super.drawPart();
        // Draw foreground.
        this.updateTexture(this.texture, 25, 0);
        if (this.workTime != 0) {
            int renderSize = this.xSize * this.workTime / this.workTimeTotal;
            int renderPos = this.xPos + (this.xSize - (renderSize));
            if (this.workTime != this.workTimeTotal) {
                --renderPos;
            }
            this.screen.blit(renderPos, this.yPos, this.offsetX, this.offsetY, renderPos, this.ySize);
        }
    }
}
