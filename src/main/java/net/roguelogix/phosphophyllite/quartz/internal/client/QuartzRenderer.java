package net.roguelogix.phosphophyllite.quartz.internal.client;

import net.roguelogix.phosphophyllite.quartz.internal.client.textures.QuartzTextureAtlas;

public class QuartzRenderer {
    private static QuartzTextureAtlas blockAtlas;
    
    public static void onModLoad() {
        // not on render thread
    }
    
    static void GLStartup() {
        // on render thread
        QuartzOperationnMode.onGLStartup();
    }
    
    static void GLShutdown() {
        // on render thread
    }
    
    static void draw() {
        // on render thread
    }
}
