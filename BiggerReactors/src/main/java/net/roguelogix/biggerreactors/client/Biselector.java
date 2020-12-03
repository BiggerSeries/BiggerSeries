package net.roguelogix.biggerreactors.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.phosphophyllite.gui.client.RenderHelper;
import net.roguelogix.phosphophyllite.gui.client.ScreenBase;
import net.roguelogix.phosphophyllite.gui.client.elements.Button;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class Biselector<T extends Container> extends Button<T> {

    /**
     * Selector state for the switch.
     */
    private int state;

    /**
     * The color of the left selector.
     */
    private final SelectorColors leftColor;

    /**
     * The color of the right selector.
     */
    private final SelectorColors rightColor;

    /**
     * Default constructor.
     *
     * @param parent       The parent screen of this element.
     * @param x            The x position of this element.
     * @param y            The y position of this element.
     * @param tooltip      The tooltip for this element. If null, a tooltip will not render. If you set a tooltip later, use StringTextComponent.EMPTY.
     * @param initialState The initial switch state to use.
     */
    public Biselector(@Nonnull ScreenBase<T> parent, int x, int y, @Nullable ITextComponent tooltip, int initialState, SelectorColors leftColor, SelectorColors rightColor) {
        super(parent, x, y, 31, 14, 0, 64, tooltip);
        this.state = initialState;
        this.leftColor = leftColor;
        this.rightColor = rightColor;
    }

    /**
     * Get the current switch state.
     *
     * @return The current switch state.
     */
    public int getState() {
        return this.state;
    }

    /**
     * Render element.
     *
     * @param mStack The current matrix stack.
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     */
    @Override
    public void render(@Nonnull MatrixStack mStack, int mouseX, int mouseY) {
        // Check conditions.
        if (this.renderEnable) {
            // Preserve the previously selected texture and bind the common texture.
            ResourceLocation preservedResource = RenderHelper.getCurrentResource();
            RenderHelper.bindTexture(CommonRender.COMMON_RESOURCE_TEXTURE);
            // Draw the selector frame.
            if (this.state == 0) {
                // Position is 0 (left), draw left frame.
                this.blit(mStack, 0, 64);
                // Check where the mouse is.
                if (this.isMouseOver(mouseX, mouseY)) {
                    // Draw active/hovered button.
                    this.blit(mStack, this.x + 1, this.y + 1, leftColor.uA, leftColor.vA, 14, 12);
                } else {
                    // Draw inactive/non-hovered button.
                    this.blit(mStack, this.x + 1, this.y + 1, leftColor.uI, leftColor.vI, 14, 12);
                }
                // Check if the selector is enabled.
                if (!this.actionEnable) {
                    // Draw disabled color overlay.
                    this.blit(mStack, this.x, this.y, 210, 0, 31, 14);
                }

            } else {
                // Position is 1 (right), draw right frame.
                this.blit(mStack, 0, 78);
                // Check if the selector is enabled.
                // Check where the mouse is.
                if (this.isMouseOver(mouseX, mouseY)) {
                    // Draw active/hovered button.
                    this.blit(mStack, this.x + 16, this.y + 1, rightColor.uA, rightColor.vA, 14, 12);
                } else {
                    // Draw inactive/non-hovered button.
                    this.blit(mStack, this.x + 16, this.y + 1, rightColor.uI, rightColor.vI, 14, 12);
                }
                // Check if the selector is enabled.
                if (!this.actionEnable) {
                    // Draw disabled color overlay.
                    this.blit(mStack, this.x, this.y, 210, 0, 31, 14);
                }
            }
            // Reset color and restore the previously bound texture.
            RenderHelper.clearRenderColor();
            RenderHelper.bindTexture(preservedResource);
            // Trigger user-defined render logic.
            if (this.onRender != null) {
                this.onRender.trigger(mStack, mouseX, mouseY);
            }
        }
    }

    /**
     * Triggered when the mouse is released.
     *
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param button The button clicked.
     * @return Whether the event was consumed.
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Check conditions.
        if (this.isMouseOver(mouseX, mouseY)) {
            // Get actual x position.
            int relativeX = this.parent.getGuiLeft() + this.x;
            // Check if the selector is enabled.
            if (this.actionEnable) {
                // Set side depending on position of click.
                if ((mouseX > relativeX) && (mouseX < relativeX + (int) (this.width / 2))) {
                    // Set to left position (0).
                    this.state = 0;
                } else {
                    // Set to right position (1).
                    this.state = 1;
                }
                // Play the selection sound.
                this.playSound(SoundEvents.UI_BUTTON_CLICK);
            } else {
                // Play the "nope" sound.
                this.playSound(SoundEvents.ENTITY_VILLAGER_NO);
            }
            // Trigger user-defined selection logic.
            if (this.onMouseReleased != null) {
                this.onMouseReleased.trigger(mouseX, mouseY, button);
            }
            // The event was consumed.
            return true;
        } else {
            // The event was not consumed.
            return false;
        }
    }
}
