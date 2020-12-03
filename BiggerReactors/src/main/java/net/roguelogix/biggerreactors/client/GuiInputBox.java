package net.roguelogix.biggerreactors.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.reactor.containers.RedstonePortContainer;
import net.roguelogix.phosphophyllite.gui.old.client.GuiPartBase;
import net.roguelogix.phosphophyllite.gui.old.client.GuiRenderHelper;
import net.roguelogix.phosphophyllite.gui.old.client.api.IHasTooltip;
import org.lwjgl.glfw.GLFW;

public class GuiInputBox<T extends Container> extends GuiPartBase<T> implements IHasTooltip {

    private boolean debounce = false;
    private boolean isFocused = false;
    private boolean numeralOnly = false;
    private int cursorPosition = 0;
    private String request = "";
    private StringBuffer inputBuffer = new StringBuffer();

    /**
     * @param screen  The screen this instance belongs to.
     * @param xPos    The X position of the part.
     * @param yPos    The Y position of the part.
     */
    public GuiInputBox(ContainerScreen<T> screen, int xPos, int yPos, boolean numeralOnly, String request, String text) {
        super(screen, xPos, yPos, 96, 16);
        this.numeralOnly = numeralOnly;
        this.request = request;
        this.updateState(text);
    }

    public void updateState(String text) {
        this.cursorPosition = text.length();
        this.inputBuffer = new StringBuffer(text);
    }

    @Override
    public boolean changeFocus(boolean focus) {
        this.isFocused = focus;
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(!this.isMouseOver(mouseX, mouseY)) {
            ((RedstonePortContainer) this.screen.getContainer()).executeRequest(request, this.inputBuffer.toString());
            changeFocus(false);
            return false;
        } else {
            changeFocus(true);
            debounce = true;
            return true;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            debounce = false;
            return true;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Escape is supreme, always close screen.
        if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.screen.closeScreen();
            return true;
        }
        // Control keys for the text box.
        if(this.isFocused) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_LEFT: {
                    if(this.cursorPosition > 0) {
                        --this.cursorPosition;
                    }
                    return true;
                }
                case GLFW.GLFW_KEY_RIGHT: {
                    if(this.cursorPosition < this.inputBuffer.length()) {
                        ++this.cursorPosition;
                    }
                    return true;
                }
                case GLFW.GLFW_KEY_BACKSPACE: {
                    if (this.cursorPosition > 0) {
                        --this.cursorPosition;
                        this.inputBuffer.deleteCharAt(this.cursorPosition);
                    }

                    return true;
                }
            }
        } else {
            // If the box is not in focus, then we can interpret an inventory key as an action, rather than a character.
            if(keyCode == this.screen.getMinecraft().gameSettings.keyBindInventory.getKey().getKeyCode()) {
                this.screen.closeScreen();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    //@Override
    //public boolean keyReleased(int keyCode, int scanCode, int modifiers) {

    //}

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if(this.isFocused) {
            if(numeralOnly) {
                if(Character.isDigit(codePoint) && SharedConstants.isAllowedCharacter(codePoint)) {
                    //this.inputBuffer.append(codePoint);
                    this.inputBuffer.insert(this.cursorPosition, codePoint);
                    ++this.cursorPosition;
                }
            } else {
                if(SharedConstants.isAllowedCharacter(codePoint)) {
                    //this.inputBuffer.append(codePoint);
                    this.inputBuffer.insert(this.cursorPosition, codePoint);
                    ++this.cursorPosition;
                }
            }

            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    /**
     * Render this element.
     */
    @Override
    public void drawPart(MatrixStack mStack) {
        // Reset and bind texture.
        super.drawPart(mStack);
        GuiRenderHelper.setTexture(new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/gui_symbols.png"));

        // Draw background.
        GuiRenderHelper.setTextureOffset(0, 144);
        GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);

        String text = "";
        try {
            if(this.inputBuffer.length() > 10) {
                // Beginning of entry.
                if(this.cursorPosition < 5) {
                    text = this.inputBuffer.substring(0, 10) + "...";
                // Ending of entry.
                } else if (this.cursorPosition > this.inputBuffer.length() - 5) {
                    text = "..." + this.inputBuffer.substring(this.inputBuffer.length() - 10, this.inputBuffer.length());
                // Middle of entry.
                } else {
                    text = "..." + this.inputBuffer.substring(this.cursorPosition - 5, this.cursorPosition + 5) + "...";
                }
            } else {
                text = this.inputBuffer.toString();
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
        // Draw text.
        Minecraft.getInstance().fontRenderer.drawString(mStack, text, this.xPos + 4, this.yPos + 4, 16777215);
    }

    @Override
    public void drawTooltip(MatrixStack mStack, int mouseX, int mouseY) {}
}