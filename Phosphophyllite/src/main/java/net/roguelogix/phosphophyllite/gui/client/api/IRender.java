package net.roguelogix.phosphophyllite.gui.client.api;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Render interface.
 */
@OnlyIn(Dist.CLIENT)
public interface IRender {

    /**
     * Render.
     *
     * @param mStack The current matrix stack.
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     */
    void render(MatrixStack mStack, int mouseX, int mouseY);
}
