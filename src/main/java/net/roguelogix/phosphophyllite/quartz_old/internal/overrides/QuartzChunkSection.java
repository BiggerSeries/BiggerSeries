package net.roguelogix.phosphophyllite.quartz_old.internal.overrides;

import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.ChunkSection;
import org.joml.Vector3i;

import javax.annotation.Nullable;

import static net.roguelogix.phosphophyllite.quartz_old.internal.Renderer.tertiaryWorkQueue;

public class QuartzChunkSection extends ChunkSection {
    public QuartzChunkSection(int yLoc, @Nullable ChunkSection section) {
        super(yLoc, (short)0, (short)0, (short)0);
        if(section != null) {
            PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
            section.write(packetBuffer);
            read(packetBuffer);
            recalculateRefCounts();
        }
    }

    @Override
    public BlockState setBlockState(int x, int y, int z, BlockState state, boolean useLocks) {
        BlockState oldBlockState = super.setBlockState(x, y, z, state, useLocks);
        tertiaryWorkQueue.enqueue(()->{
            blockStateUpdate(oldBlockState, state, new Vector3i(x, y, z));
        });
        return oldBlockState;
    }

    private static void blockStateUpdate(BlockState oldState, BlockState newState, Vector3i position){

    }
}
