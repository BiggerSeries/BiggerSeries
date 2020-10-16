package net.roguelogix.biggerreactors.items.dusts;

import net.minecraft.item.Item;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

import javax.annotation.Nonnull;

@RegisterItem(name = "cyanite_dust")
public class CyaniteDust extends Item {
    
    @RegisterItem.Instance
    public static CyaniteDust INSTANCE;
    
    @SuppressWarnings("unused")
    public CyaniteDust(@Nonnull Properties properties) {
        super(properties);
    }
}