package net.roguelogix.phosphophyllite.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector2i;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector3i;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.BiConsumer;

public class Util {
    public static String readResourceLocation(ResourceLocation location) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Minecraft.getInstance().getResourceManager().getResource(location).getInputStream()))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            return stringBuilder.toString();
        } catch (IOException ignored) {
        }
        return null;
    }
    
    public static JsonObject readJSONFile(ResourceLocation location) {
        if (location.getPath().lastIndexOf(".json") != location.getPath().length() - 5) {
            location = new ResourceLocation(location.getNamespace(), location.getPath() + ".json");
        }
        String jsonString = readResourceLocation(location);
        if (jsonString == null) {
            return null;
        }
        JsonElement element = new JsonParser().parse(jsonString);
        if (element instanceof JsonObject) {
            return (JsonObject) element;
        }
        return null;
    }
    
    public static void chunkCachedBlockStateIteration(Vector3i start, Vector3i end, World world, BiConsumer<BlockState, Vector3i> func) {
        Vector3i currentPosition = new Vector3i();
        for (int X = start.x; X < ((end.x + 16) & 0xFFFFFFF0); X += 16) {
            for (int Z = start.z; Z < ((end.z + 16) & 0xFFFFFFF0); Z += 16) {
                int chunkX = X >> 4;
                int chunkZ = Z >> 4;
                Chunk chunk = world.getChunk(chunkX, chunkZ);
                ChunkSection[] chunkSections = chunk.getSections();
                for (int Y = start.y; Y < ((end.y + 16) & 0xFFFFFFF0); Y += 16) {
                    int chunkSectionIndex = Y >> 4;
                    ChunkSection chunkSection = chunkSections[chunkSectionIndex];
                    int sectionMinX = Math.max((X) & 0xFFFFFFF0, start.x);
                    int sectionMinY = Math.max((Y) & 0xFFFFFFF0, start.y);
                    int sectionMinZ = Math.max((Z) & 0xFFFFFFF0, start.z);
                    int sectionMaxX = Math.min((X + 16) & 0xFFFFFFF0, end.x + 1);
                    int sectionMaxY = Math.min((Y + 16) & 0xFFFFFFF0, end.y + 1);
                    int sectionMaxZ = Math.min((Z + 16) & 0xFFFFFFF0, end.z + 1);
                    for (int x = sectionMinX; x < sectionMaxX; x++) {
                        for (int y = sectionMinY; y < sectionMaxY; y++) {
                            for (int z = sectionMinZ; z < sectionMaxZ; z++) {
                                currentPosition.set(x, y, z);
                                func.accept(chunkSection.getBlockState(x & 15, y & 15, z & 15), currentPosition);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static void markRangeDirty(World world, Vector2i start, Vector2i end) {
        for (int X = start.x; X < ((end.x + 16) & 0xFFFFFFF0); X += 16) {
            for (int Z = start.y; Z < ((end.y + 16) & 0xFFFFFFF0); Z += 16) {
                int chunkX = X >> 4;
                int chunkZ = Z >> 4;
                Chunk chunk = world.getChunk(chunkX, chunkZ);
                chunk.markDirty();
            }
        }
    }
}
