package net.roguelogix.biggerreactors.classic.reactor.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorBaseTile;
import net.roguelogix.biggerreactors.classic.reactor.ReactorState;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockBlock;

public class ReactorBaseBlock extends RectangularMultiblockBlock {
    public static final Properties PROPERTIES_SOLID = Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(2, 10);
    public static final Properties PROPERTIES_GLASS = Properties.create(Material.IRON).sound(SoundType.METAL).notSolid().hardnessAndResistance(2);


    public ReactorBaseBlock() {
        this(true);
    }

    public ReactorBaseBlock(boolean solid) {
        super(solid ? PROPERTIES_SOLID : PROPERTIES_GLASS);
        if(usesBlockState()) {
            setDefaultState(getDefaultState().with(ReactorState.REACTOR_STATE_ENUM_PROPERTY, ReactorState.INACTIVE));
        }
    }

    @Override
    public final boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult p_225533_6_) {
        ActionResultType superAction = super.onBlockActivated(state, worldIn, pos, player, handIn, p_225533_6_);
        if(superAction != ActionResultType.PASS){
            return superAction;
        }
        if(handIn != Hand.MAIN_HAND){
            return ActionResultType.PASS;
        }
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof ReactorBaseTile){
            ((ReactorBaseTile) te).onActivated(player);
        }
        return ActionResultType.PASS;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(ReactorState.REACTOR_STATE_ENUM_PROPERTY);
    }
}
