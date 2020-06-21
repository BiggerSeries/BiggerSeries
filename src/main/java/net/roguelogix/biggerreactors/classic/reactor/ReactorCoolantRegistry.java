package net.roguelogix.biggerreactors.classic.reactor;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.HashMap;

public class ReactorCoolantRegistry {

    public static class BlockCoolantProperties{
        Block block;
    }

    private final static HashMap<Block, BlockCoolantProperties> blocks = new HashMap<>();

    public static boolean isBlockAllowed(Block block){
        return blocks.containsKey(block);
    }

    public static BlockCoolantProperties blockCoolantProperties(Block block){
        return blocks.get(block);
    }

    static {
        blocks.put(Blocks.AIR, new BlockCoolantProperties());
    }
}
