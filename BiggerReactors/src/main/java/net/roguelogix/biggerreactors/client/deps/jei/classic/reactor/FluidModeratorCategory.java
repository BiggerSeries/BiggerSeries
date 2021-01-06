package net.roguelogix.biggerreactors.client.deps.jei.classic.reactor;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.reactor.ReactorModeratorRegistry;
import net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorTerminal;

import java.awt.*;


public class FluidModeratorCategory implements IRecipeCategory<FluidModeratorCategory.Recipe> {
    private final IDrawable background;
    private final IDrawable icon;
    public static final ResourceLocation UID = new ResourceLocation(BiggerReactors.modid, "classic/reactor_moderator_fluid");

    public FluidModeratorCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(new ItemStack(ReactorTerminal.INSTANCE));
        background = guiHelper.createDrawable(new ResourceLocation(BiggerReactors.modid, "textures/jei/common.png"), 0, 0, 144, 46);
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
        return I18n.format("jei.biggerreactors.classic.reactor_moderator_fluid");
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
        iIngredients.setInput(VanillaTypes.FLUID, recipe.getInput());
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, Recipe recipe, IIngredients iIngredients) {
        IGuiFluidStackGroup guiItemStacks = iRecipeLayout.getFluidStacks();
        guiItemStacks.init(0, true, 1, 15);

        guiItemStacks.set(iIngredients);
    }

    @Override
    public void draw(Recipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        String[] info = {
                I18n.format("jei.biggerreactors.classic.reactor_moderator_moderation", recipe.getModeratorProperties().moderation),
                I18n.format("jei.biggerreactors.classic.reactor_moderator_absorption", recipe.getModeratorProperties().absorption),
                I18n.format("jei.biggerreactors.classic.reactor_moderator_conductivity", recipe.getModeratorProperties().heatConductivity),
                I18n.format("jei.biggerreactors.classic.reactor_moderator_efficiency", recipe.getModeratorProperties().heatEfficiency)
        };
        mc.fontRenderer.drawString(matrixStack,  info[0], 80 - mc.fontRenderer.getStringWidth(info[0]) / 2F, 0, Color.BLACK.getRGB());
        mc.fontRenderer.drawString(matrixStack,  info[1], 80 - mc.fontRenderer.getStringWidth(info[1]) / 2F, 12, Color.BLACK.getRGB());
        mc.fontRenderer.drawString(matrixStack,  info[2], 80 - mc.fontRenderer.getStringWidth(info[2]) / 2F, 24, Color.BLACK.getRGB());
        mc.fontRenderer.drawString(matrixStack,  info[3], 80 - mc.fontRenderer.getStringWidth(info[3]) / 2F, 36, Color.BLACK.getRGB());
    }

    public static class Recipe {
        private final FluidStack input;
        private final ReactorModeratorRegistry.ModeratorProperties moderatorProperties;

        public Recipe(FluidStack input, ReactorModeratorRegistry.ModeratorProperties moderatorProperties) {
            this.input = input;
            this.moderatorProperties = moderatorProperties;
        }

        public FluidStack getInput() {
            return input;
        }

        public ReactorModeratorRegistry.ModeratorProperties getModeratorProperties() {
            return moderatorProperties;
        }
    }
}
