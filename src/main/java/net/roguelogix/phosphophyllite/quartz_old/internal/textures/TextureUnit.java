package net.roguelogix.phosphophyllite.quartz_old.internal.textures;

import net.minecraft.util.Tuple;
import net.roguelogix.phosphophyllite.quartz_old.internal.OperationMode;
import net.roguelogix.phosphophyllite.quartz_old.internal.Renderer;
import net.roguelogix.phosphophyllite.threading.Event;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.lwjgl.opengl.GL;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static net.roguelogix.phosphophyllite.quartz_old.internal.GLConstants.MAX_ARRAY_TEXTURE_LAYERS;
import static net.roguelogix.phosphophyllite.quartz_old.internal.GLConstants.MAX_TEXTURE_SIZE;
import static net.roguelogix.phosphophyllite.quartz_old.internal.Renderer.secondaryWorkQueue;
import static org.lwjgl.opengl.GL45.*;

public class TextureUnit {

    private final int acceptedTextureSize;
    private int width = 0, height = 0, depth = 0;
    private int handle = 0;
    private int newHandle = 0;
    final HashSet<Texture> textures = new HashSet<>();
    private final ArrayList<Vector3i> offsetsAvailable = new ArrayList<>();
    private final HashMap<Texture, Vector3i> offsets = new HashMap<>();

    public TextureUnit(int acceptedTextureSize) {
        this.acceptedTextureSize = acceptedTextureSize;
    }

    @Override
    protected void finalize() {
        secondaryWorkQueue.enqueue(() -> {
            glDeleteTextures(handle);
            glDeleteTextures(newHandle);
        });
    }

    Vector3f texturePositionNormalized(Texture texture, Vector3f vector) {
        synchronized (offsets) {
            return vector.set(offsets.get(texture)).div(width, height, depth);
        }
    }

    Vector3f texturePositionNormalized(Texture texture) {
        return texturePositionNormalized(texture, new Vector3f());
    }

    Vector3ic texturePosition(Texture texture) {
        synchronized (offsets) {
            return offsets.get(texture);
        }
    }

    public void bind(int textureUnit) {
        if (newHandle != 0) {
            glDeleteTextures(handle);
            handle = newHandle;
            newHandle = 0;
        }
        glActiveTexture(GL_TEXTURE0 + textureUnit);
        switch (OperationMode.mode()) {
            case GL45:
                glBindTexture(GL_TEXTURE_2D_ARRAY, handle);
                break;
            case GL21:
                glBindTexture(GL_TEXTURE_3D, handle);
                break;
        }
    }

    public float xscale() {
        if (width == 0) {
            return 0;
        }
        return (float) acceptedTextureSize / (float) width;
    }

    public float yscale() {
        if (width == 0) {
            return 0;
        }
        return (float) acceptedTextureSize / (float) height;
    }

    /**
     * Safe to call from any thread
     */
    public synchronized boolean addTexture(Texture texture) {
        int[] textureSize = texture.getTextureData().getA();
        if (textureSize[0] != textureSize[1] || textureSize[0] != acceptedTextureSize) {
            return false;
        }
        textures.add(texture);
        return true;
    }

    /**
     * Safe to call from any thread
     */
    public synchronized Event reloadAll() {
        Event[] textureReloadEvents = new Event[textures.size()];
        int i = 0;
        for (Texture texture : textures) {
            textureReloadEvents[i] = texture.reload();
            i++;
        }
        return secondaryWorkQueue.enqueue(this::upload, textureReloadEvents);
    }

    public synchronized void upload() {
        // TODO: mipmap levels
        // TODO: anisotropic filtering
        switch (OperationMode.mode()) {
            case GL21: {

                if (!GL.getCapabilities().OpenGL42) {
                    throw new IllegalStateException("GL21 not supported yet");
                }

                // gib more DSA plz
                int previouslyBoundTexture = glGetInteger(GL_TEXTURE_BINDING_3D);
                // increase size, upload data
                if (offsetsAvailable.size() < (textures.size() - offsets.size())) {
                    int textureCount = textures.size();

                    final int MAX_TEXTURES_PER_ROW = MAX_TEXTURE_SIZE / acceptedTextureSize;
                    final int MAX_TEXTURES_PER_LAYER = MAX_TEXTURES_PER_ROW * MAX_TEXTURES_PER_ROW;

                    int depth = textureCount / MAX_TEXTURES_PER_LAYER + ((textureCount % MAX_TEXTURES_PER_LAYER) == 0 ? 0 : 1);
                    int texturesHeight = textureCount / (MAX_TEXTURES_PER_ROW * depth) + ((textureCount % (MAX_TEXTURES_PER_ROW * depth)) == 0 ? 0 : 1);
                    int height = texturesHeight * acceptedTextureSize;
                    int texturesWidth = textureCount / (depth * height) + ((textureCount % (depth * height)) == 0 ? 0 : 1);
                    int width = texturesWidth * acceptedTextureSize;
                    if (depth > MAX_ARRAY_TEXTURE_LAYERS || height > MAX_TEXTURE_SIZE || width > MAX_TEXTURE_SIZE) {
                        throw new IllegalStateException("Attempted to allocate a larger than allowed texture");
                    }

                    int newTexture = glGenTextures();
                    try {
                        final ArrayList<Vector3i> newOffsetsAvailable = new ArrayList<>(texturesWidth * texturesHeight * textureCount);
                        for (int i = 0; i < texturesWidth; i++) {
                            for (int j = 0; j < texturesHeight; j++) {
                                for (int k = 0; k < depth; k++) {
                                    newOffsetsAvailable.add(new Vector3i(i * acceptedTextureSize, j * acceptedTextureSize, k));
                                }
                            }
                        }
                        final HashMap<Texture, Vector3i> newOffsets = new HashMap<>();

                        glBindTexture(GL_TEXTURE_3D, newTexture);
                        // TODO: replace with GL 2.1 call, not GL4.2
                        glTexStorage3D(GL_TEXTURE_3D, 1, GL_RGBA8, width, height, depth);

                        for (Texture texture : textures) {
                            Tuple<int[], ByteBuffer> textureData = texture.getTextureData();
                            Vector3i offset = newOffsetsAvailable.remove(newOffsetsAvailable.size() - 1);
                            newOffsets.put(texture, offset);
                            glTexSubImage3D(GL_TEXTURE_3D, 0, offset.x, offset.y, offset.z, acceptedTextureSize, acceptedTextureSize, 1, GL_RGB, GL_UNSIGNED_BYTE, textureData.getB());
                        }

                        this.width = width;
                        this.height = height;
                        this.depth = depth;
                        synchronized (offsets) {
                            offsets.clear();
                            offsets.putAll(newOffsets);
                        }
                        offsetsAvailable.clear();
                        offsetsAvailable.addAll(newOffsetsAvailable);
                        newHandle = newTexture;
                    } catch (Throwable e) {
                        glDeleteTextures(newTexture);
                        throw e;
                    }
                    return;
                } else {
                    // oh, there is actually a spot left over
                    synchronized (offsets) {
                        glBindTexture(GL_TEXTURE_3D, handle);
                        for (Texture texture : textures) {
                            if (!texture.hasNewData) {
                                continue;
                            }
                            Tuple<int[], ByteBuffer> textureData = texture.getTextureData();
                            Vector3i offset = offsets.get(texture);
                            if (offset == null) {
                                offset = offsetsAvailable.remove(offsetsAvailable.size() - 1);
                                offsets.put(texture, offset);
                            }
                            glTexSubImage3D(GL_TEXTURE_3D, 0, offset.x, offset.y, offset.z, acceptedTextureSize, acceptedTextureSize, 1, GL_RGB, GL_UNSIGNED_BYTE, textureData.getB());
                        }
                    }
                }
                glBindTexture(GL_TEXTURE_3D, previouslyBoundTexture);
                break;
            }
            case GL45: {
                // DSA, no need to alter bindings
                // increase size, upload data
                if (offsetsAvailable.size() < (textures.size() - offsets.size())) {
                    int textureCount = textures.size();

                    final int MAX_TEXTURES_PER_ROW = MAX_TEXTURE_SIZE / acceptedTextureSize;
                    final int MAX_TEXTURES_PER_LAYER = MAX_TEXTURES_PER_ROW * MAX_TEXTURES_PER_ROW;

                    int depth = textureCount / MAX_TEXTURES_PER_LAYER + ((textureCount % MAX_TEXTURES_PER_LAYER) == 0 ? 0 : 1);
                    int texturesHeight = textureCount / (MAX_TEXTURES_PER_ROW * depth) + ((textureCount % (MAX_TEXTURES_PER_ROW * depth)) == 0 ? 0 : 1);
                    int texturesWidth = textureCount / (depth * texturesHeight) + ((textureCount % (depth * texturesHeight)) == 0 ? 0 : 1);
                    int height = texturesHeight * acceptedTextureSize;
                    int width = texturesWidth * acceptedTextureSize;
                    if (depth > MAX_ARRAY_TEXTURE_LAYERS || height > MAX_TEXTURE_SIZE || width > MAX_TEXTURE_SIZE) {
                        throw new IllegalStateException("Attempted to allocate a larger than allowed texture");
                    }

                    int newTexture = glCreateTextures(GL_TEXTURE_2D_ARRAY);
                    glTextureParameteri(newTexture, GL_TEXTURE_WRAP_R, GL_REPEAT);
                    glTextureParameteri(newTexture, GL_TEXTURE_WRAP_S, GL_REPEAT);
                    glTextureParameteri(newTexture, GL_TEXTURE_WRAP_T, GL_REPEAT);
                    glTextureParameteri(newTexture, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                    glTextureParameteri(newTexture, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                    try {
                        final ArrayList<Vector3i> newOffsetsAvailable = new ArrayList<>(texturesWidth * texturesHeight * depth);
                        for (int i = 0; i < texturesWidth; i++) {
                            for (int j = 0; j < texturesHeight; j++) {
                                for (int k = 0; k < depth; k++) {
                                    newOffsetsAvailable.add(new Vector3i(i * acceptedTextureSize, j * acceptedTextureSize, k));
                                }
                            }
                        }
                        final HashMap<Texture, Vector3i> newOffsets = new HashMap<>();

                        glTextureStorage3D(newTexture, 1, GL_RGBA8, width, height, depth);

                        for (Texture texture : textures) {
                            Tuple<int[], ByteBuffer> textureData = texture.getTextureData();
                            Vector3i offset = newOffsetsAvailable.remove(newOffsetsAvailable.size() - 1);
                            newOffsets.put(texture, offset);
                            glTextureSubImage3D(newTexture, 0, offset.x, offset.y, offset.z, acceptedTextureSize, acceptedTextureSize, 1, GL_RGBA, GL_UNSIGNED_BYTE, textureData.getB());
                            Renderer.LOGGER.debug("Texture uploaded at " + offset.toString() + " with size " + acceptedTextureSize);
                        }

                        this.width = width;
                        this.height = height;
                        this.depth = depth;
                        synchronized (offsets) {
                            offsets.clear();
                            offsets.putAll(newOffsets);
                        }
                        offsetsAvailable.clear();
                        offsetsAvailable.addAll(newOffsetsAvailable);
                        newHandle = newTexture;
                    } catch (Throwable e) {
                        glDeleteTextures(newTexture);
                        throw e;
                    }
                    return;
                } else {
                    // oh, there is actually a spot left over
                    synchronized (offsets) {
                        for (Texture texture : textures) {
                            if (!texture.hasNewData) {
                                continue;
                            }
                            Tuple<int[], ByteBuffer> textureData = texture.getTextureData();
                            Vector3i offset = offsets.get(texture);
                            if (offset == null) {
                                offset = offsetsAvailable.remove(offsetsAvailable.size() - 1);
                                offsets.put(texture, offset);
                            }
                            glTextureSubImage3D(newHandle, 0, offset.x, offset.y, offset.z, acceptedTextureSize, acceptedTextureSize, 1, GL_RGBA, GL_UNSIGNED_BYTE, textureData.getB());
                        }
                    }
                }
                break;
            }
        }
    }
}
