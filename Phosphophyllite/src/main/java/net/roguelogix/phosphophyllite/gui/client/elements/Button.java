package net.roguelogix.phosphophyllite.gui.client.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.roguelogix.phosphophyllite.gui.client.ScreenBase;
import net.roguelogix.phosphophyllite.gui.client.api.ICallback;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Base button element.
 *
 * @param <T> Elements must belong to a Container or ContainerScreen.
 */
public class Button<T extends Container> extends Symbol<T> implements IGuiEventListener {

    /**
     * Used to enable or disable interaction with this element.
     */
    public boolean actionEnable;

    /**
     * Callback for mouse movement.
     */
    public ICallback.OnMouseMoved onMouseMoved;

    /**
     * Callback for mouse button clicks.
     */
    public ICallback.OnMouseClicked onMouseClicked;

    /**
     * Callback for mouse button releases.
     */
    public ICallback.OnMouseReleased onMouseReleased;

    /**
     * Callback for mouse dragging.
     */
    public ICallback.OnMouseDragged onMouseDragged;

    /**
     * Callback for mouse scrolling.
     */
    public ICallback.OnMouseScrolled onMouseScrolled;

    /**
     * Callback for key presses.
     */
    public ICallback.OnKeyPressed onKeyPressed;

    /**
     * Callback for key releases.
     */
    public ICallback.OnKeyReleased onKeyReleased;

    /**
     * Callback for typed characters.
     */
    public ICallback.OnCharTyped onCharTyped;

    /**
     * Default constructor.
     *
     * @param parent  The parent screen of this element.
     * @param x       The x position of this element.
     * @param y       The y position of this element.
     * @param width   The width of this element.
     * @param height  The height of this element.
     * @param u       The u offset to use when rendering this element (starting from the left, and moving right).
     * @param v       The v offset to use when rendering this element (starting from the top, and moving down).
     * @param tooltip The tooltip for this element. If null, a tooltip will not render. If you set a tooltip later, use StringTextComponent.EMPTY.
     */
    public Button(@Nonnull ScreenBase<T> parent, int x, int y, int width, int height, int u, int v, @Nullable ITextComponent tooltip) {
        super(parent, x, y, width, height, u, v, tooltip);
        this.actionEnable = true;
    }

    /**
     * Play a sound.
     *
     * @param sound The sound to play.
     */
    public void playSound(SoundEvent sound) {
        Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(sound, 1.0F));
    }

    /**
     * Play a sound.
     *
     * @param sound  The sound to play.
     * @param volume How loud to play the sound.
     */
    public void playSound(SoundEvent sound, float volume) {
        Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(sound, 1.0F, volume));
    }


    /**
     * Triggered when the mouse is moved.
     *
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     */
    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        // Check conditions, and trigger.
        if (this.actionEnable && this.onMouseMoved != null) {
            this.onMouseMoved.trigger(mouseX, mouseY);
        }
    }

    /**
     * Triggered when the mouse is clicked.
     * For most purposes, it is recommended to tie logic to #mouseReleased.
     *
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param button The button clicked.
     * @return Whether the event was consumed.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check conditions, and trigger.
        if (this.actionEnable && this.onMouseClicked != null) {
            return this.onMouseClicked.trigger(mouseX, mouseY, button);
        }
        return false;
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
        // Check conditions, and trigger.
        if (this.actionEnable && this.onMouseReleased != null) {
            return this.onMouseReleased.trigger(mouseX, mouseY, button);
        }
        return false;
    }

    /**
     * Triggered when the mouse is dragged.
     *
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param button The button clicked.
     * @param dragX  Drag x.
     * @param dragY  Drag y.
     * @return Whether the event was consumed.
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        // Check conditions, and trigger.
        if (this.actionEnable && this.onMouseDragged != null) {
            return this.onMouseDragged.trigger(mouseX, mouseY, button, dragX, dragY);
        }
        return false;
    }

    /**
     * Triggered when the mouse is scrolled.
     *
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param delta  How far the mouse scrolled.
     * @return Whether the event was consumed.
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // Check conditions, and trigger.
        if (this.actionEnable && this.onMouseScrolled != null) {
            return this.onMouseScrolled.trigger(mouseX, mouseY, delta);
        }
        return false;
    }

    /**
     * Triggered when a key is pressed.
     *
     * @param keyCode   The key code pressed.
     * @param scanCode  The scan code pressed.
     * @param modifiers Any modifiers pressed.
     * @return Whether the event was consumed.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Check conditions, and trigger.
        if (this.actionEnable && this.onKeyPressed != null) {
            return this.onKeyPressed.trigger(keyCode, scanCode, modifiers);
        }
        return false;
    }

    /**
     * Triggered when a key is released.
     *
     * @param keyCode   The key code released.
     * @param scanCode  The scan code released.
     * @param modifiers Any modifiers released.
     * @return Whether the event was consumed.
     */
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        // Check conditions, and trigger.
        if (this.actionEnable && this.onKeyReleased != null) {
            return this.onKeyReleased.trigger(keyCode, scanCode, modifiers);
        }
        return false;
    }

    /**
     * Triggered when a character is typed.
     *
     * @param codePoint The character typed.
     * @param modifiers Any modifiers released.
     * @return Whether the event was consumed.
     */
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // Check conditions, and trigger.
        if (this.actionEnable && this.onCharTyped != null) {
            return this.onCharTyped.trigger(codePoint, modifiers);
        }
        return false;
    }

    /**
     * Enable all "config" booleans for this element, effectively making this element visible.
     */
    @Override
    public void enable() {
        super.enable();
        this.actionEnable = true;
    }

    /**
     * Disable all "config" booleans for this element, effectively making this element hidden.
     */
    @Override
    public void disable() {
        super.disable();
        this.actionEnable = false;
    }
}