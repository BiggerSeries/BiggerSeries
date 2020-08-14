package net.roguelogix.biggerreactors.client.gui.old;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.gui.old.GuiPartBase;

@OnlyIn(Dist.CLIENT)
public class GuiProgressBar<T extends Container> extends GuiPartBase<T> {
    
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(BiggerReactors.modid,
            "textures/screen/parts/progress_bar.png");
    
    public GuiProgressBar(ContainerScreen<T> screen, int xPos, int yPos) {
        super(screen, GUI_TEXTURE, xPos, yPos, 25, 16, null);
    }
    
    public void drawPart(long workTime, long workTimeTotal) {
        super.drawPart();
        
        if (workTimeTotal == 0) {
            workTimeTotal = 1;
        }
        
        // May of figured out a better way to render bars and the such, this is currently buggy though.
        long textureOffset = 25 * workTime / workTimeTotal;
        this.screen.blit(this.xPos, this.yPos, 25, 0, this.xSize - (int) textureOffset, this.ySize);
    }
}
