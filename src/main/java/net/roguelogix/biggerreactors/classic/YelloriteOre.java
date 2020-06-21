package net.roguelogix.biggerreactors.classic;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;
import net.roguelogix.phosphophyllite.registry.RegisterOre;

@RegisterBlock(name = "yellorite_ore")
@RegisterOre(size = 8, minLevel = 32, maxLevel = 72)
// size, minLevel, maxLevel, offset, count, isNetherOre, spawnBiomes
public class YelloriteOre extends Block {

  public YelloriteOre() {
    super(
        Properties.create(Material.ROCK)
        .sound(SoundType.STONE)
        .hardnessAndResistance(1.0F)
    );
  }
}
