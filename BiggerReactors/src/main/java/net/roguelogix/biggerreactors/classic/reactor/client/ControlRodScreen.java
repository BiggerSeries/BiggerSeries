package net.roguelogix.biggerreactors.classic.reactor.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.reactor.containers.ControlRodContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ControlRodState;
import net.roguelogix.biggerreactors.client.GuiSymbol;
import net.roguelogix.biggerreactors.client.controlrod.GuiRodInsertButton;
import net.roguelogix.biggerreactors.client.controlrod.GuiRodLevelBar;
import net.roguelogix.biggerreactors.client.controlrod.GuiRodRetractButton;
import net.roguelogix.phosphophyllite.gui.client.GuiScreenBase;

@OnlyIn(Dist.CLIENT)
public class ControlRodScreen extends GuiScreenBase<ControlRodContainer> implements IHasContainer<ControlRodContainer> {
    
    private ControlRodState controlRodState;
    
    // Buttons.
    private GuiRodInsertButton<ControlRodContainer> rodInsert;
    private GuiRodRetractButton<ControlRodContainer> rodRetract;
    
    // Control rod bars.
    private GuiRodLevelBar<ControlRodContainer> barInsertionLevel;
    
    // Control rod bar symbols.
    private GuiSymbol<ControlRodContainer> symbolInsertionLevel;
    
    public ControlRodScreen(ControlRodContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.controlRodState = (ControlRodState) this.getContainer().getGuiPacket();
        
        // Set textures.
        this.xSize = 176;
        this.ySize = 166;
        this.updateTexture(new ResourceLocation(BiggerReactors.modid, "textures/screen/control_rod.png"), 0, 0);
        
        // Initialize buttons.
        this.rodInsert = new GuiRodInsertButton<>(this, 30, 52, 16, 16);
        this.rodRetract = new GuiRodRetractButton<>(this, 30, 74, 16, 16);
        
        // Initialize rod bars.
        this.barInsertionLevel = new GuiRodLevelBar<>(this, 7, 39);
        
        // Initialize rod bar symbols.
        this.symbolInsertionLevel = new GuiSymbol<>(this, 7, 18, 128, 32,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.control_rod.insertion").getString());
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
        this.rodInsert.keyPressed(keyCode, scanCode, modifiers);
        this.rodRetract.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }
    
    /**
     * Handle a key release.
     *
     * @param keyCode   The ASCII keycode for the press.
     * @param scanCode  The scancode for the press.
     * @param modifiers Any modifiers being held.
     * @return Whether or not the press was consumed.
     */
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        super.keyReleased(keyCode, scanCode, modifiers);
        this.rodInsert.keyReleased(keyCode, scanCode, modifiers);
        this.rodRetract.keyReleased(keyCode, scanCode, modifiers);
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
        this.rodInsert.mouseClicked(mouseX, mouseY, button);
        this.rodRetract.mouseClicked(mouseX, mouseY, button);
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
        this.rodInsert.mouseReleased(mouseX, mouseY, button);
        this.rodRetract.mouseReleased(mouseX, mouseY, button);
        return true;
    }
    
    /**
     * Update logic.
     */
    @Override
    public void tick() {
        this.controlRodState = (ControlRodState) this.getContainer().getGuiPacket();
        
        // Update buttons (that require it).
        this.rodInsert.updateState(controlRodState.insertionLevel);
        this.rodRetract.updateState(controlRodState.insertionLevel);
        
        // Update rod bars.
        this.barInsertionLevel.updateInsertion(controlRodState.insertionLevel);
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
        this.rodInsert.drawTooltip(mStack, mouseX, mouseY);
        this.rodRetract.drawTooltip(mStack, mouseX, mouseY);
        
        // Draw rod bars.
        this.barInsertionLevel.drawTooltip(mStack, mouseX, mouseY);
        
        // Draw rod bar symbols.
        this.symbolInsertionLevel.drawTooltip(mStack, mouseX, mouseY);
    }
    
    /**
     * Draw foreground elements.
     *
     * @param mouseX X position of the mouse.
     * @param mouseY Y position of the mouse.
     */
    @Override
    public void drawGuiContainerForegroundLayer(MatrixStack mStack, int mouseX, int mouseY) {
        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.reactor_control_rod").getString(), 8, 6, 4210752);
        
        // Draw button.
        this.rodInsert.drawPart(mStack);
        this.rodRetract.drawPart(mStack);
        
        // Draw rod bars.
        this.barInsertionLevel.drawPart(mStack);
        
        // Draw rod bar symbols.
        this.symbolInsertionLevel.drawPart(mStack);
    }
}
