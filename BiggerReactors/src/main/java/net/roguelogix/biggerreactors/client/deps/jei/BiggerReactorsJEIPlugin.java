package net.roguelogix.biggerreactors.client.deps.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.block.AirBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.reactor.ReactorModeratorRegistry;
import net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorTerminal;
import net.roguelogix.biggerreactors.classic.turbine.TurbineCoilRegistry;
import net.roguelogix.biggerreactors.classic.turbine.blocks.TurbineTerminal;
import net.roguelogix.biggerreactors.client.deps.jei.classic.reactor.BlockModeratorCategory;
import net.roguelogix.biggerreactors.client.deps.jei.classic.reactor.FluidModeratorCategory;
import net.roguelogix.biggerreactors.client.deps.jei.classic.turbine.CoilCategory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JeiPlugin
public class BiggerReactorsJEIPlugin implements IModPlugin {

    @Nonnull
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(BiggerReactors.modid, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CoilCategory(guiHelper));
        registration.addRecipeCategories(new BlockModeratorCategory(guiHelper));
        registration.addRecipeCategories(new FluidModeratorCategory(guiHelper));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(TurbineTerminal.INSTANCE), CoilCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(ReactorTerminal.INSTANCE), BlockModeratorCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(ReactorTerminal.INSTANCE), FluidModeratorCategory.UID);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<CoilCategory.Recipe> recipes = TurbineCoilRegistry.getImmutableRegistry().entrySet().stream()
                .map(e -> new CoilCategory.Recipe(new ItemStack(e.getKey().asItem()), e.getValue()))
                .collect(Collectors.toList());

        registration.addRecipes(recipes, CoilCategory.UID);


        List<FluidModeratorCategory.Recipe> fluidModeratorRecipes = new ArrayList<>();
        List<BlockModeratorCategory.Recipe> blockModeratorRecipes = new ArrayList<>();

        ReactorModeratorRegistry.getImmutableRegistry().forEach((block, moderatorProperties) -> {
            if (block instanceof FlowingFluidBlock) {
                FlowingFluidBlock fluidBlock = (FlowingFluidBlock) block;
                FluidStack stack = new FluidStack(fluidBlock.getFluid(), 1000);
                fluidModeratorRecipes.add(new FluidModeratorCategory.Recipe(stack, moderatorProperties));
            } else if (!(block instanceof AirBlock)) {
                ItemStack stack = new ItemStack(block.asItem());
                blockModeratorRecipes.add(new BlockModeratorCategory.Recipe(stack, moderatorProperties));
            }
        });

        registration.addRecipes(fluidModeratorRecipes, FluidModeratorCategory.UID);
        registration.addRecipes(blockModeratorRecipes, BlockModeratorCategory.UID);
    }
}
