package net.roguelogix.biggerreactors.client.gui;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.gui.GuiPartSymbol;

import javax.annotation.Nullable;

public class GuiReactorSymbol<T extends Container> extends GuiPartSymbol<T> {
    
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/reactor_symbols.png");
    
    public GuiReactorSymbol(ContainerScreen<T> screen, int xPos, int yPos, int textureIndex, @Nullable String tooltipText) {
        super(screen, GUI_TEXTURE, xPos, yPos, 16, 16, textureIndex, tooltipText);
    }
}
