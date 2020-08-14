package net.roguelogix.phosphophyllite.gui.old;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

@Deprecated
public class GuiPartBase<T extends Container> {
    
    protected ContainerScreen<T> screen;
    protected ResourceLocation guiTexture;
    protected int xPos;
    protected int yPos;
    protected int xSize;
    protected int ySize;
    protected String tooltipText;
    
    public GuiPartBase(ContainerScreen<T> screen, ResourceLocation guiTexture, int xPos, int yPos, int xSize, int ySize, @Nullable String tooltipText) {
        this.screen = screen;
        this.guiTexture = guiTexture;
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.tooltipText = tooltipText;
    }
    
    protected void drawPart() {
        this.screen.getMinecraft().getTextureManager().bindTexture(guiTexture);
        this.screen.blit(this.xPos, this.yPos, 0, 0, xSize, ySize);
    }
    
    protected void drawTooltip(int mouseX, int mouseY) {
        if (tooltipText == null) {
            return;
        }
        if (this.isMouseHovering(mouseX, mouseY)) {
            this.screen.renderTooltip(tooltipText, mouseX, mouseY);
        }
    }
    
    protected boolean isMouseHovering(int mouseX, int mouseY) {
        if (mouseX > (this.screen.getGuiLeft() + this.xPos) && mouseX < (this.screen.getGuiLeft() + this.xPos + xSize)) {
            if (mouseY > (this.screen.getGuiTop() + this.yPos) && mouseY < (this.screen.getGuiTop() + this.yPos + ySize)) {
                return true;
            }
        }
        return false;
    }
}
