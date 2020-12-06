package net.roguelogix.biggerreactors.classic.reactor.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.reactor.containers.ReactorCoolantPortContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorCoolantPortState;
import net.roguelogix.biggerreactors.client.Biselector;
import net.roguelogix.biggerreactors.client.SelectorColors;
import net.roguelogix.phosphophyllite.gui.client.ScreenBase;

import javax.annotation.Nonnull;

public class ReactorCoolantPortScreen extends ScreenBase<ReactorCoolantPortContainer> {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(BiggerReactors.modid, "textures/screen/reactor_coolant_port.png");

    private ReactorCoolantPortState reactorCoolantPortState;

    public ReactorCoolantPortScreen(ReactorCoolantPortContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title, DEFAULT_TEXTURE, 142, 72);

        // Initialize access port state.
        reactorCoolantPortState = (ReactorCoolantPortState) this.getContainer().getGuiPacket();
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
        Biselector<ReactorCoolantPortContainer> directionToggle = new Biselector<>(this, 8, 18, new TranslationTextComponent("screen.biggerreactors.reactor_coolant_port.direction_toggle.tooltip"),
                reactorCoolantPortState.direction ? 0 : 1, SelectorColors.CYAN, SelectorColors.RED);
        directionToggle.onMouseReleased = (mX, mY, btn) -> {
            // Click logic.
            this.getContainer().executeRequest("setDirection", directionToggle.getState());
            return true;
        };
        this.addElement(directionToggle);
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
        if (reactorCoolantPortState.direction) {
            // Text for an inlet:
            this.getFont().drawString(mStack, new TranslationTextComponent("screen.biggerreactors.reactor_coolant_port.direction_toggle.input").getString(), this.getGuiLeft() + 42, this.getGuiTop() + 22, 4210752);

        } else {
            // Text for an outlet:
            this.getFont().drawString(mStack, new TranslationTextComponent("screen.biggerreactors.reactor_coolant_port.direction_toggle.output").getString(), this.getGuiLeft() + 42, this.getGuiTop() + 22, 4210752);
        }
    }
}
