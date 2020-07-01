package net.roguelogix.phosphophyllite.quartz.internal.common;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.roguelogix.phosphophyllite.quartz.internal.client.QuartzClient;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.function.Supplier;

public class QuartzTextureAtlasDescriptor {
    
    private final ArrayList<ResourceLocation> textures = new ArrayList<>();
    
    public QuartzTextureAtlasDescriptor() {
    }
    
    public QuartzTextureAtlasDescriptor(PacketBuffer buf) {
        int count = buf.readInt();
        for (int i = 0; i < count; i++) {
            textures.add(new ResourceLocation(buf.readString()));
        }
    }
    
    public synchronized int addTexture(@Nonnull ResourceLocation texture) {
        int id = textures.indexOf(texture);
        if (id != -1) {
            return id;
        }
        textures.add(texture);
        return textures.size() - 1;
    }
    
    public synchronized int getTextureID(@Nonnull ResourceLocation texture) {
        return textures.indexOf(texture);
    }
    
    public synchronized ArrayList<ResourceLocation> getTextures() {
        // its fine, IDE is being dumb
        //noinspection unchecked
        return (ArrayList<ResourceLocation>) textures.clone();
    }
    
    public synchronized void toBytes(PacketBuffer buf) {
        buf.writeInt(textures.size());
        for (int i = 0; i < textures.size(); i++) {
            buf.writeString(textures.get(i).toString());
        }
    }
    
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> QuartzClient.updateTextureAtlas(this));
        ctx.get().setPacketHandled(true);
    }
}
