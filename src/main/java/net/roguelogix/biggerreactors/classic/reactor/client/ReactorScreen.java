package net.roguelogix.biggerreactors.classic.reactor.client;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.biggerreactors.classic.reactor.containers.ReactorContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorState;
import net.roguelogix.biggerreactors.client.gui.GuiEnergyTank;
import net.roguelogix.biggerreactors.client.gui.GuiFuelMixBar;
import net.roguelogix.biggerreactors.client.gui.GuiHeatBar;
import net.roguelogix.biggerreactors.client.gui.GuiSymbol;
import net.roguelogix.biggerreactors.client.gui.buttons.GuiReactorActivityToggle;
import net.roguelogix.biggerreactors.client.gui.buttons.GuiReactorAutoEjectToggle;
import net.roguelogix.biggerreactors.client.gui.buttons.GuiReactorManualEject;
import net.roguelogix.phosphophyllite.gui.GuiScreenBase;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

@OnlyIn(Dist.CLIENT)
public class ReactorScreen extends GuiScreenBase<ReactorContainer> implements IHasContainer<ReactorContainer> {
    
    // Buttons.
    private GuiReactorActivityToggle<ReactorContainer> reactorActivityToggle;
    private GuiReactorAutoEjectToggle<ReactorContainer> reactorAutoEjectToggle;
    private GuiReactorManualEject<ReactorContainer> reactorManualEject;
    
    // Reactor bars.
    private GuiFuelMixBar<ReactorContainer> barFuelMix;
    private GuiHeatBar<ReactorContainer> barCaseHeat;
    private GuiHeatBar<ReactorContainer> barFuelHeat;
    private GuiEnergyTank<ReactorContainer> energyTank;
    
    // Reactor symbols.
    private GuiSymbol<ReactorContainer> symbolFuelMix;
    private GuiSymbol<ReactorContainer> symbolCaseHeat;
    private GuiSymbol<ReactorContainer> symbolFuelHeat;
    private GuiSymbol<ReactorContainer> symbolEnergyTank;
    
    public ReactorScreen(ReactorContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        
        // Set textures.
        this.xSize = 176;
        this.ySize = 186;
        this.updateTexture(new ResourceLocation(BiggerReactors.modid, "textures/screen/reactor_terminal.png"), 0, 0);
        
        // Initialize buttons.
        this.reactorActivityToggle = new GuiReactorActivityToggle<>(this, 5, 147, 16, 16);
        this.reactorAutoEjectToggle = new GuiReactorAutoEjectToggle<>(this, 5, 165, 16, 16);
        this.reactorManualEject = new GuiReactorManualEject<>(this, 23, 165, 16, 16);
        
        // Initialize reactor bars.
        this.barFuelMix = new GuiFuelMixBar<>(this, 88, 22);
        this.barCaseHeat = new GuiHeatBar<>(this, 110, 22);
        this.barFuelHeat = new GuiHeatBar<>(this, 132, 22);
        this.energyTank = new GuiEnergyTank<>(this, 154, 22);
        
        // Initialize reactor symbols.
        this.symbolFuelMix = new GuiSymbol<>(this, 88, 5, 96, 0, new String[]{
                new TranslationTextComponent("tooltip.biggerreactors.symbols.fuel_mix.main").getFormattedText()
        });
        this.symbolCaseHeat = new GuiSymbol<>(this, 110, 5, 112, 0, new String[]{
                new TranslationTextComponent("tooltip.biggerreactors.symbols.case_heat.main").getFormattedText(),
                new TranslationTextComponent("tooltip.biggerreactors.symbols.case_heat.sub").getFormattedText()
        });
        this.symbolFuelHeat = new GuiSymbol<>(this, 132, 5, 128, 0, new String[]{
                new TranslationTextComponent("tooltip.biggerreactors.symbols.fuel_heat.main").getFormattedText(),
                new TranslationTextComponent("tooltip.biggerreactors.symbols.fuel_heat.sub").getFormattedText()
        });
        this.symbolEnergyTank = new GuiSymbol<>(this, 154, 5, 144, 0, new String[]{
                new TranslationTextComponent("tooltip.biggerreactors.symbols.energy_tank.main").getFormattedText()
        });
    }
    
    /**
     * Update logic.
     */
    @Override
    public void tick() {
        ReactorState reactorState = (ReactorState) this.getContainer().getGuiPacket();
        
        // Update buttons (that require it).
        this.reactorActivityToggle.updateState(reactorState.reactorActivity);
        this.reactorAutoEjectToggle.updateState(reactorState.doAutoEject);
        
        // Update reactor bars.
        this.barFuelMix.updateFuelWaste(reactorState.wasteStored, reactorState.fuelStored, reactorState.fuelCapacity);
        this.barCaseHeat.updateCaseHeat(reactorState.caseHeatStored, Config.Reactor.GUI.HeatDisplayMax);
        this.barFuelHeat.updateCaseHeat(reactorState.fuelHeatStored, Config.Reactor.GUI.HeatDisplayMax);
        this.energyTank.updateEnergy(reactorState.energyStored, reactorState.energyCapacity);
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
        
        // Draw bars.
        this.barFuelMix.drawTooltip(mouseX, mouseY);
        this.barCaseHeat.drawTooltip(mouseX, mouseY);
        this.barFuelHeat.drawTooltip(mouseX, mouseY);
        this.energyTank.drawTooltip(mouseX, mouseY);
        
        // Draw symbols.
        this.symbolFuelMix.drawTooltip(mouseX, mouseY);
        this.symbolCaseHeat.drawTooltip(mouseX, mouseY);
        this.symbolFuelHeat.drawTooltip(mouseX, mouseY);
        this.symbolEnergyTank.drawTooltip(mouseX, mouseY);
        
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
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(new TranslationTextComponent("screen.biggerreactors.reactor_terminal").getFormattedText(), 8.0F, (float) (this.ySize - 178), 4210752);
        
        // Draw buttons.
        this.reactorActivityToggle.drawPart();
        this.reactorAutoEjectToggle.drawPart();
        this.reactorManualEject.drawPart();
        
        // Draw bars.
        this.barFuelMix.drawPart();
        this.barCaseHeat.drawPart();
        this.barFuelHeat.drawPart();
        this.energyTank.drawPart();
        
        // Draw symbols.
        this.symbolFuelMix.drawPart();
        this.symbolCaseHeat.drawPart();
        this.symbolFuelHeat.drawPart();
        this.symbolEnergyTank.drawPart();
    }
}
