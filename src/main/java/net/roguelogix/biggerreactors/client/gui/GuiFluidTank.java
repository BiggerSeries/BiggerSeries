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
    
    private Fluid fluid;
    
    public GuiFluidTank(ContainerScreen<T> screen, int xPos, int yPos, Fluid fluid) {
        super(screen, GUI_TEXTURE, xPos, yPos, 18, 64, null);
        
        this.fluid = fluid;
    }
    
    public void drawPart(long fluidStored, long fluidCapacity) {
        super.drawPart();
        
        // TODO: Modify to allow usage of any fluid texture. Currently, it's hardcoded for water and steam only.
        long textureOffset = fluidStored * (this.ySize + 1) / fluidCapacity;
        int relativeY = (int) (this.yPos + this.ySize - textureOffset);
        int textureY = (int) (this.ySize - textureOffset);
        
        if (this.fluid == Fluids.WATER.getFluid()) {
            // Water
            this.screen.blit(this.xPos, relativeY, 36, textureY, this.xSize, (int) textureOffset);
        } else {
            // Steam
            this.screen.blit(this.xPos, relativeY, 54, textureY, this.xSize, (int) textureOffset);
        }
        
        this.screen.blit(this.xPos, this.yPos, 18, 0, this.xSize, this.ySize);
    }
    
    public void drawTooltip(int mouseX, int mouseY, long fluidStored, long fluidCapacity) {
        this.tooltipText = String.format("%d/%d mB of %s", fluidStored, fluidCapacity,
                this.fluid.getAttributes().getDisplayName(null).getFormattedText().toLowerCase());
        super.drawTooltip(mouseX, mouseY);
    }
}
