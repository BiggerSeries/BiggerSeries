package net.roguelogix.biggerreactors.client.deps.jei.classic.turbine;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.turbine.TurbineCoilRegistry;
import net.roguelogix.biggerreactors.classic.turbine.blocks.TurbineTerminal;

import java.awt.*;

public class CoilCategory implements IRecipeCategory<CoilCategory.Recipe> {

    private final IDrawable background;
    private final IDrawable icon;
    public static final ResourceLocation UID = new ResourceLocation(BiggerReactors.modid, "classic/turbine_coil");

    public CoilCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(new ItemStack(TurbineTerminal.INSTANCE));
        background = guiHelper.createDrawable(new ResourceLocation(BiggerReactors.modid, "textures/jei/common.png"), 0, 6, 144, 34);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class getRecipeClass() {
        return Recipe.class;
    }

    @Override
    public String getTitle() {
        return I18n.format("jei.biggerreactors.classic.turbine_coil_block");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(Recipe recipe, IIngredients iIngredients) {
        iIngredients.setInput(VanillaTypes.ITEM, recipe.getInput());
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, Recipe recipe, IIngredients iIngredients) {
        IGuiItemStackGroup guiItemStacks = iRecipeLayout.getItemStacks();
        guiItemStacks.init(0, true, 0, 8);

        guiItemStacks.set(iIngredients);
    }

    @Override
    public void draw(Recipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        String[] info = {
                I18n.format("jei.biggerreactors.classic.turbine_coil_bonus", recipe.getCoilData().bonus),
                I18n.format("jei.biggerreactors.classic.turbine_coil_efficiency", recipe.getCoilData().efficiency),
                I18n.format("jei.biggerreactors.classic.turbine_coil_extraction", recipe.getCoilData().extractionRate)
        };
        mc.fontRenderer.drawString(matrixStack,  info[0], 80 - mc.fontRenderer.getStringWidth(info[0]) / 2F, 0, Color.BLACK.getRGB());
        mc.fontRenderer.drawString(matrixStack,  info[1], 80 - mc.fontRenderer.getStringWidth(info[1]) / 2F, 12, Color.BLACK.getRGB());
        mc.fontRenderer.drawString(matrixStack,  info[2], 80 - mc.fontRenderer.getStringWidth(info[2]) / 2F, 24, Color.BLACK.getRGB());
    }

    public static class Recipe {
        private final ItemStack input;
        private final TurbineCoilRegistry.CoilData coilData;

        public Recipe(ItemStack input, TurbineCoilRegistry.CoilData coilData) {
            this.input = input;
            this.coilData = coilData;
        }

        public ItemStack getInput() {
            return input;
        }

        public TurbineCoilRegistry.CoilData getCoilData() {
            return coilData;
        }
    }
}
