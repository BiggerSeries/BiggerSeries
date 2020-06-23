package net.roguelogix.biggerreactors.items.ingots;

import net.minecraft.item.Item;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

@RegisterItem(name = "cyanite_ingot")
public class CyaniteIngot extends Item {

    @RegisterItem.Instance
    CyaniteIngot INSTANCE;

    public CyaniteIngot(Properties properties) {
        super(properties);
    }
}
