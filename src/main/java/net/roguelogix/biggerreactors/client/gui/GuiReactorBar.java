package net.roguelogix.biggerreactors.client.gui;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.gui.GuiPartBase;

@OnlyIn(Dist.CLIENT)
public class GuiReactorBar<T extends Container> extends GuiPartBase<T> {

  private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/reactor_bars.png");
  private static final int GUI_SIZE_X = 16;
  private static final int GUI_SIZE_Y = 64;

  private int textureIndex;

  public GuiReactorBar(ContainerScreen<T> screen, int xPos, int yPos, int textureIndex) {
    super(screen, GUI_TEXTURE, xPos, yPos, GUI_SIZE_X, GUI_SIZE_Y, null);

    this.textureIndex = textureIndex;
  }

  public void updateTextureIndex(int textureIndex) {
    this.textureIndex = textureIndex;
  }

  public void drawPart(long valueStored, long valueCapacity) {
    super.drawPart();
    long textureOffset = -((valueCapacity - valueStored) * ySize / valueCapacity) - 1;
    this.screen.blit(this.xPos, this.yPos - 1, (textureIndex * 16), (int) textureOffset, xSize, ySize);
  }

  public void drawTooltip(int mouseX, int mouseY, long valueStored, long valueCapacity) {
    this.tooltipText = String.format("%d/%d", valueStored, valueCapacity);
    super.drawTooltip(mouseX, mouseY);
  }
}
