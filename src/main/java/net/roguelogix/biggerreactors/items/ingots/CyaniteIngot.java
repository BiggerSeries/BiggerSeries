package net.roguelogix.biggerreactors.items.ingots;

import net.minecraft.item.Item;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

@RegisterItem(name = "cyanite_ingot")
public class CyaniteIngot extends Item {
    
    @RegisterItem.Instance
    public static CyaniteIngot INSTANCE;
    
    public CyaniteIngot(Properties properties) {
        super(properties);
    }
}
