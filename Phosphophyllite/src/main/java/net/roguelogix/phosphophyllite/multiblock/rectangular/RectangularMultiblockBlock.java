package net.roguelogix.phosphophyllite.multiblock.rectangular;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockBlock;

import javax.annotation.Nonnull;

import static net.roguelogix.phosphophyllite.multiblock.rectangular.AxisPosition.*;

public class RectangularMultiblockBlock extends MultiblockBlock {
    
    public RectangularMultiblockBlock(@Nonnull Properties properties) {
        super(properties);
        if (usesBlockState()) {
            setDefaultState(getDefaultState().with(X_AXIS_POSITION, MIDDLE));
            setDefaultState(getDefaultState().with(Y_AXIS_POSITION, MIDDLE));
            setDefaultState(getDefaultState().with(Z_AXIS_POSITION, MIDDLE));
        }
    }
    
    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        if (usesBlockState()) {
            builder.add(X_AXIS_POSITION);
            builder.add(Y_AXIS_POSITION);
            builder.add(Z_AXIS_POSITION);
        }
    }
    
    
}
