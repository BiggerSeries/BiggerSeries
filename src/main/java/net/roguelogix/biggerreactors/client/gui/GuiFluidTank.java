package net.roguelogix.biggerreactors.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.gui.GuiPartTextured;
import net.roguelogix.phosphophyllite.gui.api.IHasTooltip;

public class GuiFluidTank<T extends Container> extends GuiPartTextured<T> implements IHasTooltip {
    
    private Fluid fluidType;
    private int fluidStored;
    private int fluidCapacity;
    
    /**
     * @param screen The screen this instance belongs to.
     * @param xPos   The X position of the part.
     * @param yPos   The Y position of the part.
     */
    public GuiFluidTank(ContainerScreen<T> screen, int xPos, int yPos) {
        super(screen, xPos, yPos, 18, 64,
                new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/fluid_tank.png"),
                0, 0);
    }
    
    /**
     * Update the fluid values this part uses.
     *
     * @param fluidType     The type of fluid stored.
     * @param fluidStored   The amount of fluids currently stored.
     * @param fluidCapacity The max capacity of fluids storable.
     */
    public void updateFluid(Fluid fluidType, int fluidStored, int fluidCapacity) {
        this.fluidType = fluidType;
        this.fluidStored = fluidStored;
        this.fluidCapacity = fluidCapacity;
    }
    
    /**
     * Render this element.
     */
    @Override
    public void drawPart() {
        // Bind texture.
        this.screen.getMinecraft().getTextureManager().bindTexture(this.texture);
        // Draw background.
        this.updateTexture(this.texture, 0, 0);
        super.drawPart();
        // Draw foreground.
        if (this.fluidStored != 0) {
            int renderSize = this.ySize * this.fluidStored / this.fluidCapacity;
            int renderPos = this.yPos + (this.ySize - (renderSize));
            if (this.fluidStored != this.fluidCapacity) {
                --renderPos;
            }
            
            FluidAttributes fluidAttributes = this.fluidType.getAttributes();
            TextureAtlasSprite fluidSprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(fluidAttributes.getStillTexture());
            
            int colorF = fluidAttributes.getColor();
            float colorA = ((colorF >> 24) & 0xFF) / 255F;
            float colorR = ((colorF >> 16) & 0xFF) / 255F;
            float colorG = ((colorF >> 8) & 0xFF) / 255F;
            float colorB = ((colorF) & 0xFF) / 255F;
            
            // TODO: This will render *A* tile of the fluid's texture, but not the whole bar. Increasing the texture size stretches it oddly, and I'm too tired to figure out how to make it not.
            
            this.screen.getMinecraft().getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
            RenderSystem.color4f(colorR, colorG, colorB, colorA);
            AbstractGui.blit(this.xPos, renderPos, this.screen.getBlitOffset(), 16, 16, fluidSprite);
            RenderSystem.clearCurrentColor();
        }
        // Draw overlay.
        this.updateTexture(this.texture, 18, 0);
    }
    
    /**
     * Perform an action on click.
     *
     * @param mouseX The cursor's X position.
     * @param mouseY The cursor's Y position.
     */
    @Override
    public void drawTooltip(int mouseX, int mouseY) {
        if (this.isHovering(mouseX, mouseY)) {
            this.screen.renderTooltip(String.format("%d/%d mB", this.fluidStored, this.fluidCapacity), mouseX, mouseY);
        }
    }
}
