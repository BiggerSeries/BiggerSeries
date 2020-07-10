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

  private Fluid fluid;

  public GuiFluidTank(ContainerScreen<T> screen, int xPos, int yPos, Fluid fluid) {
    super(screen, GUI_TEXTURE, xPos, yPos, GUI_SIZE_X, GUI_SIZE_Y, null);

    this.fluid = fluid;
  }

  public void drawPart(long fluidStored, long fluidCapacity) {
    super.drawPart();

    // TODO: Modify to allow usage of any fluid texture. Currently, it's hardcoded for water and steam only.
    long textureOffset = -((fluidCapacity - fluidStored) * ySize / fluidCapacity) - 1;
    if(this.fluid == Fluids.WATER.getFluid()) {
      // Water
      this.screen.blit(this.xPos, this.yPos - 1, 36, (int) textureOffset, xSize, ySize);
    } else {
      // Steam
      this.screen.blit(this.xPos, this.yPos - 1, 54, (int) textureOffset, xSize, ySize);
    }

    this.screen.blit(this.xPos, this.yPos, 18, 0, xSize, ySize);
  }

  public void drawTooltip(int mouseX, int mouseY, long fluidStored, long fluidCapacity) {
    this.tooltipText = String.format("%d/%d mB of %s", fluidStored, fluidCapacity,
        new FluidStack(this.fluid, (int) fluidStored).getDisplayName().getFormattedText().toLowerCase());
    super.drawTooltip(mouseX, mouseY);
  }
}
