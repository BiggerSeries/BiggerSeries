package net.roguelogix.phosphophyllite.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.roguelogix.phosphophyllite.registry.CreativeTabBlock;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;


// "Ore"
@CreativeTabBlock
@RegisterBlock(name = "phosphophyllite_ore")
public class PhosphophylliteOre extends Block {
    
    @RegisterBlock.Instance
    public static PhosphophylliteOre INSTANCE;
    
    public PhosphophylliteOre() {
        super(Properties.create(Material.ROCK).noDrops().hardnessAndResistance(3.0F, 3.0F));
    }
}
