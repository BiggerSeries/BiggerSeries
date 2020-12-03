package net.roguelogix.biggerreactors.client.turbine;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.turbine.containers.TurbineContainer;
import net.roguelogix.phosphophyllite.gui.old.client.GuiPartBase;
import net.roguelogix.phosphophyllite.gui.old.client.GuiRenderHelper;
import net.roguelogix.phosphophyllite.gui.old.client.api.IHasTooltip;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT;

public class GuiTurbineFlowIncreaseButton<T extends Container> extends GuiPartBase<T> implements IHasTooltip {
    
    private final ResourceLocation texture = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/gui_symbols.png");
    private boolean debounce = false;
    private int alts = 0;
    private int shifts = 0;
    private int ctrls = 0;
    private long flowRate;
    
    /**
     * @param screen The screen this instance belongs to.
     * @param xPos   The X position of the part.
     * @param yPos   The Y position of the part.
     * @param xSize  The width of the part.
     * @param ySize  The height of the part.
     */
    public GuiTurbineFlowIncreaseButton(ContainerScreen<T> screen, int xPos, int yPos, int xSize, int ySize) {
        super(screen, xPos, yPos, xSize, ySize);
    }
    
    public void updateState(long flowRate) {
        this.flowRate = flowRate;
    }
    
    /**
     * Render this element.
     */
    @Override
    public void drawPart(MatrixStack mStack) {
        // Reset and bind texture.
        super.drawPart(mStack);
        GuiRenderHelper.setTexture(this.texture);
        
        // Draw button.
        if (this.debounce) {
            GuiRenderHelper.setTextureOffset(64, 48);
        } else {
            GuiRenderHelper.setTextureOffset(48, 48);
        }
        GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
    }
    
    @Override
    public void drawTooltip(MatrixStack mStack, int mouseX, int mouseY) {
        if (this.isMouseOver(mouseX, mouseY)) {
            this.screen.func_243308_b(mStack, Arrays.stream(new TranslationTextComponent("tooltip.biggerreactors.buttons.turbine.flow.increase").getString().split("\\n")).map(StringTextComponent::new).collect(Collectors.toList()), mouseX, mouseY);
        }
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
            case GLFW_KEY_LEFT_ALT:
            case GLFW_KEY_RIGHT_ALT: {
                alts++;
                break;
            }
            case GLFW_KEY_LEFT_CONTROL:
            case GLFW_KEY_RIGHT_CONTROL: {
                ctrls++;
                break;
            }
            case GLFW_KEY_LEFT_SHIFT:
            case GLFW_KEY_RIGHT_SHIFT: {
                shifts++;
            }
        }
        return true;
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
            case GLFW_KEY_LEFT_ALT:
            case GLFW_KEY_RIGHT_ALT: {
                alts--;
                break;
            }
            case GLFW_KEY_LEFT_CONTROL:
            case GLFW_KEY_RIGHT_CONTROL: {
                ctrls--;
                break;
            }
            case GLFW_KEY_LEFT_SHIFT:
            case GLFW_KEY_RIGHT_SHIFT: {
                shifts--;
            }
        }
        return true;
    }
    
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            long flowChange;
            // Check for modifiers
            if (ctrls > 0 && shifts > 0) {
                flowChange = 1000L;
            } else if (ctrls > 0) {
                flowChange = 100L;
            } else if (shifts > 0) {
                flowChange = 10L;
            } else {
                flowChange = 1L;
            }

            // Send data.
            ((TurbineContainer) this.screen.getContainer()).executeRequest("changeFlowRate", flowChange);
            assert this.screen.getMinecraft().player != null;
            this.screen.getMinecraft().player.playSound(SoundEvents.UI_BUTTON_CLICK, this.screen.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER), 1.0F);
            debounce = true;
            return true;
        }
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            debounce = false;
            return true;
        }
    }
}