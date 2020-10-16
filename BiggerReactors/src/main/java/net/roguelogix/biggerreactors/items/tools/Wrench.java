package net.roguelogix.biggerreactors.items.tools;

import net.minecraft.item.Item;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

import javax.annotation.Nonnull;

@RegisterItem(name = "wrench")
public class Wrench extends Item {
    
    @RegisterItem.Instance
    public static Wrench INSTANCE;
    
    @SuppressWarnings("unused")
    public Wrench(@Nonnull Properties properties) {
        super(properties.maxStackSize(1));
    }
}
