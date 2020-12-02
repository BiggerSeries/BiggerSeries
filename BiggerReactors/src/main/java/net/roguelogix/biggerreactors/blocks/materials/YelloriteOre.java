package net.roguelogix.biggerreactors.blocks.materials;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.phosphophyllite.registry.IPhosphophylliteOre;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;
import net.roguelogix.phosphophyllite.registry.RegisterOre;

@RegisterBlock(name = "yellorite_ore")
@RegisterOre
public class YelloriteOre extends Block implements IPhosphophylliteOre {
    
    @RegisterBlock.Instance
    public static YelloriteOre INSTANCE;
    
    public YelloriteOre() {
        super(
                Properties.create(Material.ROCK)
                        .sound(SoundType.STONE)
                        .hardnessAndResistance(1.0F)
        );
    }
    
    @Override
    public int size() {
        return Config.WorldGen.YelloriteMaxOrePerCluster;
    }
    
    @Override
    public int count() {
        return Config.WorldGen.YelloriteOreMaxClustersPerChunk;
    }
    
    @Override
    public int maxLevel() {
        return Config.WorldGen.YelloriteOreMaxSpawnY;
    }
    
    @Override
    public String[] spawnBiomes() {
        return new String[0];
    }
    
    @Override
    public boolean doSpawn() {
        return Config.WorldGen.EnableYelloriteGeneration;
    }
}
