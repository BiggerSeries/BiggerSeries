package net.roguelogix.biggerreactors.items.ingots;

import net.minecraft.item.Item;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

import javax.annotation.Nonnull;

@RegisterItem(name = "yellorium_ingot")
public class YelloriumIngot extends Item {
    
    @RegisterItem.Instance
    public static YelloriumIngot INSTANCE;
    
    @SuppressWarnings("unused")
    public YelloriumIngot(@Nonnull Properties properties) {
        super(properties);
    }
}
