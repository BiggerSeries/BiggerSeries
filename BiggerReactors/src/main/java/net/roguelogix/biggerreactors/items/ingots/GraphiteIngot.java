package net.roguelogix.biggerreactors.items.ingots;

import net.minecraft.item.Item;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

import javax.annotation.Nonnull;

@RegisterItem(name = "graphite_ingot")
public class GraphiteIngot extends Item {
    
    @RegisterItem.Instance
    public static GraphiteIngot INSTANCE;
    
    @SuppressWarnings("unused")
    public GraphiteIngot(@Nonnull Properties properties) {
        super(properties);
    }
}
