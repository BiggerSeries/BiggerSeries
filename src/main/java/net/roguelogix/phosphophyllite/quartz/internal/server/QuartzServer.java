package net.roguelogix.phosphophyllite.quartz.internal.server;

import net.minecraft.network.NetworkManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.roguelogix.phosphophyllite.quartz.internal.common.QuartzNetworking;

import static net.roguelogix.phosphophyllite.Phosphophyllite.modid;

public class QuartzServer {
    public static void onModLoad(){
        MinecraftForge.EVENT_BUS.addListener(QuartzServer::onChunkLoad);
        MinecraftForge.EVENT_BUS.addListener(QuartzServer::onChunkLoad);
    }

    public static void onChunkLoad(final ChunkEvent.Load event){
        if(event.getWorld().isRemote()){
           return;
        }
    }

    public static void onChunkUnload(final ChunkEvent.Load event){
        if(event.getWorld().isRemote()){
            return;
        }
    }
}
