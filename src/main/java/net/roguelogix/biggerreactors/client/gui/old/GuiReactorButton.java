package net.roguelogix.biggerreactors.client.gui.old;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.reactor.ReactorContainer;
import net.roguelogix.biggerreactors.classic.reactor.ReactorDatapack;
import net.roguelogix.phosphophyllite.gui.old.GuiPartButton;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class GuiReactorButton<T extends Container> extends GuiPartButton<T> {
    
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/reactor_buttons.png");
    
    private int textureIndex;
    
    public GuiReactorButton(ContainerScreen<T> screen, int xPos, int yPos, int textureIndex, @Nullable String tooltipText) {
        super(screen, GUI_TEXTURE, xPos, yPos, 16, 16, 0, tooltipText);
        
        this.textureIndex = textureIndex;
    }
    
    
    public void updateTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }
    
    @Override
    public void drawPart() {
        ReactorDatapack data = ((ReactorContainer) this.screen.getContainer()).getReactorData();
        this.updateTextureIndex(data.reactorStatus ? 1 : 0);
        this.screen.getMinecraft().getTextureManager().bindTexture(guiTexture);
        this.screen.blit(this.xPos, this.yPos, (textureIndex * 16), 0, xSize, ySize);
    }
    
    public void onClick() {
        super.onClick();
        
        
    }
}
