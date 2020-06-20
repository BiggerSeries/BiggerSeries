package net.roguelogix.phosphophyllite.quartz.internal.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.roguelogix.phosphophyllite.quartz.internal.client.glsl.Prepreprocessor;
import net.roguelogix.phosphophyllite.quartz.internal.client.textures.QuartzTextureAtlas;

import java.io.IOException;

import static net.roguelogix.phosphophyllite.Phosphophyllite.modid;

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
