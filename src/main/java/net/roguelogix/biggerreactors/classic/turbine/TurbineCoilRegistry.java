package net.roguelogix.biggerreactors.classic.turbine;

import net.minecraft.block.Block;
import net.roguelogix.biggerreactors.blocks.materials.LudicriteBlock;

public class TurbineCoilRegistry {
    public static boolean isBlockAllowed(Block block) {
        return block instanceof LudicriteBlock;
    }
}
