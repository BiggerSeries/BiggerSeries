package net.roguelogix.biggerreactors.items.ingots;

import net.minecraft.item.Item;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

@RegisterItem(name = "blutonium_ingot")
public class BlutoniumIngot extends Item {
    
    @RegisterItem.Instance
    public static BlutoniumIngot INSTANCE;
    
    public BlutoniumIngot(Properties properties) {
        super(properties);
    }
}
