package net.roguelogix.biggerreactors.classic.reactor.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorActivity;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockBlock;

import javax.annotation.Nonnull;

public class ReactorBaseBlock extends RectangularMultiblockBlock {
    public static final Properties PROPERTIES_SOLID = Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(2, 10);
    public static final Properties PROPERTIES_GLASS = Properties.create(Material.IRON).sound(SoundType.GLASS).notSolid().hardnessAndResistance(2);
    
    
    public ReactorBaseBlock() {
        this(true);
    }
    
    public ReactorBaseBlock(boolean solid) {
        super(solid ? PROPERTIES_SOLID : PROPERTIES_GLASS);
        if (usesBlockState()) {
            setDefaultState(getDefaultState().with(ReactorActivity.REACTOR_ACTIVITY_ENUM_PROPERTY, ReactorActivity.INACTIVE));
        }
    }
    
    @Override
    public final boolean hasTileEntity(BlockState state) {
        return true;
    }
    
    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(ReactorActivity.REACTOR_ACTIVITY_ENUM_PROPERTY);
    }
}
