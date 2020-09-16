package net.roguelogix.biggerreactors.classic.reactor.client;

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
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.fuel_mix").getUnformattedComponentText());
        this.symbolCaseHeat = new GuiSymbol<>(this, 111, 5, 112, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.case_heat").getUnformattedComponentText());
        this.symbolFuelHeat = new GuiSymbol<>(this, 133, 5, 128, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.fuel_heat").getUnformattedComponentText());
        this.symbolEnergyTank = new GuiSymbol<>(this, 155, 5, 144, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.energy_tank").getUnformattedComponentText());
        this.symbolCoolantTank = new GuiSymbol<>(this, 133, 100, 16, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.coolant_tank").getUnformattedComponentText());
        this.symbolHotTank = new GuiSymbol<>(this, 155, 100, 32, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.hot_tank").getUnformattedComponentText());
        
        // Initialize reactor information symbols.
        this.symbolReactorTemperature = new GuiSymbol<>(this, 7, 18, 0, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.temperature").getUnformattedComponentText());
        this.symbolReactorOutput = new GuiSymbol<>(this, 7, 39, 48, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.output").getUnformattedComponentText());
        this.symbolFuelConsumption = new GuiSymbol<>(this, 7, 59, 64, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.fuel_consumption").getUnformattedComponentText());
        this.symbolFuelReactivity = new GuiSymbol<>(this, 7, 80, 80, 0,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.reactor.fuel_reactivity").getUnformattedComponentText());
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
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(stack, mouseX, mouseY);
        
        // Draw buttons.
        this.reactorActivityToggle.drawTooltip(stack, mouseX, mouseY);
        this.reactorAutoEjectToggle.drawTooltip(stack, mouseX, mouseY);
        this.reactorManualEject.drawTooltip(stack, mouseX, mouseY);
        
        // Draw reactor bars.
        this.barFuelMix.drawTooltip(stack, mouseX, mouseY);
        this.barCaseHeat.drawTooltip(stack, mouseX, mouseY);
        this.barFuelHeat.drawTooltip(stack, mouseX, mouseY);
        this.energyTank.drawTooltip(stack, mouseX, mouseY);
        if (reactorState.reactorType == ReactorType.ACTIVE) {
            this.coolantTank.drawTooltip(stack, mouseX, mouseY);
            this.hotTank.drawTooltip(stack, mouseX, mouseY);
        }
        
        // Draw reactor bar symbols.
        this.symbolFuelMix.drawTooltip(stack, mouseX, mouseY);
        this.symbolCaseHeat.drawTooltip(stack, mouseX, mouseY);
        this.symbolFuelHeat.drawTooltip(stack, mouseX, mouseY);
        this.symbolEnergyTank.drawTooltip(stack, mouseX, mouseY);
        if (reactorState.reactorType == ReactorType.ACTIVE) {
            this.symbolCoolantTank.drawTooltip(stack, mouseX, mouseY);
            this.symbolHotTank.drawTooltip(stack, mouseX, mouseY);
        }
        
        // Draw reactor information symbols.
        this.symbolReactorTemperature.drawTooltip(stack, mouseX, mouseY);
        this.symbolReactorOutput.drawTooltip(stack, mouseX, mouseY);
        this.symbolFuelConsumption.drawTooltip(stack, mouseX, mouseY);
        this.symbolFuelReactivity.drawTooltip(stack, mouseX, mouseY);
        
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
    public void drawGuiContainerForegroundLayer(MatrixStack stack, int mouseX, int mouseY) {
        this.font.drawString(stack, new TranslationTextComponent("screen.biggerreactors.reactor_terminal").getUnformattedComponentText(), 8, 6, 4210752);
        
        // Draw buttons.
        this.reactorActivityToggle.drawPart(stack);
        this.reactorAutoEjectToggle.drawPart(stack);
        this.reactorManualEject.drawPart(stack);
        
        // Draw reactor bars.
        this.barFuelMix.drawPart(stack);
        this.barCaseHeat.drawPart(stack);
        this.barFuelHeat.drawPart(stack);
        this.energyTank.drawPart(stack);
        if (reactorState.reactorType == ReactorType.ACTIVE) {
            this.coolantTank.drawPart(stack);
            this.hotTank.drawPart(stack);
        }
        
        // Draw reactor bar symbols.
        this.symbolFuelMix.drawPart(stack);
        this.symbolCaseHeat.drawPart(stack);
        this.symbolFuelHeat.drawPart(stack);
        this.symbolEnergyTank.drawPart(stack);
        if (reactorState.reactorType == ReactorType.ACTIVE) {
            this.symbolCoolantTank.drawPart(stack);
            this.symbolHotTank.drawPart(stack);
        }
        
        // Draw reactor information symbols.
        this.symbolReactorTemperature.drawPart(stack);
        this.symbolReactorOutput.drawPart(stack);
        this.symbolFuelConsumption.drawPart(stack);
        this.symbolFuelReactivity.drawPart(stack);
        
        // Update (and draw) reactor information text.
        this.font.drawString(stack, String.format("%.0f \u00B0C", reactorState.fuelHeatStored), 26, 23, 4210752);
        if (reactorState.reactorType == ReactorType.ACTIVE) {
            this.font.drawString(stack, String.format("%.1f mB/t", reactorState.reactorOutputRate), 26, 43, 4210752);
        } else {
            this.font.drawString(stack, String.format("%.1f RF/t", reactorState.reactorOutputRate), 26, 43, 4210752);
        }
        this.font.drawString(stack, String.format("%.3f mB/t", reactorState.fuelUsageRate), 26, 63, 4210752);
        this.font.drawString(stack, String.format("%.1f%%", reactorState.reactivityRate * 100), 26, 84, 4210752);
        if (reactorState.reactorActivity == ReactorActivity.ACTIVE) {
            this.font.drawString(stack, new TranslationTextComponent("tooltip.biggerreactors.status.reactor.activity.online").getUnformattedComponentText(), 8, 103, 4210752);
        } else {
            this.font.drawString(stack, new TranslationTextComponent("tooltip.biggerreactors.status.reactor.activity.offline").getUnformattedComponentText(), 8, 103, 4210752);
        }
    }
}
