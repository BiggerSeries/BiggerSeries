package net.roguelogix.biggerreactors.classic.reactor.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.biggerreactors.classic.reactor.containers.ReactorTerminalContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorActivity;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorState;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorType;
import net.roguelogix.biggerreactors.client.Biselector;
import net.roguelogix.biggerreactors.client.SelectorColors;
import net.roguelogix.phosphophyllite.gui.client.RenderHelper;
import net.roguelogix.phosphophyllite.gui.client.ScreenBase;
import net.roguelogix.phosphophyllite.gui.client.elements.Button;
import net.roguelogix.phosphophyllite.gui.client.elements.Symbol;
import net.roguelogix.phosphophyllite.gui.client.elements.Tooltip;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class CommonReactorTerminalScreen extends ScreenBase<ReactorTerminalContainer> {

    // This state is used once, and as such can be final. Most other states should NOT be final.
    private final ReactorState initialState;

    public CommonReactorTerminalScreen(ReactorTerminalContainer container, PlayerInventory playerInventory, ITextComponent title) {
        // We override whatever Minecraft wants to set the title to. It wants "Reactor Terminal," but that's too long.
        super(container, playerInventory, new TranslationTextComponent("screen.biggerreactors.reactor_terminal"), RenderHelper.getBlankResource(), 176, 152);

        // Initialize reactor terminal state.
        initialState = (ReactorState) this.getContainer().getGuiPacket();
    }

    /**
     * Initialize whichever subscreen we need.
     */
    @Override
    public void init() {
        if (initialState.reactorType == ReactorType.ACTIVE) {
            // Initialize an actively-cooled reactor screen.
            this.getMinecraft().displayGuiScreen(new ActiveReactorTerminalScreen(this.container, this.playerInventory, this.title));
        } else {
            // Initialize a passively-cooled reactor screen.
            this.getMinecraft().displayGuiScreen(new PassiveReactorTerminalScreen(this.container, this.playerInventory, this.title));
        }
    }

    /**
     * Initialize common/shared tooltips.
     *
     * @param screen The screen to initialize.
     */
    public static void initTooltips(@Nonnull ScreenBase<ReactorTerminalContainer> screen, ReactorState reactorState) {
        // (Left) Temperature tooltip:
        screen.addElement(new Tooltip<>(screen, 8, 19, 16, 16, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.temperature.tooltip")));

        // (Left) Fuel consumption rate tooltip:
        screen.addElement(new Tooltip<>(screen, 8, 57, 16, 16, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.fuel_usage_rate.tooltip")));

        // (Left) Reactivity rate tooltip:
        screen.addElement(new Tooltip<>(screen, 8, 76, 16, 16, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.reactivity_rate.tooltip")));

        // (Top) Fuel mix gauge tooltip:
        screen.addElement(new Tooltip<>(screen, 86, 6, 16, 16, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.fuel_mix.tooltip")));

        // (Top) Case heat gauge tooltip:
        screen.addElement(new Tooltip<>(screen, 108, 6, 16, 16, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.case_heat.tooltip")));

        // (Top) Fuel heat gauge tooltip:
        screen.addElement(new Tooltip<>(screen, 130, 6, 16, 16, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.fuel_heat.tooltip")));
    }

    /**
     * Initialize common/shared controls.
     *
     * @param screen The screen to initialize.
     */
    public static void initControls(@Nonnull ScreenBase<ReactorTerminalContainer> screen, ReactorState reactorState) {
        // (Left) Activity toggle:
        Biselector<ReactorTerminalContainer> activityToggle = new Biselector<>(screen, 8, 98, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.activity_toggle.tooltip"),
                reactorState.reactorActivity.toInt(), SelectorColors.RED, SelectorColors.GREEN);
        activityToggle.onMouseReleased = (mX, mY, btn) -> {
            // Click logic.
            screen.getContainer().executeRequest("setActive", activityToggle.getState());
            return true;
        };
        screen.addElement(activityToggle);

        // (Left) Auto-eject toggle:
        Biselector<ReactorTerminalContainer> autoEjectToggle = new Biselector<>(screen, 8, 114, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.auto_eject_toggle.tooltip"),
                reactorState.doAutoEject ? 1 : 0, SelectorColors.RED, SelectorColors.CYAN);
        autoEjectToggle.onMouseReleased = (mX, mY, btn) -> {
            // Click logic.
            screen.getContainer().executeRequest("setAutoEject", autoEjectToggle.getState());
            return true;
        };
        screen.addElement(autoEjectToggle);

        // (Left) Manual eject button:
        Button<ReactorTerminalContainer> manualEjectButton = new Button<>(screen, 8, 130, 15, 15, 226, 0, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.manual_eject.tooltip"));
        manualEjectButton.onMouseReleased = (mX, mY, btn) -> {
            // Click logic. Extra check necessary since this is an "in-class" button.
            if (manualEjectButton.isMouseOver(mX, mY)) {
                // Mouse is hovering, do the thing.
                //screen.getContainer().executeRequest("ejectWaste", true);
                Minecraft.getInstance().player.sendChatMessage("No effect. This button will be removed in the future.");
                Minecraft.getInstance().player.sendChatMessage("Use the access ports to eject waste!");
                // Play the selection sound.
                manualEjectButton.playSound(SoundEvents.UI_BUTTON_CLICK);
                return true;
            } else {
                // It ain't hovered, don't do the thing.
                return false;
            }
        };
        manualEjectButton.onRender = ((mS, mX, mY) -> {
            // Custom rendering.
            if (manualEjectButton.isMouseOver(mX, mY)) {
                // Mouse is hovering, highlight it.
                manualEjectButton.blit(mS, 241, 0);
            } else {
                // It ain't hovered, don't highlight.
                manualEjectButton.blit(mS, 226, 0);
            }
        });
        // TODO: Remove with reactor manual eject.
        // This element will be removed soon, in favor of having the eject button be in the access ports.
        //screen.addElement(manualEjectButton);
    }

    /**
     * Initialize common/shared gauges.
     *
     * @param screen The screen to initialize.
     */
    public static void initGauges(@Nonnull ScreenBase<ReactorTerminalContainer> screen, ReactorState reactorState) {
        // (Top) Fuel mix gauge:
        Symbol<ReactorTerminalContainer> fuelMixGauge = new Symbol<>(screen, 85, 25, 18, 64, 0, 152, StringTextComponent.EMPTY);
        fuelMixGauge.onRender = (@Nonnull MatrixStack mS, int mX, int mY) -> CommonReactorTerminalScreen.renderFuelMixGauge(mS, fuelMixGauge, reactorState.wasteStored, reactorState.fuelStored, reactorState.fuelCapacity);
        screen.addElement(fuelMixGauge);

        // (Top) Case heat gauge:
        Symbol<ReactorTerminalContainer> caseHeatGauge = new Symbol<>(screen, 107, 25, 18, 64, 0, 152, StringTextComponent.EMPTY);
        caseHeatGauge.onRender = (@Nonnull MatrixStack mS, int mX, int mY) -> CommonReactorTerminalScreen.renderHeatGauge(mS, caseHeatGauge, reactorState.caseHeatStored, Config.Reactor.GUI.HeatDisplayMax);
        screen.addElement(caseHeatGauge);

        // (Top) Fuel heat gauge:
        Symbol<ReactorTerminalContainer> fuelHeatGauge = new Symbol<>(screen, 129, 25, 18, 64, 0, 152, StringTextComponent.EMPTY);
        fuelHeatGauge.onRender = (@Nonnull MatrixStack mS, int mX, int mY) -> CommonReactorTerminalScreen.renderHeatGauge(mS, fuelHeatGauge, reactorState.caseHeatStored, Config.Reactor.GUI.HeatDisplayMax);
        screen.addElement(fuelHeatGauge);
    }

    /**
     * Initialize common/shared symbols.
     *
     * @param screen The screen to initialize.
     */
    public static void initSymbols(@Nonnull ScreenBase<ReactorTerminalContainer> screen, ReactorState reactorState) {
        // None (yet).
    }

    /**
     * Render a reactor heat gauge.
     *
     * @param mStack       The current matrix stack.
     * @param symbol       The symbol to draw as.
     * @param heatStored   The heat value to draw.
     * @param heatCapacity The max heat capacity this gauge can display.
     */
    public static void renderHeatGauge(@Nonnull MatrixStack mStack, @Nonnull Symbol<ReactorTerminalContainer> symbol, double heatStored, double heatCapacity) {
        // If there's no heat, there's no need to draw.
        if (heatStored > 0) {
            // Calculate how much needs to be rendered.
            int renderSize = (int) ((symbol.height * heatStored) / heatCapacity);
            // Render heat.
            symbol.blit(mStack, symbol.u + 72, symbol.v);
            // Render backdrop/mask away extra heat.
            symbol.blit(mStack, symbol.width, symbol.height - renderSize, symbol.u + 18, symbol.v);
        }
        // Draw frame.
        symbol.blit(mStack);
        // Update tooltip.
        symbol.tooltip = new StringTextComponent(String.format("%.1f/%.1f \u00B0C", heatStored, heatCapacity));
    }

    /**
     * Render a reactor fuel mix gauge.
     *
     * @param mStack       The current matrix stack.
     * @param symbol       The symbol to draw as.
     * @param wasteStored  The waste value to draw.
     * @param fuelStored   The fuel value to draw.
     * @param fuelCapacity The max fuel capacity this gauge can display.
     */
    public static void renderFuelMixGauge(@Nonnull MatrixStack mStack, @Nonnull Symbol<ReactorTerminalContainer> symbol, double wasteStored, double fuelStored, double fuelCapacity) {
        // If there's no fuel or waste, there's no need to draw.
        if (wasteStored > 0 || fuelStored > 0) {
            // Calculate how much needs to be rendered.
            int wasteRenderSize = (int) ((symbol.height * wasteStored) / fuelCapacity);
            int fuelRenderSize = (int) ((symbol.height * fuelStored) / fuelCapacity);
            // Render waste.
            symbol.blit(mStack, symbol.u + 54, symbol.v);
            // Render fuel on top of waste.
            symbol.blit(mStack, symbol.width, symbol.height - (wasteRenderSize), symbol.u + 36, symbol.v);
            // Render backdrop/mask away extra waste and fuel.
            symbol.blit(mStack, symbol.width, symbol.height - (wasteRenderSize + fuelRenderSize), symbol.u + 18, symbol.v);
        }
        // Draw frame.
        symbol.blit(mStack);
        // Update tooltip.
        symbol.tooltip = new StringTextComponent(String.format("\u00A76%s\u00A7r+\u00A7b%s\u00A7r/%s",
                RenderHelper.formatValue(fuelStored / 1000.0, null, true),
                RenderHelper.formatValue(wasteStored / 1000.0, null, true),
                RenderHelper.formatValue(fuelCapacity / 1000.0, "B", true)));
    }

    /**
     * Render status text.
     *
     * @param mStack         The current matrix stack.
     * @param reactorActivity The reactor status to draw.
     * @param doAutoEject     The auto-eject status to draw.
     * @param heatStored     The heat value to draw.
     * @param fuelUsageRate  The fuel usage rate to draw.
     * @param reactivityRate The reactivity rate to draw.
     * @implNote Output rate is not rendered by this function, since it changes depending on reactor type. Do that yourself.
     */
    public static void renderStatusText(@Nonnull MatrixStack mStack, @Nonnull ScreenBase<ReactorTerminalContainer> screen, ReactorActivity reactorActivity, boolean doAutoEject, double heatStored, double fuelUsageRate, double reactivityRate) {
        // Render text for reactor temperature (no fancy suffix for Celsius):
        screen.getFont().drawString(mStack, String.format("%.0f \u00B0C", heatStored), screen.getGuiLeft() + 27, screen.getGuiTop() + 23, 4210752);

        // Render text for fuel consumption rate:
        screen.getFont().drawString(mStack, RenderHelper.formatValue((fuelUsageRate / 1000.0), 3, "B/t", true), screen.getGuiLeft() + 27, screen.getGuiTop() + 61, 4210752);

        // Render text for reactivity rate (no fancy suffix for percentages):
        screen.getFont().drawString(mStack, String.format("%.1f%%", (reactivityRate * 100.0)), screen.getGuiLeft() + 27, screen.getGuiTop() + 80, 4210752);

        // Render text for online/offline status:
        if (reactorActivity == ReactorActivity.ACTIVE) {
            // Text for an online reactor:
            screen.getFont().drawString(mStack, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.activity_toggle.online").getString(), screen.getGuiLeft() + 42, screen.getGuiTop() + 102, 4210752);

        } else {
            // Text for an offline reactor:
            screen.getFont().drawString(mStack, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.activity_toggle.offline").getString(), screen.getGuiLeft() + 42, screen.getGuiTop() + 102, 4210752);
        }

        // Render text for auto-eject status:
        if (doAutoEject) {
            // Text for enabled auto-ejection:
            screen.getFont().drawString(mStack, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.auto_eject_toggle.enabled").getString(), screen.getGuiLeft() + 42, screen.getGuiTop() + 118, 4210752);
        } else {
            // Text for disabled auto-ejection:
            screen.getFont().drawString(mStack, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.auto_eject_toggle.disabled").getString(), screen.getGuiLeft() + 42, screen.getGuiTop() + 118, 4210752);
        }

        // Render text for manual eject button:
        // TODO: Remove with reactor manual eject.
        //screen.getFont().drawString(mStack, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.manual_eject").getString(), screen.getGuiLeft() + 26, screen.getGuiTop() + 134, 4210752);
    }
}
