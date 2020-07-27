package net.roguelogix.biggerreactors.client.gui;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.gui.GuiPartBase;

@OnlyIn(Dist.CLIENT)
public class GuiReactorBar<T extends Container> extends GuiPartBase<T> {
    
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/reactor_bars.png");
    
    private int textureIndex;
    
    public GuiReactorBar(ContainerScreen<T> screen, int xPos, int yPos, int textureIndex) {
        super(screen, GUI_TEXTURE, xPos, yPos, 16, 64, null);
        
        this.textureIndex = textureIndex;
    }
    
    public void updateTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }
    
    // In practice, valueA is fuel and valueB is waste. This method is used for literally one bar.
    // God damn I need to redo this gui system, but it works for now.
    public void drawPart(long valueStoredA, long valueStoredB, long valueCapacity) {
        
        if (valueCapacity == 0) {
            valueCapacity = 1;
        }
        
        drawPart(valueStoredA, valueCapacity);
        
        updateTextureIndex(3);
        
        long textureOffsetA = valueStoredA * (this.ySize + 1) / valueCapacity;
        long textureOffsetB = valueStoredB * (this.ySize + 1) / valueCapacity;
        int relativeY = (int) (this.yPos + this.ySize - textureOffsetB);
        int textureY = (int) (this.ySize - textureOffsetB);
        
        this.screen.blit(this.xPos, relativeY - (int) textureOffsetA + 1, (textureIndex * 16), textureY, this.xSize, (int) (textureOffsetB));
        
        updateTextureIndex(4);
    }
    
    public void drawPart(long valueStored, long valueCapacity) {
        super.drawPart();
        
        if (valueCapacity == 0) {
            valueCapacity = 1;
        }
        
        long textureOffset = valueStored * (this.ySize + 1) / valueCapacity;
        int relativeY = (int) (this.yPos + this.ySize - textureOffset);
        int textureY = (int) (this.ySize - textureOffset);
        this.screen.blit(this.xPos, relativeY, (textureIndex * 16), textureY, this.xSize, (int) (textureOffset));
    }
    
    public void drawTooltip(int mouseX, int mouseY, long valueStored, long valueCapacity) {
        this.tooltipText = String.format("%d/%d", valueStored, valueCapacity);
        super.drawTooltip(mouseX, mouseY);
    }
}
