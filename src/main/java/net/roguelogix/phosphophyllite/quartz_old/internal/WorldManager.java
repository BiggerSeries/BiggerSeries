package net.roguelogix.phosphophyllite.quartz_old.internal;

import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.roguelogix.phosphophyllite.quartz_old.QuartzState;
import net.roguelogix.phosphophyllite.quartz_old.internal.chunk.RenderChunkStateManager;
import org.joml.Vector3i;

import java.util.concurrent.ConcurrentHashMap;

import static net.roguelogix.phosphophyllite.quartz_old.internal.Renderer.secondaryWorkQueue;

public class WorldManager {

    private static final ConcurrentHashMap<Vector3i, RenderChunkStateManager> loadedChunks = new ConcurrentHashMap<>();

    public static void shutdown() {
        loadedChunks.clear();
    }

    public static void loadChunk(Chunk chunk) {
        // its safe to access the chunk's data here, but not from the secondary work queue
        // so here it be accessed
        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
        final ChunkSection[] chunkSections = new ChunkSection[16];
        for (int i = 0; i < 16; i++) {
            // should i do a null check? i overwrite this before hand regardless
            chunk.getSections()[i].write(packetBuffer);
            chunkSections[i].read(packetBuffer);
            packetBuffer.clear();
        }
        long chunkPos = chunk.getPos().asLong();
        secondaryWorkQueue.enqueue(() -> {
            Vector3i position = new Vector3i(ChunkPos.getX(chunkPos), 0, ChunkPos.getZ(chunkPos));
            position.mul(16);
            for (int i = 0; i < 16; i++) {
                ChunkSection section = chunkSections[i];
                if (!section.isEmpty()) {
                    position.set(position.x, i * 16, position.z);
                    loadedChunks.put(new Vector3i(position), RenderChunkStateManager.createFromChunkSection(section, position));
                }
            }
        });
    }

    public static void unloadChunk(Chunk chunk) {

    }

    public static void updateBlockState(BlockState newState, Vector3i position) {
    }

    public static void updateQuartzState(QuartzState newState, Vector3i position) {
    }

    static void tick() {

    }
}
