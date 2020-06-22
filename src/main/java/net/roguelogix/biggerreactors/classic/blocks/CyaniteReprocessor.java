package net.roguelogix.biggerreactors.classic.blocks;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

@RegisterBlock(name = "cyanite_reprocessor", tileEntityClass = CyaniteReprocessorTile.class)
public class CyaniteReprocessor extends ContainerBlock {

    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    @RegisterBlock.Instance
    public static CyaniteReprocessor INSTANCE;

    public CyaniteReprocessor() {
        super(
            Properties.create(Material.IRON)
                .sound(SoundType.STONE)
                .hardnessAndResistance(1.0F)
        );
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH).with(ENABLED,
            Boolean.FALSE));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if(stack.hasDisplayName()) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof CyaniteReprocessorTile) {
                ((CyaniteReprocessorTile) tileEntity).setCustomName(stack.getDisplayName());
            }
        }

        if (entity != null) {
            world.setBlockState(pos, state
                .with(FACING, getFacingFromEntity(pos, entity))
                // TODO: add enable/lit logic
                .with(ENABLED, false), 2);
        }
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
        return Direction.getFacingFromVector((float) (entity.getPosX() - clickedBlock.getX()), 0.0F, (float) (entity.getPosZ() - clickedBlock.getZ()));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(ENABLED);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new CyaniteReprocessorTile();
    }
}
