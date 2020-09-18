package net.roguelogix.phosphophyllite.gui.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.phosphophyllite.gui.client.api.IHasGuiTexture;

@OnlyIn(Dist.CLIENT)
public class GuiScreenBase<T extends Container> extends ContainerScreen<T> implements IHasGuiTexture {
    
    /**
     * The texture to draw with.
     */
    protected ResourceLocation texture;
    /**
     * X offset to fetch a sub-texture from.
     */
    protected int offsetX;
    /**
     * Y offset to fetch a sub-texture from.
     */
    protected int offsetY;
    
    public GuiScreenBase(T container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }
    
    /**
     * Update the texture this part uses.
     *
     * @param texture The texture resource to use.
     * @param offsetX The texture offset to use (x-axis).
     * @param offsetY The texture offset to use (y-axis).
     */
    @Override
    public void updateTexture(ResourceLocation texture, int offsetX, int offsetY) {
        this.texture = texture;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
    
    
    /**
     * Draw foreground elements.
     *
     * @param mouseX X position of the mouse.
     * @param mouseY Y position of the mouse.
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(this.texture);
    
        int relativeX = (this.width - this.xSize) / 2;
        int relativeY = (this.height - this.ySize) / 2;
    
        this.blit(matrixStack, relativeX, relativeY, this.offsetX, this.offsetY, this.xSize, this.ySize);
    }
}
