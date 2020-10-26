package net.roguelogix.biggerreactors.classic.turbine.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.turbine.containers.TurbineContainer;
import net.roguelogix.biggerreactors.classic.turbine.state.TurbineActivity;
import net.roguelogix.biggerreactors.classic.turbine.state.TurbineState;
import net.roguelogix.biggerreactors.client.GuiEnergyTank;
import net.roguelogix.biggerreactors.client.GuiFluidTank;
import net.roguelogix.biggerreactors.client.GuiSymbol;
import net.roguelogix.biggerreactors.client.turbine.*;
import net.roguelogix.biggerreactors.fluids.FluidIrradiatedSteam;
import net.roguelogix.phosphophyllite.gui.client.GuiScreenBase;

public class TurbineScreen extends GuiScreenBase<TurbineContainer> implements IHasContainer<TurbineContainer> {
    
    private TurbineState turbineState;
    
    // Buttons.
    private GuiTurbineActivityToggle<TurbineContainer> turbineActivityToggle;
    private GuiTurbineVentStateToggle<TurbineContainer> turbineVentStateToggle;
    private GuiTurbineCoilToggle<TurbineContainer> turbineCoilToggle;
    private GuiTurbineFlowIncreaseButton<TurbineContainer> turbineFlowIncrease;
    private GuiTurbineFlowDecreaseButton<TurbineContainer> turbineFlowDecrease;
    
    // Turbine bars.
    private GuiTurbineTachometerBar<TurbineContainer> barTachometer;
    private GuiFluidTank<TurbineContainer> intakeTank;
    private GuiFluidTank<TurbineContainer> exhaustTank;
    private GuiEnergyTank<TurbineContainer> energyTank;
    
    // Turbine bar symbols.
    private GuiSymbol<TurbineContainer> symbolTachometer;
    private GuiSymbol<TurbineContainer> symbolIntakeTank;
    private GuiSymbol<TurbineContainer> symbolExhaustTank;
    private GuiSymbol<TurbineContainer> symbolEnergyTank;
    
    // Turbine information symbols.
    private GuiSymbol<TurbineContainer> symbolTachometerReadout;
    private GuiSymbol<TurbineContainer> symbolGovernor;
    private GuiSymbol<TurbineContainer> symbolTurbineOutput;
    private GuiSymbol<TurbineContainer> symbolRotorEfficiency;
    
    public TurbineScreen(TurbineContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.turbineState = (TurbineState) this.getContainer().getGuiPacket();
        
        // Set textures.
        this.xSize = 176;
        this.ySize = 186;
        this.updateTexture(new ResourceLocation(BiggerReactors.modid, "textures/screen/terminal.png"), 0, 0);
        
        // Initialize buttons.
        this.turbineActivityToggle = new GuiTurbineActivityToggle<>(this, 5, 129, 16, 16);
        this.turbineVentStateToggle = new GuiTurbineVentStateToggle<>(this, 5, 147, 16, 16);
        this.turbineCoilToggle = new GuiTurbineCoilToggle<>(this, 23, 147, 16, 16);
        this.turbineFlowIncrease = new GuiTurbineFlowIncreaseButton<>(this, 5, 165, 16, 16);
        this.turbineFlowDecrease = new GuiTurbineFlowDecreaseButton<>(this, 23, 165, 16, 16);
        
        // Initialize turbine bars.
        this.barTachometer = new GuiTurbineTachometerBar<>(this, 88, 22);
        this.intakeTank = new GuiFluidTank<>(this, 110, 22);
        this.exhaustTank = new GuiFluidTank<>(this, 132, 22);
        this.energyTank = new GuiEnergyTank<>(this, 154, 22);
        
        // Initialize turbine bar symbols.
        this.symbolTachometer = new GuiSymbol<>(this, 89, 5, 0, 32,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.turbine.tachometer").getString());
        this.symbolIntakeTank = new GuiSymbol<>(this, 111, 5, 48, 32,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.turbine.intake_tank").getString());
        this.symbolExhaustTank = new GuiSymbol<>(this, 133, 5, 32, 32,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.turbine.exhaust_tank").getString());
        this.symbolEnergyTank = new GuiSymbol<>(this, 155, 5, 96, 32,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.turbine.energy_tank").getString());
        
        // Initialize turbine information symbols.
        this.symbolTachometerReadout = new GuiSymbol<>(this, 7, 18, 0, 32,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.turbine.tachometer").getString());
        this.symbolTurbineOutput = new GuiSymbol<>(this, 7, 39, 80, 32,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.turbine.output").getString());
        this.symbolGovernor = new GuiSymbol<>(this, 7, 59, 112, 32,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.turbine.governor").getString());
        this.symbolRotorEfficiency = new GuiSymbol<>(this, 7, 80, 16, 32,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.turbine.rotor_efficiency").getString());
    }
    
    /**
     * Handle a key press.
     *
     * @param keyCode   The ASCII keycode for the press.
     * @param scanCode  The scancode for the press.
     * @param modifiers Any modifiers being held.
     * @return Whether or not the press was consumed.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);
        this.turbineFlowIncrease.keyPressed(keyCode, scanCode, modifiers);
        this.turbineFlowDecrease.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }
    
    /**
     * Handle a mouse click.
     *
     * @param mouseX The mouse X position.
     * @param mouseY The mouse Y position.
     * @param button The mouse button pressed.
     * @return Whether or not the press was consumed.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        this.turbineActivityToggle.mouseClicked(mouseX, mouseY, button);
        this.turbineVentStateToggle.mouseClicked(mouseX, mouseY, button);
        this.turbineCoilToggle.mouseClicked(mouseX, mouseY, button);
        this.turbineFlowIncrease.mouseClicked(mouseX, mouseY, button);
        this.turbineFlowDecrease.mouseClicked(mouseX, mouseY, button);
        return true;
    }
    
    /**
     * Handle a mouse release.
     *
     * @param mouseX The mouse X position.
     * @param mouseY The mouse Y position.
     * @param button The mouse button released.
     * @return Whether or not the release was consumed.
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        this.turbineFlowIncrease.mouseReleased(mouseX, mouseY, button);
        this.turbineFlowDecrease.mouseReleased(mouseX, mouseY, button);
        return true;
    }
    
    /**
     * Update logic.
     */
    @Override
    public void tick() {
        this.turbineState = (TurbineState) this.getContainer().getGuiPacket();
        
        // Update buttons (that require it).
        this.turbineActivityToggle.updateState(turbineState.turbineActivity);
        this.turbineVentStateToggle.updateState(turbineState.ventState);
        this.turbineCoilToggle.updateState(turbineState.coilStatus);
        this.turbineFlowIncrease.updateState(turbineState.flowRate);
        this.turbineFlowDecrease.updateState(turbineState.flowRate);
        
        // Update turbine bars.
        this.barTachometer.updateRPM(turbineState.currentRPM, turbineState.maxRPM);
        this.intakeTank.updateFluid(FluidIrradiatedSteam.INSTANCE.getFluid(), turbineState.intakeStored, turbineState.intakeCapacity);
        this.exhaustTank.updateFluid(Fluids.WATER.getFluid(), turbineState.exhaustStored, turbineState.exhaustCapacity);
        this.energyTank.updateEnergy(turbineState.energyStored, turbineState.energyCapacity);
    }
    
    /**
     * Render tooltips.
     *
     * @param mouseX       X position of the mouse.
     * @param mouseY       Y position of the mouse.
     * @param partialTicks Good question.
     */
    @Override
    public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(mStack);
        super.render(mStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(mStack, mouseX, mouseY);
        
        // Draw buttons.
        this.turbineActivityToggle.drawTooltip(mStack, mouseX, mouseY);
        this.turbineVentStateToggle.drawTooltip(mStack, mouseX, mouseY);
        this.turbineCoilToggle.drawTooltip(mStack, mouseX, mouseY);
        this.turbineFlowIncrease.drawTooltip(mStack, mouseX, mouseY);
        this.turbineFlowDecrease.drawTooltip(mStack, mouseX, mouseY);
        
        // Draw turbine bars.
        this.barTachometer.drawTooltip(mStack, mouseX, mouseY);
        this.intakeTank.drawTooltip(mStack, mouseX, mouseY);
        this.exhaustTank.drawTooltip(mStack, mouseX, mouseY);
        this.energyTank.drawTooltip(mStack, mouseX, mouseY);
        
        // Draw turbine bar symbols.
        this.symbolTachometer.drawTooltip(mStack, mouseX, mouseY);
        this.symbolIntakeTank.drawTooltip(mStack, mouseX, mouseY);
        this.symbolExhaustTank.drawTooltip(mStack, mouseX, mouseY);
        this.symbolEnergyTank.drawTooltip(mStack, mouseX, mouseY);
        
        // Draw turbine information symbols.
        this.symbolTachometerReadout.drawTooltip(mStack, mouseX, mouseY);
        this.symbolTurbineOutput.drawTooltip(mStack, mouseX, mouseY);
        this.symbolGovernor.drawTooltip(mStack, mouseX, mouseY);
        this.symbolRotorEfficiency.drawTooltip(mStack, mouseX, mouseY);
    }
    
    @Override
    public void drawGuiContainerForegroundLayer(MatrixStack mStack, int mouseX, int mouseY) {
        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.turbine_terminal").getString(), 8, 6, 4210752);
        
        // Draw buttons.
        this.turbineActivityToggle.drawPart(mStack);
        this.turbineVentStateToggle.drawPart(mStack);
        this.turbineCoilToggle.drawPart(mStack);
        this.turbineFlowIncrease.drawPart(mStack);
        this.turbineFlowDecrease.drawPart(mStack);
        
        // Draw turbine bars.
        this.barTachometer.drawPart(mStack);
        this.intakeTank.drawPart(mStack);
        this.exhaustTank.drawPart(mStack);
        this.energyTank.drawPart(mStack);
        
        // Draw turbine bar symbols.
        this.symbolTachometer.drawPart(mStack);
        this.symbolIntakeTank.drawPart(mStack);
        this.symbolExhaustTank.drawPart(mStack);
        this.symbolEnergyTank.drawPart(mStack);
        
        // Draw turbine information symbols.
        this.symbolTachometerReadout.drawPart(mStack);
        this.symbolTurbineOutput.drawPart(mStack);
        this.symbolGovernor.drawPart(mStack);
        this.symbolRotorEfficiency.drawPart(mStack);
        
        // Update (and draw) turbine information text.
        this.font.drawString(mStack, String.format("%.1f RPM", turbineState.currentRPM), 26, 23, 4210752);
        this.font.drawString(mStack, String.format("%.1f RF/t", turbineState.turbineOutputRate), 26, 43, 4210752);
        this.font.drawString(mStack, String.format("%d mB/t", turbineState.flowRate), 26, 63, 4210752);
        this.font.drawString(mStack, String.format("%.1f%%", turbineState.efficiencyRate * 100), 26, 84, 4210752);
        if (turbineState.turbineActivity == TurbineActivity.ACTIVE) {
            this.font.drawString(mStack, new TranslationTextComponent("tooltip.biggerreactors.status.turbine.activity.online").getString(), 8, 103, 4210752);
        } else {
            this.font.drawString(mStack, new TranslationTextComponent("tooltip.biggerreactors.status.turbine.activity.offline").getString(), 8, 103, 4210752);
        }
    }
}