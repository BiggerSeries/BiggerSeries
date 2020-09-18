package net.roguelogix.biggerreactors.blocks.materials;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

@RegisterBlock(name = "yellorium_block")
public class YelloriumBlock extends Block {
    
    public YelloriumBlock() {
        super(
                Properties.create(Material.IRON)
                        .sound(SoundType.STONE)
                        .hardnessAndResistance(1.0F)
        );
    }
}