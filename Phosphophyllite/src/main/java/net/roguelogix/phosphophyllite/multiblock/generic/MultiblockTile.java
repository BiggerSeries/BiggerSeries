package net.roguelogix.phosphophyllite.multiblock.generic;

import static net.minecraftforge.common.util.Constants.BlockFlags.BLOCK_UPDATE;
import static net.minecraftforge.common.util.Constants.BlockFlags.NOTIFY_NEIGHBORS;
import static net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockPositions.DISASSEMBLED;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.roguelogix.phosphophyllite.Phosphophyllite;
import net.roguelogix.phosphophyllite.items.DebugTool;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockPositions;

public abstract class MultiblockTile extends TileEntity {
    protected MultiblockController controller;
    
    long lastSavedTick = 0;
    
    public void attemptAttach() {
        controller = null;
        attemptAttach = true;
        assert world != null;
        if (!world.isRemote) {
            Phosphophyllite.tilesToAttach.add(this);
        }
    }
    
    private boolean attemptAttach = true;
    private boolean allowAttach = true;
    
    @SuppressWarnings("CanBeFinal")
    protected Validator<MultiblockController> attachableControllerValidator = c -> true;
    
    public MultiblockTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    
    public void attachToNeighbors() {
        assert world != null;
        if (allowAttach && attemptAttach && !world.isRemote) {
            attemptAttach = false;
            if (controller != null) {
                controller.detach(this);
                controller = null;
            }
            if (attachableControllerValidator != null) {
                // at this point, i need to get or create a controller
                BlockPos[] possiblePositions = {
                        pos.add(-1, 0, 0),
                        pos.add(1, 0, 0),
                        pos.add(0, 0, -1),
                        pos.add(0, 0, 1),
                        pos.add(0, -1, 0),
                        pos.add(0, 1, 0),
                };
                TileEntity[] possibleTiles = {
                        world.isBlockLoaded(possiblePositions[0]) ? world.getTileEntity(possiblePositions[0]) : null,
                        world.isBlockLoaded(possiblePositions[1]) ? world.getTileEntity(possiblePositions[1]) : null,
                        world.isBlockLoaded(possiblePositions[2]) ? world.getTileEntity(possiblePositions[2]) : null,
                        world.isBlockLoaded(possiblePositions[3]) ? world.getTileEntity(possiblePositions[3]) : null,
                        world.isBlockLoaded(possiblePositions[4]) ? world.getTileEntity(possiblePositions[4]) : null,
                        world.isBlockLoaded(possiblePositions[5]) ? world.getTileEntity(possiblePositions[5]) : null,
                };
                for (TileEntity possibleTile : possibleTiles) {
                    if (possibleTile instanceof MultiblockTile) {
                        if (((MultiblockTile) possibleTile).controller != null) {
                            if (attachableControllerValidator.validate(((MultiblockTile) possibleTile).controller)) {
                                ((MultiblockTile) possibleTile).controller.attemptAttach(this);
                            }
                        } else {
                            ((MultiblockTile) possibleTile).attemptAttach = true;
                        }
                    }
                }
            }
            if (controller == null) {
                createController().attemptAttach(this);
            }
        }
    }
    
    @Override
    public void validate() {
        super.validate();
        attemptAttach();
    }
    
    @Override
    public void onLoad() {
        attemptAttach();
    }
    
    @Override
    public final void remove() {
        if (controller != null) {
            controller.detach(this);
            allowAttach = false;
        }
        super.remove();
    }
    
    @Override
    public void onChunkUnloaded() {
        if (controller != null) {
            controller.detach(this, true);
            allowAttach = false;
        }
    }
    
    public abstract MultiblockController createController();
    
    protected void readNBT(CompoundNBT compound) {
    }
    
    protected CompoundNBT writeNBT() {
        return new CompoundNBT();
    }
    
    CompoundNBT controllerData = null;
    
    @Override
    public final void read(BlockState state, @Nonnull CompoundNBT compound) {
        super.read(state, compound);
        if (compound.contains("controllerData")) {
            controllerData = compound.getCompound("controllerData");
        }
        if (compound.contains("bakedmodeldata")) {
            updateBakedModelState(compound.getCompound("bakedmodeldata"));
        }
        if (compound.contains("userdata")) {
            readNBT(compound.getCompound("userdata"));
        }
    }
    
    
    @Override
    public final CompoundNBT write(@Nonnull CompoundNBT compound) {
        super.write(compound);
        if (controller != null && controller.blocks.contains(this)) {
            compound.put("controllerData", controller.getNBT());
        }
        compound.put("bakedmodeldata", getBakedModelState());
        compound.put("userdata", writeNBT());
        return compound;
    }
    
    public CompoundNBT getBakedModelState() {
        return new CompoundNBT();
    }
    
    public void updateBakedModelState(CompoundNBT nbt) {
    
    }
    
    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }
    
    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("bakedmodeldata", getBakedModelState());
        return new SUpdateTileEntityPacket(pos, 1, nbt);
    }
    
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        ModelDataManager.requestModelDataRefresh(this);
        CompoundNBT nbt = pkt.getNbtCompound();
        if (nbt.contains("bakedmodeldata")) {
            updateBakedModelState(nbt.getCompound("bakedmodeldata"));
        }
        ModelDataManager.requestModelDataRefresh(this);
        assert world != null;
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), BLOCK_UPDATE + NOTIFY_NEIGHBORS);
    }
    
    public boolean doBlockStateUpdate() {
        return true;
    }
    
    @Nonnull
    @Override
    public IModelData getModelData() {
        ModelDataMap.Builder builder = new ModelDataMap.Builder();
        appendModelData(builder);
        return builder.build();
    }
    
    protected void appendModelData(ModelDataMap.Builder builder) {
    }
    
    protected void onAssemblyAttempted() {
    }
    
    protected String getDebugInfo() {
        return controller.getDebugInfo();
    }
    
    public ActionResultType onBlockActivated(PlayerEntity player, Hand handIn) {
        if (handIn == Hand.MAIN_HAND) {
            // TODO: 8/8/20 add a generic layer for this to check against
            //              currently not a problem as a only use rectangular multiblocks
            if (player.getHeldItemMainhand() == ItemStack.EMPTY && getBlockState().get(RectangularMultiblockPositions.POSITIONS_ENUM_PROPERTY) == DISASSEMBLED) {
                if (controller != null) {
                    if (controller.lastValidationError != null) {
                        player.sendMessage(controller.lastValidationError.getTextComponent(), player.getUniqueID());
                    }else{
                        player.sendMessage(new TranslationTextComponent("multiblock.error.phosphophyllite.unknown"), player.getUniqueID());
                    }
                }
                return ActionResultType.SUCCESS;
                
            } else if (player.getHeldItemMainhand().getItem() == DebugTool.INSTANCE) {
                // no its not getting translated, its debug info, *english*
                if (controller != null) {
                    player.sendMessage(new StringTextComponent(getDebugInfo()), player.getUniqueID());
                }
                return ActionResultType.SUCCESS;
                
            }
        }
        return ActionResultType.PASS;
    }
}
