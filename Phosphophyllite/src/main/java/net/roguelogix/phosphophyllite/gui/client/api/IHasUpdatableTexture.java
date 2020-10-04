package net.roguelogix.phosphophyllite.gui.client.api;

import net.minecraft.util.ResourceLocation;

public interface IHasUpdatableTexture {
    
    /**
     * Update the texture this part uses.
     *
     * @param texture The texture resource to use.
     * @param offsetX The texture offset to use (x-axis).
     * @param offsetY The texture offset to use (y-axis).
     */
    void updateTexture(ResourceLocation texture, int offsetX, int offsetY);
}
