package net.roguelogix.biggerreactors.client;

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

public class GuiSymbol<T extends Container> extends GuiPartBase<T> implements IHasTooltip {
    
    private final ResourceLocation texture = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/gui_symbols.png");
    private String tooltip;
    private int xOffset;
    private int yOffset;
    
    /**
     * @param screen  The screen this instance belongs to.
     * @param xPos    The X position of the part.
     * @param yPos    The Y position of the part.
     * @param xOffset The X offset of the texture.
     * @param yOffset The Y offset of the texture.
     * @param tooltip The tooltip to apply to this symbol.
     */
    public GuiSymbol(ContainerScreen<T> screen, int xPos, int yPos, int xOffset, int yOffset, String tooltip) {
        super(screen, xPos, yPos, 16, 16);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.tooltip = tooltip;
    }
    
    /**
     * Update the energy values this part uses.
     *
     * @param xOffset The X offset of the texture.
     * @param yOffset The Y offset of the texture.
     */
    public void updateTextureOffset(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
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
        GuiRenderHelper.setTextureOffset(xOffset, yOffset);
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
        if (this.isHovering(mouseX, mouseY)) {
            this.screen.func_243308_b(mStack, Arrays.stream(tooltip.split("\\n")).map(StringTextComponent::new).collect(Collectors.toList()), mouseX, mouseY);
        }
    }
}
