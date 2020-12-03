package net.roguelogix.biggerreactors.client.redstoneport;

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
import net.roguelogix.phosphophyllite.gui.old.client.GuiPartBase;
import net.roguelogix.phosphophyllite.gui.old.client.GuiRenderHelper;
import net.roguelogix.phosphophyllite.gui.old.client.api.IHasTooltip;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GuiRedstoneCommitButton <T extends Container> extends GuiPartBase<T> implements IHasTooltip {

    private final ResourceLocation texture = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/gui_symbols.png");
    private boolean debounce = false;
    private boolean revertOrCommit = false;

    /**
     * @param screen         The screen this instance belongs to.
     * @param xPos           The X position of the part.
     * @param yPos           The Y position of the part.
     * @param xSize          The width of the part.
     * @param ySize          The height of the part.
     * @param revertOrCommit Revert if false, commit if true.
     */
    public GuiRedstoneCommitButton(ContainerScreen<T> screen, int xPos, int yPos, int xSize, int ySize, boolean revertOrCommit) {
        super(screen, xPos, yPos, xSize, ySize);
        this.revertOrCommit = revertOrCommit;
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
        if (this.revertOrCommit) {
            // Revert changes.
            if (this.debounce) {
                GuiRenderHelper.setTextureOffset(16, 128);
            } else {
                GuiRenderHelper.setTextureOffset(16, 112);
            }
        } else {
            // Commit changes.
            if (this.debounce) {
                GuiRenderHelper.setTextureOffset(0, 128);
            } else {
                GuiRenderHelper.setTextureOffset(0, 112);
            }
        }
        GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
    }

    @Override
    public void drawTooltip(MatrixStack mStack, int mouseX, int mouseY) {
        if (this.isMouseOver(mouseX, mouseY)) {
            if (this.revertOrCommit) {
                this.screen.func_243308_b(mStack, Arrays.stream(new TranslationTextComponent("tooltip.biggerreactors.buttons.redstone_port.commit").getString().split("\\n")).map(StringTextComponent::new).collect(Collectors.toList()), mouseX, mouseY);
            } else {
                this.screen.func_243308_b(mStack, Arrays.stream(new TranslationTextComponent("tooltip.biggerreactors.buttons.redstone_port.revert").getString().split("\\n")).map(StringTextComponent::new).collect(Collectors.toList()), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            if (revertOrCommit) {
                ((RedstonePortContainer) this.screen.getContainer()).executeRequest("commitChanges", true);
            } else {
                ((RedstonePortContainer) this.screen.getContainer()).executeRequest("revertChanges", true);
            }
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