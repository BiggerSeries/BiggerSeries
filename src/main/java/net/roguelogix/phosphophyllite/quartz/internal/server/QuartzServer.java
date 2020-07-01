package net.roguelogix.phosphophyllite.quartz.internal.server;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;

public class QuartzServer {
    public static void onModLoad() {
        MinecraftForge.EVENT_BUS.addListener(QuartzServer::onChunkLoad);
        MinecraftForge.EVENT_BUS.addListener(QuartzServer::onChunkLoad);
    }
    
    public static void onChunkLoad(final ChunkEvent.Load event) {
        if (event.getWorld().isRemote()) {
            return;
        }
    }
    
    public static void onChunkUnload(final ChunkEvent.Load event) {
        if (event.getWorld().isRemote()) {
            return;
        }
    }
}
