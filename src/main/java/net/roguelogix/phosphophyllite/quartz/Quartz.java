package net.roguelogix.phosphophyllite.quartz;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.roguelogix.phosphophyllite.quartz.events.QuartzLoad;
import net.roguelogix.phosphophyllite.quartz.internal.client.QuartzEventHandling;
import net.roguelogix.phosphophyllite.quartz.internal.client.QuartzRenderer;

public class Quartz {
    public static void onModLoad() {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }
        QuartzEventHandling.onModLoad();
        QuartzRenderer.onModLoad();
        MinecraftForge.EVENT_BUS.post(new QuartzLoad());
    }
    
    public static void reloadTextures() {
    
    }
    
    public static void reloadShaders() {
    
    }
    
    public static void reloadModels() {
    
    }
    
    public static void processShaders() {
        System.out.println("SHADERS!");
    }
}
