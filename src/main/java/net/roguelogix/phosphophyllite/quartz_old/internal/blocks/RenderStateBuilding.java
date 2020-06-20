package net.roguelogix.phosphophyllite.quartz_old.internal.blocks;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.roguelogix.phosphophyllite.quartz_old.QuartzState;
import net.roguelogix.phosphophyllite.quartz_old.internal.Renderer;
import net.roguelogix.phosphophyllite.quartz_old.internal.textures.TextureRegistry;
import net.roguelogix.phosphophyllite.threading.Event;
import org.joml.Vector2ic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static net.roguelogix.phosphophyllite.quartz_old.internal.Renderer.tertiaryWorkQueue;
import static net.roguelogix.phosphophyllite.quartz_old.internal.Util.readJSONFile;

public class RenderStateBuilding {
    private static final ArrayList<QuartzState> cache = new ArrayList<>();
    private static final ArrayList<BlockRenderInfo> renderInfoCache = new ArrayList<>();

    public static int writeStateCache(QuartzState state) {
        synchronized (cache) {
            cache.add(state);
            return cache.size() - 1;
        }
    }

    public static QuartzState readStateCache(int cid) {
        synchronized (cache) {
            return cache.get(cid);
        }
    }

    private static final HashMap<Block, RenderInfoBuilder> renderInfoBuilders = new HashMap<>();

    public static BlockRenderInfo buildBaseInfo(Block block, QuartzState state) {
        RenderInfoBuilder builder;
        synchronized (renderInfoBuilders) {
            builder = renderInfoBuilders.get(block);
            if (builder == null) {
                ResourceLocation location = block.getRegistryName();
                builder = new RenderInfoBuilder(new ResourceLocation(location.getNamespace(), "quartzstates/" + location.getPath()));
                renderInfoBuilders.put(block, builder);
            }
        }
        return builder.build(state);
    }

    private static class RenderInfoBuilder {

        private static class RenderInfoBuildingInfo{
            String allTexture = null;
            String topTexture = null;
            String bottomTexture = null;
            String northTexture = null;
            String southTexture = null;
            String eastTexture = null;
            String westTexture = null;
            int allRotation = 0;
            boolean allRotationSet = false;
            int topRotation = 0;
            boolean topRotationSet = false;
            int bottomRotation = 0;
            boolean bottomRotationSet = false;
            int northRotation = 0;
            boolean northRotationSet = false;
            int southRotation = 0;
            boolean southRotationSet = false;
            int eastRotation = 0;
            boolean eastRotationSet = false;
            int westRotation = 0;
            boolean westRotationSet = false;
        }
        
        private final ResourceLocation location;
        private JsonObject json;
        private final Event loadedEvent;
        private final HashMap<QuartzState, BlockRenderInfo> cache = new HashMap<>();

        public RenderInfoBuilder(ResourceLocation location) {
            loadedEvent = tertiaryWorkQueue.enqueue(() -> json = readJSONFile(location));
            this.location = location;
        }

        public BlockRenderInfo build(QuartzState state) {
            loadedEvent.join();
            synchronized (cache) {
                BlockRenderInfo cachedInfo = cache.get(state);
                if (cachedInfo != null) {
                    return cachedInfo.shallowCopy();
                }
            }
            RenderInfoBuildingInfo buildingInfo = new RenderInfoBuildingInfo();
            JsonObject currentLevel = json;
            JsonObject nextLevel = null;
            while (currentLevel != null) {
                Set<Map.Entry<String, JsonElement>> entries = currentLevel.entrySet();
                for (Map.Entry<String, JsonElement> entry : entries) {
                    String entryKey = entry.getKey();
                    JsonElement element = entry.getValue();
                    String stateValue = state.values.get(entryKey);
                    if (stateValue != null && element instanceof JsonObject) {
                        JsonElement nextLevelElement = ((JsonObject) element).get(stateValue);
                        if (nextLevelElement instanceof JsonObject) {
                            if (nextLevel != null) {
                                // not sure where to go, just exit traversal
                                Renderer.LOGGER.warn("");
                                break;
//                                throw new IllegalStateException("Two possible next levels for state!\nState: " + state.toString() + "\n\nStateFile: " + location.toString());
                            }
                            nextLevel = (JsonObject) element;
                        }
                    }
                    // ok, so, i need to update the render info
                    switch (entryKey) {
                        case "textures": {
                            if(!element.isJsonObject()){
                                continue;
                            }
                            JsonObject textureEntries = (JsonObject) element;
                            {
                                JsonElement allTexturesElement = textureEntries.get("all");
                                if(allTexturesElement.isJsonPrimitive()){
                                    buildingInfo.allTexture = allTexturesElement.getAsString();
                                }
                            }
                            {
                                JsonElement topTexturesElement = textureEntries.get("top");
                                if(topTexturesElement.isJsonPrimitive()){
                                    buildingInfo.topTexture = topTexturesElement.getAsString();
                                }
                            }
                            {
                                JsonElement bottomTexturesElement = textureEntries.get("bottom");
                                if(bottomTexturesElement.isJsonPrimitive()){
                                    buildingInfo.bottomTexture = bottomTexturesElement.getAsString();
                                }
                            }
                            {
                                JsonElement northTexturesElement = textureEntries.get("north");
                                if(northTexturesElement.isJsonPrimitive()){
                                    buildingInfo.northTexture = northTexturesElement.getAsString();
                                }
                            }
                            {
                                JsonElement southTexturesElement = textureEntries.get("south");
                                if(southTexturesElement.isJsonPrimitive()){
                                    buildingInfo.southTexture = southTexturesElement.getAsString();
                                }
                            }
                            {
                                JsonElement eastTexturesElement = textureEntries.get("east");
                                if(eastTexturesElement.isJsonPrimitive()){
                                    buildingInfo.eastTexture = eastTexturesElement.getAsString();
                                }
                            }
                            {
                                JsonElement westTexturesElement = textureEntries.get("west");
                                if(westTexturesElement.isJsonPrimitive()){
                                    buildingInfo.westTexture = westTexturesElement.getAsString();
                                }
                            }
                            break;
                        }
                        case "rotations":{
                            if(!element.isJsonObject()){
                                continue;
                            }
                            JsonObject rotationEntries = (JsonObject) element;
                            {
                                JsonElement allRotationsElement = rotationEntries.get("all");
                                if(allRotationsElement.isJsonPrimitive()){
                                    buildingInfo.allRotation = allRotationsElement.getAsInt();
                                    buildingInfo.allRotationSet = true;
                                }
                            }
                            {
                                JsonElement topRotationsElement = rotationEntries.get("top");
                                if(topRotationsElement.isJsonPrimitive()){
                                    buildingInfo.topRotation = topRotationsElement.getAsInt();
                                    buildingInfo.topRotationSet = true;
                                }
                            }
                            {
                                JsonElement bottomRotationsElement = rotationEntries.get("bottom");
                                if(bottomRotationsElement.isJsonPrimitive()){
                                    buildingInfo.bottomRotation = bottomRotationsElement.getAsInt();
                                    buildingInfo.bottomRotationSet = true;
                                }
                            }
                            {
                                JsonElement northRotationsElement = rotationEntries.get("north");
                                if(northRotationsElement.isJsonPrimitive()){
                                    buildingInfo.northRotation = northRotationsElement.getAsInt();
                                    buildingInfo.northRotationSet = true;
                                }
                            }
                            {
                                JsonElement southRotationsElement = rotationEntries.get("south");
                                if(southRotationsElement.isJsonPrimitive()){
                                    buildingInfo.southRotation = southRotationsElement.getAsInt();
                                    buildingInfo.southRotationSet = true;
                                }
                            }
                            {
                                JsonElement eastRotationsElement = rotationEntries.get("east");
                                if(eastRotationsElement.isJsonPrimitive()){
                                    buildingInfo.eastRotation = eastRotationsElement.getAsInt();
                                    buildingInfo.eastRotationSet = true;
                                }
                            }
                            {
                                JsonElement westRotationsElement = rotationEntries.get("west");
                                if(westRotationsElement.isJsonPrimitive()){
                                    buildingInfo.westRotation = westRotationsElement.getAsInt();
                                    buildingInfo.westRotationSet = true;
                                }
                            }
                            break;
                        }
                        default:
                            break;
                    }
                }
                currentLevel = nextLevel;
                nextLevel = null;
            }

            // ok, time to take the json info and actually build this fucker

            BlockRenderInfo info = new BlockRenderInfo();

            if(buildingInfo.allTexture != null){
                Vector2ic allTextureLocation = TextureRegistry.index(new ResourceLocation(buildingInfo.allTexture));
                if(allTextureLocation != null) {
                    int textureLocation = ((allTextureLocation.x() & 0x7FFF) << 15) & ((allTextureLocation.y() & 0x7FFF) << 2);
                    info.textureOffsetRotation0 = textureLocation;
                    info.textureOffsetRotation1 = textureLocation;
                    info.textureOffsetRotation2 = textureLocation;
                    info.textureOffsetRotation3 = textureLocation;
                    info.textureOffsetRotation4 = textureLocation;
                    info.textureOffsetRotation5 = textureLocation;
                }
            }
            if(buildingInfo.topTexture != null){
                Vector2ic topTextureLocation = TextureRegistry.index(new ResourceLocation(buildingInfo.topTexture));
                if(topTextureLocation != null) {
                    int textureLocation = ((topTextureLocation.x() & 0x7FFF) << 15) & ((topTextureLocation.y() & 0x7FFF) << 2);
                    info.textureOffsetRotation3 &= textureLocation;
                }
            }
            if(buildingInfo.bottomTexture != null){
                Vector2ic bottomTextureLocation = TextureRegistry.index(new ResourceLocation(buildingInfo.bottomTexture));
                if(bottomTextureLocation != null) {
                    int textureLocation = ((bottomTextureLocation.x() & 0x7FFF) << 15) & ((bottomTextureLocation.y() & 0x7FFF) << 2);
                    info.textureOffsetRotation2 &= textureLocation;
                }
            }
            
            if(buildingInfo.northTexture != null){
                Vector2ic northTextureLocation = TextureRegistry.index(new ResourceLocation(buildingInfo.northTexture));
                if(northTextureLocation != null) {
                    int textureLocation = ((northTextureLocation.x() & 0x7FFF) << 15) & ((northTextureLocation.y() & 0x7FFF) << 2);
                    info.textureOffsetRotation5 &= textureLocation;
                }
            }
            if(buildingInfo.southTexture != null){
                Vector2ic southTextureLocation = TextureRegistry.index(new ResourceLocation(buildingInfo.southTexture));
                if(southTextureLocation != null) {
                    int textureLocation = ((southTextureLocation.x() & 0x7FFF) << 15) & ((southTextureLocation.y() & 0x7FFF) << 2);
                    info.textureOffsetRotation4 &= textureLocation;
                }
            }

            if(buildingInfo.eastTexture != null){
                Vector2ic eastTextureLocation = TextureRegistry.index(new ResourceLocation(buildingInfo.eastTexture));
                if(eastTextureLocation != null) {
                    int textureLocation = ((eastTextureLocation.x() & 0x7FFF) << 15) & ((eastTextureLocation.y() & 0x7FFF) << 2);
                    info.textureOffsetRotation1 &= textureLocation;
                }
            }
            if(buildingInfo.westTexture != null){
                Vector2ic westTextureLocation = TextureRegistry.index(new ResourceLocation(buildingInfo.westTexture));
                if(westTextureLocation != null) {
                    int textureLocation = ((westTextureLocation.x() & 0x7FFF) << 15) & ((westTextureLocation.y() & 0x7FFF) << 2);
                    info.textureOffsetRotation0 &= textureLocation;
                }
            }

            if(buildingInfo.allRotationSet){
                info.textureOffsetRotation0 &= ~3;
                info.textureOffsetRotation0 &= buildingInfo.allRotation & 3;

                info.textureOffsetRotation1 &= ~3;
                info.textureOffsetRotation1 &= buildingInfo.allRotation & 3;

                info.textureOffsetRotation2 &= ~3;
                info.textureOffsetRotation2 &= buildingInfo.allRotation & 3;

                info.textureOffsetRotation3 &= ~3;
                info.textureOffsetRotation3 &= buildingInfo.allRotation & 3;

                info.textureOffsetRotation4 &= ~3;
                info.textureOffsetRotation4 &= buildingInfo.allRotation & 3;

                info.textureOffsetRotation5 &= ~3;
                info.textureOffsetRotation5 &= buildingInfo.allRotation & 3;
            }

            if(buildingInfo.topRotationSet){
                info.textureOffsetRotation3 &= ~3;
                info.textureOffsetRotation3 &= buildingInfo.topRotation & 3;
            }
            if(buildingInfo.bottomRotationSet){
                info.textureOffsetRotation2 &= ~3;
                info.textureOffsetRotation2 &= buildingInfo.bottomRotation & 3;
            }

            if(buildingInfo.northRotationSet){
                info.textureOffsetRotation5 &= ~3;
                info.textureOffsetRotation5 &= buildingInfo.northRotation & 3;
            }
            if(buildingInfo.southRotationSet){
                info.textureOffsetRotation4 &= ~3;
                info.textureOffsetRotation4 &= buildingInfo.southRotation & 3;
            }

            if(buildingInfo.eastRotationSet){
                info.textureOffsetRotation1 &= ~3;
                info.textureOffsetRotation1 &= buildingInfo.eastRotation & 3;
            }
            if(buildingInfo.westRotationSet){
                info.textureOffsetRotation0 &= ~3;
                info.textureOffsetRotation0 &= buildingInfo.westRotation & 3;
            }

            return info;
        }
    }
}
