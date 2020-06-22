package net.roguelogix.biggerreactors.classic.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;
import net.roguelogix.phosphophyllite.registry.RegisterOre;

@RegisterBlock(name = "yellorite_ore")
@RegisterOre(size = Config.YelloriteMaxOrePerCluster, maxLevel = Config.YelloriteOreMaxSpawnY, count = Config.YelloriteOreMaxClustersPerChunk)
public class YelloriteOre extends Block {

  public YelloriteOre() {
    super(
        Properties.create(Material.ROCK)
        .sound(SoundType.STONE)
        .hardnessAndResistance(1.0F)
    );
  }
}
