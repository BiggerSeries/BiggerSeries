package net.roguelogix.biggerreactors.classic.reactor.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.reactor.containers.ControlRodContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ControlRodState;
import net.roguelogix.phosphophyllite.gui.client.GuiScreenBase;

@OnlyIn(Dist.CLIENT)
public class ControlRodScreen extends GuiScreenBase<ControlRodContainer> implements IHasContainer<ControlRodContainer> {
    
    private ControlRodState controlRodState;
    
    public ControlRodScreen(ControlRodContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.controlRodState = (ControlRodState) this.getContainer().getGuiPacket();
        
        // Set textures.
        this.xSize = 176;
        this.ySize = 166;
        this.updateTexture(new ResourceLocation(BiggerReactors.modid, "textures/screen/control_rod.png"), 0, 0);
    }
    
    /**
     * Update logic.
     */
    @Override
    public void tick() {
        this.controlRodState = (ControlRodState) this.getContainer().getGuiPacket();
    }
    
    /**
     * Render tooltips.
     *
     * @param mouseX       X position of the mouse.
     * @param mouseY       Y position of the mouse.
     * @param partialTicks Good question.
     */
    @Override
    public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(mStack);
        super.render(mStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(mStack, mouseX, mouseY);
    }
    
    /**
     * Draw foreground elements.
     *
     * @param mouseX X position of the mouse.
     * @param mouseY Y position of the mouse.
     */
    @Override
    public void drawGuiContainerForegroundLayer(MatrixStack mStack, int mouseX, int mouseY) {
        this.font.drawString(mStack, new TranslationTextComponent("screen.biggerreactors.reactor_control_rod").getString(), 8, 6, 4210752);
    }
}
