package net.roguelogix.phosphophyllite.gui.client.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.roguelogix.phosphophyllite.gui.client.ScreenBase;
import net.roguelogix.phosphophyllite.gui.client.api.ITooltip;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Base tooltip element.
 *
 * @param <T> Elements must belong to a Container or ContainerScreen.
 */
public class Tooltip<T extends Container> extends AbstractElement<T> implements ITooltip {

    /**
     * Used to enable or disable the tooltip element.
     */
    public boolean tooltipEnable;

    /**
     * Tooltip for this element. If null, a tooltip will not render.
     */
    public ITextComponent tooltip;

    /**
     * Default constructor.
     *
     * @param parent  The parent screen of this element.
     * @param x       The x position of this element.
     * @param y       The y position of this element.
     * @param width   The width of this element.
     * @param height  The height of this element.
     * @param tooltip The tooltip for this element. If null, a tooltip will not render. If you set a tooltip later, use StringTextComponent.EMPTY.
     */
    public Tooltip(@Nonnull ScreenBase<T> parent, int x, int y, int width, int height, @Nullable ITextComponent tooltip) {
        super(parent, x, y, width, height);
        this.tooltipEnable = (tooltip != null);
        this.tooltip = tooltip;
    }

    /**
     * Render tooltip.
     *
     * @param mStack The current matrix stack.
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     */
    @Override
    public void renderTooltip(@Nonnull MatrixStack mStack, int mouseX, int mouseY) {
        // Check conditions, and render tooltip.
        if (this.tooltipEnable && this.tooltip != null && this.isMouseOver(mouseX, mouseY)) {
            this.parent.renderTooltip(mStack, this.tooltip, mouseX, mouseY);
        }
    }

    /**
     * Enable all "config" booleans for this element, effectively making this element visible.
     */
    @Override
    public void enable() {
        this.tooltipEnable = true;
    }

    /**
     * Disable all "config" booleans for this element, effectively making this element hidden.
     */
    @Override
    public void disable() {
        this.tooltipEnable = false;
    }
}