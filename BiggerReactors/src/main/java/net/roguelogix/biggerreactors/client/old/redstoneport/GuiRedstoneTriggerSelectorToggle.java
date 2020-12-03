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
import net.roguelogix.biggerreactors.classic.reactor.containers.RedstonePortContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.RedstonePortSelection;
import net.roguelogix.phosphophyllite.gui.old.client.GuiPartBase;
import net.roguelogix.phosphophyllite.gui.old.client.GuiRenderHelper;
import net.roguelogix.phosphophyllite.gui.old.client.api.IHasTooltip;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GuiRedstoneTriggerSelectorToggle<T extends Container> extends GuiPartBase<T> implements IHasTooltip {

    private final ResourceLocation texture = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/gui_symbols.png");
    private boolean debounce = false;
    private boolean selected = false;

    // Yes, this is horrible.
    // Yes, this GUI system is jank as hell.
    // Yes, I plan on rewriting it to be clean.
    // ... what am I even doing anymore.
    // I'll fix this during the GUI rewrite.
    // Coming soon to a JAR file near you!

    private String tooltipTranslationKey;
    private RedstonePortSelection activeSelector;


    /**
     * @param screen The screen this instance belongs to.
     * @param xPos   The X position of the part.
     * @param yPos   The Y position of the part.
     * @param xSize  The width of the part.
     * @param ySize  The height of the part.
     */
    public GuiRedstoneTriggerSelectorToggle(ContainerScreen<T> screen, int xPos, int yPos, int xSize, int ySize, String tooltipTranslationKey, RedstonePortSelection activeSelector) {
        super(screen, xPos, yPos, xSize, ySize);
        this.tooltipTranslationKey = tooltipTranslationKey;
        this.activeSelector = activeSelector;
    }

    public void updateState(boolean selected) {
        this.selected = selected;
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
        if (this.selected) {
            // Button trigger * 16... yeah its wack but it works til rewrite.
            GuiRenderHelper.setTextureOffset((RedstonePortSelection.fromInt(activeSelector) * 16), 96);
        } else {
            GuiRenderHelper.setTextureOffset((RedstonePortSelection.fromInt(activeSelector) * 16), 80);
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
            ((RedstonePortContainer) this.screen.getContainer()).executeRequest("setSelectedButton", RedstonePortSelection.fromInt(activeSelector));
            assert this.screen.getMinecraft().player != null;
            this.screen.getMinecraft().player.playSound(SoundEvents.UI_BUTTON_CLICK, this.screen.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER), 1.0F);
            return true;
        }
    }
}