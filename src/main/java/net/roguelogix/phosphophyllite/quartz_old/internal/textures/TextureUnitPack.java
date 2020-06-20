package net.roguelogix.phosphophyllite.quartz_old.internal.textures;

import net.minecraft.util.Tuple;
import net.roguelogix.phosphophyllite.quartz_old.internal.OperationMode;
import net.roguelogix.phosphophyllite.threading.Event;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static net.roguelogix.phosphophyllite.quartz_old.internal.Renderer.LOGGER;
import static net.roguelogix.phosphophyllite.quartz_old.internal.Renderer.secondaryWorkQueue;
import static org.lwjgl.opengl.GL45.*;

public class TextureUnitPack {
    // give me one good reason to change this, and it will be changed
    // as is, its big enough size wise, small enough memory size wise, and i dont give enough fucks to make it resizable
    // if being changed, be wary of GL_MAX_TEXTURE_SIZE limits
    // note: RenderChunk TBO relies on this being at *most* 32768
    private static final int size = 1024;
    private final FloatBuffer buffer;
    private final ArrayList<TextureUnit> textureUnits = new ArrayList<>();
    private final ArrayList<Vector2i> offsetsAvailable = new ArrayList<>();
    private final HashMap<Texture, Tuple<Integer, Vector2i>> offsets = new HashMap<>();
    private final int handle;

    public TextureUnitPack() {
        switch (OperationMode.mode()) {
            case GL45:
                handle = glCreateTextures(GL_TEXTURE_2D);
                glTextureParameteri(handle, GL_TEXTURE_WRAP_R, GL_REPEAT);
                glTextureParameteri(handle, GL_TEXTURE_WRAP_S, GL_REPEAT);
                glTextureParameteri(handle, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTextureParameteri(handle, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTextureStorage2D(handle, 1, GL_RGBA32F, size, size);
                buffer = BufferUtils.createFloatBuffer(size * size * 16);
                break;
            case GL21:
                //TODO: GL21
                throw new IllegalStateException("GL21 not supported yet");
//                handle = glGenTextures();
//                glBindTexture(GL_TEXTURE_2D, handle);
//                break;
            default:
                throw new IllegalStateException("Unknown Op Mode");
        }
        for (int i = size; i >= 0; i--) {
            for (int j = size; j >= 0; j--) {
                offsetsAvailable.add(new Vector2i(i, j));
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        secondaryWorkQueue.enqueue(()->{
            glDeleteTextures(handle);
        });
    }

    public void bind(int baseTextureUnit) {
        glActiveTexture(GL_TEXTURE0 + baseTextureUnit);
        glBindTexture(GL_TEXTURE_2D, handle);
        for (int i = 0; i < textureUnits.size(); i++) {
            TextureUnit textureUnit = textureUnits.get(i);
            textureUnit.bind(baseTextureUnit + i + 1);
        }
    }

    public void setupUniforms(int baseTextureUnit, int indexUniform, int baseScaleUniform, int baseUnitUniform){
        glUniform1i(indexUniform, baseTextureUnit);
        for (int i = 0; i < textureUnits.size(); i++) {
            TextureUnit textureUnit = textureUnits.get(i);
            glUniform1i(baseUnitUniform + i, baseTextureUnit + i + 1);
            glUniform2f(baseScaleUniform + i, textureUnit.xscale(), textureUnit.yscale());
        }
    }

    public synchronized void addTextureUnit(TextureUnit textureUnit) {
        textureUnits.add(textureUnit);
    }

    public synchronized Vector2ic getTextureIndex(Texture texture) {
        return offsets.get(texture).getB();
    }

    public synchronized Vector2f getTextureIndexNormalized(Texture texture, Vector2f vector) {
        return vector.set(offsets.get(texture).getB()).div(size, size);
    }

    public synchronized void addTextures(Texture... textures) {
        for (Texture texture : textures) {
            if (this.offsets.containsKey(texture)) {
                continue;
            }
            for (TextureUnit textureUnit : textureUnits) {
                if (textureUnit.addTexture(texture)) {
                    Vector2i offset = offsetsAvailable.remove(offsetsAvailable.size() - 1);
                    offsets.put(texture, new Tuple<>(textureUnits.indexOf(textureUnit), offset));
                    LOGGER.debug("Texture " + texture.location.toString() + " given index offset " + offset.toString());
                    break;
                }
            }
        }
    }

    public Event reloadAll() {
        Event[] textureUnitReloadEvents = new Event[textureUnits.size()];
        int i = 0;
        for (TextureUnit textureUnit : textureUnits) {
            textureUnitReloadEvents[i] = textureUnit.reloadAll();
            i++;
        }
        return secondaryWorkQueue.enqueue(this::upload, textureUnitReloadEvents);
    }

    public synchronized void upload() {
        textureUnits.forEach(TextureUnit::upload);
        final Vector3f offset = new Vector3f();
        offsets.forEach((texture, tuple) -> {
            int textureUnit = tuple.getA();
            Vector2i textureOffset = tuple.getB();
            int bufferOffset = 4 * ((textureOffset.y * size) + textureOffset.x);
            textureUnits.get(textureUnit).texturePositionNormalized(texture, offset);
            buffer.put(bufferOffset, offset.x);
            buffer.put(bufferOffset + 1, offset.y);
            buffer.put(bufferOffset + 2, offset.z);
            buffer.put(bufferOffset + 3, textureUnit);
            LOGGER.debug("Texture " + texture.location.toString() + " given index data " + offset.toString() + " for atlas " + textureUnit);
        });
        switch (OperationMode.mode()) {

            case GL45:
                glTextureSubImage2D(handle, 0, 0, 0, size, size, GL_RGBA, GL_FLOAT, buffer);
                break;
            case GL21:
                // TODO: GL21
                throw new IllegalStateException("GL21 not supported yet");
//                break;
            default:
                throw new IllegalStateException("Unknown operation mode");
        }
    }
}
