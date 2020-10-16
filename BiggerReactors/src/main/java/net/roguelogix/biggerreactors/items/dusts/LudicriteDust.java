package net.roguelogix.biggerreactors.items.dusts;

import net.minecraft.item.Item;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

import javax.annotation.Nonnull;

@RegisterItem(name = "ludicrite_dust")
public class LudicriteDust extends Item {
    
    @RegisterItem.Instance
    public static LudicriteDust INSTANCE;
    
    @SuppressWarnings("unused")
    public LudicriteDust(@Nonnull Properties properties) {
        super(properties);
    }
}