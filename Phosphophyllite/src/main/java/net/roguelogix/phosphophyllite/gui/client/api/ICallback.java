package net.roguelogix.phosphophyllite.gui.client.api;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Screen callbacks used for hooking functions to screens.
 */
@OnlyIn(Dist.CLIENT)
public interface ICallback {

    /**
     * Callback for custom rendering.
     */
    interface OnRender {
        void trigger(@Nonnull MatrixStack mStack, int mouseX, int mouseY);
    }

    /**
     * Callback for ticks/updates.
     */
    interface OnTick {
        void trigger();
    }

    /**
     * Callback for mouse movement.
     */
    interface OnMouseMoved {
        void trigger(double mouseX, double mouseY);
    }

    /**
     * Callback for mouse button clicks.
     */
    interface OnMouseClicked {
        boolean trigger(double mouseX, double mouseY, int button);
    }

    /**
     * Callback for mouse button releases.
     */
    interface OnMouseReleased {
        boolean trigger(double mouseX, double mouseY, int button);
    }

    /**
     * Callback for mouse dragging.
     */
    interface OnMouseDragged {
        boolean trigger(double mouseX, double mouseY, int button, double dragX, double dragY);
    }

    /**
     * Callback for mouse scrolling.
     */
    interface OnMouseScrolled {
        boolean trigger(double mouseX, double mouseY, double delta);
    }

    /**
     * Callback for key presses.
     */
    interface OnKeyPressed {
        boolean trigger(int keyCode, int scanCode, int modifiers);
    }

    /**
     * Callback for key releases.
     */
    interface OnKeyReleased {
        boolean trigger(int keyCode, int scanCode, int modifiers);
    }

    /**
     * Callback for typed characters.
     */
    interface OnCharTyped {
        boolean trigger(char codePoint, int modifiers);
    }
}
