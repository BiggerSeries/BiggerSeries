package net.roguelogix.biggerreactors.items.ingots;

import net.minecraft.item.Item;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

import javax.annotation.Nonnull;

@RegisterItem(name = "ludicrite_ingot")
public class LudicriteIngot extends Item {
    
    @RegisterItem.Instance
    public static LudicriteIngot INSTANCE;
    
    @SuppressWarnings("unused")
    public LudicriteIngot(@Nonnull Properties properties) {
        super(properties);
    }
}
