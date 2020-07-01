package net.roguelogix.phosphophyllite.quartz_old.internal.textures;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.roguelogix.phosphophyllite.Phosphophyllite;
import net.roguelogix.phosphophyllite.threading.Event;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

import static net.roguelogix.phosphophyllite.quartz_old.internal.Renderer.tertiaryWorkQueue;

/**
 * Safe to use from any thread
 */
public class Texture {
    final ResourceLocation location;
    private Tuple<int[], ByteBuffer> textureData;
    boolean hasNewData = true;
    
    public Texture(ResourceLocation location) {
        if (location.getPath().lastIndexOf(".png") != location.getPath().length() - 4) {
            location = new ResourceLocation(location.getNamespace(), location.getPath() + ".png");
        }
        this.location = location;
        textureData = load(location);
        if (textureData == null) {
            throw new IllegalStateException("Couldn't load texture: " + location.toString());
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        STBImage.stbi_image_free(textureData.getB());
    }
    
    public Event reload() {
        return tertiaryWorkQueue.enqueue(() -> {
            Tuple<int[], ByteBuffer> newData = load(location);
            if (newData != null) {
                if (!Arrays.equals(textureData.getA(), newData.getA())) {
                    Phosphophyllite.LOGGER.error("Texture size cannot change at runtime");
                    return;
                }
                //noinspection SynchronizeOnNonFinalField
                synchronized (textureData) {
                    newData.getB().rewind();
                    textureData.getB().rewind();
                    if (newData.getB().equals(textureData.getB())) {
                        // didnt change, dont trigger GL update then
                        return;
                    }
                    STBImage.stbi_image_free(textureData.getB());
                    textureData = newData;
                    hasNewData = true;
                }
            }
        });
    }
    
    Tuple<int[], ByteBuffer> getTextureData() {
        //noinspection SynchronizeOnNonFinalField
        synchronized (textureData) {
            hasNewData = false;
            return textureData;
        }
    }
    
    private static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int len;
        
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        return outputStream.toByteArray();
    }
    
    private static Tuple<int[], ByteBuffer> load(ResourceLocation location) {
        ByteBuffer byteBuffer;
        try (InputStream inputStream = Minecraft.getInstance().getResourceManager().getResource(location).getInputStream()) {
            byte[] array = toByteArray(inputStream);
            byteBuffer = BufferUtils.createByteBuffer(array.length).put(array);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        
        int[] w = new int[2];
        int[] h = new int[1];
        int[] comp = new int[1];
        byteBuffer.rewind();
        ByteBuffer imageData = STBImage.stbi_load_from_memory(byteBuffer, w, h, comp, 4);
        if (imageData == null) {
            System.out.println("STB image load failed: " + location.toString());
            System.out.println(STBImage.stbi_failure_reason());
            return null;
        }
        
        w[1] = h[0];
        
        return new Tuple<>(w, imageData);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Texture texture = (Texture) o;
        // i only care about the location, because you shouldn't be reloading individual textures yourself
        return Objects.equals(location, texture.location);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(location, textureData, hasNewData);
    }
}
