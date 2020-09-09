package net.roguelogix.biggerreactors.client.gui;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.gui.GuiPartBase;
import net.roguelogix.phosphophyllite.gui.GuiRenderHelper;
import net.roguelogix.phosphophyllite.gui.api.IHasTooltip;

public class GuiSymbol<T extends Container> extends GuiPartBase<T> implements IHasTooltip {
    
    private final ResourceLocation texture = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/gui_symbols.png");
    private String[] tooltips;
    private int xOffset;
    private int yOffset;
    
    /**
     * @param screen The screen this instance belongs to.
     * @param xPos   The X position of the part.
     * @param yPos   The Y position of the part.
     */
    public GuiSymbol(ContainerScreen<T> screen, int xPos, int yPos, int xOffset, int yOffset, String[] tooltips) {
        super(screen, xPos, yPos, 16, 16);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.tooltips = tooltips;
    }
    
    /**
     * Render this element.
     */
    @Override
    public void drawPart() {
        // Reset and bind texture.
        super.drawPart();
        GuiRenderHelper.setTexture(this.texture);
        
        // Draw background.
        GuiRenderHelper.setTextureOffset(xOffset, yOffset);
        GuiRenderHelper.draw(this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
    }
    
    /**
     * Perform an action on click.
     *
     * @param mouseX The cursor's X position.
     * @param mouseY The cursor's Y position.
     */
    @Override
    public void drawTooltip(int mouseX, int mouseY) {
        int i = tooltips.length;
        if (this.isHovering(mouseX, mouseY)) {
            for (String tooltip : tooltips) {
                this.screen.renderTooltip(tooltip, mouseX, mouseY - (i * 14));
                --i;
                ;
            }
        }
    }
}
