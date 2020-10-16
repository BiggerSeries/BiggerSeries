package net.roguelogix.biggerreactors.items.dusts;

import net.minecraft.item.Item;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

import javax.annotation.Nonnull;

@RegisterItem(name = "graphite_dust")
public class GraphiteDust extends Item {
    
    @RegisterItem.Instance
    public static GraphiteDust INSTANCE;
    
    @SuppressWarnings("unused")
    public GraphiteDust(@Nonnull Properties properties) {
        super(properties);
    }
}