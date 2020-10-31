package net.roguelogix.phosphophyllite.multiblock.rectangular;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock.generic.ValidationError;
import net.roguelogix.phosphophyllite.multiblock.generic.Validator;
import net.roguelogix.phosphophyllite.util.Util;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.HashMap;

import static net.minecraftforge.common.util.Constants.BlockFlags.BLOCK_UPDATE;
import static net.minecraftforge.common.util.Constants.BlockFlags.NOTIFY_NEIGHBORS;

public class RectangularMultiblockController extends MultiblockController {
    
    public RectangularMultiblockController(@Nonnull World world) {
        super(world);
        setAssemblyValidator(null);
    }
    
    protected boolean orientationAgnostic = true;
    protected boolean xzAgnostic = true;
    
    protected final Vector3i minSize = new Vector3i();
    protected final Vector3i maxSize = new Vector3i();
    
    protected Validator<Block> cornerValidator = null;
    protected Validator<Block> frameValidator = null;
    protected Validator<Block> exteriorValidator = null;
    protected Validator<Block> interiorValidator = null;
    protected Validator<Block> genericValidator = null;
    
    private static final Validator<MultiblockController> mainValidator = genericController -> {
        if (!(genericController instanceof RectangularMultiblockController)) {
            // TODO: 6/29/20 invalid controller error
            // if this *ever* gets hit, normally,
            throw new ValidationError("TODO: Invalid controller error");
        }
        
        RectangularMultiblockController controller = (RectangularMultiblockController) genericController;
        
        int minX = controller.minCoord().x();
        int minY = controller.minCoord().y();
        int minZ = controller.minCoord().z();
        int maxX = controller.maxCoord().x();
        int maxY = controller.maxCoord().y();
        int maxZ = controller.maxCoord().z();
        
        Vector3i[] allowedOrientations = new Vector3i[controller.orientationAgnostic ? 6 : controller.xzAgnostic ? 2 : 1];
        
        if (controller.orientationAgnostic) {
            allowedOrientations[0] = new Vector3i(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
            allowedOrientations[1] = new Vector3i(maxX - minX + 1, maxZ - minZ + 1, maxY - minY + 1);
            
            allowedOrientations[2] = new Vector3i(maxY - minY + 1, maxX - minX + 1, maxZ - minZ + 1);
            allowedOrientations[3] = new Vector3i(maxY - minY + 1, maxZ - minZ + 1, maxX - minX + 1);
            
            allowedOrientations[4] = new Vector3i(maxZ - minZ + 1, maxX - minX + 1, maxY - minY + 1);
            allowedOrientations[5] = new Vector3i(maxZ - minZ + 1, maxY - minY + 1, maxX - minX + 1);
        } else if (controller.xzAgnostic) {
            allowedOrientations[0] = new Vector3i(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
            allowedOrientations[1] = new Vector3i(maxZ - minZ + 1, maxY - minY + 1, maxX - minX + 1);
        } else {
            allowedOrientations[0] = new Vector3i(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
        }
        
        Vector3i dimensions = null;
        for (Vector3i allowedOrientation : allowedOrientations) {
            // if all are positive, technically zero is valid for them
            // dont know why you would use zero, but that's not my problem
            // i guess to lock out using the machine?
            if ((controller.minSize.x | controller.minSize.y | controller.minSize.z) > 0) {
                if (
                        allowedOrientation.x < controller.minSize.x ||
                                allowedOrientation.y < controller.minSize.y ||
                                allowedOrientation.z < controller.minSize.z
                ) {
                    continue;
                }
            }
            // you can also just set one of these lower than the above
            // see the below bounds checks
            if ((controller.maxSize.x | controller.maxSize.y | controller.maxSize.z) > 0) {
                if (
                        allowedOrientation.x > controller.maxSize.x ||
                                allowedOrientation.y > controller.maxSize.y ||
                                allowedOrientation.z > controller.maxSize.z
                ) {
                    continue;
                }
            }
            dimensions = allowedOrientation;
            break;
        }
        // dimension check failed in all orientations
        if (dimensions == null) {
            // TODO: 6/29/20 dimensions error
            throw new ValidationError(new TranslationTextComponent("multiblock.error.phosphophyllite.dimensions",
                    allowedOrientations[0].x, allowedOrientations[0].y, allowedOrientations[0].z,
                    controller.minSize.x, controller.minSize.y, controller.minSize.z,
                    controller.maxSize.x, controller.maxSize.y, controller.maxSize.z));
        }
        // or it didnt, at this point i dont really know, and you dont either, works(tm)
        
        Util.chunkCachedBlockStateIteration(new Vector3i(minX, minY, minZ), new Vector3i(maxX, maxY, maxZ), controller.world, (blockState, pos) -> {
            Block block = blockState.getBlock();
            int extremes = 0;
            if (pos.x == minX || pos.x == maxX) {
                extremes++;
            }
            if (pos.y == minY || pos.y == maxY) {
                extremes++;
            }
            if (pos.z == minZ || pos.z == maxZ) {
                extremes++;
            }
            switch (extremes) {
                case 3: {
                    if (controller.cornerValidator != null) {
                        // can you be a corner?
                        if (!controller.cornerValidator.validate(block)) {
                            throw new InvalidBlock(block, pos, "corner");
                        } else {
                            break;
                        }
                    }
                }
                case 2: {
                    if (controller.frameValidator != null) {
                        // dont care whats on the corners, but we do on the frame as a whole
                        if (!controller.frameValidator.validate(block)) {
                            throw new InvalidBlock(block, pos, "frame");
                        } else {
                            break;
                        }
                    }
                }
                case 1: {
                    if (controller.exteriorValidator != null) {
                        // oh, so you dont give a fuck about the frame either, do you even care are the exterior
                        if (!controller.exteriorValidator.validate(block)) {
                            throw new InvalidBlock(block, pos, "exterior");
                        } else {
                            break;
                        }
                    }
                }
                default: {
                    if (extremes == 0) {
                        if (controller.interiorValidator != null) {
                            // you must care about the inside, right?
                            if (!controller.interiorValidator.validate(block)) {
                                throw new InvalidBlock(block, pos, "interior");
                            } else {
                                break;
                            }
                        }
                    }
                    if (controller.genericValidator != null) {
                        // anything at all?
                        if (!controller.genericValidator.validate(block)) {
                            throw new InvalidBlock(block, pos, "generic");
                        } else {
                            break;
                        }
                    }
                }
            }
        });
        return true;
    };
    
    @Override
    protected final void setAssemblyValidator(@Nullable Validator<MultiblockController> validator) {
        if (validator == null) {
            super.setAssemblyValidator(mainValidator);
            return;
        }
        super.setAssemblyValidator(Validator.and(mainValidator, validator));
    }
    
    private void assembledBlockStates() {
        HashMap<BlockPos, BlockState> newStates = new HashMap<>();
        blocks.forEach(block -> {
            if (block instanceof RectangularMultiblockTile) {
                BlockPos pos = block.getPos();
                
                int extremes = 0;
                int frameLoc = 0;
                if (pos.getX() == minCoord().x() || pos.getX() == maxCoord().x()) {
                    extremes++;
                    frameLoc += 1;
                }
                if (pos.getY() == minCoord().y() || pos.getY() == maxCoord().y()){
                    extremes++;
                    frameLoc += 2;
                }
                if (pos.getZ() == minCoord().z() || pos.getZ() == maxCoord().z()){
                    extremes++;
                    frameLoc += 4;
                }
                RectangularMultiblockPositions position;
                switch (extremes) {
                    case 3: {
                        position = RectangularMultiblockPositions.CORNER;
                        break;
                    }
                    case 2: {
                        switch (frameLoc) {
                            case 3: {
                                position = RectangularMultiblockPositions.FRAME_Z;
                                break;
                            }
                            default:
                            case 5: {
                                position = RectangularMultiblockPositions.FRAME_Y;
                                break;
                            }
                            case 6: {
                                position = RectangularMultiblockPositions.FRAME_X;
                                break;
                            }
                        }
                        break;
                    }
                    case 1: {
                        position = RectangularMultiblockPositions.FACE;
                        break;
                    }
                    default: {
                        position = RectangularMultiblockPositions.INTERIOR;
                        break;
                    }
                }
                ((RectangularMultiblockTile) block).position = position;
                if (block.doBlockStateUpdate()) {
                    newStates.put(pos, block.getBlockState().with(RectangularMultiblockPositions.POSITIONS_ENUM_PROPERTY, position));
                }
            }
        });
        Util.setBlockStates(newStates, world);
        blocks.forEach(TileEntity::markDirty);
    }
    
    private void disassembledBlockStates() {
        HashMap<BlockPos, BlockState> newStates = new HashMap<>();
        blocks.forEach(block -> {
            if (block instanceof RectangularMultiblockTile) {
                ((RectangularMultiblockTile) block).position = RectangularMultiblockPositions.DISASSEMBLED;
                if (block.doBlockStateUpdate()) {
                    newStates.put(block.getPos(), block.getBlockState().with(RectangularMultiblockPositions.POSITIONS_ENUM_PROPERTY, RectangularMultiblockPositions.DISASSEMBLED));
                }
            }
        });
        Util.setBlockStates(newStates, world);
        blocks.forEach(TileEntity::markDirty);
    }
    
    protected void onAssembly() {
    }
    
    @Override
    protected final void onAssembled() {
        assembledBlockStates();
        onAssembly();
    }
    
    protected void onDisassembly() {
    }
    
    @Override
    protected final void onDisassembled() {
        disassembledBlockStates();
        onDisassembly();
    }
    
    protected void onUnpause() {
    }
    
    @Override
    protected final void onUnpaused() {
        assembledBlockStates();
        onUnpause();
    }
}