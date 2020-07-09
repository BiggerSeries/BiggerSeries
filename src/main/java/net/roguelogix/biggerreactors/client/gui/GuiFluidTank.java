package net.roguelogix.biggerreactors.client.gui;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.gui.GuiPartBase;

@OnlyIn(Dist.CLIENT)
public class GuiFluidTank<T extends Container> extends GuiPartBase<T> {

  private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(BiggerReactors.modid,
      "textures/screen/parts/fluid_tank.png");
  private static final int GUI_SIZE_X = 18;
  private static final int GUI_SIZE_Y = 64;
  private Fluid fluidType;

  public GuiFluidTank(ContainerScreen<T> screen, int xPos, int yPos, Fluid fluidType,
      boolean enableTooltip) {
    super(screen, xPos, yPos, GUI_TEXTURE, GUI_SIZE_X, GUI_SIZE_Y);
    this.fluidType = fluidType;
    this.enableTooltip = enableTooltip;
  }

  public void drawPart(int fluidStored, int fluidCapacity) {
    super.drawPart();

    // TODO: Modify to allow usage of any fluid texture. Currently, it's hardcoded for water and steam only.

    long fluidOffset = -((fluidCapacity - fluidStored) * ySize / fluidCapacity) - 1;
    if(this.fluidType == Fluids.WATER.getFluid()) {
      // Water
      this.screen.blit(this.xPos, this.yPos - 1, 36, (int) fluidOffset, xSize, ySize);
    } else {
      // Steam
      this.screen.blit(this.xPos, this.yPos - 1, 54, (int) fluidOffset, xSize, ySize);
    }

    // Draw fill level marks.
    this.screen.blit(this.xPos, this.yPos, 18, 0, xSize, ySize);
  }

  public void drawTooltip(int mouseX, int mouseY, int fluidStored, int fluidCapacity) {
    if (!this.enableTooltip) {
      return;
    }
    super.drawTooltip(mouseX, mouseY);
    if (this.isMouseHovering(mouseX, mouseY)) {
      this.screen.renderTooltip(String.format("%d/%d mB of %s", fluidStored, fluidCapacity,
          new FluidStack(fluidType, fluidStored).getDisplayName().getFormattedText().toLowerCase()),
          mouseX, mouseY);
    }
  }
}
