package net.roguelogix.phosphophyllite.multiblock.rectangular;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockBlock;

import javax.annotation.Nonnull;

import static net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockPositions.POSITIONS_ENUM_PROPERTY;

public class RectangularMultiblockBlock extends MultiblockBlock {
    
    public RectangularMultiblockBlock(Properties properties) {
        super(properties);
        if (usesBlockState()) {
            setDefaultState(getStateContainer().getBaseState().with(POSITIONS_ENUM_PROPERTY, RectangularMultiblockPositions.DISASSEMBLED));
        }
    }
    
    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(POSITIONS_ENUM_PROPERTY);
    }
    
    
}
