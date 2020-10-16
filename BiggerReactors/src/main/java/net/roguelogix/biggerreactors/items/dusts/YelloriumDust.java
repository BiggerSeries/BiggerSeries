package net.roguelogix.biggerreactors.items.dusts;

import net.minecraft.item.Item;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

import javax.annotation.Nonnull;

@RegisterItem(name = "yellorium_dust")
public class YelloriumDust extends Item {
    
    @RegisterItem.Instance
    public static YelloriumDust INSTANCE;
    
    @SuppressWarnings("unused")
    public YelloriumDust(@Nonnull Properties properties) {
        super(properties);
    }
}