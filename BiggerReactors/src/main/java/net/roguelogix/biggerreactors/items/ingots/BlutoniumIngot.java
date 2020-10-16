package net.roguelogix.biggerreactors.items.ingots;

import net.minecraft.item.Item;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

import javax.annotation.Nonnull;

@RegisterItem(name = "blutonium_ingot")
public class BlutoniumIngot extends Item {
    
    @RegisterItem.Instance
    public static BlutoniumIngot INSTANCE;
    
    @SuppressWarnings("unused")
    public BlutoniumIngot(@Nonnull Properties properties) {
        super(properties);
    }
}
