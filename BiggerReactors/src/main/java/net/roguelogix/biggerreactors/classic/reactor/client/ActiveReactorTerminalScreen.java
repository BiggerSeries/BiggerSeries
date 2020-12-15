package net.roguelogix.biggerreactors.classic.reactor.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.reactor.containers.ReactorTerminalContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorActivity;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorState;
import net.roguelogix.biggerreactors.client.CommonRender;
import net.roguelogix.biggerreactors.fluids.FluidIrradiatedSteam;
import net.roguelogix.phosphophyllite.gui.client.RenderHelper;
import net.roguelogix.phosphophyllite.gui.client.ScreenBase;
import net.roguelogix.phosphophyllite.gui.client.elements.Symbol;
import net.roguelogix.phosphophyllite.gui.client.elements.Tooltip;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class ActiveReactorTerminalScreen extends ScreenBase<ReactorTerminalContainer> {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(BiggerReactors.modid, "textures/screen/reactor_terminal_active.png");

    private ReactorState reactorState;

    private final int screenWorkTimeTotal = 400;
    private int screenWorkTime = 0;

    public ActiveReactorTerminalScreen(ReactorTerminalContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title, DEFAULT_TEXTURE, 198, 152);

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
        this.initSymbols();
    }

    /**
     * Initialize tooltips.
     */
    private void initTooltips() {
        // (Left) Steam generation readout tooltip:
        Tooltip<ReactorTerminalContainer> generationRateReadoutTooltip = new Tooltip<>(this, 26, 38, 53, 16, StringTextComponent.EMPTY);
        generationRateReadoutTooltip.onTick = () -> {
            generationRateReadoutTooltip.tooltip = new StringTextComponent(String.format("%.3f mB/t", this.reactorState.reactorOutputRate));
        };
        this.addElement(generationRateReadoutTooltip);
    }

    /**
     * Initialize gauges.
     */
    private void initGauges() {
        // (Top) Coolant intake tank:
        Symbol<ReactorTerminalContainer> coolantIntakeTank = new Symbol<>(this, 151, 25, 18, 64, 0, 152, StringTextComponent.EMPTY);
        coolantIntakeTank.onRender = (@Nonnull MatrixStack mS, int mX, int mY) -> CommonRender.renderFluidGauge(mS,
                coolantIntakeTank, reactorState.coolantStored, reactorState.coolantCapacity,
                Fluids.WATER.getStillFluid());
        this.addElement(coolantIntakeTank);

        // (Top) Steam exhaust tank:
        Symbol<ReactorTerminalContainer> steamExhaustTank = new Symbol<>(this, 173, 25, 18, 64, 0, 152, StringTextComponent.EMPTY);
        steamExhaustTank.onRender = (@Nonnull MatrixStack mS, int mX, int mY) -> CommonRender.renderFluidGauge(mS,
                steamExhaustTank, reactorState.steamStored, reactorState.steamCapacity,
                FluidIrradiatedSteam.INSTANCE.getStillFluid());
        this.addElement(steamExhaustTank);

        // (Bottom) Progress bar:
        Symbol<ReactorTerminalContainer> progressBar = new Symbol<>(this, 173, 90, 18, 26, 90, 152, null);
        progressBar.onRender = (@Nonnull MatrixStack mS, int mX, int mY) -> {
            // Custom rendering.
            if (reactorState.coolantStored > 0) {
                ActiveReactorTerminalScreen.renderProgressBar(mS, progressBar, reactorState.reactorActivity, screenWorkTime++, screenWorkTimeTotal);
                if (screenWorkTime >= screenWorkTimeTotal) {
                    screenWorkTime = 0;
                }
            }
        };
        this.addElement(progressBar);
    }

    /**
     * Initialize symbols.
     */
    private void initSymbols() {
        // (Top) Coolant intake tank symbol:
        Symbol<ReactorTerminalContainer> coolantIntakeTankSymbol = new Symbol<>(this, 152, 6, 16, 16, 174, 152, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.coolant_intake_tank.tooltip"));
        coolantIntakeTankSymbol.onRender = (@Nonnull MatrixStack mS, int mX, int mY) -> RenderHelper.drawMaskedFluid(mS,
                coolantIntakeTankSymbol.x, coolantIntakeTankSymbol.y, this.getBlitOffset(),
                coolantIntakeTankSymbol.width, coolantIntakeTankSymbol.height,
                coolantIntakeTankSymbol.u, coolantIntakeTankSymbol.v, Fluids.WATER.getStillFluid());
        this.addElement(coolantIntakeTankSymbol);

        // (Top) Steam exhaust tank symbol:
        Symbol<ReactorTerminalContainer> steamExhaustTankSymbol = new Symbol<>(this, 174, 6, 16, 16, 158, 152, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.steam_exhaust_tank.tooltip"));
        steamExhaustTankSymbol.onRender = (@Nonnull MatrixStack mS, int mX, int mY) -> RenderHelper.drawMaskedFluid(mS,
                steamExhaustTankSymbol.x, steamExhaustTankSymbol.y, this.getBlitOffset(),
                steamExhaustTankSymbol.width, steamExhaustTankSymbol.height,
                steamExhaustTankSymbol.u, steamExhaustTankSymbol.v, FluidIrradiatedSteam.INSTANCE.getStillFluid());
        this.addElement(steamExhaustTankSymbol);

        // (Left) Steam generation rate symbol:
        Symbol<ReactorTerminalContainer> steamGenerationRateSymbol = new Symbol<>(this, 8, 38, 16, 16, 142, 152, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.steam_generation_rate.tooltip"));
        steamGenerationRateSymbol.onRender = (@Nonnull MatrixStack mS, int mX, int mY) -> RenderHelper.drawMaskedFluid(mS,
                steamGenerationRateSymbol.x, steamGenerationRateSymbol.y, this.getBlitOffset(),
                steamGenerationRateSymbol.width, steamGenerationRateSymbol.height,
                steamGenerationRateSymbol.u, steamGenerationRateSymbol.v, FluidIrradiatedSteam.INSTANCE.getStillFluid());
        this.addElement(steamGenerationRateSymbol);
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
        this.getFont().drawString(mStack, RenderHelper.formatValue((reactorState.reactorOutputRate / 1000.0), "B/t"), this.getGuiLeft() + 27, this.getGuiTop() + 42, 4210752);
    }

    /**
     * Render the progress bar.
     *
     * @param mStack          The current matrix stack.
     * @param symbol          The symbol to draw as.
     * @param reactorActivity Current status of the reactor.
     * @param workTime        The time the machine has been working.
     * @param workTimeTotal   The total time needed for completion.
     */
    private static void renderProgressBar(@Nonnull MatrixStack mStack, @Nonnull Symbol<ReactorTerminalContainer> symbol, ReactorActivity reactorActivity, int workTime, int workTimeTotal) {
        // Check that the reactor is active. If not, reset work time.
        if (reactorActivity != ReactorActivity.ACTIVE) {
            workTime = 0;
        }
        // If there's no progress, there's no need to draw.
        // This isn't the way I wanted to render this bar, but it's vertical, so funky is as funky does.
        if (workTime > 0) {
            // Calculate how much needs to be rendered.
            int renderSize = (int) ((symbol.height * workTime) / workTimeTotal);
            // Render progress.
            symbol.blit(mStack, symbol.u + 18, symbol.v);
            // Render backdrop/mask away "not progress."
            symbol.blit(mStack, symbol.width, symbol.height - renderSize, symbol.u, symbol.v);
        }
        // If work time exceeds work time total, then reset to 0.

        // Render the fluid "undertank."
        RenderHelper.drawMaskedFluid(mStack, symbol.x + 1, symbol.y + 26, 0, 16, 16, symbol.u + 36, symbol.v, Fluids.WATER.getStillFluid());
    }
}
