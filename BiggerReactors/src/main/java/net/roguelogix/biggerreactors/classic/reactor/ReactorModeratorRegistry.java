package net.roguelogix.biggerreactors.classic.reactor;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.biggerreactors.classic.turbine.TurbineCoilRegistry;

import java.util.HashMap;

public class ReactorModeratorRegistry {
    
    public static class ModeratorProperties {
        
        public final float absorption;
        public final float heatEfficiency;
        public final float moderation;
        public final float heatConductivity;
        
        public ModeratorProperties(float absorption, float heatEfficiency, float moderation, float heatConductivity) {
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
    
    public static synchronized void registerConfigValues(ITagCollection<Block> blockTags, Config.ReactorModeratorConfigValues values) {
        ModeratorProperties data = new ModeratorProperties(values.absorption, values.heatEfficiency, values.moderation, values.conductivity);
        if (values.locationType == Config.ReactorModeratorConfigValues.LocationType.REGISTRY) {
            registry.put(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(values.location)), data);
        }else{
            ITag<Block> blockTag = blockTags.get(new ResourceLocation(values.location));
            if(blockTag == null){
                return;
            }
            for (Block element : blockTag.getAllElements()) {
                registry.put(element, data);
            }
        }
    }
    
    public static synchronized void clearRegistry(){
        registry.clear();
    }
}
