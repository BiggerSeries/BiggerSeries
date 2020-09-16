package net.roguelogix.biggerreactors.classic.machine.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.machine.containers.CyaniteReprocessorContainer;
import net.roguelogix.biggerreactors.classic.machine.state.CyaniteReprocessorState;
import net.roguelogix.biggerreactors.client.GuiEnergyTank;
import net.roguelogix.biggerreactors.client.GuiFluidTank;
import net.roguelogix.biggerreactors.client.GuiProgressBar;
import net.roguelogix.phosphophyllite.gui.client.GuiScreenBase;

@OnlyIn(Dist.CLIENT)
public class CyaniteReprocessorScreen extends GuiScreenBase<CyaniteReprocessorContainer> implements IHasContainer<CyaniteReprocessorContainer> {
    
    private CyaniteReprocessorState cyaniteReprocessorState;
    
    private GuiProgressBar<CyaniteReprocessorContainer> progressBar;
    private GuiEnergyTank<CyaniteReprocessorContainer> energyTank;
    private GuiFluidTank<CyaniteReprocessorContainer> waterTank;
    
    public CyaniteReprocessorScreen(CyaniteReprocessorContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        // Set textures.
        this.xSize = 246;
        this.ySize = 175;
        this.updateTexture(new ResourceLocation(BiggerReactors.modid, "textures/screen/cyanite_reprocessor.png"), 0, 0);
        // Initialize machine bars.
        this.progressBar = new GuiProgressBar<>(this, 75, 41);
        this.energyTank = new GuiEnergyTank<>(this, 148, 16);
        this.waterTank = new GuiFluidTank<>(this, 8, 16);
    }
    
    /**
     * Update logic.
     */
    @Override
    public void tick() {
        this.cyaniteReprocessorState = (CyaniteReprocessorState) this.getContainer().getGuiPacket();
        this.progressBar.updateWorkTime(cyaniteReprocessorState.workTime, cyaniteReprocessorState.workTimeTotal);
        this.energyTank.updateEnergy(cyaniteReprocessorState.energyStored, cyaniteReprocessorState.energyCapacity);
        this.waterTank.updateFluid(Fluids.WATER.getFluid(), cyaniteReprocessorState.waterStored, cyaniteReprocessorState.waterCapacity);
    }
    
    /**
     * Render tooltips.
     *
     * @param mouseX       X position of the mouse.
     * @param mouseY       Y position of the mouse.
     * @param partialTicks Good question.
     */
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(stack, mouseX, mouseY);
        
        this.energyTank.drawTooltip(stack, mouseX, mouseY);
        this.waterTank.drawTooltip(stack, mouseX, mouseY);
    }
    
    /**
     * Draw foreground elements.
     *
     * @param mouseX X position of the mouse.
     * @param mouseY Y position of the mouse.
     */
    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack stack, int mouseX, int mouseY) {
        this.font.drawString(stack, new TranslationTextComponent("screen.biggerreactors.cyanite_reprocessor").getUnformattedComponentText(), 8, (this.ySize - 168), 4210752);
        this.font.drawString(stack, this.playerInventory.getDisplayName().getUnformattedComponentText(), 8, (this.ySize - 94), 4210752);
        
        this.progressBar.drawPart(stack);
        this.energyTank.drawPart(stack);
        this.waterTank.drawPart(stack);
    }
}
