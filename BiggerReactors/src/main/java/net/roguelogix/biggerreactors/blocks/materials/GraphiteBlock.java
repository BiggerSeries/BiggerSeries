package net.roguelogix.biggerreactors.blocks.materials;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

@RegisterBlock(name = "graphite_block")
public class GraphiteBlock extends Block {
    
    @RegisterBlock.Instance
    public static GraphiteBlock INSTANCE;
    
    public GraphiteBlock() {
        super(
                Properties.create(Material.IRON)
                        .sound(SoundType.STONE)
                        .hardnessAndResistance(1.0F)
        );
    }
}