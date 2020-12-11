package net.roguelogix.biggerreactors.client.old.redstoneport;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.reactor.containers.ReactorRedstonePortContainer;
import net.roguelogix.phosphophyllite.gui.old.client.GuiPartBase;
import net.roguelogix.phosphophyllite.gui.old.client.GuiRenderHelper;
import net.roguelogix.phosphophyllite.gui.old.client.api.IHasTooltip;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GuiRedstoneToggles <T extends Container> extends GuiPartBase<T> implements IHasTooltip {

    private final ResourceLocation texture = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/gui_symbols.png");
    private boolean debounce = false;
    // If false, pulse or active above. If true, then signal or active below.
    private boolean state = false;

    // Yes, this is horrible.
    // Yes, this GUI system is jank as hell.
    // Yes, I plan on rewriting it to be clean.
    // ... what am I even doing anymore.
    // I'll fix this during the GUI rewrite.
    // Coming soon to a JAR file near you!

    private String tooltipTranslationKey;
    // False if this toggle is for Active Above/Active Below, true if for Pulse/Signal.
    private boolean aboveBelowOrPulseSignal;


    /**
     * @param screen The screen this instance belongs to.
     * @param xPos   The X position of the part.
     * @param yPos   The Y position of the part.
     * @param xSize  The width of the part.
     * @param ySize  The height of the part.
     */
    public GuiRedstoneToggles(ContainerScreen<T> screen, int xPos, int yPos, int xSize, int ySize, String tooltipTranslationKey, boolean aboveBelowOrPulseSignal) {
        super(screen, xPos, yPos, xSize, ySize);
        this.tooltipTranslationKey = tooltipTranslationKey;
        this.aboveBelowOrPulseSignal = aboveBelowOrPulseSignal;
    }

    public void updateState(boolean state) {
        this.state = state;
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
        if (this.aboveBelowOrPulseSignal) {
            if(this.state) {
                GuiRenderHelper.setTextureOffset(48, 128);
            } else {
                GuiRenderHelper.setTextureOffset(48, 112);
            }
        } else {
            if(this.state) {
                GuiRenderHelper.setTextureOffset(64, 128);
            } else {
                GuiRenderHelper.setTextureOffset(64, 112);
            }
        }
        GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
    }

    @Override
    public void drawTooltip(MatrixStack mStack, int mouseX, int mouseY) {
        if (this.isMouseOver(mouseX, mouseY)) {
            this.screen.func_243308_b(mStack, Arrays.stream(new TranslationTextComponent(tooltipTranslationKey).getString().split("\\n")).map(StringTextComponent::new).collect(Collectors.toList()), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            if(aboveBelowOrPulseSignal) {
                ((ReactorRedstonePortContainer) this.screen.getContainer()).executeRequest("setPulseOrSignal", !state);
            } else {
                ((ReactorRedstonePortContainer) this.screen.getContainer()).executeRequest("setAboveOrBelow", !state);
            }
            assert this.screen.getMinecraft().player != null;
            this.screen.getMinecraft().player.playSound(SoundEvents.UI_BUTTON_CLICK, this.screen.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER), 1.0F);
            return true;
        }
    }
}
