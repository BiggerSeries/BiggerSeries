package net.roguelogix.biggerreactors.client.gui;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.gui.GuiPartTextured;
import net.roguelogix.phosphophyllite.gui.api.IHasTooltip;

public class GuiEnergyTank<T extends Container> extends GuiPartTextured<T> implements IHasTooltip {
    
    private int energyStored;
    private int energyCapacity;
    
    /**
     * @param screen The screen this instance belongs to.
     * @param xPos   The X position of the part.
     * @param yPos   The Y position of the part.
     */
    public GuiEnergyTank(ContainerScreen<T> screen, int xPos, int yPos) {
        super(screen, xPos, yPos, 16, 64,
                new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/energy_tank.png"),
                0, 0);
    }
    
    /**
     * Update the energy values this part uses.
     *
     * @param energyStored   The amount of energy currently stored.
     * @param energyCapacity The max capacity of energy storable.
     */
    public void updateEnergy(int energyStored, int energyCapacity) {
        this.energyStored = energyStored;
        this.energyCapacity = energyCapacity;
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
        this.updateTexture(this.texture, 16, 0);
        if (this.energyStored != 0) {
            int renderSize = this.ySize * this.energyStored / this.energyCapacity;
            int renderPos = this.yPos + (this.ySize - (renderSize));
            if (this.energyStored != this.energyCapacity) {
                --renderPos;
            }
            this.screen.blit(this.xPos, renderPos, this.offsetX, this.offsetY, this.xSize, renderSize);
        }
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
            this.screen.renderTooltip(String.format("%d/%d RF", this.energyStored, this.energyCapacity), mouseX, mouseY);
        }
    }
}
