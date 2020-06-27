package net.roguelogix.phosphophyllite.multiblock.generic;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.roguelogix.phosphophyllite.Phosphophyllite;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraftforge.common.util.Constants.BlockFlags.BLOCK_UPDATE;
import static net.minecraftforge.common.util.Constants.BlockFlags.NOTIFY_NEIGHBORS;

public abstract class MultiblockTile extends TileEntity {
    protected MultiblockController controller;

    long lastSavedTick = 0;

    public void attemptAttach(){
        controller = null;
        attemptAttach = true;
        if (!world.isRemote) {
            Phosphophyllite.tilesToAttach.add(this);
        }
    }

    private boolean attemptAttach = true;
    private boolean allowAttach = true;

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

//    @Override
    public final void read(CompoundNBT compound) {
//        super.read(compound);
        if (compound.contains("controllerData")) {
            controllerData = compound.getCompound("controllerData");
        }
        if(compound.contains("bakedmodeldata")){
            updateBakedModelState(compound.getCompound("bakedmodeldata"));
        }
        if(compound.contains("userdata")){
            readNBT(compound.getCompound("userdata"));
        }
    }

    // TODO: 6/25/20 mappings 
    @Override
    public void func_230337_a_(BlockState blockState, CompoundNBT compoundNBT) {
        super.func_230337_a_(blockState, compoundNBT);
        read(compoundNBT);
    }

    @Override
    public final CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if(controller != null && controller.blocks.contains(this)) {
            compound.put("controllerData", controller.getNBT());
        }
        compound.put("bakedmodeldata", getBakedModelState());
        compound.put("userdata", writeNBT());
        return compound;
    }

    public CompoundNBT getBakedModelState(){
        return new CompoundNBT();
    }

    public void updateBakedModelState(CompoundNBT nbt){

    }

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
        if(nbt.contains("bakedmodeldata")){
            updateBakedModelState(nbt.getCompound("bakedmodeldata"));
        }
        ModelDataManager.requestModelDataRefresh(this);
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(),BLOCK_UPDATE + NOTIFY_NEIGHBORS);
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

    protected void appendModelData(ModelDataMap.Builder builder){
    }

    protected void onAssemblyAttempted(){
    }
}
