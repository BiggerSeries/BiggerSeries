package net.roguelogix.biggerreactors.classic.blocks.client;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.blocks.CyaniteReprocessorContainer;
import net.roguelogix.biggerreactors.client.gui.GuiEnergyTank;
import net.roguelogix.biggerreactors.client.gui.GuiFluidTank;
import net.roguelogix.biggerreactors.client.gui.GuiProgressBar;
import net.roguelogix.phosphophyllite.gui.GuiScreenBase;

@OnlyIn(Dist.CLIENT)
public class CyaniteReprocessorScreen extends GuiScreenBase<CyaniteReprocessorContainer> implements IHasContainer<CyaniteReprocessorContainer> {
    
    private GuiProgressBar<CyaniteReprocessorContainer> progressBar;
    private GuiEnergyTank<CyaniteReprocessorContainer> energyTank;
    private GuiFluidTank<CyaniteReprocessorContainer> waterTank;
    
    public CyaniteReprocessorScreen(CyaniteReprocessorContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.xSize = 246;
        this.ySize = 175;
        
        this.updateTexture(new ResourceLocation(BiggerReactors.modid, "textures/screen/cyanite_reprocessor.png"), 0, 0);
        this.progressBar = new GuiProgressBar<>(this, 75, 41);
        this.energyTank = new GuiEnergyTank<>(this, 148, 16);
        this.waterTank = new GuiFluidTank<>(this, 8, 16);
    }
    
    @Override
    public void tick() {
        this.progressBar.updateWorkTime(this.getContainer().getWorkTime(), this.getContainer().getWorkTimeTotal());
        this.energyTank.updateEnergy(this.getContainer().getEnergyStored(), this.getContainer().getEnergyCapacity());
        this.waterTank.updateFluid(Fluids.WATER.getFluid(), this.getContainer().getFluidStored(), this.getContainer().getFluidCapacity());
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        
        this.energyTank.drawTooltip(mouseX, mouseY);
        this.waterTank.drawTooltip(mouseX, mouseY);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(this.title.getFormattedText(), 8.0F, (float) (this.ySize - 168), 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 94), 4210752);
        
        // Draw machine gauges.
        this.progressBar.drawPart();
        this.energyTank.drawPart();
        this.waterTank.drawPart();
    }
}
