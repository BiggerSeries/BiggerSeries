package net.roguelogix.biggerreactors.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.phosphophyllite.gui.client.RenderHelper;
import net.roguelogix.phosphophyllite.gui.client.ScreenBase;
import net.roguelogix.phosphophyllite.gui.client.elements.Button;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class CommonButton<T extends Container> extends Button<T> {

    /**
     * Default constructor.
     *
     * @param parent       The parent screen of this element.
     * @param x            The x position of this element.
     * @param y            The y position of this element.
     * @param width   The width of this element.
     * @param height  The height of this element.
     * @param u       The u offset to use when rendering this element (starting from the left, and moving right).
     * @param v       The v offset to use when rendering this element (starting from the top, and moving down).
     * @param tooltip      The tooltip for this element. If null, a tooltip will not render. If you set a tooltip later, use StringTextComponent.EMPTY.
     */
    public CommonButton(@Nonnull ScreenBase<T> parent, int x, int y, int width, int height, int u, int v, @Nullable ITextComponent tooltip) {
        super(parent, x, y, width, height, u, v, tooltip);
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
            // Check where the mouse is.
            if (this.isMouseOver(mouseX, mouseY)) {
                // Draw active/hovered button.
                this.blit(mStack, this.u, this.v + this.height);
            } else {
                // Draw inactive/non-hovered button.
                this.blit(mStack, this.u, this.v);
            }
            // Check if the button is enabled.
            if (!this.actionEnable) {
                // Draw disabled color overlay.
                this.blit(mStack,210, 0);
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
        if (this.actionEnable && this.isMouseOver(mouseX, mouseY)) {
            // Play the selection sound.
            this.playSound(SoundEvents.UI_BUTTON_CLICK);
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
