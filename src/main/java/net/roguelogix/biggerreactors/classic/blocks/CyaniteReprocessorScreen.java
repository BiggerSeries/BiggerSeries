package net.roguelogix.biggerreactors.classic.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.BiggerReactors;

@OnlyIn(Dist.CLIENT)
public class CyaniteReprocessorScreen extends ContainerScreen<CyaniteReprocessorContainer> implements IHasContainer<CyaniteReprocessorContainer> {

  private ResourceLocation GUI_TEXTURE = new ResourceLocation(BiggerReactors.modid, "textures/gui/cyanite_reprocessor.png");
  private int GUI_TEXTURE_WIDTH = 176;
  private int GUI_TEXTURE_HEIGHT = 166;

  public CyaniteReprocessorScreen(CyaniteReprocessorContainer screenContainer, PlayerInventory inventory, ITextComponent title) {
    super(screenContainer, inventory, title);
  }

  @Override
  public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.func_230446_a_(matrixStack);
    super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
    this.func_230459_a_(matrixStack, mouseX, mouseY);
  }

  @Override
  protected void func_230451_b_(MatrixStack matrixStack, int mouseX, int mouseY) {
    this.field_230712_o_.func_238422_b_(matrixStack, this.field_230704_d_, (float) this.field_230708_k_, (float) this.field_230709_l_, 4210752);
    this.field_230712_o_.func_238422_b_(matrixStack, this.playerInventory.getDisplayName(), (float) 8, (float) (this.ySize - 96 + 2), 4210752);
  }

  @Override
  protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

    this.field_230706_i_.getTextureManager().bindTexture(GUI_TEXTURE);

    int relativeX = (this.field_230708_k_ - this.xSize) / 2;
    int relativeY = (this.field_230709_l_ - this.ySize) / 2;

    func_238463_a_(matrixStack, relativeX, relativeY, 0, 0, this.xSize, this.ySize, GUI_TEXTURE_WIDTH, GUI_TEXTURE_HEIGHT);
  }
}
