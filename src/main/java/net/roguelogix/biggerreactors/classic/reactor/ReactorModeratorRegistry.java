package net.roguelogix.biggerreactors.classic.reactor;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;

public class ReactorModeratorRegistry {
    
    public static class ModeratorProperties {
        
        public final float absorption;
        public final float heatEfficiency;
        public final float moderation;
        public final float heatConductivity;
        
        public ModeratorProperties(float absorption, float heatEfficiency, float moderation, float heatConductivity) {
            this.absorption = absorption;
            this.heatEfficiency = heatEfficiency;
            this.moderation = moderation;
            this.heatConductivity = heatConductivity;
        }
        
    }
    
    public interface IModeratorPropertiesProvider {
        
        ModeratorProperties blockModeratorProperties();
    }
    
    private final static HashMap<Block, ModeratorProperties> blocks = new HashMap<>();
    
    public static boolean isBlockAllowed(Block block) {
        return blocks.containsKey(block);
    }
    
    public static ModeratorProperties blockModeratorProperties(Block block) {
        return blocks.get(block);
    }
    
    public static void registerBlock(String location) {
        registerBlock(new ResourceLocation(location));
    }
    
    public static void registerBlock(ResourceLocation location) {
        Block block = ForgeRegistries.BLOCKS.getValue(location);
        if (block instanceof IModeratorPropertiesProvider) {
            registerBlock(block, ((IModeratorPropertiesProvider) block).blockModeratorProperties());
        }
    }
    
    public static void registerBlock(String location, float absorption, float heatEfficiency, float moderation, float conductivity) {
        registerBlock(new ResourceLocation(location), absorption, heatEfficiency, moderation, conductivity);
    }
    
    public static void registerBlock(ResourceLocation location, float absorption, float heatEfficiency, float moderation, float conductivity) {
        registerBlock(ForgeRegistries.BLOCKS.getValue(location), absorption, heatEfficiency, moderation, conductivity);
    }
    
    public static void registerBlock(Block block, float absorption, float heatEfficiency, float moderation, float conductivity) {
        registerBlock(block, new ModeratorProperties(absorption, heatEfficiency, moderation, conductivity));
    }
    
    public static void registerBlock(Block block, ModeratorProperties properties) {
        blocks.put(block, properties);
    }
}
