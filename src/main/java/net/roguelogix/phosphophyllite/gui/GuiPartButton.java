package net.roguelogix.phosphophyllite.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

import static org.lwjgl.glfw.GLFW.*;

public class GuiPartButton<T extends Container> extends GuiPartBase<T> {
    
    private int textureIndex;
    
    public GuiPartButton(ContainerScreen<T> screen, ResourceLocation guiTexture, int xPos, int yPos, int xSize, int ySize, int textureIndex, @Nullable String tooltipText) {
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
    
    // Needed to ensure single click per click, rather than one per frame.
    private boolean pressState = false;
    
    @Override
    // Hijacked tooltip function for button press logic... yup, nothing sketchy here.
    // TODO: Unsketchify this.
    public void drawTooltip(int mouseX, int mouseY) {
        super.drawTooltip(mouseX, mouseY);
        
        if (isMouseHovering(mouseX, mouseY)) {
            if (glfwGetMouseButton(Minecraft.getInstance().getMainWindow().getHandle(), GLFW_MOUSE_BUTTON_1) == GLFW_PRESS
                    && !pressState) {
                
                this.onClick();
                pressState = true;
            }
            
            if (glfwGetMouseButton(Minecraft.getInstance().getMainWindow().getHandle(), GLFW_MOUSE_BUTTON_1) == GLFW_RELEASE
                    && pressState) {
                
                pressState = false;
            }
        }
    }
    
    public void onClick() {
    }
}
