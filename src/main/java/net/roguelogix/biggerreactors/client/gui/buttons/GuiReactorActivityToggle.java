package net.roguelogix.biggerreactors.client.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.reactor.containers.ReactorContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorActivity;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorState;
import net.roguelogix.phosphophyllite.gui.GuiPartBase;
import net.roguelogix.phosphophyllite.gui.GuiRenderHelper;
import net.roguelogix.phosphophyllite.gui.api.IHasButton;
import net.roguelogix.phosphophyllite.gui.api.IHasTooltip;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class GuiReactorActivityToggle<T extends Container> extends GuiPartBase<T> implements IHasTooltip, IHasButton {
    
    private final ResourceLocation texture = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/gui_symbols.png");
    private boolean debounce = false;
    private ReactorActivity reactorActivity;
    
    /**
     * @param screen The screen this instance belongs to.
     * @param xPos   The X position of the part.
     * @param yPos   The Y position of the part.
     * @param xSize  The width of the part.
     * @param ySize  The height of the part.
     */
    public GuiReactorActivityToggle(ContainerScreen<T> screen, int xPos, int yPos, int xSize, int ySize) {
        super(screen, xPos, yPos, xSize, ySize);
    }
    
    public void updateState(ReactorActivity reactorActivity) {
        this.reactorActivity = reactorActivity;
    }
    
    /**
     * Render this element.
     */
    @Override
    public void drawPart() {
        // Reset and bind texture.
        super.drawPart();
        GuiRenderHelper.setTexture(this.texture);
        
        // Draw button.
        if (this.reactorActivity == ReactorActivity.ACTIVE) {
            GuiRenderHelper.setTextureOffset(16, 16);
        } else {
            GuiRenderHelper.setTextureOffset(0, 16);
        }
        GuiRenderHelper.draw(this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
    }
    
    @Override
    public void drawTooltip(int mouseX, int mouseY) {
        if (this.isHovering(mouseX, mouseY)) {
            if (this.reactorActivity == ReactorActivity.ACTIVE) {
                this.screen.renderTooltip(Arrays.asList(new TranslationTextComponent("tooltip.biggerreactors.buttons.reactor_activity.deactivate").getFormattedText().split("\\n")), mouseX, mouseY);
            } else {
                this.screen.renderTooltip(Arrays.asList(new TranslationTextComponent("tooltip.biggerreactors.buttons.reactor_activity.activate").getFormattedText().split("\\n")), mouseX, mouseY);
            }
        }
    }
    
    @Override
    public void doClick(int mouseX, int mouseY, int mouseButton) {
        if (!isHovering(mouseX, mouseY)) {
            return;
        }
        
        // Check for a click (and enable debounce).
        if (glfwGetMouseButton(Minecraft.getInstance().getMainWindow().getHandle(), GLFW_MOUSE_BUTTON_1) == GLFW_PRESS
                && !debounce) {
            
            // Do click logic.
            ReactorState reactorState = (ReactorState) ((ReactorContainer) this.screen.getContainer()).getGuiPacket();
            if (reactorState.reactorActivity == ReactorActivity.ACTIVE) {
                ((ReactorContainer) this.screen.getContainer()).executeRequest("setActive", false);
            } else {
                ((ReactorContainer) this.screen.getContainer()).executeRequest("setActive", true);
            }
            debounce = true;
        }
        
        // Check for release (and disable debounce).
        if (glfwGetMouseButton(Minecraft.getInstance().getMainWindow().getHandle(), GLFW_MOUSE_BUTTON_1) == GLFW_RELEASE
                && debounce) {
            debounce = false;
        }
    }
}