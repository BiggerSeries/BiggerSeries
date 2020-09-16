package net.roguelogix.phosphophyllite.multiblock.generic;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.roguelogix.phosphophyllite.Phosphophyllite;
import net.roguelogix.phosphophyllite.util.Util;
import org.joml.Vector2i;
import org.joml.Vector3i;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/*
todo:
assembly errors
 */

public class MultiblockController {
    
    protected final World world;
    
    protected final Set<MultiblockTile> blocks = new HashSet<>();
    private boolean checkForDetachments = false;
    private long updateAssemblyAtTick = Long.MAX_VALUE;
    protected final Set<MultiblockController> controllersToMerge = new HashSet<>();
    
    public MultiblockController(World world) {
        this.world = world;
        Phosphophyllite.controllersToTick.add(this);
    }
    
    private int MinX, MinY, MinZ;
    private int MaxX, MaxY, MaxZ;
    
    public int minX() {
        return MinX;
    }
    
    public int minY() {
        return MinY;
    }
    
    public int minZ() {
        return MinZ;
    }
    
    public int maxX() {
        return MaxX;
    }
    
    public int maxY() {
        return MaxY;
    }
    
    public int maxZ() {
        return MaxZ;
    }
    
    
    protected Validator<MultiblockTile> tileAttachValidator;
    
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
        MinX = minX;
        MinY = minY;
        MinZ = minZ;
        MaxX = maxX;
        MaxY = maxY;
        MaxZ = maxZ;
    }
    
    final void attemptAttach(MultiblockTile toAttach) {
        if (tileAttachValidator != null && !tileAttachValidator.validate(toAttach)) {
            return;
        }
        if (toAttach.controller != null && toAttach.controller != this) {
            controllersToMerge.add(toAttach.controller);
        }
        
        // ok, its a valid tile to attach, so ima attach it
        blocks.add(toAttach);
        toAttach.controller = this;
        onPartAdded(toAttach);
        world.setBlockState(toAttach.getPos(), toAttach.getBlockState().getBlock().getDefaultState());
        if (toAttach.controllerData != null) {
            onBlockWithNBTAttached(toAttach.controllerData);
        }
        updateAssemblyAtTick = Phosphophyllite.tickNumber() + 1;
    }
    
    protected void onPartAdded(MultiblockTile toAttach) {
    }
    
    final void detach(MultiblockTile toDetach) {
        detach(toDetach, false);
    }
    
    final void detach(MultiblockTile toDetach, boolean onChunkUnload) {
        blocks.remove(toDetach);
        onPartRemoved(toDetach);
        toDetach.attemptAttach();
        if (onChunkUnload) {
            state = AssemblyState.PAUSED;
            onPaused();
            updateNBT();
        }
        
        if (blocks.isEmpty()) {
            Phosphophyllite.controllersToTick.remove(this);
        }
        
        checkForDetachments = true;
        updateAssemblyAtTick = Phosphophyllite.tickNumber() + 1;
    }
    
    protected void onPartRemoved(MultiblockTile tile) {
    }
    
    protected void onMerge(MultiblockController otherController) {
    }
    
    private Validator<MultiblockController> assemblyValidator = c -> true;
    
    protected void setAssemblyValidator(Validator<MultiblockController> validator) {
        if (validator != null) {
            assemblyValidator = validator;
        }
    }
    
    long lastTick = -1;
    
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
                onPartAdded(block);
                if (block.doBlockStateUpdate()) {
                    world.setBlockState(block.getPos(), block.getBlockState().getBlock().getDefaultState());
                }
            }
        }
        controllersToMerge.clear();
        if (state == AssemblyState.ASSEMBLED) {
            tick();
            updateNBT();
        }
    }
    
    public void tick() {
    }
    
    public AssemblyState assemblyState() {
        return state;
    }
    
    public void suicide() {
        Set<MultiblockTile> blocks = new HashSet<>(this.blocks);
        for (MultiblockTile block : blocks) {
            block.onChunkUnloaded();
        }
    }
    
    public enum AssemblyState {
        ASSEMBLED,
        DISASSEMBLED,
        PAUSED,
    }
    
    protected AssemblyState state = AssemblyState.DISASSEMBLED;
    
    protected ValidationError lastValidationError = null;
    
    private void updateAssemblyState() {
        AssemblyState oldState = state;
//        System.out.println("VALIDATING! " + this.hashCode());
//        long startTime = System.nanoTime();
        boolean validated = false;
        lastValidationError = null;
        try {
            validated = assemblyValidator.validate(this);
        } catch (ValidationError e) {
            lastValidationError = e;
        }
//        long endTime = System.nanoTime();
//        System.out.println("VALIDATED! " + this.hashCode() + "\t" + validated + "\t" + ((float) (endTime - startTime) / 1_000_000));
        if (validated) {
            state = AssemblyState.ASSEMBLED;
            if (storedNBT != null) {
                read(storedNBT.getCompound("userdata"));
            }
            blocks.forEach(block -> world.notifyNeighborsOfStateChange(block.getPos(), block.getBlockState().getBlock()));
            onAssembled();
            updateNBT();
        } else {
            if (oldState == AssemblyState.ASSEMBLED) {
                state = AssemblyState.DISASSEMBLED;
                onDisassembled();
                updateNBT();
            }
        }
        for (MultiblockTile block : blocks) {
            block.onAssemblyAttempted();
        }
    }
    
    protected void onAssembled() {
    }
    
    protected void onDisassembled() {
    }
    
    protected void onPaused() {
    }
    
    private CompoundNBT storedNBT = null;
    private CompoundNBT multiblockData = null;
    
    private void onBlockWithNBTAttached(CompoundNBT nbt) {
        if (multiblockData == null) {
            readNBT(nbt);
        }
        CompoundNBT multiblockData = nbt.getCompound("multiblockData");
        if (this.multiblockData.getInt("controller") != multiblockData.getInt("controller")) {
            // todo merge the NBTs
            return;
        }
    }
    
    final void readNBT(CompoundNBT nbt) {
        /*
         * to future me, or someone else, i dont judge
         * if you are looking here because a reactor is loading in weird when its between loaded and unloaded
         * whats happening is that the NBT is loading it in as Assembled, not caring
         * then it sits at disassembled, really shouldn't be causing an issue, but hey, here you are, reading this
         * which means it is
         * solution that is 100% not tested (because why would i do that)
         * when reading NBT, check for an assembled state being read, check if the area is loaded, and if not, pause it
         * im not doing that because *performance* (not that i seem to care elsewhere, meh)
         */
        if (!nbt.isEmpty()) {
            storedNBT = nbt.copy();
            multiblockData = storedNBT.getCompound("multiblockData");
            if (multiblockData.contains("assemblyState")) {
                // dont just shove this into this.state
                // if you do onDisassembled will be called incorrectly
                AssemblyState nbtState = AssemblyState.valueOf(multiblockData.getString("assemblyState"));
                if (state == AssemblyState.DISASSEMBLED && nbtState == AssemblyState.PAUSED) {
                    state = AssemblyState.PAUSED;
                }
            }
        }
    }
    
    final CompoundNBT getNBT() {
        if (storedNBT == null) {
            updateNBT();
        }
        return storedNBT.copy();
    }
    
    final void updateNBT() {
        CompoundNBT compound = new CompoundNBT();
        compound.put("userdata", write());
        CompoundNBT multiblockData = new CompoundNBT();
        compound.put("multiblockData", multiblockData);
        {
            // instead of storing an exhaustive list of all the blocks we had
            // just save the controller hash, and make sure we have the right number
            multiblockData.putInt("controller", hashCode());
            multiblockData.putString("assemblyState", state.toString());
        }
        storedNBT = compound;
    }
    
    protected void markDirty(){
        Util.markRangeDirty(world, new Vector2i(minX(), minZ()), new Vector2i(maxX(), maxZ()));
    }
    
    protected void read(CompoundNBT compound) {
    }
    
    protected CompoundNBT write() {
        return new CompoundNBT();
    }
    
    public String getDebugInfo() {
        return "BlockCount: " + blocks.size() + "\n" +
                "Min (" + minX() + ", " + minY() + ", " + minZ() + ")\n" +
                "Max (" + maxX() + ", " + maxY() + ", " + maxZ() + ")\n" +
                "Controller: " + this + "\n" +
                "AssemblyState: " + state + "\n";
    }
}
