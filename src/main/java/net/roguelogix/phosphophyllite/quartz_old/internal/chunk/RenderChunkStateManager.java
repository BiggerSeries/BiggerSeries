package net.roguelogix.phosphophyllite.quartz_old.internal.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.roguelogix.phosphophyllite.quartz_old.QuartzState;
import net.roguelogix.phosphophyllite.quartz_old.internal.blocks.BlockRenderInfo;
import net.roguelogix.phosphophyllite.quartz_old.internal.blocks.RenderStateBuilding;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.HashMap;

import static net.roguelogix.phosphophyllite.util.Util.chunkCachedBlockStateIteration;

public class RenderChunkStateManager {

    private final RenderChunk chunk;
    private final HashMap<Vector3ic, BlockRenderInfo> blocks = new HashMap<>();

    public RenderChunkStateManager(RenderChunk chunk) {
        this.chunk = chunk;
    }

    public void updateBlockQuartzState(Block block, QuartzState info, Vector3ic position) {
        BlockRenderInfo newRenderInfo = RenderStateBuilding.buildBaseInfo(block, info);
        assert position != null;
        BlockRenderInfo renderInfo = blocks.get(position);
        assert renderInfo != null;
        renderInfo.textureOffsetRotation0 = newRenderInfo.textureOffsetRotation0;
        renderInfo.textureOffsetRotation1 = newRenderInfo.textureOffsetRotation1;
        renderInfo.textureOffsetRotation2 = newRenderInfo.textureOffsetRotation2;
        renderInfo.textureOffsetRotation3 = newRenderInfo.textureOffsetRotation3;
        renderInfo.textureOffsetRotation4 = newRenderInfo.textureOffsetRotation4;
        renderInfo.textureOffsetRotation5 = newRenderInfo.textureOffsetRotation5;
        chunk.setBlocks(renderInfo);
    }

    private static BlockState getBlockStateFromChunk(Chunk chunk, BlockPos pos) {
        if (chunk.getPos().x == pos.getX() >> 4 && chunk.getPos().z == pos.getZ() >> 4) {
            return chunk.getBlockState(pos);
        }
        // its not in this chunk, so it has to be looked up from the world
        return chunk.getWorld().getBlockState(pos);
    }

    public void updateShownFaces(BlockState state, Vector3ic position) {
        // oh lovely, having to query MC's state
        assert Minecraft.getInstance().world != null;
        Chunk chunk = Minecraft.getInstance().world.getChunk(position.x() >> 4, position.z() >> 4);
        getBlockStateFromChunk(chunk, new BlockPos(position.x(), position.y(), position.z()));
    }

    // todo: maybe add ability for single block updates?
    public void updateLighting() {
        final int[][][] lightArray = new int[18][18][18];
        final BlockPos.Mutable blockPos = new BlockPos.Mutable();
        assert Minecraft.getInstance().world != null;
        chunkCachedBlockStateIteration(new Vector3i(chunk.position).sub(1, 1, 1), new Vector3i(chunk.position).add(1, 1, 1), Minecraft.getInstance().world, (blockstate, pos) -> {
            pos.add(1, 1, 1);
            blockPos.setPos(pos.x, pos.y, pos.z);
            lightArray[pos.x][pos.y][pos.z] = WorldRenderer.getPackedLightmapCoords(Minecraft.getInstance().world, blockstate, blockPos);
        });
        Vector3i pos = new Vector3i();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    pos.set(i, j, k);
                    updateLightLevelsForBlock(pos, lightArray);
                }
            }
        }
    }

    private void updateLightLevelsForBlock(Vector3i position, int[][][] lightArray) {
        BlockRenderInfo info = blocks.get(position);
        if (info != null){
//            info.
        }
    }

    public static RenderChunkStateManager createFromChunkSection(ChunkSection section, Vector3i position){
        RenderChunk renderChunk = new RenderChunk(position);
        RenderChunkStateManager stateManager = new RenderChunkStateManager(renderChunk);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    BlockState state = section.getBlockState(i, j, k);
                }
            }
        }
        return null;
    }
}
