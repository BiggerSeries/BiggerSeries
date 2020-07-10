package net.roguelogix.phosphophyllite.gui;

import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.roguelogix.biggerreactors.BiggerReactors;

public class GuiPartSymbol<T extends Container> extends GuiPartBase<T> {

  private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/symbols.png");
  private static final int GUI_SIZE_X = 16;
  private static final int GUI_SIZE_Y = 16;

  private int textureIndex;

  public GuiPartSymbol(ContainerScreen<T> screen, int xPos, int yPos, int textureIndex, @Nullable String tooltipText) {
    super(screen, GUI_TEXTURE, xPos, yPos, GUI_SIZE_X, GUI_SIZE_Y, tooltipText);

    this.textureIndex = textureIndex;
  }

  public void updateTextureIndex(int textureIndex) {
    this.textureIndex = textureIndex;
  }

  @Override
  public void drawPart() {
    this.screen.getMinecraft().getTextureManager().bindTexture(GUI_TEXTURE);
    this.screen.blit(this.xPos, this.yPos, (textureIndex * 16), 0, GUI_SIZE_X, GUI_SIZE_Y);
  }

  @Override
  public void drawTooltip(int mouseX, int mouseY) {
    super.drawTooltip(mouseX, mouseY);
  }
}
