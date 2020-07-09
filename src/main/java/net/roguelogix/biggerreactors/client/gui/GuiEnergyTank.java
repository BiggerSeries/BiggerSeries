package net.roguelogix.biggerreactors.client.gui;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.gui.GuiPartBase;

@OnlyIn(Dist.CLIENT)
public class GuiEnergyTank<T extends Container> extends GuiPartBase<T> {

  private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/energy_tank.png");
  private static final int GUI_SIZE_X = 16;
  private static final int GUI_SIZE_Y = 64;

  public GuiEnergyTank(ContainerScreen<T> screen, int xPos, int yPos, boolean enableTooltip) {
    super(screen, xPos, yPos, GUI_TEXTURE, GUI_SIZE_X, GUI_SIZE_Y);
    this.enableTooltip = enableTooltip;
  }

  public void drawPart(int energyStored, int energyCapacity) {
    super.drawPart();
    long energyOffset = -((energyCapacity - energyStored) * ySize / energyCapacity) - 1;
    this.screen.blit(this.xPos, this.yPos - 1, 16, (int) energyOffset, xSize, ySize);
  }

  public void drawTooltip(int mouseX, int mouseY, int energyStored, int energyCapacity) {
    if(!this.enableTooltip) return;
    super.drawTooltip(mouseX, mouseY);
    if(this.isMouseHovering(mouseX, mouseY)) {
      this.screen.renderTooltip(String.format("%d/%d RF", energyStored, energyCapacity), mouseX, mouseY);
    }
  }
}
