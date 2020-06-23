package net.roguelogix.biggerreactors.items.tools;

import net.minecraft.item.Item;
import net.roguelogix.biggerreactors.items.ingots.CyaniteIngot;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

@RegisterItem(name = "wrench")
public class Wrench extends Item {

    @RegisterItem.Instance
    Wrench INSTANCE;

    public Wrench(Properties properties) {
        super(properties);
    }
}
