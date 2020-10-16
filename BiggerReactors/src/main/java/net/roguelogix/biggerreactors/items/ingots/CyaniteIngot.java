package net.roguelogix.biggerreactors.items.ingots;

import net.minecraft.item.Item;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

import javax.annotation.Nonnull;

@RegisterItem(name = "cyanite_ingot")
public class CyaniteIngot extends Item {
    
    @RegisterItem.Instance
    public static CyaniteIngot INSTANCE;
    
    @SuppressWarnings("unused")
    public CyaniteIngot(@Nonnull Properties properties) {
        super(properties);
    }
}
