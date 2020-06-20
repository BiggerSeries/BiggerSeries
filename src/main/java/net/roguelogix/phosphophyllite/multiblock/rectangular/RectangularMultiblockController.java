package net.roguelogix.phosphophyllite.multiblock.rectangular;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock.generic.Validator;
import net.roguelogix.phosphophyllite.quartz_old.internal.Util;
import org.joml.Vector3i;

import static net.minecraftforge.common.util.Constants.BlockFlags.BLOCK_UPDATE;
import static net.minecraftforge.common.util.Constants.BlockFlags.NOTIFY_NEIGHBORS;

/*
Assembly errors
 */
public class RectangularMultiblockController extends MultiblockController {

    public RectangularMultiblockController(World world) {
        super(world);
        setAssemblyValidator(k -> true);
    }

    protected boolean strictDimensions = false;
    protected int minLength = -1, minWidth = -1, minHeight = -1;
    protected int maxLength = -1, maxWidth = -1, maxHeight = -1;

    protected Validator<Block> cornerValidator = null;
    protected Validator<Block> frameValidator = null;
    protected Validator<Block> exteriorValidator = null;
    protected Validator<Block> interiorValidator = null;
    protected Validator<Block> genericValidator = null;

    private static final Validator<MultiblockController> mainValidator = genericController -> {
        if (!(genericController instanceof RectangularMultiblockController)) {
            return false;
        }

        RectangularMultiblockController controller = (RectangularMultiblockController) genericController;

        int minX = controller.minX();
        int minY = controller.minY();
        int minZ = controller.minZ();
        int maxX = controller.maxX();
        int maxY = controller.maxY();
        int maxZ = controller.maxZ();

        // dimensions are direction agnostic
        BlockPos dimensions = null;
        for (int i = 0; i < (controller.strictDimensions ? 1 : 3); i++) {
            dimensions = new BlockPos(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
            for (int j = 0; j < i; j++) {
                dimensions = new BlockPos(dimensions.getY(), dimensions.getZ(), dimensions.getX());
            }
            // if all are positive, technically zero is valid for them
            // dont know why you would use zero, but thats not my problem
            // i guess to lock out using the machine?
            if ((controller.minLength | controller.minWidth | controller.minHeight) > 0) {
                if (
                        dimensions.getX() < controller.minLength ||
                                dimensions.getY() < controller.minWidth ||
                                dimensions.getZ() < controller.minHeight
                ) {
                    dimensions = null;
                    continue;
                }
            }
            // you can also just set one of these lower than the above
            // see the below bounds checks
            if ((controller.maxLength | controller.maxWidth | controller.maxHeight) > 0) {
                if (
                        dimensions.getX() > controller.minLength ||
                                dimensions.getY() > controller.minWidth ||
                                dimensions.getZ() > controller.minHeight
                ) {
                    dimensions = null;
                    continue;
                }
            }
            break;
        }
        // dimension check failed in all orientations
        if (dimensions == null) {
            return false;
        }
        // or it didnt, at this point i dont know, and you dont either

        return Util.chunkCachedBlockStateIteration(new Vector3i(minX, minY, minZ), new Vector3i(maxX, maxY, maxZ), controller.world, (blockState, pos) -> {
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
                            return false;
                        } else {
                            break;
                        }
                    }
                }
                case 2: {
                    if (controller.frameValidator != null) {
                        // dont care whats on the corners, but we do on the frame as a whole
                        if (!controller.frameValidator.validate(block)) {
                            return false;
                        } else {
                            break;
                        }
                    }
                }
                case 1: {
                    if (controller.exteriorValidator != null) {
                        // oh, so you dont give a fuck about the frame either, do you even care are the exterior
                        if (!controller.exteriorValidator.validate(block)) {
                            return false;
                        } else {
                            break;
                        }
                    }
                }
                default: {
                    if (extremes == 0) {
                        if (controller.interiorValidator != null) {
                            // oh, so you dont give a fuck about the frame either, do you even care are the exterior
                            if (!controller.interiorValidator.validate(block)) {
                                return false;
                            } else {
                                break;
                            }
                        }
                    }
                    if (controller.genericValidator != null) {
                        // oh, so you dont give a fuck about the frame either, do you even care are the exterior
                        if (!controller.genericValidator.validate(block)) {
                            return false;
                        } else {
                            break;
                        }
                    }
                }
            }
            return true;
        });
    };

    @Override
    protected final void setAssemblyValidator(Validator<MultiblockController> validator) {
        super.setAssemblyValidator(Validator.and(mainValidator, validator));
    }

    private void assembledBlockStates() {
        blocks.forEach(block -> {
            BlockPos pos = block.getPos();

            int extremes = 0;
            int frameLoc = 0;
            if (pos.getX() == minX() || pos.getX() == maxX()) {
                extremes++;
                frameLoc += 1;
            }
            if (pos.getY() == minY() || pos.getY() == maxY()) {
                extremes++;
                frameLoc += 2;
            }
            if (pos.getZ() == minZ() || pos.getZ() == maxZ()) {
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
            if (block.doBlockStateUpdate()) {
                world.setBlockState(pos, block.getBlockState().with(RectangularMultiblockPositions.POSITIONS_ENUM_PROPERTY, position));
            }
            if(block instanceof RectangularMultiblockTile){
                ((RectangularMultiblockTile) block).position = position;
            }
            world.notifyBlockUpdate(pos, block.getBlockState(), block.getBlockState(), BLOCK_UPDATE + NOTIFY_NEIGHBORS);
            block.markDirty();
        });
    }

    private void disassembledBlockStates() {
        blocks.forEach(block -> {
            if (block.doBlockStateUpdate()) {
                world.setBlockState(block.getPos(), block.getBlockState().with(RectangularMultiblockPositions.POSITIONS_ENUM_PROPERTY, RectangularMultiblockPositions.DISASSEMBLED));
            }
            if(block instanceof RectangularMultiblockTile){
                ((RectangularMultiblockTile) block).position = RectangularMultiblockPositions.DISASSEMBLED;
            }
            world.notifyBlockUpdate(block.getPos(), block.getBlockState(), block.getBlockState(), BLOCK_UPDATE + NOTIFY_NEIGHBORS);
            block.markDirty();
        });
    }

    protected void onAssembly() {
    }

    @Override
    protected final void onAssembled() {
//        assembledBlockStates();
        onAssembly();
    }

    protected void onDisassembly() {
    }

    @Override
    protected final void onDisassembled() {
//        disassembledBlockStates();
        onDisassembly();
    }

    protected void onPause() {
    }

    @Override
    protected final void onPaused() {
//        disassembledBlockStates();
        onPause();
    }
}