package net.roguelogix.biggerreactors.classic.blocks.reactor;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;

public class ReactorCoolantRegistry {

    public static class BlockCoolantProperties {
        final Block block;
        final float absorption;
        final float heatEfficiency;
        final float moderation;
        final float conductivity;

        public BlockCoolantProperties(Block block, float absorption, float heatEfficiency, float moderation, float conductivity) {
            this.block = block;
            this.absorption = absorption;
            this.heatEfficiency = heatEfficiency;
            this.moderation = moderation;
            this.conductivity = conductivity;
        }
    }

    public interface IBlockCoolantPropertiesProvider{
        BlockCoolantProperties blockCoolantProperties();
    }

    private final static HashMap<Block, BlockCoolantProperties> blocks = new HashMap<>();

    public static boolean isBlockAllowed(Block block) {
        return blocks.containsKey(block);
    }

    public static BlockCoolantProperties blockCoolantProperties(Block block) {
        return blocks.get(block);
    }

    public static void registerBlock(String location) {
        registerBlock(new ResourceLocation(location));
    }

    public static void registerBlock(ResourceLocation location) {
        Block block = ForgeRegistries.BLOCKS.getValue(location);
        if (block instanceof IBlockCoolantPropertiesProvider) {
            registerBlock(((IBlockCoolantPropertiesProvider) block).blockCoolantProperties());
        }
    }

    public static void registerBlock(String location, float absorption, float heatEfficiency, float moderation, float conductivity) {
        registerBlock(new ResourceLocation(location), absorption, heatEfficiency, moderation, conductivity);
    }

    public static void registerBlock(ResourceLocation location, float absorption, float heatEfficiency, float moderation, float conductivity) {
        registerBlock(ForgeRegistries.BLOCKS.getValue(location), absorption, heatEfficiency, moderation, conductivity);
    }

    public static void registerBlock(Block block, float absorption, float heatEfficiency, float moderation, float conductivity) {
        registerBlock(new BlockCoolantProperties(block, absorption, heatEfficiency, moderation, conductivity));
    }

    public static void registerBlock(BlockCoolantProperties properties) {
        blocks.put(properties.block, properties);
    }

    static {
        registerBlock(Blocks.AIR, 0.1f, 0.25f, 1.1f, 0.05f);
    }
}
