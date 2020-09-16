package net.roguelogix.phosphophyllite.gui.client.api;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface IHasTooltip {
    
    /**
     * Perform an action on click.
     *
     * @param mouseX The cursor's X position.
     * @param mouseY The cursor's Y position.
     */
    void drawTooltip(MatrixStack stack, int mouseX, int mouseY);
}
