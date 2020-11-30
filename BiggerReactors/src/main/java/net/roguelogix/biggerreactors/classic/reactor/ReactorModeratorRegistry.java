package net.roguelogix.biggerreactors.classic.reactor;

import net.minecraft.block.Block;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.registries.ForgeRegistries;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.repack.tnjson.ParseException;
import net.roguelogix.phosphophyllite.repack.tnjson.TnJson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReactorModeratorRegistry {
    
    public static class ModeratorProperties {
        
        public final double absorption;
        public final double heatEfficiency;
        public final double moderation;
        public final double heatConductivity;
        
        public ModeratorProperties(double absorption, double heatEfficiency, double moderation, double heatConductivity) {
            this.absorption = absorption;
            this.heatEfficiency = heatEfficiency;
            this.moderation = moderation;
            this.heatConductivity = heatConductivity;
        }
        
    }
    
    private final static HashMap<Block, ModeratorProperties> registry = new HashMap<>();
    
    public static boolean isBlockAllowed(Block block) {
        return registry.containsKey(block);
    }
    
    public static ModeratorProperties blockModeratorProperties(Block block) {
        return registry.get(block);
    }
    
    public static void loadRegistry(ITagCollection<Block> blockTags) {
        BiggerReactors.LOGGER.info("Loading reactor moderators");
        registry.clear();
        // TODO: generify this code in Phosphophyllite
        IResourceManager resourceManager = BiggerReactors.dataPackRegistries.getResourceManager();
        Collection<ResourceLocation> resourceLocations = resourceManager.getAllResourceLocations("ebcr/moderators", s -> s.contains(".json"));
        
        for (ResourceLocation resourceLocation : resourceLocations) {
            if (!resourceLocation.getNamespace().equals("biggerreactors")) {
                continue;
            }
            String json;
            try {
                IResource resource = resourceManager.getResource(resourceLocation);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    json = builder.toString();
                }
                
            } catch (IOException e) {
                BiggerReactors.LOGGER.error("Error reading moderator json at " + resourceLocation.toString());
                e.printStackTrace();
                continue;
            }
            
            Map<String, Object> map;
            try {
                map = TnJson.parse(json);
            } catch (ParseException e) {
                BiggerReactors.LOGGER.error("Error parsing moderator json at " + resourceLocation.toString());
                e.printStackTrace();
                continue;
            }
            
            Object typeObject = map.get("type");
            Object locationObject = map.get("location");
            Object absorptionObject = map.get("absorption");
            Object efficiencyObject = map.get("efficiency");
            Object moderationObject = map.get("moderation");
            Object conductivityObject = map.get("conductivity");
            
            if (typeObject == null) {
                BiggerReactors.LOGGER.error("Error moderator location type not found in json at " + resourceLocation.toString());
                continue;
            }
            
            if (locationObject == null) {
                BiggerReactors.LOGGER.error("Error moderator location not found in json at " + resourceLocation.toString());
                continue;
            }
            
            if (absorptionObject == null) {
                BiggerReactors.LOGGER.error("Error moderator absorption not found in json at " + resourceLocation.toString());
                continue;
            }
            
            if (efficiencyObject == null) {
                BiggerReactors.LOGGER.error("Error moderator efficiency not found in json at " + resourceLocation.toString());
                continue;
            }
            
            if (moderationObject == null) {
                BiggerReactors.LOGGER.error("Error moderator moderation not found in json at " + resourceLocation.toString());
                continue;
            }
            
            if (conductivityObject == null) {
                BiggerReactors.LOGGER.error("Error moderator conductivity not found in json at " + resourceLocation.toString());
                continue;
            }
            
            if (!(typeObject instanceof String)) {
                BiggerReactors.LOGGER.error("Error moderator location type not a string in json at " + resourceLocation.toString());
                continue;
            }
            
            if (!(locationObject instanceof String)) {
                BiggerReactors.LOGGER.error("Error moderator location not a string in json at " + resourceLocation.toString());
                continue;
            }
            
            if (!(absorptionObject instanceof Number)) {
                BiggerReactors.LOGGER.error("Error moderator absorption not a number in json at " + resourceLocation.toString());
                continue;
            }
            
            if (!(efficiencyObject instanceof Number)) {
                BiggerReactors.LOGGER.error("Error moderator efficiency not a number in json at " + resourceLocation.toString());
                continue;
            }
            
            if (!(moderationObject instanceof Number)) {
                BiggerReactors.LOGGER.error("Error moderator moderation not a number in json at " + resourceLocation.toString());
                continue;
            }
            
            if (!(conductivityObject instanceof Number)) {
                BiggerReactors.LOGGER.error("Error moderator conductivity not a number in json at " + resourceLocation.toString());
                continue;
            }
            
            String typeStr = (String) typeObject;
            String locationStr = (String) locationObject;
            double absorption = ((Number) absorptionObject).doubleValue();
            double efficiency = ((Number) efficiencyObject).doubleValue();
            double moderation = ((Number) moderationObject).doubleValue();
            double conductivity = ((Number) conductivityObject).doubleValue();
            
            boolean isTag;
            if (typeStr.equals("tag")) {
                isTag = true;
            } else if (typeStr.equals("registry")) {
                isTag = false;
            } else {
                BiggerReactors.LOGGER.error("Error location type not a \"tag\" or \"registry\" in json at " + resourceLocation.toString() + ", got " + typeStr);
                continue;
            }
            
            ResourceLocation location;
            try {
                location = new ResourceLocation(locationStr);
            } catch (ResourceLocationException e) {
                BiggerReactors.LOGGER.error("Error invalid resource location given in json at " + resourceLocation.toString() + ", got " + locationStr);
                e.printStackTrace();
                continue;
            }
            
            
            if (absorption < 0.0 || absorption > 1.0) {
                BiggerReactors.LOGGER.error("Error absorption out of range in json at " + resourceLocation.toString() + ", got " + absorption + ", valid range is [0, 1]");
            }
            
            if (efficiency < 0.0 || efficiency > 1.0) {
                BiggerReactors.LOGGER.error("Error efficiency out of range in json at " + resourceLocation.toString() + ", got " + efficiency + ", valid range is [0, 1]");
            }
            
            if (moderation < 0.0 ) {
                BiggerReactors.LOGGER.error("Error moderation out of range in json at " + resourceLocation.toString() + ", got " + moderation + ", valid range is [0,)");
            }
            
            if (conductivity < 0.0) {
                BiggerReactors.LOGGER.error("Error conductivity out of range in json at " + resourceLocation.toString() + ", got " + conductivity + ", valid range is [0,)");
            }
            
            ModeratorProperties properties = new ModeratorProperties(absorption, efficiency, moderation, conductivity);
            
            if (isTag) {
                ITag<Block> blockTag = blockTags.get(location);
                if (blockTag == null) {
                    continue;
                }
                for (Block element : blockTag.getAllElements()) {
                    registry.put(element, properties);
                }
            } else {
                registry.put(ForgeRegistries.BLOCKS.getValue(location), properties);
            }
        }
        BiggerReactors.LOGGER.info("Loaded " + registry.size() + " moderator entries");
    }
}
