package net.roguelogix.biggerreactors.items.ingots;

import net.minecraft.item.Item;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

@RegisterItem(name = "yellorium_ingot")
public class YelloriumIngot extends Item {
    
    @RegisterItem.Instance
    public static YelloriumIngot INSTANCE;
    
    public YelloriumIngot(Properties properties) {
        super(properties);
    }
}
