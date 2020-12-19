package net.roguelogix.phosphophyllite.multiblock.generic;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.roguelogix.phosphophyllite.Phosphophyllite;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector2i;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector3i;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector3ic;
import net.roguelogix.phosphophyllite.util.AStarList;
import net.roguelogix.phosphophyllite.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class MultiblockController {
    
    protected final World world;
    
    protected final Map<BlockPos, MultiblockTile> blocks = new LinkedHashMap<>();
    protected final Set<ITickableMultiblockTile> toTick = new HashSet<>();
    protected final Set<IAssemblyAttemptedTile> assemblyAttemptedTiles = new HashSet<>();
    private boolean checkForDetachments = false;
    private boolean updateExtremes = true;
    private long updateAssemblyAtTick = Long.MAX_VALUE;
    protected final Set<MultiblockController> controllersToMerge = new HashSet<>();
    protected final Set<BlockPos> removedBlocks = new HashSet<>();
    
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
        Phosphophyllite.controllersToTick.computeIfAbsent((ServerWorld) world, k -> new ArrayList<>()).add(this);
    }
    
    public Vector3ic minCoord() {
        return minCoord;
    }
    
    public Vector3ic maxCoord() {
        return maxCoord;
    }
    
    
    private void updateMinMaxCoordinates() {
        if (blocks.isEmpty() || !updateExtremes) {
            return;
        }
        updateExtremes = false;
        int minX, minY, minZ;
        int maxX, maxY, maxZ;
        BlockPos firstPos = blocks.keySet().iterator().next();
        minX = firstPos.getX();
        minY = firstPos.getY();
        minZ = firstPos.getZ();
        maxX = firstPos.getX();
        maxY = firstPos.getY();
        maxZ = firstPos.getZ();
        for (BlockPos pos : blocks.keySet()) {
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
        BlockPos toAttachPos = toAttach.getPos();
        blocks.put(toAttachPos, toAttach);
        
        // update minmax
        minCoord.x = Math.min(minCoord.x, toAttachPos.getX());
        minCoord.y = Math.min(minCoord.y, toAttachPos.getY());
        minCoord.z = Math.min(minCoord.z, toAttachPos.getZ());
        maxCoord.x = Math.max(maxCoord.x, toAttachPos.getX());
        maxCoord.x = Math.max(maxCoord.y, toAttachPos.getY());
        maxCoord.x = Math.max(maxCoord.z, toAttachPos.getZ());
        
        if (toAttach instanceof ITickableMultiblockTile) {
            toTick.add((ITickableMultiblockTile) toAttach);
        }
        if (toAttach instanceof IAssemblyAttemptedTile) {
            assemblyAttemptedTiles.add((IAssemblyAttemptedTile) toAttach);
        }
        toAttach.controller = this;
        if (toAttach.preExistingBlock) {
            if (toAttach.controllerData != null) {
                onBlockWithNBTAttached(toAttach.controllerData);
                toAttach.controllerData = null;
            }
            onPartAttached(toAttach);
        } else {
            onPartPlaced(toAttach);
        }
        updateAssemblyAtTick = Phosphophyllite.tickNumber() + 1;
    }
    
    final void detach(@Nonnull MultiblockTile toDetach) {
        detach(toDetach, false);
    }
    
    final void detach(@Nonnull MultiblockTile toDetach, boolean onChunkUnload) {
        detach(toDetach, onChunkUnload, true);
    }
    
    final void detach(@Nonnull MultiblockTile toDetach, boolean onChunkUnload, boolean checkForDetachments) {
        blocks.remove(toDetach.getPos());
        if (toDetach instanceof ITickableMultiblockTile) {
            toTick.remove(toDetach);
        }
        if (toDetach instanceof IAssemblyAttemptedTile) {
            assemblyAttemptedTiles.remove(toDetach);
        }
        
        if (onChunkUnload) {
            onPartDetached(toDetach);
            state = AssemblyState.PAUSED;
        } else {
            onPartBroken(toDetach);
        }
        
        toDetach.attemptAttach();
        
        if (blocks.isEmpty()) {
            //noinspection SuspiciousMethodCalls
            ArrayList<MultiblockController> controllers = Phosphophyllite.controllersToTick.get(world);
            if (controllers != null) {
                controllers.remove(this);
            }
        }
        
        this.checkForDetachments = this.checkForDetachments || checkForDetachments;
        if (checkForDetachments) {
            removedBlocks.add(toDetach.getPos());
        }
        BlockPos toDetachPos = toDetach.getPos();
        if (toDetachPos.getX() == minCoord.x || toDetachPos.getY() == minCoord.y || toDetachPos.getZ() == minCoord.z ||
                toDetachPos.getX() == maxCoord.x || toDetachPos.getY() == maxCoord.y || toDetachPos.getZ() == maxCoord.z) {
            updateExtremes = true;
        }
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
            //noinspection SuspiciousMethodCalls
            ArrayList<MultiblockController> controllers = Phosphophyllite.controllersToTick.get(world);
            if (controllers != null) {
                controllers.remove(this);
            }
            checkForDetachments = false;
        }
        
        if (checkForDetachments) {
            BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();
            
            AStarList aStarList = new AStarList();
            
            for (BlockPos removedBlock : removedBlocks) {
                for (Direction value : Direction.values()) {
                    mutableBlockPos.setPos(removedBlock);
                    mutableBlockPos.move(value);
                    MultiblockTile tile = blocks.get(mutableBlockPos);
                    if (tile != null && tile.controller == this) {
                        aStarList.addTarget(tile.getPos());
                    }
                }
            }
            removedBlocks.clear();
            
            while (!aStarList.done()) {
                BlockPos node = aStarList.nextNode();
                for (Direction value : Direction.values()) {
                    mutableBlockPos.setPos(node);
                    mutableBlockPos.move(value);
                    MultiblockTile tile = blocks.get(mutableBlockPos);
                    if (tile != null && tile.controller == this && tile.lastSavedTick != this.lastTick) {
                        tile.lastSavedTick = this.lastTick;
                        aStarList.addNode(tile.getPos());
                    }
                }
            }
            
            if (!aStarList.foundAll()) {
                HashSet<MultiblockTile> toOrphan = new HashSet<>();
                for (MultiblockTile block : blocks.values()) {
                    if (block.lastSavedTick != this.lastTick) {
                        toOrphan.add(block);
                    }
                }
                if (!toOrphan.isEmpty()) {
                    for (MultiblockTile tile : toOrphan) {
                        detach(tile, state == AssemblyState.PAUSED, false);
                    }
                }
            }
            checkForDetachments = false;
        }
        if (!controllersToMerge.isEmpty()) {
            HashSet<MultiblockController> newToMerge = new HashSet<>();
            for (MultiblockController otherController : controllersToMerge) {
                //noinspection SuspiciousMethodCalls
                Phosphophyllite.controllersToTick.get(world).remove(otherController);
                otherController.controllersToMerge.remove(this);
                newToMerge.addAll(otherController.controllersToMerge);
                otherController.controllersToMerge.clear();
                this.onMerge(otherController);
                this.blocks.putAll(otherController.blocks);
                for (MultiblockTile block : otherController.blocks.values()) {
                    block.controller = this;
                    onPartPlaced(block);
                }
            }
            controllersToMerge.clear();
            controllersToMerge.addAll(newToMerge);
        }
        
        if (state == AssemblyState.ASSEMBLED && world.isAreaLoaded(minCoord().x(), minCoord().y(), minCoord().z(), maxCoord().x(), maxCoord().y(), maxCoord().z())) {
            tick();
            toTick.forEach(ITickableMultiblockTile::tick);
        }
    }
    
    public void suicide() {
        Set<MultiblockTile> blocks = new HashSet<>(this.blocks.values());
        for (MultiblockTile block : blocks) {
            block.onChunkUnloaded();
        }
        //noinspection SuspiciousMethodCalls
        ArrayList<MultiblockController> controllers = Phosphophyllite.controllersToTick.get(world);
        if (controllers != null) {
            controllers.remove(this);
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
            blocks.forEach((pos, block) -> world.notifyNeighborsOfStateChange(pos, block.getBlockState().getBlock()));
            if (oldState == AssemblyState.PAUSED) {
                onUnpaused();
            } else {
                onAssembled();
            }
            assembledBlockStates();
        } else {
            if (oldState == AssemblyState.ASSEMBLED) {
                state = AssemblyState.DISASSEMBLED;
                onDisassembled();
                disassembledBlockStates();
                updateCachedNBT();
            }
        }
        for (IAssemblyAttemptedTile assemblyAttemptedTile : assemblyAttemptedTiles) {
            assemblyAttemptedTile.onAssemblyAttempted();
        }
    }
    
    
    private void onBlockWithNBTAttached(CompoundNBT nbt) {
        if (cachedNBT == null) {
            readNBT(nbt);
        }
        if (cachedNBT == null) {
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
    
    private void assembledBlockStates() {
        final HashMap<BlockPos, BlockState> newStates = new HashMap<>();
        blocks.forEach((pos, tile) -> {
            BlockState state = assembledTileState(tile);
            if (state != tile.getBlockState()) {
                newStates.put(pos, state);
            }
        });
        Util.setBlockStates(newStates, world);
        blocks.forEach((pos, tile) -> tile.markDirty());
    }
    
    private void disassembledBlockStates() {
        final HashMap<BlockPos, BlockState> newStates = new HashMap<>();
        blocks.forEach((pos, tile) -> {
            BlockState state = disassembledTileState(tile);
            if (state != tile.getBlockState()) {
                newStates.put(tile.getPos(), state);
            }
        });
        Util.setBlockStates(newStates, world);
        blocks.forEach((pos, tile) -> tile.markDirty());
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
        return cachedNBT == null ? new CompoundNBT() : cachedNBT;
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
    
    protected BlockState assembledTileState(MultiblockTile tile) {
        BlockState state = tile.getBlockState();
        if (((MultiblockBlock) tile.getBlockState().getBlock()).usesAssmeblyState()) {
            state = state.with(MultiblockBlock.ASSEMBLED, true);
        }
        return state;
    }
    
    protected BlockState disassembledTileState(MultiblockTile tile) {
        BlockState state = tile.getBlockState();
        if (((MultiblockBlock) tile.getBlockState().getBlock()).usesAssmeblyState()) {
            state = state.with(MultiblockBlock.ASSEMBLED, false);
        }
        return state;
    }
}
