package net.roguelogix.biggerreactors.client.controlrod;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.reactor.containers.ControlRodContainer;
import net.roguelogix.phosphophyllite.gui.client.GuiPartBase;
import net.roguelogix.phosphophyllite.gui.client.GuiRenderHelper;
import net.roguelogix.phosphophyllite.gui.client.api.IHasButton;
import net.roguelogix.phosphophyllite.gui.client.api.IHasTooltip;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class GuiRodInsertButton<T extends Container> extends GuiPartBase<T> implements IHasTooltip, IHasButton {

    private final ResourceLocation texture = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/gui_symbols.png");
    private boolean debounce = false;
    private double insertionLevel;

    /**
     * @param screen The screen this instance belongs to.
     * @param xPos   The X position of the part.
     * @param yPos   The Y position of the part.
     * @param xSize  The width of the part.
     * @param ySize  The height of the part.
     */
    public GuiRodInsertButton(ContainerScreen<T> screen, int xPos, int yPos, int xSize, int ySize) {
        super(screen, xPos, yPos, xSize, ySize);
    }

    public void updateState(double insertionLevel) {
        this.insertionLevel = insertionLevel;
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
        if (this.debounce) {
            GuiRenderHelper.setTextureOffset(64, 48);
        } else {
            GuiRenderHelper.setTextureOffset(48, 48);
        }
        GuiRenderHelper.draw(this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
    }

    @Override
    public void drawTooltip(int mouseX, int mouseY) {
        if (this.isHovering(mouseX, mouseY)) {
            this.screen.renderTooltip(Arrays.asList(new TranslationTextComponent("tooltip.biggerreactors.buttons.control_rod.insertion.insert").getString().split("\\n")), mouseX, mouseY);
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
            double levelChange = 0;
            boolean shiftPressed = (glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS)
                    || (glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), GLFW_KEY_RIGHT_SHIFT) == GLFW_PRESS);
            boolean ctrlPressed = glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS
                    || (glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), GLFW_KEY_RIGHT_CONTROL) == GLFW_PRESS);
            boolean altPressed = (glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), GLFW_KEY_LEFT_ALT) == GLFW_PRESS
                    || (glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), GLFW_KEY_RIGHT) == GLFW_PRESS));

            // Do click logic.
            if (shiftPressed && ctrlPressed) {
                levelChange = +1000;
            } else if (ctrlPressed) {
                levelChange = +50;
            } else if (shiftPressed) {
                levelChange = +10;
            } else {
                levelChange = +1;
            }

            ((ControlRodContainer) this.screen.getContainer()).executeRequest("changeInsertionLevel", new Pair<>(levelChange, altPressed));

            assert this.screen.getMinecraft().player != null;
            this.screen.getMinecraft().player.playSound(SoundEvents.UI_BUTTON_CLICK, this.screen.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER), 1.0F);
            debounce = true;
        }

        // Check for release (and disable debounce).
        if (glfwGetMouseButton(Minecraft.getInstance().getMainWindow().getHandle(), GLFW_MOUSE_BUTTON_1) == GLFW_RELEASE
                && debounce) {
            debounce = false;
        }
    }
}