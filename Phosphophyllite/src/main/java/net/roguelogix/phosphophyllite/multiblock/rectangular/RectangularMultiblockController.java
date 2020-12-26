package net.roguelogix.phosphophyllite.multiblock.rectangular;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockTile;
import net.roguelogix.phosphophyllite.multiblock.generic.ValidationError;
import net.roguelogix.phosphophyllite.multiblock.generic.Validator;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector3i;
import net.roguelogix.phosphophyllite.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.roguelogix.phosphophyllite.multiblock.rectangular.AxisPosition.*;

public class RectangularMultiblockController<ControllerType extends RectangularMultiblockController<ControllerType, TileType>, TileType extends RectangularMultiblockTile<ControllerType, TileType>> extends MultiblockController<ControllerType, TileType> {
    
    public RectangularMultiblockController(@Nonnull World world, @Nonnull Validator<MultiblockTile<?, ?>> tileTypeValidator) {
        super(world, tileTypeValidator);
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
    
    private final Validator<ControllerType> mainValidator = controller -> {
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
        
        Util.chunkCachedBlockStateIteration(controller.minCoord(), controller.maxCoord(), controller.world, (blockState, pos) -> {
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
    protected final void setAssemblyValidator(@Nullable Validator<ControllerType> validator) {
        if (validator == null) {
            super.setAssemblyValidator(mainValidator);
            return;
        }
        super.setAssemblyValidator(Validator.and(mainValidator, validator));
    }
    
    @Override
    protected BlockState assembledTileState(TileType tile) {
        BlockState state = super.assembledTileState(tile);
        RectangularMultiblockBlock block = (RectangularMultiblockBlock) tile.getBlockState().getBlock();
        if (block.usesAxisPositions()) {
            BlockPos pos = tile.getPos();
            
            if (pos.getX() == minCoord().x()) {
                state = state.with(X_AXIS_POSITION, AxisPosition.LOWER);
            } else if (pos.getX() == maxCoord().x()) {
                state = state.with(X_AXIS_POSITION, AxisPosition.UPPER);
            } else {
                state = state.with(X_AXIS_POSITION, AxisPosition.MIDDLE);
            }
            
            if (pos.getY() == minCoord().y()) {
                state = state.with(Y_AXIS_POSITION, AxisPosition.LOWER);
            } else if (pos.getY() == maxCoord().y()) {
                state = state.with(Y_AXIS_POSITION, AxisPosition.UPPER);
            } else {
                state = state.with(Y_AXIS_POSITION, AxisPosition.MIDDLE);
            }
            
            if (pos.getZ() == minCoord().z()) {
                state = state.with(Z_AXIS_POSITION, AxisPosition.LOWER);
            } else if (pos.getZ() == maxCoord().z()) {
                state = state.with(Z_AXIS_POSITION, AxisPosition.UPPER);
            } else {
                state = state.with(Z_AXIS_POSITION, AxisPosition.MIDDLE);
            }
        }
        return state;
    }
}