package net.roguelogix.phosphophyllite.quartz_old.internal.textures;

import net.minecraft.util.ResourceLocation;
import org.joml.Vector2ic;

import java.util.HashMap;

import static net.roguelogix.phosphophyllite.quartz_old.internal.Renderer.secondaryWorkQueue;

public class TextureRegistry {
    private static boolean doUpload = true;
    private static TextureUnitPack textureUnitPack = null;
    
    public static synchronized void startup() {
        if (textureUnitPack == null) {
            textureUnitPack = new TextureUnitPack();
            textureUnitPack.addTextureUnit(new TextureUnit(16));
            textureUnitPack.addTextureUnit(new TextureUnit(32));
            registeredTextures.forEach((k, texture) -> textureUnitPack.addTextures(texture));
        }
    }
    
    public static void shutdown() {
        textureUnitPack = null;
    }
    
    public static void bind(int baseTextureUnit) {
        if (doUpload) {
            secondaryWorkQueue.enqueue(textureUnitPack::upload);
            doUpload = false;
        }
        textureUnitPack.bind(baseTextureUnit);
    }
    
    public static void setupUniforms(int baseTextureUnit, int indexUniform, int baseScaleUniform, int baseUnitUniform) {
        textureUnitPack.setupUniforms(baseTextureUnit, indexUniform, baseScaleUniform, baseUnitUniform);
    }
    
    private static final HashMap<ResourceLocation, Texture> registeredTextures = new HashMap<>();
    
    public static synchronized Texture getOrRegister(ResourceLocation location) {
        if (location == null) {
            return null;
        }
        Texture texture = registeredTextures.get(location);
        if (texture != null) {
            return texture;
        }
        texture = new Texture(location);
        if (textureUnitPack != null) {
            textureUnitPack.addTextures(texture);
        }
        registeredTextures.put(location, texture);
        return texture;
    }
    
    public static synchronized void reload(ResourceLocation location) {
        if (location == null) {
            textureUnitPack.reloadAll();
            return;
        }
        Texture texture = registeredTextures.get(location);
        if (texture != null) {
            texture.reload();
        }
        doUpload = true;
    }
    
    public static Vector2ic index(ResourceLocation location) {
        Texture texture = getOrRegister(location);
        if (texture == null) {
            return null;
        }
        return textureUnitPack.getTextureIndex(texture);
    }
    
}
