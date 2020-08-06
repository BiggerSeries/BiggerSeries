package net.roguelogix.biggerreactors.classic.turbine;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.biggerreactors.classic.turbine.blocks.*;
import net.roguelogix.biggerreactors.classic.turbine.tiles.*;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockTile;
import net.roguelogix.phosphophyllite.multiblock.generic.ValidationError;
import net.roguelogix.phosphophyllite.multiblock.generic.Validator;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockController;
import net.roguelogix.phosphophyllite.util.Util;
import org.joml.Vector3i;

import java.util.HashSet;
import java.util.Set;

// ahh shit, here we go again
public class TurbineMultiblockController extends RectangularMultiblockController {
    public TurbineMultiblockController(World world) {
        super(world);
        minX = minZ = 5;
        minY = 4;
        maxX = Config.Turbine.MaxLength;
        maxZ = Config.Turbine.MaxWidth;
        maxY = Config.Turbine.MaxHeight;
        tileAttachValidator = tile -> {
            return tile instanceof TurbineBaseTile;
        };
        frameValidator = block -> {
            return block instanceof TurbineCasing;
        };
        exteriorValidator = Validator.or(frameValidator, block -> {
            return block instanceof TurbineTerminal
                    || block instanceof TurbineCoolantPort
                    || block instanceof TurbineRotorBearing
                    || block instanceof TurbineGlass;
        });
        interiorValidator = block -> {
            if (TurbineCoilRegistry.isBlockAllowed(block)) {
                return true;
            }
            return block instanceof TurbineRotorShaft
                    || block instanceof TurbineRotorBlade
                    || block instanceof AirBlock;
        };
        setAssemblyValidator(multiblockController -> {
            if (rotorBearings.size() != 2) {
                throw new ValidationError("multiblock.error.biggerreactors.turbine.rotor_bearing_count");
            }
            
            TurbineRotorBearingTile bearing = rotorBearings.iterator().next();
            BlockPos bearingPosition = bearing.getPos();
            Direction marchDirection;
            if (bearingPosition.getX() == minX()) {
                marchDirection = Direction.EAST;
            } else if (bearingPosition.getX() == maxX()) {
                marchDirection = Direction.WEST;
            } else if (bearingPosition.getY() == minY()) {
                marchDirection = Direction.UP;
            } else if (bearingPosition.getY() == maxY()) {
                marchDirection = Direction.DOWN;
            } else if (bearingPosition.getZ() == minZ()) {
                marchDirection = Direction.SOUTH;
            } else if (bearingPosition.getZ() == maxZ()) {
                marchDirection = Direction.NORTH;
            } else {
                throw new ValidationError("multiblock.error.biggerreactors.turbine.rotor_bearing_position_undefined");
            }
            int marchedBlocks = 0;
            BlockPos currentPos = bearingPosition.offset(marchDirection);
            while (world.getBlockState(currentPos).getBlock() instanceof TurbineRotorShaft) {
                currentPos = currentPos.offset(marchDirection);
                marchedBlocks++;
            }
            if (!(world.getBlockState(currentPos).getBlock() instanceof TurbineRotorBearing)) {
                throw new ValidationError("multiblock.error.biggerreactors.turbine.rotor_shaft_bearing_ends");
            }
            
            if (rotorShafts.size() != marchedBlocks) {
                throw new ValidationError("multiblock.error.biggerreactors.turbine.rotor_shaft_off_shaft");
            }
            
            marchedBlocks = 0;
            
            for (TurbineRotorShaftTile rotorShaft : rotorShafts) {
                for (Direction value : Direction.values()) {
                    BlockPos pos = rotorShaft.getPos();
                    while (true) {
                        pos = pos.offset(value);
                        Block block = world.getBlockState(pos).getBlock();
                        if (!(block instanceof TurbineRotorBlade)) {
                            break;
                        }
                        marchedBlocks++;
                    }
                }
            }
            
            if (marchedBlocks != rotorBlades.size()) {
                throw new ValidationError("multiblock.error.biggerreactors.turbine.rotor_blade_off_blade");
            }
            
            boolean inCoil = false;
            boolean inBlades = false;
            boolean switched = false;
            
            int validCoilBlocks = 0;
            
            currentPos = bearingPosition;
            while (true) {
                currentPos = currentPos.offset(marchDirection);
                TileEntity te = world.getTileEntity(currentPos);
                if (!(te instanceof TurbineRotorShaftTile)) {
                    break;
                }
                TurbineRotorShaftTile rotorShaft = (TurbineRotorShaftTile) te;
                boolean isCoilShaft = false;
                boolean isBladeShaft = false;
                for (Direction value : Direction.values()) {
                    if (value == marchDirection || value == marchDirection.getOpposite()) {
                        continue;
                    }
                    BlockPos offsetPos = rotorShaft.getPos().offset(value);
                    Vec3i secondaryOffset = value.getDirectionVec().crossProduct(marchDirection.getDirectionVec());
                    Direction secondaryDirection = Direction.getFacingFromVector(secondaryOffset.getX(), secondaryOffset.getY(), secondaryOffset.getZ());
                    BlockPos secondaryOffsetPos = offsetPos.offset(secondaryDirection);
                    
                    Block primaryBlock = world.getBlockState(offsetPos).getBlock();
                    Block secondaryBlock = world.getBlockState(secondaryOffsetPos).getBlock();
                    if (primaryBlock instanceof TurbineRotorBlade) {
                        isBladeShaft = true;
                    } else if (!(primaryBlock instanceof AirBlock)) {
                        isCoilShaft = true;
                        validCoilBlocks++;
                    }
                    
                    if (!(secondaryBlock instanceof AirBlock)) {
                        isCoilShaft = true;
                        validCoilBlocks++;
                    }
                    
                    if (isCoilShaft && isBladeShaft) {
                        throw new ValidationError("multiblock.error.biggerreactors.turbine.mixed_blades_and_coil");
                    }
                }
                if (isCoilShaft) {
                    if (inBlades) {
                        if (switched) {
                            throw new ValidationError("multiblock.error.biggerreactors.turbine.multiple_groups");
                        }
                        inBlades = false;
                        switched = true;
                    }
                    inCoil = true;
                }
                if (isBladeShaft) {
                    if (inCoil) {
                        if (switched) {
                            throw new ValidationError("multiblock.error.biggerreactors.turbine.multiple_groups");
                        }
                        inCoil = false;
                        switched = true;
                    }
                    inBlades = true;
                }
            }
            
            int[] totalCoilBlocks = new int[]{0};
            
            Util.chunkCachedBlockStateIteration(new Vector3i(minX(), minY(), minZ()), new Vector3i(maxX(), maxY(), maxZ()), world, (block, pos) -> {
                if(block.getBlock() instanceof TurbineBaseBlock){
                    TileEntity te = world.getTileEntity(new BlockPos(pos.x, pos.y, pos.z));
                    if(te instanceof TurbineBaseTile){
                        if(!((TurbineBaseTile) te).isCurrentController(this)){
                            throw new ValidationError("multiblock.error.biggerreactors.dangling_internal_part");
                        }
                    }
                    return;
                }
                if(block.getBlock() instanceof AirBlock){
                    return;
                }
                totalCoilBlocks[0]++;
            });
            
            if(totalCoilBlocks[0] != validCoilBlocks){
                throw new ValidationError("multiblock.error.biggerreactors.turbine.dangling_coil");
            }
            
            return true;
        });
    }
    
    private TurbineState turbineState = TurbineState.INACTIVE;
    
    private final Set<TurbineTerminalTile> terminals = new HashSet<>();
    private final Set<TurbineCoolantPortTile> coolantPorts = new HashSet<>();
    private final Set<TurbineRotorBearingTile> rotorBearings = new HashSet<>();
    private final Set<TurbineRotorShaftTile> rotorShafts = new HashSet<>();
    private final Set<TurbineRotorBladeTile> rotorBlades = new HashSet<>();
    
    @Override
    protected void onPartAdded(MultiblockTile tile) {
        if (tile instanceof TurbineTerminalTile) {
            terminals.add((TurbineTerminalTile) tile);
        }
        if (tile instanceof TurbineCoolantPortTile) {
            coolantPorts.add((TurbineCoolantPortTile) tile);
        }
        if (tile instanceof TurbineRotorBearingTile) {
            rotorBearings.add((TurbineRotorBearingTile) tile);
        }
        if (tile instanceof TurbineRotorShaftTile) {
            rotorShafts.add((TurbineRotorShaftTile) tile);
        }
        if (tile instanceof TurbineRotorBladeTile) {
            rotorBlades.add((TurbineRotorBladeTile) tile);
        }
    }
    
    @Override
    protected void onPartRemoved(MultiblockTile tile) {
        if (tile instanceof TurbineTerminalTile) {
            terminals.remove(tile);
        }
        if (tile instanceof TurbineCoolantPortTile) {
            coolantPorts.remove(tile);
        }
        if (tile instanceof TurbineRotorBearingTile) {
            rotorBearings.remove(tile);
        }
        if (tile instanceof TurbineRotorShaftTile) {
            rotorShafts.remove(tile);
        }
        if (tile instanceof TurbineRotorBladeTile) {
            rotorBlades.remove(tile);
        }
    }
    
    public void updateBlockStates() {
        terminals.forEach(terminal -> {
            world.setBlockState(terminal.getPos(), terminal.getBlockState().with(TurbineState.TURBINE_STATE_ENUM_PROPERTY, turbineState));
            terminal.markDirty();
        });
    }
    
    public void setActive(TurbineState newState) {
        if (turbineState != newState) {
            turbineState = newState;
            updateBlockStates();
        }
    }
    
    protected void read(CompoundNBT compound) {
        if (compound.contains("turbineState")) {
            turbineState = TurbineState.valueOf(compound.getString("turbineState").toUpperCase());
        }
        
        updateBlockStates();
    }
    
    protected CompoundNBT write() {
        CompoundNBT compound = new CompoundNBT();
        {
            compound.putString("turbineState", turbineState.toString());
        }
        return compound;
    }
    
    @Override
    protected void onAssembly() {
    
    }
    
    @Override
    protected void onDisassembly() {
    
    }
    
    public long extractWater(long maxDrain, boolean simulate) {
        return 0;
    }
    
    public long addSteam(long amount, boolean simulate) {
        return 0;
    }
    
    public void runRequest(String requestName, Object requestData) {
    
    }
}
