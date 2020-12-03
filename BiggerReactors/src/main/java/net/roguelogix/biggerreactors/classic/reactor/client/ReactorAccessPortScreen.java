package net.roguelogix.biggerreactors.classic.reactor.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.reactor.containers.ReactorAccessPortContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorAccessPortState;
import net.roguelogix.biggerreactors.client.old.GuiSymbol;
import net.roguelogix.biggerreactors.client.old.ioport.GuiFuelModeToggle;
import net.roguelogix.biggerreactors.client.old.ioport.GuiPortDirectionToggle;
import net.roguelogix.phosphophyllite.gui.old.client.GuiScreenBase;

public class ReactorAccessPortScreen extends GuiScreenBase<ReactorAccessPortContainer> implements IHasContainer<ReactorAccessPortContainer> {

    private ReactorAccessPortState accessPortState;

    // Buttons.
    private GuiPortDirectionToggle<ReactorAccessPortContainer> ioToggle;
    private GuiFuelModeToggle<ReactorAccessPortContainer> fuelModeToggle;

    // Information symbol.
    private GuiSymbol<ReactorAccessPortContainer> symbolIoState;
    private GuiSymbol<ReactorAccessPortContainer> symbolGuiChange;

    public ReactorAccessPortScreen(ReactorAccessPortContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.accessPortState = (ReactorAccessPortState) this.getContainer().getGuiPacket();

        // Set textures.
        this.xSize = 122;
        this.ySize = 72;
        this.updateTexture(new ResourceLocation(BiggerReactors.modid, "textures/screen/io_port.png"), 0, 0);

        // Initialize buttons.
        this.ioToggle = new GuiPortDirectionToggle<>(this, 35, 25, 16, 16);
        this.fuelModeToggle = new GuiFuelModeToggle<>(this, 55, 25, 16, 16);

        // Initialize port symbols.
        this.symbolIoState = new GuiSymbol<>(this, 75, 25, 64, 64,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.io.state").getString());
        this.symbolGuiChange = new GuiSymbol<>(this, 104, 54, 128, 64,
                new TranslationTextComponent("tooltip.biggerreactors.symbols.io.gui_change").getString());
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
        this.ioToggle.mouseClicked(mouseX, mouseY, button);
        this.fuelModeToggle.mouseClicked(mouseX, mouseY, button);
        return true;
    }

    /**
     * Update logic.
     */
    @Override
    public void tick() {
        this.accessPortState = (ReactorAccessPortState) this.getContainer().getGuiPacket();

        // Update buttons (that require it).
        this.ioToggle.updateState(accessPortState.inputState);
        this.fuelModeToggle.updateState(accessPortState.fuelMode, !accessPortState.inputState);
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
        this.ioToggle.drawTooltip(mStack, mouseX, mouseY);
        this.fuelModeToggle.drawTooltip(mStack, mouseX, mouseY);

        // Draw port symbols.
        // Draw status text.
        if (accessPortState.inputState) {
            this.symbolIoState.updateTooltip(new TranslationTextComponent("tooltip.biggerreactors.symbols.io.input").getString());
        } else {
            this.symbolIoState.updateTooltip(new TranslationTextComponent("tooltip.biggerreactors.symbols.io.output").getString());
        }
        this.symbolIoState.drawTooltip(mStack, mouseX, mouseY);
        this.symbolGuiChange.drawTooltip(mStack, mouseX, mouseY);
    }

    /**
     * Draw foreground elements.
     *
     * @param mouseX X position of the mouse.
     * @param mouseY Y position of the mouse.
     */
    @Override
    public void drawGuiContainerForegroundLayer(MatrixStack mStack, int mouseX, int mouseY) {
        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.reactor_access_port").getString(), 8, 6, 4210752);

        // Draw button.
        this.ioToggle.drawPart(mStack);
        this.fuelModeToggle.drawPart(mStack);

        // Draw status text.
        if (accessPortState.inputState) {
            this.font.drawString(mStack, new TranslationTextComponent("tooltip.biggerreactors.status.io.input").getString(), 8, 56, 4210752);
        } else {
            this.font.drawString(mStack, new TranslationTextComponent("tooltip.biggerreactors.status.io.output").getString(), 8, 56, 4210752);
        }

        // Draw port symbols.
        if (accessPortState.inputState) {
            this.symbolIoState.updateTextureOffset(64, 64);
        } else {
            this.symbolIoState.updateTextureOffset(80, 64);
        }
        this.symbolIoState.drawPart(mStack);
        this.symbolGuiChange.drawPart(mStack);
    }
}