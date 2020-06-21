package net.roguelogix.biggerreactors.classic.reactor;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.HashMap;

public class ReactorCoolantRegistry {

    class BlockCoolantProperties{
        Block block;
    }

    private final static HashMap<Block, BlockCoolantProperties> blocks = new HashMap<>();

    boolean isBlockAllowed(Block block){
        return blocks.containsKey(block);
    }

    BlockCoolantProperties blockCoolantProperties(Block block){
        return blocks.get(block);
    }

    {
        blocks.put(Blocks.AIR, new BlockCoolantProperties());
    }
}
