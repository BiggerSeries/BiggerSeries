package net.roguelogix.phosphophyllite.gui.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiPartBase<T extends Container> {
    
    protected ContainerScreen<T> screen;
    protected int xPos, yPos;
    protected int xSize, ySize;
    
    /**
     * @param screen The screen this instance belongs to.
     * @param xPos   The X position of the part.
     * @param yPos   The Y position of the part.
     * @param xSize  The width of the part.
     * @param ySize  The height of the part.
     */
    protected GuiPartBase(ContainerScreen<T> screen, int xPos, int yPos, int xSize, int ySize) {
        this.screen = screen;
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
    }
    
    /**
     * Render this element.
     */
    protected void drawPart(MatrixStack stack) {
        // Default method resets variables for next usage.
        GuiRenderHelper.setTexture(new ResourceLocation("minecraft", "textures/block/dirt.png"));
        GuiRenderHelper.setTextureOffset(0, 0);
        GuiRenderHelper.clearRenderColor();
    }
    
    /**
     * Check if the cursor is hovering over this element.
     *
     * @param mouseX The cursor's X position.
     * @param mouseY The cursor's Y position.
     * @return True if the cursor is hovering, false otherwise.
     */
    protected boolean isHovering(int mouseX, int mouseY) {
        int relativeX = this.screen.getGuiLeft() + this.xPos;
        int relativeY = this.screen.getGuiTop() + this.yPos;
        if ((mouseX > relativeX) && (mouseX < relativeX + this.xSize)) {
            if ((mouseY > relativeY) && (mouseY < relativeY + this.ySize)) {
                return true;
            }
        }
        return false;
    }
}
