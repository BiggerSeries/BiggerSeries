package net.roguelogix.biggerreactors.classic.turbine.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateContainer;
import net.roguelogix.biggerreactors.classic.turbine.state.TurbineActivity;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockBlock;

import javax.annotation.Nonnull;

public class TurbineBaseBlock extends RectangularMultiblockBlock {
    public static final Block.Properties PROPERTIES_SOLID = Block.Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(2, 10);
    public static final Block.Properties PROPERTIES_GLASS = Block.Properties.create(Material.IRON).sound(SoundType.METAL).notSolid().hardnessAndResistance(2);
    
    
    public TurbineBaseBlock() {
        this(true);
    }
    
    public TurbineBaseBlock(boolean solid) {
        super(solid ? PROPERTIES_SOLID : PROPERTIES_GLASS);
        if (usesBlockState()) {
            setDefaultState(getDefaultState().with(TurbineActivity.TURBINE_STATE_ENUM_PROPERTY, TurbineActivity.INACTIVE));
        }
    }
    
    @Override
    public final boolean hasTileEntity(BlockState state) {
        return true;
    }
    
    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(TurbineActivity.TURBINE_STATE_ENUM_PROPERTY);
    }
}
