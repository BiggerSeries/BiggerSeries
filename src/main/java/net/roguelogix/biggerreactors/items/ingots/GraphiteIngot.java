package net.roguelogix.biggerreactors.items.ingots;

import net.minecraft.item.Item;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

@RegisterItem(name = "graphite_ingot")
public class GraphiteIngot extends Item {
    
    @RegisterItem.Instance
    public static GraphiteIngot INSTANCE;
    
    public GraphiteIngot(Properties properties) {
        super(properties);
    }
}
