package net.roguelogix.phosphophyllite.quartz_old;

import net.minecraft.block.BlockState;
import net.minecraft.world.World;
import org.joml.Vector3ic;

public interface QuartzBlock {
    QuartzState buildQuartzState(Vector3ic position, World world, BlockState state);
}
