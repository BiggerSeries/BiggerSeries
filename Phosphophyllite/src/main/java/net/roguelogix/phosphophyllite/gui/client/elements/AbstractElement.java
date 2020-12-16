package net.roguelogix.phosphophyllite.gui.client.elements;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.inventory.container.Container;
import net.roguelogix.phosphophyllite.gui.client.ScreenBase;
import net.roguelogix.phosphophyllite.gui.client.api.ICallback;

import javax.annotation.Nonnull;

/**
 * Base element, used by all other elements. Custom elements must, at some point, extend this class.
 *
 * @param <T> Elements must belong to a Container or ContainerScreen.
 */
public abstract class AbstractElement<T extends Container> implements ITickable, IGuiEventListener {

    /**
     * The parent screen of this element.
     */
    protected ScreenBase<T> parent;

    /**
     * The position of this element.
     */
    public int x, y;

    /**
     * The dimensions of this element.
     */
    public int width, height;

    /**
     * Free state enable boolean for usage by any child classes.
     */
    public boolean stateEnable;

    /**
     * Callback for ticks/updates.
     */
    public ICallback.OnTick onTick;

    /**
     * Default constructor.
     *
     * @param parent The parent screen of this element.
     * @param x      The x position of this element.
     * @param y      The y position of this element.
     * @param width  The width of this element.
     * @param height The height of this element.
     */
    public AbstractElement(@Nonnull ScreenBase<T> parent, int x, int y, int width, int height) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Tick/update this element.
     */
    @Override
    public void tick() {
        // Check conditions, and trigger.
        if (this.onTick != null) {
            this.onTick.trigger();
        }
    }

    /**
     * Enable all "config" booleans for this element, effectively making this element visible.
     */
    public abstract void enable();

    /**
     * Disable all "config" booleans for this element, effectively making this element hidden.
     */
    public abstract void disable();

    /**
     * Returns whether the mouse is over the current element or not.
     *
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @return True if the mouse is over this element, false otherwise.
     */
    public boolean isMouseOver(double mouseX, double mouseY) {
        // Get actual x and y positions.
        int relativeX = this.parent.getGuiLeft() + this.x;
        int relativeY = this.parent.getGuiTop() + this.y;
        // Check the mouse.
        return ((mouseX > relativeX) && (mouseX < relativeX + this.width)
                && (mouseY > relativeY) && (mouseY < relativeY + this.height));
    }
}
