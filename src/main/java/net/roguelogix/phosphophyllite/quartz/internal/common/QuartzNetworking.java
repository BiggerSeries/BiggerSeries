package net.roguelogix.phosphophyllite.quartz.internal.common;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import static net.roguelogix.phosphophyllite.Phosphophyllite.modid;

public class QuartzNetworking {
    public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(modid, "quartz"), () -> "0.0", s -> s.equals("0.0"), s -> s.equals("0.0"));

    static {
        int i = 0;
        channel.registerMessage(i++, QuartzTextureAtlasDescriptor.class, QuartzTextureAtlasDescriptor::toBytes, QuartzTextureAtlasDescriptor::new, QuartzTextureAtlasDescriptor::handle);
        channel.registerMessage(i++, QuartzRenderChunkUpdate.class, QuartzRenderChunkUpdate::toBytes, QuartzRenderChunkUpdate::new, QuartzRenderChunkUpdate::handle);
    }
}
