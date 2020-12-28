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
import net.roguelogix.biggerreactors.classic.reactor.containers.ReactorAccessPortContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorAccessPortState;
import net.roguelogix.biggerreactors.client.Biselector;
import net.roguelogix.biggerreactors.client.SelectorColors;
import net.roguelogix.phosphophyllite.gui.client.ScreenBase;
import net.roguelogix.phosphophyllite.gui.client.elements.Button;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class ReactorAccessPortScreen extends ScreenBase<ReactorAccessPortContainer> {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(BiggerReactors.modid, "textures/screen/reactor_access_port.png");

    private ReactorAccessPortState reactorAccessPortState;

    public ReactorAccessPortScreen(ReactorAccessPortContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title, DEFAULT_TEXTURE, 142, 72);

        // Initialize access port state.
        reactorAccessPortState = (ReactorAccessPortState) this.getContainer().getGuiPacket();
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
        // (Left) Direction toggle:
        Biselector<ReactorAccessPortContainer> directionToggle = new Biselector<>(this, 8, 18, new TranslationTextComponent("screen.biggerreactors.reactor_access_port.direction_toggle.tooltip"),
                () -> reactorAccessPortState.direction ? 0 : 1, SelectorColors.YELLOW, SelectorColors.BLUE);
        directionToggle.onMouseReleased = (mX, mY, btn) -> {
            // Click logic.
            this.getContainer().executeRequest("setDirection", directionToggle.getState() == 0 ? 1 : 0);
            return true;
        };
        this.addElement(directionToggle);

        // (Left) Fuel mode toggle:
        Biselector<ReactorAccessPortContainer> fuelModeToggle = new Biselector<>(this, 8, 34, new TranslationTextComponent("screen.biggerreactors.reactor_access_port.fuel_mode_toggle.tooltip"),
                () -> reactorAccessPortState.fuelMode ? 1 : 0, SelectorColors.CYAN, SelectorColors.YELLOW);
        fuelModeToggle.onMouseReleased = (mX, mY, btn) -> {
            // Click logic.
            this.getContainer().executeRequest("setFuelMode", fuelModeToggle.getState() == 0 ? 1 : 0);
            return true;
        };
        fuelModeToggle.onTick = () -> {
            // Check if the element should be enabled.
            fuelModeToggle.actionEnable = (directionToggle.getState() != 0);
        };
        this.addElement(fuelModeToggle);

        // (Left) Manual eject button:
        Button<ReactorAccessPortContainer> manualEjectButton = new Button<>(this, 8, 50, 15, 15, 226, 0, new TranslationTextComponent("screen.biggerreactors.reactor_access_port.manual_eject.tooltip"));
        manualEjectButton.onMouseReleased = (mX, mY, btn) -> {
            // Click logic. Extra check necessary since this is an "in-class" button.
            if (manualEjectButton.isMouseOver(mX, mY)) {
                // Mouse is hovering, do the thing.
                this.getContainer().executeRequest("ejectWaste", true);
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
        this.addElement(manualEjectButton);
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

        // Render text for input/output direction:
        if (reactorAccessPortState.direction) {
            // Text for an inlet:
            this.getFont().drawString(mStack, new TranslationTextComponent("screen.biggerreactors.reactor_access_port.direction_toggle.input").getString(), this.getGuiLeft() + 42, this.getGuiTop() + 22, 4210752);

        } else {
            // Text for an outlet:
            this.getFont().drawString(mStack, new TranslationTextComponent("screen.biggerreactors.reactor_access_port.direction_toggle.output").getString(), this.getGuiLeft() + 42, this.getGuiTop() + 22, 4210752);
        }

        // Check if we render output type:
        if (!reactorAccessPortState.direction) {
            // Render text for fuel/waste mode:
            if (reactorAccessPortState.fuelMode) {
                // Text for an inlet:
                this.getFont().drawString(mStack, new TranslationTextComponent("screen.biggerreactors.reactor_access_port.fuel_mode_toggle.fuel").getString(), this.getGuiLeft() + 42, this.getGuiTop() + 38, 4210752);

            } else {
                // Text for an outlet:
                this.getFont().drawString(mStack, new TranslationTextComponent("screen.biggerreactors.reactor_access_port.fuel_mode_toggle.waste").getString(), this.getGuiLeft() + 42, this.getGuiTop() + 38, 4210752);
            }
        } else {
            // Text for no output:
            this.getFont().drawString(mStack, new TranslationTextComponent("screen.biggerreactors.reactor_access_port.fuel_mode_toggle.nope").getString(), this.getGuiLeft() + 42, this.getGuiTop() + 38, 4210752);
        }

        // Render text for manual waste eject:
        this.getFont().drawString(mStack, new TranslationTextComponent("screen.biggerreactors.reactor_access_port.manual_eject").getString(), this.getGuiLeft() + 26, this.getGuiTop() + 54, 4210752);
    }
}