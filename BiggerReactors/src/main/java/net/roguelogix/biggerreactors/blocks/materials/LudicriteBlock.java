package net.roguelogix.biggerreactors.blocks.materials;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

@RegisterBlock(name = "ludicrite_block")
public class LudicriteBlock extends Block {
    
    @RegisterBlock.Instance
    public static LudicriteBlock INSTANCE;
    
    public LudicriteBlock() {
        super(
                Properties.create(Material.IRON)
                        .sound(SoundType.STONE)
                        .hardnessAndResistance(1.0F)
        );
    }
}