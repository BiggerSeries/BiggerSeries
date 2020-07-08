package net.roguelogix.phosphophyllite.gui;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;

public class GuiPartBase<T extends Container> {

  protected final ResourceLocation guiTexture;
  protected final int xSize;
  protected final int ySize;
  protected final ContainerScreen<T> screen;
  protected final int xPos;
  protected final int yPos;
  protected boolean enableTooltip = true;

  public GuiPartBase(ContainerScreen<T> screen, int xPos, int yPos, ResourceLocation guiTexture, int xSize, int ySize) {
    this.screen = screen;
    this.xPos = xPos;
    this.yPos = yPos;
    this.guiTexture = guiTexture;
    this.xSize = xSize;
    this.ySize = ySize;
  }

  protected void drawPart() {
    this.screen.getMinecraft().getTextureManager().bindTexture(guiTexture);
    this.screen.blit(this.xPos, this.yPos, 0, 0, xSize, ySize);
  }

  protected void drawTooltip(int mouseX, int mouseY) {
  }

  protected boolean isMouseHovering(int mouseX, int mouseY) {
    if(mouseX > (this.screen.getGuiLeft() + this.xPos) && mouseX < (this.screen.getGuiLeft() + this.xPos + xSize)) {
      if(mouseY > (this.screen.getGuiTop() + this.yPos) && mouseY < (this.screen.getGuiTop() + this.yPos + ySize)) {
        return true;
      }
    }
    return false;
  }
}
