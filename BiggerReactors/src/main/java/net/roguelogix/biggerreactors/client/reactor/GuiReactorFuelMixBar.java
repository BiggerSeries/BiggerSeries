package net.roguelogix.biggerreactors.client.reactor;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.gui.client.GuiPartBase;
import net.roguelogix.phosphophyllite.gui.client.GuiRenderHelper;
import net.roguelogix.phosphophyllite.gui.client.api.IHasTooltip;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GuiReactorFuelMixBar<T extends Container> extends GuiPartBase<T> implements IHasTooltip {
    
    private final ResourceLocation texture = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/gui_bars.png");
    private long wasteStored;
    private long fuelStored;
    private long fuelCapacity;
    
    /**
     * @param screen The screen this instance belongs to.
     * @param xPos   The X position of the part.
     * @param yPos   The Y position of the part.
     */
    public GuiReactorFuelMixBar(ContainerScreen<T> screen, int xPos, int yPos) {
        super(screen, xPos, yPos, 18, 64);
    }
    
    /**
     * Update the fuel values this part uses.
     *
     * @param wasteStored  The amount of waste currently stored.
     * @param fuelStored   The amount of fuel currently stored.
     * @param fuelCapacity The max capacity of fuel/waste storable.
     */
    public void updateFuelMix(long wasteStored, long fuelStored, long fuelCapacity) {
        this.wasteStored = wasteStored;
        this.fuelStored = fuelStored;
        this.fuelCapacity = fuelCapacity;
    }
    
    /**
     * Render this element.
     */
    @Override
    public void drawPart(MatrixStack mStack) {
        // Reset and bind texture.
        super.drawPart(mStack);
        GuiRenderHelper.setTexture(this.texture);
        
        // Draw background.
        GuiRenderHelper.setTextureOffset(54, 0);
        GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
        
        // Draw foreground.
        if (this.fuelCapacity != 0) {
            // Determine amount to draw.
            int renderSizeWaste = (int) (this.ySize * this.wasteStored / this.fuelCapacity);
            int renderSizeFuel = (int) (this.ySize * this.fuelStored / this.fuelCapacity);
            
            // Draw waste.
            GuiRenderHelper.setTextureOffset(108, 0);
            GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
            
            // Draw fuel.
            GuiRenderHelper.setTextureOffset(90, 0);
            GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize - renderSizeWaste);
            
            // Mask away empty bit.
            GuiRenderHelper.setTextureOffset(54, 0);
            GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize - (renderSizeFuel + renderSizeWaste));
        }
        // Draw frame.
        GuiRenderHelper.setTextureOffset(18, 0);
        GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
    }
    
    /**
     * Perform an action on click.
     *
     * @param mouseX The cursor's X position.
     * @param mouseY The cursor's Y position.
     */
    @Override
    public void drawTooltip(MatrixStack mStack, int mouseX, int mouseY) {
        if (this.isMouseOver(mouseX, mouseY)) {
            this.screen.func_243308_b(mStack, Arrays.stream(
                    String.format("%d/%d mB\n%.1f%% Fuel, %.1f%% Waste",
                            (this.wasteStored + this.fuelStored),
                            this.fuelCapacity,
                            ((float) this.fuelStored / this.fuelCapacity * 100.0),
                            ((float) this.wasteStored / this.fuelCapacity) * 100.0)
                            .split("\\n")).map(StringTextComponent::new).collect(Collectors.toList()), mouseX, mouseY);
        }
    }
}
