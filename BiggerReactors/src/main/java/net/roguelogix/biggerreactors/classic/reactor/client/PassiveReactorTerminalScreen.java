package net.roguelogix.biggerreactors.classic.reactor.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.reactor.containers.ReactorTerminalContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorState;
import net.roguelogix.biggerreactors.client.CommonRender;
import net.roguelogix.phosphophyllite.gui.client.RenderHelper;
import net.roguelogix.phosphophyllite.gui.client.ScreenBase;
import net.roguelogix.phosphophyllite.gui.client.elements.Symbol;
import net.roguelogix.phosphophyllite.gui.client.elements.Tooltip;

import javax.annotation.Nonnull;

public class PassiveReactorTerminalScreen extends ScreenBase<ReactorTerminalContainer> {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(BiggerReactors.modid, "textures/screen/reactor_terminal_passive.png");

    private ReactorState reactorState;

    public PassiveReactorTerminalScreen(ReactorTerminalContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title, DEFAULT_TEXTURE, 176, 152);

        // Initialize reactor state.
        reactorState = (ReactorState) this.getContainer().getGuiPacket();
    }

    /**
     * Initialize the screen.
     */
    @Override
    public void init() {
        super.init();

        // Initialize tooltips:
        CommonReactorTerminalScreen.initTooltips(this, reactorState);
        this.initTooltips();

        // Initialize controls:
        CommonReactorTerminalScreen.initControls(this, reactorState);

        // Initialize gauges:
        CommonReactorTerminalScreen.initGauges(this, reactorState);
        this.initGauges();

        // Initialize symbols:
        CommonReactorTerminalScreen.initSymbols(this, reactorState);
    }

    /**
     * Initialize tooltips.
     */
    private void initTooltips() {
        // (Left) RF generation rate tooltip:
        this.addElement(new Tooltip<>(this, 8, 38, 16, 16, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.energy_generation_rate.tooltip")));

        // (Top) Internal battery tooltip:
        this.addElement(new Tooltip<>(this, 152, 6, 16, 16, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.internal_battery.tooltip")));
    }

    /**
     * Initialize gauges.
     */
    private void initGauges() {
        // (Top) Internal battery:
        Symbol<ReactorTerminalContainer> internalBattery = new Symbol<>(this, 151, 25, 18, 64, 0, 152, StringTextComponent.EMPTY);
        internalBattery.onRender = (@Nonnull MatrixStack mS, int mX, int mY) -> CommonRender.renderEnergyGauge(mS,
                internalBattery, reactorState.energyStored, reactorState.energyCapacity);
        this.addElement(internalBattery);
    }

    /**
     * Tick/update this screen.
     */
    @Override
    public void tick() {
        // Update reactor state and tick.
        reactorState = (ReactorState) this.getContainer().getGuiPacket();
        super.tick();
    }

    /**
     * Draw the status text for this screen.
     *
     * @param mStack       The current matrix stack.
     * @param mouseX       The x position of the mouse.
     * @param mouseY       The y position of the mouse.
     * @param partialTicks Partial ticks.
     */
    @Override
    public void render(@Nonnull MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        super.render(mStack, mouseX, mouseY, partialTicks);

        // Render the other text:
        CommonReactorTerminalScreen.renderStatusText(mStack, this, reactorState.reactorActivity, reactorState.doAutoEject,
                reactorState.caseHeatStored, reactorState.fuelUsageRate, reactorState.reactivityRate);

        // Render text for output rate:
        this.getFont().drawString(mStack, RenderHelper.formatValue(reactorState.reactorOutputRate, "RF/t"), this.getGuiLeft() + 27, this.getGuiTop() + 42, 4210752);
    }
}