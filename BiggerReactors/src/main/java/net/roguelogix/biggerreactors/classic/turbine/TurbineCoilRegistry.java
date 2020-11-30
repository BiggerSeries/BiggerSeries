package net.roguelogix.biggerreactors.classic.turbine;

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

public class TurbineCoilRegistry {
    
    
    public static class CoilData {
        public final double efficiency;
        public final double bonus;
        public final double extractionRate;
        
        public CoilData(double efficiency, double bonus, double extractionRate) {
            this.efficiency = efficiency;
            this.bonus = bonus;
            this.extractionRate = extractionRate;
        }
    }
    
    private static final HashMap<Block, CoilData> registry = new HashMap<>();
    
    public static synchronized boolean isBlockAllowed(Block block) {
        return registry.containsKey(block);
    }
    
    public static synchronized CoilData getCoilData(Block block) {
        return registry.get(block);
    }
    
    public static void loadRegistry(ITagCollection<Block> blockTags) {
        BiggerReactors.LOGGER.info("Loading turbine coils");
        registry.clear();
        // TODO: generify this code in Phosphophyllite
        IResourceManager resourceManager = BiggerReactors.dataPackRegistries.getResourceManager();
        Collection<ResourceLocation> resourceLocations = resourceManager.getAllResourceLocations("ebest/coils", s -> s.contains(".json"));
        
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
            Object efficiencyObject = map.get("efficiency");
            Object extractionRateObject = map.get("extractionRate");
            Object bonusObject = map.get("bonus");
            
            if (typeObject == null) {
                BiggerReactors.LOGGER.error("Error moderator location type not found in json at " + resourceLocation.toString());
                continue;
            }
            
            if (locationObject == null) {
                BiggerReactors.LOGGER.error("Error moderator location not found in json at " + resourceLocation.toString());
                continue;
            }
            
            if (efficiencyObject == null) {
                BiggerReactors.LOGGER.error("Error moderator efficiency not found in json at " + resourceLocation.toString());
                continue;
            }
            
            if (extractionRateObject == null) {
                BiggerReactors.LOGGER.error("Error moderator extractionRate not found in json at " + resourceLocation.toString());
                continue;
            }
            
            if (bonusObject == null) {
                BiggerReactors.LOGGER.error("Error moderator bonus not found in json at " + resourceLocation.toString());
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
            
            if (!(efficiencyObject instanceof Number)) {
                BiggerReactors.LOGGER.error("Error moderator efficiency not a number in json at " + resourceLocation.toString());
                continue;
            }
            
            if (!(extractionRateObject instanceof Number)) {
                BiggerReactors.LOGGER.error("Error moderator extractionRate not a number in json at " + resourceLocation.toString());
                continue;
            }
            
            if (!(bonusObject instanceof Number)) {
                BiggerReactors.LOGGER.error("Error moderator bonus not a number in json at " + resourceLocation.toString());
                continue;
            }
            
            String typeStr = (String) typeObject;
            String locationStr = (String) locationObject;
            double efficiency = ((Number) efficiencyObject).doubleValue();
            double extractionRate = ((Number) extractionRateObject).doubleValue();
            double bonus = ((Number) bonusObject).doubleValue();
            
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
            
            
            if (efficiency < 0.0) {
                BiggerReactors.LOGGER.error("Error efficiency out of range in json at " + resourceLocation.toString() + ", got " + efficiency + ", valid range is [0,), clamping");
                efficiency = Math.max(0.0, efficiency);
            }
            
            if (extractionRate < 0.0) {
                BiggerReactors.LOGGER.error("Error extractionRate out of range in json at " + resourceLocation.toString() + ", got " + extractionRate + ", valid range is [0,), clamping");
                extractionRate = Math.max(0.0, extractionRate);
            }
            
            if (bonus < 1.0) {
                BiggerReactors.LOGGER.error("Error bonus out of range in json at " + resourceLocation.toString() + ", got " + bonus + ", valid range is [1,), clamping");
                bonus = Math.max(1.0, bonus);
            }
            
            CoilData properties = new CoilData(efficiency, bonus, extractionRate);
            
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
        BiggerReactors.LOGGER.info("Loaded " + registry.size() + " coil entries");
    }
}
