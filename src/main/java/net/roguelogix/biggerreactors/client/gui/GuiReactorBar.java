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

  private int textureIndex;

  public GuiReactorBar(ContainerScreen<T> screen, int xPos, int yPos, int textureIndex) {
    super(screen, GUI_TEXTURE, xPos, yPos, 16, 64, null);

    this.textureIndex = textureIndex;
  }

  public void updateTextureIndex(int textureIndex) {
    this.textureIndex = textureIndex;
  }

  public void drawPart(long valueStored, long valueCapacity) {
    super.drawPart();

    long textureOffset = valueStored * (this.ySize + 1) / valueCapacity;
    int relativeY = (int) (this.yPos + this.ySize - textureOffset);
    int textureY = (int) (this.ySize - textureOffset);
    this.screen.blit(this.xPos, relativeY, (textureIndex * 16), textureY, this.xSize, (int) (textureOffset));
  }

  public void drawTooltip(int mouseX, int mouseY, long valueStored, long valueCapacity) {
    this.tooltipText = String.format("%d/%d", valueStored, valueCapacity);
    super.drawTooltip(mouseX, mouseY);
  }
}
