package net.roguelogix.biggerreactors.classic.reactor.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.reactor.containers.RedstonePortContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.RedstonePortSelector;
import net.roguelogix.biggerreactors.classic.reactor.state.RedstonePortState;
import net.roguelogix.biggerreactors.client.GuiInputBox;
import net.roguelogix.biggerreactors.client.redstoneport.GuiRedstoneCommitButton;
import net.roguelogix.biggerreactors.client.redstoneport.GuiRedstoneToggles;
import net.roguelogix.biggerreactors.client.redstoneport.GuiRedstoneTriggerSelectorToggle;
import net.roguelogix.phosphophyllite.gui.client.GuiScreenBase;

public class RedstonePortScreen extends GuiScreenBase<RedstonePortContainer> implements IHasContainer<RedstonePortContainer> {

    private RedstonePortState redstonePortState;

    // Jank buttons.
    private GuiRedstoneTriggerSelectorToggle<RedstonePortContainer> selectorInputActivity;
    private GuiRedstoneTriggerSelectorToggle<RedstonePortContainer> selectorInputControlRodInsertion;
    private GuiRedstoneTriggerSelectorToggle<RedstonePortContainer> selectorInputEjectWaste;
    private GuiRedstoneTriggerSelectorToggle<RedstonePortContainer> selectorOutputFuelTemp;
    private GuiRedstoneTriggerSelectorToggle<RedstonePortContainer> selectorOutputCasingTemp;
    private GuiRedstoneTriggerSelectorToggle<RedstonePortContainer> selectorOutputFuelEnrichment;
    private GuiRedstoneTriggerSelectorToggle<RedstonePortContainer> selectorOutputFuelAmount;
    private GuiRedstoneTriggerSelectorToggle<RedstonePortContainer> selectorOutputWasteAmount;
    private GuiRedstoneTriggerSelectorToggle<RedstonePortContainer> selectorOutputEnergyAmount;

    private GuiRedstoneCommitButton<RedstonePortContainer> commitButton;
    private GuiRedstoneCommitButton<RedstonePortContainer> revertButton;

    private GuiRedstoneToggles<RedstonePortContainer> togglePulseOrSignal;
    private GuiRedstoneToggles<RedstonePortContainer> toggleActiveAboveOrBelow;
    private GuiInputBox<RedstonePortContainer> textMainBuffer;
    private GuiInputBox<RedstonePortContainer> textSecondBuffer;

    public RedstonePortScreen(RedstonePortContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.redstonePortState = (RedstonePortState) this.getContainer().getGuiPacket();

        // Set textures.
        this.xSize = 176;
        this.ySize = 204;
        this.updateTexture(new ResourceLocation(BiggerReactors.modid, "textures/screen/redstone_port.png"), 0, 0);

        // Initialize selectors.
        // I reuse the translation strings for the screen.
        selectorInputActivity = new GuiRedstoneTriggerSelectorToggle<>(this, 8, 30, 16, 16,
                new TranslationTextComponent("screen.biggerreactors.status.redstone.input_activity").getString(), RedstonePortSelector.INPUT_ACTIVITY);
        selectorInputControlRodInsertion = new GuiRedstoneTriggerSelectorToggle<>(this, 28, 30, 16, 16,
                new TranslationTextComponent("screen.biggerreactors.status.redstone.input_control_rod_insertion").getString(), RedstonePortSelector.INPUT_CONTROL_ROD_INSERTION);
        selectorInputEjectWaste = new GuiRedstoneTriggerSelectorToggle<>(this, 48, 30, 16, 16,
                new TranslationTextComponent("screen.biggerreactors.status.redstone.input_eject_waste").getString(), RedstonePortSelector.INPUT_EJECT_WASTE);
        selectorOutputFuelTemp = new GuiRedstoneTriggerSelectorToggle<>(this, 68, 30, 16, 16,
                new TranslationTextComponent("screen.biggerreactors.status.redstone.output_fuel_temp").getString(), RedstonePortSelector.OUTPUT_FUEL_TEMP);
        selectorOutputCasingTemp = new GuiRedstoneTriggerSelectorToggle<>(this, 88, 30, 16, 16,
                new TranslationTextComponent("screen.biggerreactors.status.redstone.output_casing_temp").getString(), RedstonePortSelector.OUTPUT_CASING_TEMP);
        selectorOutputFuelEnrichment = new GuiRedstoneTriggerSelectorToggle<>(this, 8, 50, 16, 16,
                new TranslationTextComponent("screen.biggerreactors.status.redstone.output_fuel_enrichment").getString(), RedstonePortSelector.OUTPUT_FUEL_ENRICHMENT);
        selectorOutputFuelAmount = new GuiRedstoneTriggerSelectorToggle<>(this, 28, 50, 16, 16,
                new TranslationTextComponent("screen.biggerreactors.status.redstone.output_fuel_amount").getString(), RedstonePortSelector.OUTPUT_FUEL_AMOUNT);
        selectorOutputWasteAmount = new GuiRedstoneTriggerSelectorToggle<>(this, 48, 50, 16, 16,
                new TranslationTextComponent("screen.biggerreactors.status.redstone.output_waste_amount").getString(), RedstonePortSelector.OUTPUT_WASTE_AMOUNT);
        selectorOutputEnergyAmount = new GuiRedstoneTriggerSelectorToggle<>(this, 68, 50, 16, 16,
                new TranslationTextComponent("screen.biggerreactors.status.redstone.output_energy_amount").getString(), RedstonePortSelector.OUTPUT_ENERGY_AMOUNT);

        commitButton = new GuiRedstoneCommitButton<>(this, 152, 30, 16, 16, true);
        revertButton = new GuiRedstoneCommitButton<>(this, 152, 50, 16, 16, false);

        togglePulseOrSignal = new GuiRedstoneToggles<>(this, 8, 92, 16, 16, "", true);
        toggleActiveAboveOrBelow = new GuiRedstoneToggles<>(this, 8, 92, 16, 16, "", false);

        textMainBuffer = new GuiInputBox<>(this, 8, 124, true, "setMainBuffer", redstonePortState.mainBuffer);
        textSecondBuffer = new GuiInputBox<>(this, 8, 154, true, "setSecondBuffer", redstonePortState.secondBuffer);
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
        selectorInputActivity.mouseClicked(mouseX, mouseY, button);
        selectorInputControlRodInsertion.mouseClicked(mouseX, mouseY, button);
        selectorInputEjectWaste.mouseClicked(mouseX, mouseY, button);
        selectorOutputFuelTemp.mouseClicked(mouseX, mouseY, button);
        selectorOutputCasingTemp.mouseClicked(mouseX, mouseY, button);
        selectorOutputFuelEnrichment.mouseClicked(mouseX, mouseY, button);
        selectorOutputFuelAmount.mouseClicked(mouseX, mouseY, button);
        selectorOutputWasteAmount.mouseClicked(mouseX, mouseY, button);
        selectorOutputEnergyAmount.mouseClicked(mouseX, mouseY, button);

        commitButton.mouseClicked(mouseX, mouseY, button);
        revertButton.mouseClicked(mouseX, mouseY, button);

        switch (RedstonePortSelector.valueOf(this.redstonePortState.settingId)) {
            case INPUT_ACTIVITY:
            case INPUT_CONTROL_ROD_INSERTION: {
                // MODE BUTTON
                togglePulseOrSignal.mouseClicked(mouseX, mouseY, button);
                break;
            }
            case INPUT_EJECT_WASTE: {
                break;
            }
            case OUTPUT_FUEL_TEMP:
            case OUTPUT_CASING_TEMP:
            case OUTPUT_FUEL_ENRICHMENT:
            case OUTPUT_FUEL_AMOUNT:
            case OUTPUT_WASTE_AMOUNT:
            case OUTPUT_ENERGY_AMOUNT: {
                toggleActiveAboveOrBelow.mouseClicked(mouseX, mouseY, button);
                break;
            }
            default:
                break;
        }

        textMainBuffer.mouseClicked(mouseX, mouseY, button);
        textSecondBuffer.mouseClicked(mouseX, mouseY, button);

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
        selectorInputActivity.mouseReleased(mouseX, mouseY, button);
        selectorInputControlRodInsertion.mouseReleased(mouseX, mouseY, button);
        selectorInputEjectWaste.mouseReleased(mouseX, mouseY, button);
        selectorOutputFuelTemp.mouseReleased(mouseX, mouseY, button);
        selectorOutputCasingTemp.mouseReleased(mouseX, mouseY, button);
        selectorOutputFuelEnrichment.mouseReleased(mouseX, mouseY, button);
        selectorOutputFuelAmount.mouseReleased(mouseX, mouseY, button);
        selectorOutputWasteAmount.mouseReleased(mouseX, mouseY, button);
        selectorOutputEnergyAmount.mouseReleased(mouseX, mouseY, button);

        commitButton.mouseReleased(mouseX, mouseY, button);
        revertButton.mouseReleased(mouseX, mouseY, button);

        switch (RedstonePortSelector.valueOf(this.redstonePortState.settingId)) {
            case INPUT_ACTIVITY:
            case INPUT_CONTROL_ROD_INSERTION: {
                // MODE BUTTON
                togglePulseOrSignal.mouseReleased(mouseX, mouseY, button);
                textMainBuffer.mouseReleased(mouseX, mouseY, button);
                textSecondBuffer.mouseReleased(mouseX, mouseY, button);
                break;
            }
            case INPUT_EJECT_WASTE: {

            }
            case OUTPUT_FUEL_TEMP:
            case OUTPUT_CASING_TEMP:
            case OUTPUT_FUEL_ENRICHMENT:
            case OUTPUT_FUEL_AMOUNT:
            case OUTPUT_WASTE_AMOUNT:
            case OUTPUT_ENERGY_AMOUNT: {
                toggleActiveAboveOrBelow.mouseReleased(mouseX, mouseY, button);
                textMainBuffer.mouseReleased(mouseX, mouseY, button);
                break;
            }
            default:
                break;
        }

        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        textMainBuffer.keyPressed(keyCode, scanCode, modifiers);
        textSecondBuffer.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        textMainBuffer.charTyped(codePoint, modifiers);
        textSecondBuffer.charTyped(codePoint, modifiers);
        return true;
    }

    /**
     * Update logic.
     */
    @Override
    public void tick() {
        this.redstonePortState = (RedstonePortState) this.getContainer().getGuiPacket();

        selectorInputActivity.updateState(redstonePortState.settingId == RedstonePortSelector.valueOf(RedstonePortSelector.INPUT_ACTIVITY));
        selectorInputControlRodInsertion.updateState(redstonePortState.settingId == RedstonePortSelector.valueOf(RedstonePortSelector.INPUT_CONTROL_ROD_INSERTION));
        selectorInputEjectWaste.updateState(redstonePortState.settingId == RedstonePortSelector.valueOf(RedstonePortSelector.INPUT_EJECT_WASTE));
        selectorOutputFuelTemp.updateState(redstonePortState.settingId == RedstonePortSelector.valueOf(RedstonePortSelector.OUTPUT_FUEL_TEMP));
        selectorOutputCasingTemp.updateState(redstonePortState.settingId == RedstonePortSelector.valueOf(RedstonePortSelector.OUTPUT_CASING_TEMP));
        selectorOutputFuelEnrichment.updateState(redstonePortState.settingId == RedstonePortSelector.valueOf(RedstonePortSelector.OUTPUT_FUEL_ENRICHMENT));
        selectorOutputFuelAmount.updateState(redstonePortState.settingId == RedstonePortSelector.valueOf(RedstonePortSelector.OUTPUT_FUEL_AMOUNT));
        selectorOutputWasteAmount.updateState(redstonePortState.settingId == RedstonePortSelector.valueOf(RedstonePortSelector.OUTPUT_WASTE_AMOUNT));
        selectorOutputEnergyAmount.updateState(redstonePortState.settingId == RedstonePortSelector.valueOf(RedstonePortSelector.OUTPUT_ENERGY_AMOUNT));

        togglePulseOrSignal.updateState(redstonePortState.triggerPulseOrSignal);
        toggleActiveAboveOrBelow.updateState(redstonePortState.triggerAboveOrBelow);
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

        selectorInputActivity.drawTooltip(mStack, mouseX, mouseY);
        selectorInputControlRodInsertion.drawTooltip(mStack, mouseX, mouseY);
        selectorInputEjectWaste.drawTooltip(mStack, mouseX, mouseY);
        selectorOutputFuelTemp.drawTooltip(mStack, mouseX, mouseY);
        selectorOutputCasingTemp.drawTooltip(mStack, mouseX, mouseY);
        selectorOutputFuelEnrichment.drawTooltip(mStack, mouseX, mouseY);
        selectorOutputFuelAmount.drawTooltip(mStack, mouseX, mouseY);
        selectorOutputWasteAmount.drawTooltip(mStack, mouseX, mouseY);
        selectorOutputEnergyAmount.drawTooltip(mStack, mouseX, mouseY);

        commitButton.drawTooltip(mStack, mouseX, mouseY);
        revertButton.drawTooltip(mStack, mouseX, mouseY);
    }

    /**
     * Draw foreground elements.
     *
     * @param mouseX X position of the mouse.
     * @param mouseY Y position of the mouse.
     */
    @Override
    public void drawGuiContainerForegroundLayer(MatrixStack mStack, int mouseX, int mouseY) {
        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.reactor_redstone_port").getString(), 8, 6, 4210752);
        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.pick_setting").getString(), 8, 20, 4210752);

        selectorInputActivity.drawPart(mStack);
        selectorInputControlRodInsertion.drawPart(mStack);
        selectorInputEjectWaste.drawPart(mStack);
        selectorOutputFuelTemp.drawPart(mStack);
        selectorOutputCasingTemp.drawPart(mStack);
        selectorOutputFuelEnrichment.drawPart(mStack);
        selectorOutputFuelAmount.drawPart(mStack);
        selectorOutputWasteAmount.drawPart(mStack);
        selectorOutputEnergyAmount.drawPart(mStack);

        switch (RedstonePortSelector.valueOf(this.redstonePortState.settingId)) {
            case INPUT_ACTIVITY:
                renderInputActivityOptions(mStack);
                break;
            case INPUT_CONTROL_ROD_INSERTION:
                renderInputControlRodInsertion(mStack);
                break;
            case INPUT_EJECT_WASTE:
                renderInputEjectWaste(mStack);
                break;
            case OUTPUT_FUEL_TEMP:
                renderOutputFuelTemp(mStack);
                break;
            case OUTPUT_CASING_TEMP:
                renderOutputCasingTemp(mStack);
                break;
            case OUTPUT_FUEL_ENRICHMENT:
                renderOutputFuelEnrichment(mStack);
                break;
            case OUTPUT_FUEL_AMOUNT:
                renderOutputFuelAmount(mStack);
                break;
            case OUTPUT_WASTE_AMOUNT:
                renderOutputWasteAmount(mStack);
                break;
            case OUTPUT_ENERGY_AMOUNT:
                renderOutputEnergyAmount(mStack);
                break;
            default:
                break;
        }

        commitButton.drawPart(mStack);
        revertButton.drawPart(mStack);
    }

    private void renderInputActivityOptions(MatrixStack mStack) {
        this.togglePulseOrSignal.drawPart(mStack);
        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.input_activity").getString(), 8, 76, 4210752);

        if (redstonePortState.triggerPulseOrSignal) {
            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.set_from_signal").getString(), 26, 96, 4210752);
        } else {
            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.toggle_on_pulse").getString(), 26, 96, 4210752);
        }
    }

    private void renderInputControlRodInsertion(MatrixStack mStack) {
        this.togglePulseOrSignal.drawPart(mStack);
        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.input_control_rod_insertion").getString(), 8, 76, 4210752);

        if (redstonePortState.triggerPulseOrSignal) {
            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.set_from_signal").getString(), 26, 96, 4210752);

            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.while_on").getString(), 8, 114, 4210752);
            textMainBuffer.drawPart(mStack);
            this.font.drawString(mStack, "%", 110, 128, 4210752);
            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.while_off").getString(), 8, 144, 4210752);
            textSecondBuffer.drawPart(mStack);
            this.font.drawString(mStack, "%", 110, 158, 4210752);
        } else {
            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.set_on_pulse").getString(), 26, 96, 4210752);

            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.set_to").getString(), 8, 114, 4210752);
            textMainBuffer.drawPart(mStack);
            this.font.drawString(mStack, "%", 110, 128, 4210752);
        }
    }

    private void renderInputEjectWaste(MatrixStack mStack) {
        this.togglePulseOrSignal.drawPart(mStack);
        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.input_eject_waste").getString(), 8, 76, 4210752);

        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.eject_on_pulse").getString(), 26, 96, 4210752);
    }

    private void renderOutputFuelTemp(MatrixStack mStack) {
        this.toggleActiveAboveOrBelow.drawPart(mStack);
        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.output_fuel_temp").getString(), 8, 76, 4210752);

        if (redstonePortState.triggerAboveOrBelow) {
            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.active_while_below").getString(), 26, 96, 4210752);
        } else {
            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.active_while_above").getString(), 26, 96, 4210752);
        }

        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.trigger_at").getString(), 8, 114, 4210752);
        textMainBuffer.drawPart(mStack);
        this.font.drawString(mStack, "\u00B0C", 110, 128, 4210752);
    }

    private void renderOutputCasingTemp(MatrixStack mStack) {
        this.toggleActiveAboveOrBelow.drawPart(mStack);
        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.output_casing_temp").getString(), 8, 76, 4210752);

        if (redstonePortState.triggerAboveOrBelow) {
            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.active_while_below").getString(), 26, 96, 4210752);
        } else {
            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.active_while_above").getString(), 26, 96, 4210752);
        }

        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.trigger_at").getString(), 8, 114, 4210752);
        textMainBuffer.drawPart(mStack);
        this.font.drawString(mStack, "\u00B0C", 110, 128, 4210752);
    }

    private void renderOutputFuelEnrichment(MatrixStack mStack) {
        this.toggleActiveAboveOrBelow.drawPart(mStack);
        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.output_fuel_enrichment").getString(), 8, 76, 4210752);

        if (redstonePortState.triggerAboveOrBelow) {
            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.active_while_below").getString(), 26, 96, 4210752);
        } else {
            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.active_while_above").getString(), 26, 96, 4210752);
        }

        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.trigger_at").getString(), 8, 114, 4210752);
        textMainBuffer.drawPart(mStack);
        this.font.drawString(mStack, "%", 110, 128, 4210752);
    }

    private void renderOutputFuelAmount(MatrixStack mStack) {
        this.toggleActiveAboveOrBelow.drawPart(mStack);
        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.output_fuel_amount").getString(), 8, 76, 4210752);

        if (redstonePortState.triggerAboveOrBelow) {
            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.active_while_below").getString(), 26, 96, 4210752);
        } else {
            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.active_while_above").getString(), 26, 96, 4210752);
        }

        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.trigger_at").getString(), 8, 114, 4210752);
        textMainBuffer.drawPart(mStack);
        this.font.drawString(mStack, "mB", 110, 128, 4210752);
    }

    private void renderOutputWasteAmount(MatrixStack mStack) {
        this.toggleActiveAboveOrBelow.drawPart(mStack);
        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.output_waste_amount").getString(), 8, 76, 4210752);

        if (redstonePortState.triggerAboveOrBelow) {
            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.active_while_below").getString(), 26, 96, 4210752);
        } else {
            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.active_while_above").getString(), 26, 96, 4210752);
        }

        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.trigger_at").getString(), 8, 114, 4210752);
        textMainBuffer.drawPart(mStack);
        this.font.drawString(mStack, "mB", 110, 128, 4210752);
    }

    private void renderOutputEnergyAmount(MatrixStack mStack) {
        this.toggleActiveAboveOrBelow.drawPart(mStack);
        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.output_energy_amount").getString(), 8, 76, 4210752);

        if (redstonePortState.triggerAboveOrBelow) {
            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.active_while_below").getString(), 26, 96, 4210752);
        } else {
            this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.active_while_above").getString(), 26, 96, 4210752);
        }

        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.status.redstone.trigger_at").getString(), 8, 114, 4210752);
        textMainBuffer.drawPart(mStack);
        this.font.drawString(mStack, "%", 110, 128, 4210752);
    }
}
