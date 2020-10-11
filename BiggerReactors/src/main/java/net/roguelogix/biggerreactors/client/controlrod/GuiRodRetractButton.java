package net.roguelogix.biggerreactors.client.controlrod;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.reactor.containers.ControlRodContainer;
import net.roguelogix.phosphophyllite.gui.client.GuiPartBase;
import net.roguelogix.phosphophyllite.gui.client.GuiRenderHelper;
import net.roguelogix.phosphophyllite.gui.client.api.IHasTooltip;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;

public class GuiRodRetractButton<T extends Container> extends GuiPartBase<T> implements IHasTooltip {
    
    private final ResourceLocation texture = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/gui_symbols.png");
    private boolean debounce = false;
    private boolean alt = false;
    private int modifiers = 0;
    private double insertionLevel;
    
    /**
     * @param screen The screen this instance belongs to.
     * @param xPos   The X position of the part.
     * @param yPos   The Y position of the part.
     * @param xSize  The width of the part.
     * @param ySize  The height of the part.
     */
    public GuiRodRetractButton(ContainerScreen<T> screen, int xPos, int yPos, int xSize, int ySize) {
        super(screen, xPos, yPos, xSize, ySize);
    }
    
    public void updateState(double insertionLevel) {
        this.insertionLevel = insertionLevel;
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
            GuiRenderHelper.setTextureOffset(96, 48);
        } else {
            GuiRenderHelper.setTextureOffset(80, 48);
        }
        GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
    }
    
    @Override
    public void drawTooltip(MatrixStack mStack, int mouseX, int mouseY) {
        if (this.isMouseOver(mouseX, mouseY)) {
            this.screen.func_243308_b(mStack, Arrays.stream(new TranslationTextComponent("tooltip.biggerreactors.buttons.control_rod.insertion.retract").getString().split("\\n")).map(StringTextComponent::new).collect(Collectors.toList()), mouseX, mouseY);
        }
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // This is necessary to solve a weird bug.
        this.modifiers = modifiers;
        if(modifiers > 0x3 && modifiers < 0x8) {
            this.alt = true;
            this.modifiers -= GLFW_MOD_ALT;
        } else {
            this.alt = false;
        }
        return true;
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        // This is necessary to solve a weird bug.
        this.modifiers = modifiers;
        if(modifiers > 0x3 && modifiers < 0x8) {
            this.alt = true;
            this.modifiers -= GLFW_MOD_ALT;
        } else {
            this.alt = false;
        }
        return true;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            double newInsertionLevel = insertionLevel;
            // Check for modifiers
            if (modifiers == GLFW_MOD_SHIFT + GLFW_MOD_CONTROL) {
                newInsertionLevel -= 100D;
            } else if (modifiers == GLFW_MOD_CONTROL) {
                newInsertionLevel -= 50D;
            } else if (modifiers == GLFW_MOD_SHIFT) {
                newInsertionLevel -= 10D;
            } else {
                newInsertionLevel -= 1L;
            }
    
            // Check for bounds.
            if(newInsertionLevel < 0D) newInsertionLevel = 0D;
    
            // Send data.
            ((ControlRodContainer) this.screen.getContainer()).executeRequest("setRodInsertion", new Pair<>(newInsertionLevel, this.alt));
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