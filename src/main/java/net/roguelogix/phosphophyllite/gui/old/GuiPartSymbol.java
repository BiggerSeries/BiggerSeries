package net.roguelogix.phosphophyllite.gui.old;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

@Deprecated
public class GuiPartSymbol<T extends Container> extends GuiPartBase<T> {
    
    private int textureIndex;
    
    public GuiPartSymbol(ContainerScreen<T> screen, ResourceLocation guiTexture, int xPos, int yPos, int xSize, int ySize, int textureIndex, @Nullable String tooltipText) {
        super(screen, guiTexture, xPos, yPos, xSize, ySize, tooltipText);
        
        this.textureIndex = textureIndex;
    }
    
    public void updateTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }
    
    @Override
    public void drawPart() {
        this.screen.getMinecraft().getTextureManager().bindTexture(guiTexture);
        this.screen.blit(this.xPos, this.yPos, (textureIndex * 16), 0, xSize, ySize);
    }
    
    @Override
    public void drawTooltip(int mouseX, int mouseY) {
        super.drawTooltip(mouseX, mouseY);
    }
}
