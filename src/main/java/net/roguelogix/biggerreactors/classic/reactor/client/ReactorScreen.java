package net.roguelogix.biggerreactors.classic.reactor.client;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.biggerreactors.classic.reactor.containers.ReactorContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorActivity;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorState;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorType;
import net.roguelogix.biggerreactors.client.GuiEnergyTank;
import net.roguelogix.biggerreactors.client.GuiFluidTank;
import net.roguelogix.biggerreactors.client.GuiSymbol;
import net.roguelogix.biggerreactors.client.reactor.*;
import net.roguelogix.biggerreactors.fluids.FluidIrradiatedSteam;
import net.roguelogix.phosphophyllite.gui.client.GuiScreenBase;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

@OnlyIn(Dist.CLIENT)
public class ReactorScreen extends GuiScreenBase<ReactorContainer> implements IHasContainer<ReactorContainer> {
    
    private ReactorState reactorState;
    
    // Buttons.
    private GuiReactorActivityToggle<ReactorContainer> reactorActivityToggle;
    private GuiReactorAutoEjectToggle<ReactorContainer> reactorAutoEjectToggle;
    private GuiReactorManualEjectButton<ReactorContainer> reactorManualEject;
    
    // Reactor bars.
    private GuiReactorFuelMixBar<ReactorContainer> barFuelMix;
    private GuiReactorHeatBar<ReactorContainer> barCaseHeat;
    private GuiReactorHeatBar<ReactorContainer> barFuelHeat;
    private GuiEnergyTank<ReactorContainer> energyTank;
    private GuiFluidTank<ReactorContainer> coolantTank;
    private GuiFluidTank<ReactorContainer> hotTank;
    
    // Reactor bar symbols.
    private GuiSymbol<ReactorContainer> symbolFuelMix;
    private GuiSymbol<ReactorContainer> symbolCaseHeat;
    private GuiSymbol<ReactorContainer> symbolFuelHeat;
    private GuiSymbol<ReactorContainer> symbolEnergyTank;
    private GuiSymbol<ReactorContainer> symbolCoolantTank;
    private GuiSymbol<ReactorContainer> symbolHotTank;
    
    // Reactor information symbols.
    private GuiSymbol<ReactorContainer> symbolReactorTemperature;
    private GuiSymbol<ReactorContainer> symbolReactorOutput;
    private GuiSymbol<ReactorContainer> symbolFuelConsumption;
    private GuiSymbol<ReactorContainer> symbolFuelReactivity;
    
    public ReactorScreen(ReactorContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.reactorState = (ReactorState) this.getContainer().getGuiPacket();
        
        // Set textures.
        this.xSize = 176;
        this.ySize = 186;
        this.updateTexture(new ResourceLocation(BiggerReactors.modid, "textures/screen/terminal.png"), 0, 0);
        
        // Initialize buttons.
        this.reactorActivityToggle = new GuiReactorActivityToggle<>(this, 5, 147, 16, 16);
        this.reactorAutoEjectToggle = new GuiReactorAutoEjectToggle<>(this, 5, 165, 16, 16);
        this.reactorManualEject = new GuiReactorManualEjectButton<>(this, 23, 165, 16, 16);
        
        // Initialize reactor bars.
        this.barFuelMix = new GuiReactorFuelMixBar<>(this, 88, 22);
        this.barCaseHeat = new GuiReactorHeatBar<>(this, 110, 22);
        this.barFuelHeat = new GuiReactorHeatBar<>(this, 132, 22);
        this.energyTank = new GuiEnergyTank<>(this, 154, 22);
        this.coolantTank = new GuiFluidTank<>(this, 132, 117);
        this.hotTank = new GuiFluidTank<>(this, 154, 117);
        
        // Initialize reactor bar symbols.
        this.symbolFuelMix = new GuiSymbol<>(this, 89, 5, 96, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.fuel_mix").getFormattedText());
        this.symbolCaseHeat = new GuiSymbol<>(this, 111, 5, 112, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.case_heat").getFormattedText());
        this.symbolFuelHeat = new GuiSymbol<>(this, 133, 5, 128, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.fuel_heat").getFormattedText());
        this.symbolEnergyTank = new GuiSymbol<>(this, 155, 5, 144, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.energy_tank").getFormattedText());
        this.symbolCoolantTank = new GuiSymbol<>(this, 133, 100, 16, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.coolant_tank").getFormattedText());
        this.symbolHotTank = new GuiSymbol<>(this, 155, 100, 32, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.hot_tank").getFormattedText());
        
        // Initialize reactor information symbols.
        this.symbolReactorTemperature = new GuiSymbol<>(this, 7, 18, 0, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.temperature").getFormattedText());
        this.symbolReactorOutput = new GuiSymbol<>(this, 7, 39, 48, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.output").getFormattedText());
        this.symbolFuelConsumption = new GuiSymbol<>(this, 7, 59, 64, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.fuel_consumption").getFormattedText());
        this.symbolFuelReactivity = new GuiSymbol<>(this, 7, 80, 80, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.fuel_reactivity").getFormattedText());
    }
    
    /**
     * Update logic.
     */
    @Override
    public void tick() {
        this.reactorState = (ReactorState) this.getContainer().getGuiPacket();
        
        // Update buttons (that require it).
        this.reactorActivityToggle.updateState(reactorState.reactorActivity);
        this.reactorAutoEjectToggle.updateState(reactorState.doAutoEject);
        
        // Update reactor bars.
        this.barFuelMix.updateFuelMix(reactorState.wasteStored, reactorState.fuelStored, reactorState.fuelCapacity);
        this.barCaseHeat.updateHeat(reactorState.caseHeatStored, Config.Reactor.GUI.HeatDisplayMax);
        this.barFuelHeat.updateHeat(reactorState.fuelHeatStored, Config.Reactor.GUI.HeatDisplayMax);
        this.energyTank.updateEnergy(reactorState.energyStored, reactorState.energyCapacity);
        this.coolantTank.updateFluid(Fluids.WATER.getFluid(), reactorState.coolantStored, reactorState.coolantCapacity);
        this.hotTank.updateFluid(FluidIrradiatedSteam.INSTANCE.getFluid(), reactorState.steamStored, reactorState.steamCapacity);
        
        // Update reactor symbols.
        if (reactorState.reactorType == ReactorType.ACTIVE) {
            // Yes, this occurs every tick. I'll move it later (probably).
            this.symbolReactorOutput.updateTextureOffset(32, 0);
        }
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
        this.reactorActivityToggle.drawTooltip(mouseX, mouseY);
        this.reactorAutoEjectToggle.drawTooltip(mouseX, mouseY);
        this.reactorManualEject.drawTooltip(mouseX, mouseY);
        
        // Draw reactor bars.
        this.barFuelMix.drawTooltip(mouseX, mouseY);
        this.barCaseHeat.drawTooltip(mouseX, mouseY);
        this.barFuelHeat.drawTooltip(mouseX, mouseY);
        this.energyTank.drawTooltip(mouseX, mouseY);
        if (reactorState.reactorType == ReactorType.ACTIVE) {
            this.coolantTank.drawTooltip(mouseX, mouseY);
            this.hotTank.drawTooltip(mouseX, mouseY);
        }
        
        // Draw reactor bar symbols.
        this.symbolFuelMix.drawTooltip(mouseX, mouseY);
        this.symbolCaseHeat.drawTooltip(mouseX, mouseY);
        this.symbolFuelHeat.drawTooltip(mouseX, mouseY);
        this.symbolEnergyTank.drawTooltip(mouseX, mouseY);
        if (reactorState.reactorType == ReactorType.ACTIVE) {
            this.symbolCoolantTank.drawTooltip(mouseX, mouseY);
            this.symbolHotTank.drawTooltip(mouseX, mouseY);
        }
        
        // Draw reactor information symbols.
        this.symbolReactorTemperature.drawTooltip(mouseX, mouseY);
        this.symbolReactorOutput.drawTooltip(mouseX, mouseY);
        this.symbolFuelConsumption.drawTooltip(mouseX, mouseY);
        this.symbolFuelReactivity.drawTooltip(mouseX, mouseY);
        
        // Check for updatable elements.
        this.reactorActivityToggle.doClick(mouseX, mouseY, GLFW_MOUSE_BUTTON_1);
        this.reactorAutoEjectToggle.doClick(mouseX, mouseY, GLFW_MOUSE_BUTTON_1);
        this.reactorManualEject.doClick(mouseX, mouseY, GLFW_MOUSE_BUTTON_1);
    }
    
    /**
     * Draw foreground elements.
     *
     * @param mouseX X position of the mouse.
     * @param mouseY Y position of the mouse.
     */
    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(new TranslationTextComponent("screen.biggerreactors.reactor_terminal").getFormattedText(), 8, 6, 4210752);
        
        // Draw buttons.
        this.reactorActivityToggle.drawPart();
        this.reactorAutoEjectToggle.drawPart();
        this.reactorManualEject.drawPart();
        
        // Draw reactor bars.
        this.barFuelMix.drawPart();
        this.barCaseHeat.drawPart();
        this.barFuelHeat.drawPart();
        this.energyTank.drawPart();
        if (reactorState.reactorType == ReactorType.ACTIVE) {
            this.coolantTank.drawPart();
            this.hotTank.drawPart();
        }
        
        // Draw reactor bar symbols.
        this.symbolFuelMix.drawPart();
        this.symbolCaseHeat.drawPart();
        this.symbolFuelHeat.drawPart();
        this.symbolEnergyTank.drawPart();
        if (reactorState.reactorType == ReactorType.ACTIVE) {
            this.symbolCoolantTank.drawPart();
            this.symbolHotTank.drawPart();
        }
        
        // Draw reactor information symbols.
        this.symbolReactorTemperature.drawPart();
        this.symbolReactorOutput.drawPart();
        this.symbolFuelConsumption.drawPart();
        this.symbolFuelReactivity.drawPart();
        
        // Update (and draw) reactor information text.
        this.font.drawString(String.format("%.0f \u00B0C", reactorState.fuelHeatStored), 26, 23, 4210752);
        if (reactorState.reactorType == ReactorType.ACTIVE) {
            this.font.drawString(String.format("%.1f mB/t", reactorState.reactorOutputRate), 26, 43, 4210752);
        } else {
            this.font.drawString(String.format("%.1f RF/t", reactorState.reactorOutputRate), 26, 43, 4210752);
        }
        this.font.drawString(String.format("%.3f mB/t", reactorState.fuelUsageRate), 26, 63, 4210752);
        this.font.drawString(String.format("%.1f%%", reactorState.reactivityRate * 100), 26, 84, 4210752);
        if (reactorState.reactorActivity == ReactorActivity.ACTIVE) {
            this.font.drawString(new TranslationTextComponent("tooltip.biggerreactors.status.reactor.activity.online").getFormattedText(), 8, 103, 4210752);
        } else {
            this.font.drawString(new TranslationTextComponent("tooltip.biggerreactors.status.reactor.activity.offline").getFormattedText(), 8, 103, 4210752);
        }
    }
}
