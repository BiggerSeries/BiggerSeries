package net.roguelogix.biggerreactors.classic.reactor.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.reactor.containers.ReactorRedstonePortContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorRedstonePortSelection;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorRedstonePortState;
import net.roguelogix.biggerreactors.client.Biselector;
import net.roguelogix.biggerreactors.client.SelectorColors;
import net.roguelogix.biggerreactors.client.TextBox;
import net.roguelogix.biggerreactors.client.Triselector;
import net.roguelogix.phosphophyllite.gui.client.ScreenBase;
import net.roguelogix.phosphophyllite.gui.client.elements.Button;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class ReactorRedstonePortScreen extends ScreenBase<ReactorRedstonePortContainer> {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(BiggerReactors.modid, "textures/screen/reactor_redstone_port.png");

    private static final String[] INPUT_TRANSLATIONS = new String[]{
            "screen.biggerreactors.reactor_redstone_port.input_reactor_activity",
            "screen.biggerreactors.reactor_redstone_port.input_control_rod_insertion",
            "screen.biggerreactors.reactor_redstone_port.input_eject_waste"
    };

    private static final String[] OUTPUT_TRANSLATIONS = new String[]{
            "screen.biggerreactors.reactor_redstone_port.output_fuel_temp",
            "screen.biggerreactors.reactor_redstone_port.output_casing_temp",
            "screen.biggerreactors.reactor_redstone_port.output_fuel_enrichment",
            "screen.biggerreactors.reactor_redstone_port.output_fuel_amount",
            "screen.biggerreactors.reactor_redstone_port.output_waste_amount",
            "screen.biggerreactors.reactor_redstone_port.output_output_stored"
    };

    private ReactorRedstonePortState reactorRedstonePortState;

    public ReactorRedstonePortScreen(ReactorRedstonePortContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title, DEFAULT_TEXTURE, 178, 178);
        // Initialize access port state.
        reactorRedstonePortState = (ReactorRedstonePortState) this.getContainer().getGuiPacket();
    }

    /**
     * Initialize the screen.
     */
    @Override
    public void init() {
        super.init();

        // Set title to be drawn in the center.
        this.titleX = (this.getWidth() / 2) - (this.getFont().getStringPropertyWidth(this.getTitle()) / 2);

        // Initialize tooltips:

        // Initialize controls:
        this.initControls();

        // Initialize gauges:

        // Initialize symbols:
    }

    /**
     * Initialize controls.
     */
    public void initControls() {
        // Requst names for redstone port:
        // setSelectedTab (int)
        // setTriggerPS (bool)
        // setTriggerAB (bool)
        // setTriggerMode (int)
        // setTextBufferA (String)
        // setTextBufferB (String)
        // revertChanges (void)
        // commitChanges (void)


        // TODO: Figure out why tabs can't be clicked, while tooltips show up fine.


        // (Left) Add input tab buttons:
        for (int i = 0; i < 3; i++) {
            final int cI = i;
            final Button<ReactorRedstonePortContainer> inputTab = new Button<>(this, 0, (cI * 25), 25, 24, 206, (cI * 24), new TranslationTextComponent(INPUT_TRANSLATIONS[cI] + ".tooltip"));
            inputTab.onMouseReleased = (mX, mY, btn) -> {
                // Click logic. Extra check necessary since this is an "in-class" button.
                if (inputTab.isMouseOver(mX, mY)) {
                    // Mouse is hovering, do the thing.
                    this.getContainer().executeRequest("setSelectedTab", cI);
                    // Play the selection sound.
                    inputTab.playSound(SoundEvents.UI_BUTTON_CLICK);
                    return true;
                } else {
                    // It ain't hovered, don't do the thing.
                    return false;
                }
            };
            inputTab.onRender = ((mS, mX, mY) -> {
                // Custom rendering.
                if (inputTab.actionEnable) {
                    // Tab is selected.
                    inputTab.blit(mS, 231, (cI * 24));
                } else {
                    // Tab is not selected.
                    inputTab.blit(mS, 206, (cI * 24));
                }
            });
            inputTab.onTick = () -> {
                // Check if this tab is selected.
                inputTab.actionEnable = (reactorRedstonePortState.selectedTab == ReactorRedstonePortSelection.fromInt(cI));
            };
            this.addElement(inputTab);
        }

        // (Right) Add output tab buttons:
        /*
        for (int i = 0; i < 6; i++) {
            final int cI = i;
            Button<ReactorRedstonePortContainer> outputTab = new Button<>(this, 153, (cI * 25), 25, 24, 206, (cI * 24) + 72, new TranslationTextComponent(OUTPUT_TRANSLATIONS[cI] + ".tooltip"));
            outputTab.onMouseReleased = (mX, mY, btn) -> {
                // Click logic. Extra check necessary since this is an "in-class" button.
                if (outputTab.isMouseOver(mX, mY)) {
                    // Mouse is hovering, do the thing.
                    this.getContainer().executeRequest("setSelectedTab", cI);
                    // Play the selection sound.
                    outputTab.playSound(SoundEvents.UI_BUTTON_CLICK);
                    return true;
                } else {
                    // It ain't hovered, don't do the thing.
                    return false;
                }
            };
            outputTab.onRender = (mS, mX, mY) -> {
                // Custom rendering.
                if (outputTab.actionEnable) {
                    // Tab is selected.
                    outputTab.blit(mS, 231, (cI * 24) + 72);
                } else {
                    // Tab is not selected.
                    outputTab.blit(mS, 206, (cI * 24) + 72);
                }
            };
            outputTab.onTick = () -> {
                // Check if this tab is selected.
                outputTab.actionEnable = (reactorRedstonePortState.selectedTab == ReactorRedstonePortSelection.fromInt(cI));
            };
            this.addElement(outputTab);
        }
         */

        // (Bottom) Commit changes button:
        Button<ReactorRedstonePortContainer> commitChangesButton = new Button<>(this, 135, 155, 15, 15, 226, 216, new TranslationTextComponent("screen.biggerreactors.reactor_redstone_port.commit_changes.tooltip"));
        commitChangesButton.onMouseReleased = (mX, mY, btn) -> {
            // Click logic. Extra check necessary since this is an "in-class" button.
            if (commitChangesButton.isMouseOver(mX, mY)) {
                // Mouse is hovering, do the thing.
                this.getContainer().executeRequest("commitChanges", 0);
                // Play the selection sound.
                commitChangesButton.playSound(SoundEvents.UI_BUTTON_CLICK);
                return true;
            } else {
                // It ain't hovered, don't do the thing.
                return false;
            }
        };
        commitChangesButton.onRender = (mS, mX, mY) -> {
            // Custom rendering.
            if (commitChangesButton.isMouseOver(mX, mY)) {
                // Mouse is hovering, highlight it.
                commitChangesButton.blit(mS, 241, 216);
            } else {
                // It ain't hovered, don't highlight.
                commitChangesButton.blit(mS, 226, 216);
            }
        };
        this.addElement(commitChangesButton);

        // (Bottom) Revert changes button:
        Button<ReactorRedstonePortContainer> revertChangesButton = new Button<>(this, 116, 155, 15, 15, 226, 231, new TranslationTextComponent("screen.biggerreactors.reactor_redstone_port.trigger_mode_toggle.tooltip"));
        revertChangesButton.onMouseReleased = (mX, mY, btn) -> {
            // Click logic. Extra check necessary since this is an "in-class" button.
            if (revertChangesButton.isMouseOver(mX, mY)) {
                // Mouse is hovering, do the thing.
                this.getContainer().executeRequest("revertChanges", 0);
                // Play the selection sound.
                revertChangesButton.playSound(SoundEvents.UI_BUTTON_CLICK);
                return true;
            } else {
                // It ain't hovered, don't do the thing.
                return false;
            }
        };
        revertChangesButton.onRender = (mS, mX, mY) -> {
            // Custom rendering.
            if (revertChangesButton.isMouseOver(mX, mY)) {
                // Mouse is hovering, highlight it.
                revertChangesButton.blit(mS, 241, 231);
            } else {
                // It ain't hovered, don't highlight.
                revertChangesButton.blit(mS, 216, 231);
            }
        };
        this.addElement(revertChangesButton);

        // (Left) Trigger type toggle:
        Biselector<ReactorRedstonePortContainer> triggerTypeToggle = new Biselector<>(this, 8, 34, new TranslationTextComponent("screen.biggerreactors.reactor_redstone_port.toggle_input_trigger.tooltip"),
                (reactorRedstonePortState.triggerPS.toBool() || reactorRedstonePortState.triggerAB.toBool())
                        ? 1 : 0, SelectorColors.GREEN, SelectorColors.RED);
        triggerTypeToggle.onMouseReleased = (mX, mY, btn) -> {
            // Click logic. Check if PS or AB.
            if (reactorRedstonePortState.isInput()) {
                // This is an input, so update the PS value.
                this.getContainer().executeRequest("setTriggerPS", triggerTypeToggle.getState() != 0);
            } else {
                // This is an output, so update the AB value.
                this.getContainer().executeRequest("setTriggerAB", triggerTypeToggle.getState() != 0);
            }
            return true;
        };
        triggerTypeToggle.onTick = () -> {
            // Check if the element should be PS or AB, and update.
            if (reactorRedstonePortState.isInput()) {
                // This is an input, so use the PS tooltip.
                triggerTypeToggle.tooltip = new TranslationTextComponent("screen.biggerreactors.reactor_redstone_port.toggle_input_trigger.tooltip");
            } else {
                // This is an output, so use the AB tooltip.
                triggerTypeToggle.tooltip = new TranslationTextComponent("screen.biggerreactors.reactor_redstone_port.toggle_output_trigger.tooltip");
            }
        };
        this.addElement(triggerTypeToggle);

        // (Left) Trigger mode toggle:
        Triselector<ReactorRedstonePortContainer> triggerModeToggle = new Triselector<>(this, 8, 114, new TranslationTextComponent("screen.biggerreactors.reactor_terminal.auto_eject_toggle.tooltip"),
                reactorRedstonePortState.triggerMode, SelectorColors.GREEN, SelectorColors.RED, SelectorColors.YELLOW);
        triggerModeToggle.onMouseReleased = (mX, mY, btn) -> {
            // Click logic.
            this.getContainer().executeRequest("setTriggerMode", triggerModeToggle.getState());
            return true;
        };
        this.addElement(triggerModeToggle);

        // (Top) Text buffer A:
        TextBox<ReactorRedstonePortContainer> textBufferA = new TextBox<>(this, this.font, 6, 100, 96, 16, reactorRedstonePortState.textBufferA);
        this.addElement(textBufferA);

        // (Top) Text buffer A enter button:
        Button<ReactorRedstonePortContainer> textBufferAEnterButton = new Button<>(this, 114, 101, 17, 14, 192, 216, new TranslationTextComponent("screen.biggerreactors.reactor_redstone_port.apply.tooltip"));
        textBufferAEnterButton.onMouseReleased = (mX, mY, btn) -> {
            // Click logic. Extra check necessary since this is an "in-class" button.
            if (textBufferAEnterButton.isMouseOver(mX, mY)) {
                // Mouse is hovering, do the thing.
                this.getContainer().executeRequest("setTextBufferA", textBufferA.getContents());
                // Play the selection sound.
                textBufferAEnterButton.playSound(SoundEvents.UI_BUTTON_CLICK);
                return true;
            } else {
                // It ain't hovered, don't do the thing.
                return false;
            }
        };
        textBufferAEnterButton.onRender = ((mS, mX, mY) -> {
            // Custom rendering.
            if (textBufferAEnterButton.isMouseOver(mX, mY)) {
                // Mouse is hovering, highlight it.
                textBufferAEnterButton.blit(mS, 209, 216);
            } else {
                // It ain't hovered, don't highlight.
                textBufferAEnterButton.blit(mS, 192, 216);
            }
        });
        this.addElement(textBufferAEnterButton);

        // (Top) Text buffer B:
        TextBox<ReactorRedstonePortContainer> textBufferB = new TextBox<>(this, this.font, 6, 130, 96, 16, reactorRedstonePortState.textBufferB);
        this.addElement(textBufferB);

        // (Top) Text buffer B enter button:
        Button<ReactorRedstonePortContainer> textBufferBEnterButton = new Button<>(this, 114, 131, 17, 14, 192, 216, new TranslationTextComponent("screen.biggerreactors.reactor_redstone_port.apply.tooltip"));
        textBufferBEnterButton.onMouseReleased = (mX, mY, btn) -> {
            // Click logic. Extra check necessary since this is an "in-class" button.
            if (textBufferBEnterButton.isMouseOver(mX, mY)) {
                // Mouse is hovering, do the thing.
                this.getContainer().executeRequest("setTextBufferB", textBufferB.getContents());
                // Play the selection sound.
                textBufferBEnterButton.playSound(SoundEvents.UI_BUTTON_CLICK);
                return true;
            } else {
                // It ain't hovered, don't do the thing.
                return false;
            }
        };
        textBufferBEnterButton.onRender = ((mS, mX, mY) -> {
            // Custom rendering.
            if (textBufferBEnterButton.isMouseOver(mX, mY)) {
                // Mouse is hovering, highlight it.
                textBufferBEnterButton.blit(mS, 209, 216);
            } else {
                // It ain't hovered, don't highlight.
                textBufferBEnterButton.blit(mS, 192, 216);
            }
        });
        this.addElement(textBufferBEnterButton);
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

        // Render text for selection tab.
        if (reactorRedstonePortState.isInput()) {
            // Text for an input:
            this.getFont().drawString(mStack, new TranslationTextComponent(INPUT_TRANSLATIONS[reactorRedstonePortState.selectedTab.toInt()]).getString(), this.getGuiLeft() + 42, this.getGuiTop() + 22, 4210752);

        } else {
            // Text for an output:
            this.getFont().drawString(mStack, new TranslationTextComponent(OUTPUT_TRANSLATIONS[reactorRedstonePortState.selectedTab.toInt() - 3]).getString(), this.getGuiLeft() + 42, this.getGuiTop() + 22, 4210752);
        }
    }
}
