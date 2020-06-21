package net.roguelogix.biggerreactors.classic.reactor;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.HashMap;

public class ReactorCoolantRegistry {

    public static class BlockCoolantProperties{
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

    private final static HashMap<Block, BlockCoolantProperties> blocks = new HashMap<>();

    public static boolean isBlockAllowed(Block block){
        return blocks.containsKey(block);
    }

    public static BlockCoolantProperties blockCoolantProperties(Block block){
        return blocks.get(block);
    }

    public static void registerBlock(BlockCoolantProperties properties){
        blocks.put(properties.block, properties);
    }

    static {
        registerBlock(new BlockCoolantProperties(Blocks.AIR, 0.1f, 0.25f, 1.1f, 0.05f));
    }
}
