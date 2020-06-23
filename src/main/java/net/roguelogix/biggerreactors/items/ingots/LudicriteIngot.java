package net.roguelogix.biggerreactors.items.ingots;

import net.minecraft.item.Item;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

@RegisterItem(name = "ludicrite_ingot")
public class LudicriteIngot extends Item {

    @RegisterItem.Instance
    LudicriteIngot INSTANCE;

    public LudicriteIngot(Properties properties) {
        super(properties);
    }
}
