package net.roguelogix.phosphophyllite.blocks.blackholes;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nullable;

@RegisterBlock(name = "fluid_black_hole", tileEntityClass = FluidBlackHoleTile.class)
public class FluidBlackHole extends Block {
    
    @RegisterBlock.Instance
    public static FluidBlackHole INSTANCE;
    
    public FluidBlackHole() {
        super(Properties.create(Material.IRON));
    }
    
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new FluidBlackHoleTile();
    }
}
