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

  public GuiEnergyTank(ContainerScreen<T> screen, int xPos, int yPos) {
    super(screen, GUI_TEXTURE, xPos, yPos, GUI_SIZE_X, GUI_SIZE_Y, null);
  }

  public void drawPart(long energyStored, long energyCapacity) {
    super.drawPart();
    long textureOffset = -((energyCapacity - energyStored) * ySize / energyCapacity) - 1;
    this.screen.blit(this.xPos, this.yPos - 1, 16, (int) textureOffset, xSize, ySize);
  }

  public void drawTooltip(int mouseX, int mouseY, long energyStored, long energyCapacity) {
    this.tooltipText = String.format("%d/%d RF", energyStored, energyCapacity);
    super.drawTooltip(mouseX, mouseY);
  }
}
