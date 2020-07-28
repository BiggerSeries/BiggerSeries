package net.roguelogix.biggerreactors.classic.reactor.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.biggerreactors.classic.reactor.ReactorContainer;
import net.roguelogix.biggerreactors.classic.reactor.ReactorDatapack;
import net.roguelogix.biggerreactors.client.gui.GuiEnergyTank;
import net.roguelogix.biggerreactors.client.gui.GuiFluidTank;
import net.roguelogix.biggerreactors.client.gui.GuiReactorBar;
import net.roguelogix.biggerreactors.client.gui.GuiReactorSymbol;
import net.roguelogix.biggerreactors.client.gui.buttons.GuiReactorToggleActiveButton;
import net.roguelogix.biggerreactors.fluids.IrradiatedSteam;

@OnlyIn(Dist.CLIENT)
public class ReactorScreen extends ContainerScreen<ReactorContainer> implements IHasContainer<ReactorContainer> {
    
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(BiggerReactors.modid, "textures/screen/reactor_terminal.png");
    
    // String status symbols.
    private GuiReactorSymbol<ReactorContainer> coreHeatSymbol;
    private GuiReactorSymbol<ReactorContainer> outputSymbol;
    private GuiReactorSymbol<ReactorContainer> fuelRateSymbol;
    private GuiReactorSymbol<ReactorContainer> reactivitySymbol;
    
    // Upper gauges.
    private GuiReactorSymbol<ReactorContainer> fuelSymbol;
    private GuiReactorBar<ReactorContainer> fuelTank;
    private GuiReactorSymbol<ReactorContainer> caseHeatSymbol;
    private GuiReactorBar<ReactorContainer> caseHeatTank;
    private GuiReactorSymbol<ReactorContainer> fuelHeatSymbol;
    private GuiReactorBar<ReactorContainer> fuelHeatTank;
    private GuiReactorSymbol<ReactorContainer> energySymbol;
    private GuiEnergyTank<ReactorContainer> energyTank;
    
    // Lower gauges.
    private GuiReactorSymbol<ReactorContainer> waterSymbol;
    private GuiFluidTank<ReactorContainer> waterTank;
    private GuiReactorSymbol<ReactorContainer> steamSymbol;
    private GuiFluidTank<ReactorContainer> steamTank;
    
    // Buttons.
    private GuiReactorToggleActiveButton<ReactorContainer> reactorStatusButton;
    
    public ReactorScreen(ReactorContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.xSize = 176;
        this.ySize = 186;
        
        // String status symbols.
        this.coreHeatSymbol = new GuiReactorSymbol<>(this, 6, (this.ySize - 170), 0, new TranslationTextComponent("").getFormattedText());
        this.coreHeatSymbol = new GuiReactorSymbol<>(this, 6, 16, 0, new TranslationTextComponent("").getFormattedText());
        this.outputSymbol = new GuiReactorSymbol<>(this, 6, 38, 3, new TranslationTextComponent("").getFormattedText());
        this.fuelRateSymbol = new GuiReactorSymbol<>(this, 6, 58, 4, new TranslationTextComponent("").getFormattedText());
        this.reactivitySymbol = new GuiReactorSymbol<>(this, 6, 79, 5, new TranslationTextComponent("").getFormattedText());
        
        // Upper gauges.
        this.fuelSymbol = new GuiReactorSymbol<>(this, 88, 5, 6, new TranslationTextComponent("").getFormattedText());
        this.fuelTank = new GuiReactorBar<>(this, 88, 22, 3);
        this.caseHeatSymbol = new GuiReactorSymbol<>(this, 110, 5, 7, new TranslationTextComponent("").getFormattedText());
        this.caseHeatTank = new GuiReactorBar<>(this, 110, 22, 2);
        this.fuelHeatSymbol = new GuiReactorSymbol<>(this, 132, 5, 8, new TranslationTextComponent("").getFormattedText());
        this.fuelHeatTank = new GuiReactorBar<>(this, 132, 22, 2);
        this.energySymbol = new GuiReactorSymbol<>(this, 154, 5, 9, new TranslationTextComponent("").getFormattedText());
        this.energyTank = new GuiEnergyTank<>(this, 154, 22);
        
        // Lower gauges.
        this.waterSymbol = new GuiReactorSymbol<>(this, 132, 96, 1, new TranslationTextComponent("").getFormattedText());
        this.waterTank = new GuiFluidTank<>(this, 131, 113, Fluids.WATER);
        this.steamSymbol = new GuiReactorSymbol<>(this, 154, 96, 2, new TranslationTextComponent("").getFormattedText());
        this.steamTank = new GuiFluidTank<>(this, 153, 113, IrradiatedSteam.INSTANCE);
        
        // Buttons
        this.reactorStatusButton = new GuiReactorToggleActiveButton<>(this, 5, 165, 0, new TranslationTextComponent("").getFormattedText());
    }
    
    @Override
    // 1.16: func_230430_a_
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(); // 1.16: this.func_230446_a_
        
        // Normally, we'd call super.render(), but we don't use the inventory in this screen..
        this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(this.guiLeft, this.guiTop, 0);
        this.drawGuiContainerForegroundLayer(mouseX, mouseY);
        RenderSystem.popMatrix();
        
        this.renderHoveredToolTip(mouseX, mouseY);  // 1.16: this.func_230459_a_
        
        ReactorDatapack reactorData = this.getContainer().getReactorData();
        
        // String status symbols.
        this.coreHeatSymbol.drawTooltip(mouseX, mouseY);
        this.outputSymbol.drawTooltip(mouseX, mouseY);
        this.fuelRateSymbol.drawTooltip(mouseX, mouseY);
        this.reactivitySymbol.drawTooltip(mouseX, mouseY);
        
        // Upper gauges.
        this.fuelSymbol.drawTooltip(mouseX, mouseY);
        //this.fuelTank.drawTooltip(mouseX, mouseY, _, _);
        this.caseHeatSymbol.drawTooltip(mouseX, mouseY);
        this.caseHeatTank.drawTooltip(mouseX, mouseY, (long) reactorData.caseHeatStored, Config.Reactor.GUI.HeatDisplayMax);
        this.fuelHeatSymbol.drawTooltip(mouseX, mouseY);
        this.fuelHeatTank.drawTooltip(mouseX, mouseY, (long) reactorData.fuelHeatStored, Config.Reactor.GUI.HeatDisplayMax);
        this.energySymbol.drawTooltip(mouseX, mouseY);
        this.energyTank.drawTooltip(mouseX, mouseY, reactorData.energyStored, reactorData.energyCapacity);
        
        // Lower gauges.
        this.waterSymbol.drawTooltip(mouseX, mouseY);
        this.waterTank.drawTooltip(mouseX, mouseY, reactorData.coolantStored, reactorData.coolantCapacity);
        this.steamSymbol.drawTooltip(mouseX, mouseY);
        this.steamTank.drawTooltip(mouseX, mouseY, reactorData.steamStored, reactorData.coolantCapacity);
        
        // Buttons.
        this.reactorStatusButton.drawTooltip(mouseX, mouseY);
    }
    
    private void drawReactorStatus(ReactorDatapack reactorData) {
        // Draw core heat status.
        this.coreHeatSymbol.drawPart();
        this.font.drawString(String.format("%.0f C", reactorData.fuelHeatStored), 25.0F, (float) (this.ySize - 165), 4210752);
        
        // Draw reactor output status.
        if (reactorData.reactorType) {
            // Active reactor, display as steam.
            this.outputSymbol.updateTextureIndex(2);
            this.outputSymbol.drawPart();
            this.font.drawString(String.format("%.2f mB/t", reactorData.reactorOutputRate), 25.0F, (float) (this.ySize - 144), 4210752);
        } else {
            // Passive reactor, display as energy.
            this.outputSymbol.updateTextureIndex(3);
            this.outputSymbol.drawPart();
            this.font.drawString(String.format("%.2f RF/t", reactorData.reactorOutputRate), 25.0F, (float) (this.ySize - 144), 4210752);
        }
        
        // Draw fuel rate status.
        this.fuelRateSymbol.drawPart();
        this.font.drawString(String.format("%.3f mB/t", reactorData.fuelUsageRate), 25.0F, (float) (this.ySize - 123), 4210752);
        
        // Draw reactivity status.
        this.reactivitySymbol.drawPart();
        this.font.drawString(String.format("%.1f%%", reactorData.reactivityRate * 100), 25.0F, (float) (this.ySize - 103), 4210752);
        
        // Draw reactor status.
        if (reactorData.reactorStatus) {
            // Reactor is online.
            this.font.drawString("Status: \u00A72Online", 6.0F, (float) (this.ySize - 84), 4210752);
        } else {
            // Reactor is offline.
            this.font.drawString("Status: \u00A74Offline", 6.0F, (float) (this.ySize - 84), 4210752);
        }
    }
    
    private void drawReactorGauges(ReactorDatapack reactorData) {
        // Draw fuel/waste gauge.
        this.fuelSymbol.drawPart();
        this.fuelTank.drawPart(reactorData.wasteStored, reactorData.reactantStored, reactorData.fuelCapacity);
        
        // Draw case heat gauge.
        this.caseHeatSymbol.drawPart();
        this.caseHeatTank.drawPart((long) reactorData.caseHeatStored, Config.Reactor.GUI.HeatDisplayMax);
        
        // Draw fuel heat gauge.
        this.fuelHeatSymbol.drawPart();
        this.fuelHeatTank.drawPart((long) reactorData.fuelHeatStored, Config.Reactor.GUI.HeatDisplayMax);
        
        // Draw energy gauge.
        this.energySymbol.drawPart();
        this.energyTank.drawPart(reactorData.energyStored, reactorData.energyCapacity);
        
        // Draw fluid tanks (if reactor is active type).
        if (reactorData.reactorType) {
            this.waterSymbol.drawPart();
            this.waterTank.drawPart(reactorData.coolantStored, reactorData.coolantCapacity);
            this.steamSymbol.drawPart();
            this.steamTank.drawPart(reactorData.steamStored, reactorData.coolantCapacity);
        }
    }
    
    private void drawReactorControls(ReactorDatapack reactorData) {
        this.reactorStatusButton.drawPart();
    }
    
    @Override
    // 1.16: func_230451_b_
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        ReactorDatapack reactorData = this.getContainer().getReactorData();
        // TODO: This label doesn't quite fit on screen correctly, may need to modify texture a little.
        //this.font.drawString(this.title.getFormattedText(), 8.0F, (float) (this.ySize - 168), 4210752);
        
        this.drawReactorStatus(reactorData);
        this.drawReactorGauges(reactorData);
        this.drawReactorControls(reactorData);
    }
    
    @Override
    // 1.16: func_230450_a_
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(GUI_TEXTURE); // 1.16: field_230706_i_
        
        int relativeX = (this.width - this.xSize) / 2; // 1.16: field_230708_k_
        int relativeY = (this.height - this.ySize) / 2; // 1.16: field_230709_l_
        
        this.blit(relativeX, relativeY, 0, 0, this.xSize, this.ySize); // 1.16: func_238474_b_
    }
}
