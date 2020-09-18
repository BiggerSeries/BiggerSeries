package net.roguelogix.biggerreactors.blocks.materials;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

@RegisterBlock(name = "blutonium_block")
public class BlutoniumBlock extends Block {
    
    public BlutoniumBlock() {
        super(
                Properties.create(Material.IRON)
                        .sound(SoundType.STONE)
                        .hardnessAndResistance(1.0F)
        );
    }
}