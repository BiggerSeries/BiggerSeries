package net.roguelogix.biggerreactors.items.tools;

import net.minecraft.item.Item;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

@RegisterItem(name = "wrench")
public class Wrench extends Item {
    
    @RegisterItem.Instance
    public static Wrench INSTANCE;
    
    public Wrench(Properties properties) {
        super(properties.maxStackSize(1));
    }
}
