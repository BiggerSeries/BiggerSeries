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
  private int textureOffset = 0;

  public GuiReactorBar(ContainerScreen<T> screen, int xPos, int yPos, int textureIndex, boolean enableTooltip) {
    super(screen, xPos, yPos, GUI_TEXTURE, GUI_SIZE_X, GUI_SIZE_Y);
    this.textureOffset = 16 + textureIndex;
    this.enableTooltip = enableTooltip;
  }

  // TODO: Generalize this.

  public void drawPart(int valueStored, int valueCapacity) {
    super.drawPart();
    long valueOffset = -((valueCapacity - valueStored) * ySize / valueCapacity) - 1;
    this.screen.blit(this.xPos, this.yPos - 1, textureOffset, (int) valueOffset, xSize, ySize);
  }

  public void drawTooltip(int mouseX, int mouseY, int valueStored, int valueCapacity) {
    if(!this.enableTooltip) return;
    super.drawTooltip(mouseX, mouseY);
    if(this.isMouseHovering(mouseX, mouseY)) {
      this.screen.renderTooltip(String.format("%d/%d", valueStored, valueCapacity), mouseX, mouseY);
    }
  }
}
