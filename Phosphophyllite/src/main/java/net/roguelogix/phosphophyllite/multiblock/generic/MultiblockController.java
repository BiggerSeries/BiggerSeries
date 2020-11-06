package net.roguelogix.phosphophyllite.multiblock.generic;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.roguelogix.phosphophyllite.Phosphophyllite;
import net.roguelogix.phosphophyllite.PhosphophylliteConfig;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector2i;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector3i;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector3ic;
import net.roguelogix.phosphophyllite.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class MultiblockController {
    
    protected final World world;
    
    protected final Set<MultiblockTile> blocks = new HashSet<>();
    protected final Set<ITickableMultiblockTile> toTick = new HashSet<>();
    private boolean checkForDetachments = false;
    private long updateAssemblyAtTick = Long.MAX_VALUE;
    protected final Set<MultiblockController> controllersToMerge = new HashSet<>();
    
    private final Vector3i minCoord = new Vector3i();
    private final Vector3i maxCoord = new Vector3i();
    
    public enum AssemblyState {
        ASSEMBLED,
        DISASSEMBLED,
        PAUSED,
    }
    
    protected AssemblyState state = AssemblyState.DISASSEMBLED;
    
    private boolean shouldUpdateNBT = false;
    private CompoundNBT cachedNBT = null;
    
    protected Validator<MultiblockTile> tileAttachValidator;
    private Validator<MultiblockController> assemblyValidator = c -> true;
    
    protected ValidationError lastValidationError = null;
    
    long lastTick = -1;
    
    
    public MultiblockController(@Nonnull World world) {
        this.world = world;
        Phosphophyllite.controllersToTick.add(this);
    }
    
    public Vector3ic minCoord() {
        return minCoord;
    }
    
    public Vector3ic maxCoord() {
        return maxCoord;
    }
    
    
    private void updateMinMaxCoordinates() {
        if (blocks.isEmpty()) {
            return;
        }
        int minX, minY, minZ;
        int maxX, maxY, maxZ;
        BlockPos firstPos = blocks.iterator().next().getPos();
        minX = firstPos.getX();
        minY = firstPos.getY();
        minZ = firstPos.getZ();
        maxX = firstPos.getX();
        maxY = firstPos.getY();
        maxZ = firstPos.getZ();
        for (MultiblockTile block : blocks) {
            BlockPos pos = block.getPos();
            if (pos.getX() < minX) {
                minX = pos.getX();
            }
            if (pos.getY() < minY) {
                minY = pos.getY();
            }
            if (pos.getZ() < minZ) {
                minZ = pos.getZ();
            }
            if (pos.getX() > maxX) {
                maxX = pos.getX();
            }
            if (pos.getY() > maxY) {
                maxY = pos.getY();
            }
            if (pos.getZ() > maxZ) {
                maxZ = pos.getZ();
            }
        }
        minCoord.set(minX, minY, minZ);
        maxCoord.set(maxX, maxY, maxZ);
    }
    
    final void attemptAttach(@Nonnull MultiblockTile toAttach) {
        if (tileAttachValidator != null && !tileAttachValidator.validate(toAttach)) {
            return;
        }
        if (toAttach.controller != null && toAttach.controller != this) {
            controllersToMerge.add(toAttach.controller);
        }
        
        // ok, its a valid tile to attach, so ima attach it
        blocks.add(toAttach);
        if (toAttach instanceof ITickableMultiblockTile) {
            toTick.add((ITickableMultiblockTile) toAttach);
        }
        toAttach.controller = this;
        if (toAttach.preExistingBlock) {
            if(toAttach.controllerData != null) {
                onBlockWithNBTAttached(toAttach.controllerData);
                toAttach.controllerData = null;
            }
            onPartAttached(toAttach);
        } else {
            if (state == AssemblyState.PAUSED) {
                // this is only possible with large multiblocks and low render distances
                // dont, just dont, it can break shit, and i dont want to deal with it right now
                // fun thing, relaunching should get around this
                throw new IllegalStateException("Attempt to add a new block to a paused multiblock");
            }
            onPartPlaced(toAttach);
        }
        updateAssemblyAtTick = Phosphophyllite.tickNumber() + 1;
    }
    
    final void detach(@Nonnull MultiblockTile toDetach) {
        detach(toDetach, false);
    }
    
    final void detach(@Nonnull MultiblockTile toDetach, boolean onChunkUnload) {
        blocks.remove(toDetach);
        if (toDetach instanceof ITickableMultiblockTile) {
            toTick.remove(toDetach);
        }
        
        if (onChunkUnload) {
            onPartDetached(toDetach);
            state = AssemblyState.PAUSED;
        } else {
            if (state == AssemblyState.PAUSED) {
                // this is only possible with large multiblocks and low render distances
                // dont, just dont, it can break shit, and i dont want to deal with it right now
                // fun thing, relaunching should get around this
                throw new IllegalStateException("Attempt to remove a block from a paused multiblock");
            }
            onPartBroken(toDetach);
        }
        
        toDetach.attemptAttach();
        
        if (blocks.isEmpty()) {
            Phosphophyllite.controllersToTick.remove(this);
        }
        
        checkForDetachments = true;
        updateAssemblyAtTick = Phosphophyllite.tickNumber() + 1;
    }
    
    public void update() {
        if (lastTick >= Phosphophyllite.tickNumber()) {
            return;
        }
        lastTick = Phosphophyllite.tickNumber();
        
        if (updateAssemblyAtTick < lastTick) {
            updateMinMaxCoordinates();
            updateAssemblyState();
            updateAssemblyAtTick = Long.MAX_VALUE;
        }
        
        if (blocks.isEmpty()) {
            // why are we being ticked?
            Phosphophyllite.controllersToTick.remove(this);
            checkForDetachments = false;
        }
        
        if (checkForDetachments) {
            MultiblockTile firstBlock = blocks.iterator().next();
            
            HashSet<MultiblockTile> toSave = new HashSet<>();
            Stack<MultiblockTile> workingStack = new Stack<>();
            toSave.add(firstBlock);
            workingStack.add(firstBlock);
            while (!workingStack.empty()) {
                MultiblockTile tile = workingStack.pop();
                BlockPos pos = tile.getPos();
                BlockPos[] possiblePositions = {
                        pos.add(1, 0, 0),
                        pos.add(-1, 0, 0),
                        pos.add(0, 0, 1),
                        pos.add(0, 0, -1),
                        pos.add(0, 1, 0),
                        pos.add(0, -1, 0),
                };
                @SuppressWarnings({"DuplicatedCode", "deprecation"})
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
                        if (((MultiblockTile) possibleTile).controller == this && ((MultiblockTile) possibleTile).lastSavedTick != this.lastTick) {
                            ((MultiblockTile) possibleTile).lastSavedTick = lastTick;
                            toSave.add((MultiblockTile) possibleTile);
                            workingStack.push((MultiblockTile) possibleTile);
                        }
                    }
                }
            }
            
            HashSet<MultiblockTile> toOrphan = new HashSet<>(blocks);
            toOrphan.removeAll(toSave);
            blocks.removeAll(toOrphan);
            //noinspection SuspiciousMethodCalls
            toTick.removeAll(toOrphan);
            if (!toOrphan.isEmpty()) {
                for (MultiblockTile tile : toOrphan) {
                    detach(tile);
                }
            }
            checkForDetachments = false;
        }
        for (MultiblockController otherController : controllersToMerge) {
            Phosphophyllite.controllersToTick.remove(otherController);
            otherController.controllersToMerge.clear();
            this.onMerge(otherController);
            this.blocks.addAll(otherController.blocks);
            for (MultiblockTile block : otherController.blocks) {
                block.controller = this;
                onPartPlaced(block);
            }
        }
        controllersToMerge.clear();
        if (state == AssemblyState.ASSEMBLED) {
            tick();
            toTick.forEach(ITickableMultiblockTile::tick);
        }
    }
    
    public void suicide() {
        Set<MultiblockTile> blocks = new HashSet<>(this.blocks);
        for (MultiblockTile block : blocks) {
            block.onChunkUnloaded();
        }
    }
    
    private void updateAssemblyState() {
        AssemblyState oldState = state;
        boolean validated = false;
        lastValidationError = null;
        try {
            validated = assemblyValidator.validate(this);
        } catch (ValidationError e) {
            lastValidationError = e;
        }
        if (validated) {
            state = AssemblyState.ASSEMBLED;
            if (cachedNBT != null) {
                read(cachedNBT.getCompound("userdata"));
                shouldUpdateNBT = true;
            }
            blocks.forEach(block -> world.notifyNeighborsOfStateChange(block.getPos(), block.getBlockState().getBlock()));
            if (oldState == AssemblyState.PAUSED) {
                onUnpaused();
            } else {
                onAssembled();
            }
        } else {
            if (oldState == AssemblyState.ASSEMBLED) {
                state = AssemblyState.DISASSEMBLED;
                onDisassembled();
                updateCachedNBT();
            }
        }
        for (MultiblockTile block : blocks) {
            block.onAssemblyAttempted();
        }
    }
    
    
    private void onBlockWithNBTAttached(CompoundNBT nbt) {
        if (cachedNBT == null) {
            readNBT(nbt);
        }
        if(cachedNBT == null){
            return;
        }
        if (!nbt.equals(cachedNBT)) {
            // TODO: introduce when i can maybe worlds
//            if (PhosphophylliteConfig.Multiblock.StrictNBTConsistency) {
//                throw new IllegalStateException("Inconsistent Multiblock NBT! " + minCoord.toString());
//            }else{
            Phosphophyllite.LOGGER.warn("Inconsistent Multiblock NBT! " + minCoord.toString());
//            }
        }
        CompoundNBT multiblockData = nbt.getCompound("multiblockData");
        if (cachedNBT.getCompound("multiblockData").getInt("controller") != multiblockData.getInt("controller")) {
            // todo merge the NBTs
            //noinspection UnnecessaryReturnStatement
            return;
        }
    }
    
    /**
     * Read from the NBT saved by member blocks
     *
     * @param nbt previously returned by getNBT that represents this multiblock
     */
    final void readNBT(CompoundNBT nbt) {
        if (!nbt.isEmpty()) {
            cachedNBT = nbt.copy();
            CompoundNBT multiblockData = cachedNBT.getCompound("multiblockData");
            if (multiblockData.contains("assemblyState")) {
                // dont just shove this into this.state
                // if you do onDisassembled will be called incorrectly
                AssemblyState nbtState = AssemblyState.valueOf(multiblockData.getString("assemblyState"));
                // because minecraft is dumb, and saves chunks before unloading them, i need to treat assembled as paused too
                if (state == AssemblyState.DISASSEMBLED && (nbtState == AssemblyState.PAUSED || nbtState == AssemblyState.ASSEMBLED)) {
                    state = AssemblyState.PAUSED;
                }
            }
        }
    }
    
    /**
     * Called by member blocks when saving to world
     * may not directly call write() from here
     * <p>
     * DO NOT EDIT THE RETURNED NBT, weird things can happen if you do
     *
     * @return NBT that represents this multiblock
     */
    @Nonnull
    final CompoundNBT getNBT() {
        if (shouldUpdateNBT) {
            shouldUpdateNBT = false;
            updateCachedNBT();
        }
        return cachedNBT == null ? new CompoundNBT() :  cachedNBT;
    }
    
    private void updateCachedNBT() {
        cachedNBT = new CompoundNBT();
        cachedNBT.put("userdata", write());
        CompoundNBT multiblockData = new CompoundNBT();
        cachedNBT.put("multiblockData", multiblockData);
        {
            // instead of storing an exhaustive list of all the blocks we had
            // just save the controller hash, and make sure we have the right number
            multiblockData.putInt("controller", hashCode());
            multiblockData.putString("assemblyState", state.toString());
        }
    }
    
    /**
     * Marks multiblocks structure as dirty to minecraft so it is saved
     */
    protected final void markDirty() {
        shouldUpdateNBT = true;
        Util.markRangeDirty(world, new Vector2i(minCoord.x, minCoord.z), new Vector2i(maxCoord.x, maxCoord.z));
    }
    
    @Nonnull
    public AssemblyState assemblyState() {
        return state;
    }
    
    /**
     * Gets the info printed to chat when block is clicked with the @DebugTool
     * safe to override, just append to the string returned by super.getDebugInfo
     * not my fault if you manage to break it
     *
     * @return string to print
     */
    @Nonnull
    public String getDebugInfo() {
        return "BlockCount: " + blocks.size() + "\n" +
                "Min " + minCoord.toString() + "\n" +
                "Max " + maxCoord.toString() + "\n" +
                "Controller: " + this + "\n" +
                "AssemblyState: " + state + "\n";
    }
    
    
    // -- API --
    
    /**
     * Sets, or removes, the validator to be used to determine if the multiblock is assembled or not
     *
     * @param validator the new Validator, or null to remove
     */
    
    protected void setAssemblyValidator(@Nullable Validator<MultiblockController> validator) {
        if (validator != null) {
            assemblyValidator = validator;
        }
    }
    
    
    /**
     * Called at the end of a tick for assembled multiblocks only
     * is not called if the multiblock is dissassembled or paused
     */
    public void tick() {
    }
    
    /**
     * Called when a part is added to the multiblock structure
     * not called in conjunction with onPartPlaced
     * <p>
     * CANNOT ALTER NBT STATE
     *
     * @param toAttach, the part that was added
     */
    protected void onPartAttached(@Nonnull MultiblockTile toAttach) {
    
    }
    
    /**
     * Called when a part is removed to the multiblock structure
     * not called in conjunction with onPartBroken
     * <p>
     * CANNOT ALTER NBT STATE
     *
     * @param toDetach, the part that was removed
     */
    protected void onPartDetached(@Nonnull MultiblockTile toDetach) {
    
    }
    
    /**
     * Called when a new part is added to the world, or a block is merged in from another multiblock
     * <p>
     * not called when a previously placed block is reloaded
     *
     * @param placed the block that was placed
     */
    protected void onPartPlaced(@Nonnull MultiblockTile placed) {
    
    }
    
    /**
     * Called when a part is removed from the world, or a block is detached durring separation
     * <p>
     * not called when a part is unloaded
     *
     * @param broken the block that was broken
     */
    protected void onPartBroken(@Nonnull MultiblockTile broken) {
    
    }
    
    /**
     * Called when two multiblock controllers are merged together
     * <p>
     * this happens with a block connecting the two is placed
     * <p>
     * only called for the controller that will reside over the blocks both controllers control
     * <p>
     * called before blocks have been moved to the primary controller
     *
     * @param otherController the controller to merge into this one
     */
    protected void onMerge(@Nonnull MultiblockController otherController) {
    }
    
    /**
     * Called when a multiblock is assembled by a placed block
     * <p>
     * called after @onPartPlaced
     */
    protected void onAssembled() {
    }
    
    /**
     * Called when a multiblock is assembled by a destroyed block
     * <p>
     * called after @onPartBroken, called before @write
     */
    protected void onDisassembled() {
    }
    
    /**
     * Called when a multiblock is to be resumed from a paused state
     * <p>
     * called after @read but before first call to @tick
     */
    protected void onUnpaused() {
    }
    
    /**
     * Called after a multiblock has passes assembly validation, and has an NBT to read from
     * <p>
     * may not be called for new multiblocks
     *
     * @param compound the NBT that was written to in the last write call
     */
    protected void read(@Nonnull CompoundNBT compound) {
    }
    
    /**
     * Create an NBT tag to be saved and re-read upon multiblock re-assembly
     * <p>
     * Can be called at any time, and multiblock must be able to resume from this NBT regardless of its current state
     *
     * @return the NBT to save
     */
    @Nonnull
    protected CompoundNBT write() {
        return new CompoundNBT();
    }
}
