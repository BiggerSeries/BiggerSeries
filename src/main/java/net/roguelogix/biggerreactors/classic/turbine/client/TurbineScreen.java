package net.roguelogix.biggerreactors.classic.turbine.client;

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

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

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
                new TranslationTextComponent("tooltip.biggerreactors.symbols.turbine.tachometer").getFormattedText());
        this.symbolIntakeTank = new GuiSymbol<>(this, 111, 5, 32, 32,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.turbine.intake_tank").getFormattedText());
        this.symbolExhaustTank = new GuiSymbol<>(this, 133, 5, 48, 32,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.turbine.exhaust_tank").getFormattedText());
        this.symbolEnergyTank = new GuiSymbol<>(this, 155, 5, 96, 32,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.turbine.energy_tank").getFormattedText());
        
        // Initialize turbine information symbols.
        this.symbolTachometerReadout = new GuiSymbol<>(this, 7, 18, 0, 32,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.turbine.tachometer").getFormattedText());
        this.symbolTurbineOutput = new GuiSymbol<>(this, 7, 39, 80, 32,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.turbine.output").getFormattedText());
        this.symbolGovernor = new GuiSymbol<>(this, 7, 59, 112, 32,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.turbine.governor").getFormattedText());
        this.symbolRotorEfficiency = new GuiSymbol<>(this, 7, 80, 16, 32,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.turbine.rotor_efficiency").getFormattedText());
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
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        
        // Draw buttons.
        this.turbineActivityToggle.drawTooltip(mouseX, mouseY);
        this.turbineVentStateToggle.drawTooltip(mouseX, mouseY);
        this.turbineCoilToggle.drawTooltip(mouseX, mouseY);
        this.turbineFlowIncrease.drawTooltip(mouseX, mouseY);
        this.turbineFlowDecrease.drawTooltip(mouseX, mouseY);
        
        // Draw turbine bars.
        this.barTachometer.drawTooltip(mouseX, mouseY);
        this.intakeTank.drawTooltip(mouseX, mouseY);
        this.exhaustTank.drawTooltip(mouseX, mouseY);
        this.energyTank.drawTooltip(mouseX, mouseY);
        
        // Draw turbine bar symbols.
        this.symbolTachometer.drawTooltip(mouseX, mouseY);
        this.symbolIntakeTank.drawTooltip(mouseX, mouseY);
        this.symbolExhaustTank.drawTooltip(mouseX, mouseY);
        this.symbolEnergyTank.drawTooltip(mouseX, mouseY);
        
        // Draw turbine information symbols.
        this.symbolTachometerReadout.drawTooltip(mouseX, mouseY);
        this.symbolTurbineOutput.drawTooltip(mouseX, mouseY);
        this.symbolGovernor.drawTooltip(mouseX, mouseY);
        this.symbolRotorEfficiency.drawTooltip(mouseX, mouseY);
        
        // Check for updateable elements.
        this.turbineActivityToggle.doClick(mouseX, mouseY, GLFW_MOUSE_BUTTON_1);
        this.turbineVentStateToggle.doClick(mouseX, mouseY, GLFW_MOUSE_BUTTON_1);
        this.turbineCoilToggle.doClick(mouseX, mouseY, GLFW_MOUSE_BUTTON_1);
        this.turbineFlowIncrease.doClick(mouseX, mouseY, GLFW_MOUSE_BUTTON_1);
        this.turbineFlowDecrease.doClick(mouseX, mouseY, GLFW_MOUSE_BUTTON_1);
    }
    
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(new TranslationTextComponent("screen.biggerreactors.turbine_terminal").getFormattedText(), 8, 6, 4210752);
        
        // Draw buttons.
        this.turbineActivityToggle.drawPart();
        this.turbineVentStateToggle.drawPart();
        this.turbineCoilToggle.drawPart();
        this.turbineFlowIncrease.drawPart();
        this.turbineFlowDecrease.drawPart();
        
        // Draw turbine bars.
        this.barTachometer.drawPart();
        this.intakeTank.drawPart();
        this.exhaustTank.drawPart();
        this.energyTank.drawPart();
        
        // Draw turbine bar symbols.
        this.symbolTachometer.drawPart();
        this.symbolIntakeTank.drawPart();
        this.symbolExhaustTank.drawPart();
        this.symbolEnergyTank.drawPart();
        
        // Draw turbine information symbols.
        this.symbolTachometerReadout.drawPart();
        this.symbolTurbineOutput.drawPart();
        this.symbolGovernor.drawPart();
        this.symbolRotorEfficiency.drawPart();
        
        // Update (and draw) turbine information text.
        this.font.drawString(String.format("%.1f RPM", turbineState.currentRPM), 26, 23, 4210752);
        this.font.drawString(String.format("%.1f RF/t", turbineState.turbineOutputRate), 26, 43, 4210752);
        this.font.drawString(String.format("%d mB/t", turbineState.flowRate), 26, 63, 4210752);
        this.font.drawString(String.format("%.1f%%", turbineState.efficiencyRate * 100), 26, 84, 4210752);
        if (turbineState.turbineActivity == TurbineActivity.ACTIVE) {
            this.font.drawString(new TranslationTextComponent("tooltip.biggerreactors.status.turbine.activity.online").getFormattedText(), 8, 103, 4210752);
        } else {
            this.font.drawString(new TranslationTextComponent("tooltip.biggerreactors.status.turbine.activity.offline").getFormattedText(), 8, 103, 4210752);
        }
    }
}