package net.roguelogix.biggerreactors.client.gui.buttons;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.roguelogix.biggerreactors.classic.reactor.ReactorContainer;
import net.roguelogix.biggerreactors.classic.reactor.ReactorDatapack;
import net.roguelogix.biggerreactors.client.gui.GuiReactorButton;

import javax.annotation.Nullable;

public class GuiReactorToggleActiveButton<T extends Container> extends GuiReactorButton<T> {
    
    public GuiReactorToggleActiveButton(ContainerScreen<T> screen, int xPos, int yPos, int textureIndex, @Nullable String tooltipText) {
        super(screen, xPos, yPos, textureIndex, tooltipText);
    }
    
    @Override
    public void onClick() {
        ReactorDatapack data = ((ReactorContainer) this.screen.getContainer()).getReactorData();
        // TODO: update variable names in ReactorDatapack, as they don't match the request keys
        if (data.reactorStatus) {
            ((ReactorContainer) this.screen.getContainer()).executeRequest("setActive", false);
        } else {
            ((ReactorContainer) this.screen.getContainer()).executeRequest("setActive", true);
        }
    }
}
