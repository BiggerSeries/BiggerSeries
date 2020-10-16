package net.roguelogix.biggerreactors.classic.turbine;

import net.minecraft.block.Block;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.roguelogix.biggerreactors.Config;

import java.util.HashMap;

public class TurbineCoilRegistry {
    
    
    public static class TurbineCoilData {
        public final double efficiency;
        public final double bonus;
        public final double extractionRate;
        
        public TurbineCoilData(double efficiency, double bonus, double extractionRate) {
            this.efficiency = efficiency;
            this.bonus = bonus;
            this.extractionRate = extractionRate;
        }
    }
    
    private static final HashMap<Block, TurbineCoilData> registry = new HashMap<>();
    
    public static synchronized boolean isBlockAllowed(Block block) {
        return registry.containsKey(block);
    }
    
    public static synchronized TurbineCoilData getCoilData(Block block) {
        return registry.get(block);
    }
    
    public static synchronized void registerConfigValues(ITagCollection<Block> blockTags, Config.TurbineCoilConfigValues values) {
        TurbineCoilData data = new TurbineCoilData(values.efficiency, values.bonus, values.extractionRate);
        if (values.locationType == Config.TurbineCoilConfigValues.LocationType.REGISTRY) {
            registry.put(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(values.location)), data);
        } else {
            ITag<Block> blockTag = blockTags.get(new ResourceLocation(values.location));
            if (blockTag == null) {
                return;
            }
            for (Block element : blockTag.getAllElements()) {
                registry.put(element, data);
            }
        }
    }
    
    public static synchronized void clearRegistry() {
        registry.clear();
    }
}
