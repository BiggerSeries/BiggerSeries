package net.roguelogix.biggerreactors.items.dusts;

import net.minecraft.item.Item;
import net.roguelogix.biggerreactors.items.ingots.GraphiteIngot;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

import javax.annotation.Nonnull;

@RegisterItem(name = "blutonium_dust")
public class BlutoniumDust extends Item {
    
    @RegisterItem.Instance
    public static BlutoniumDust INSTANCE;
    
    @SuppressWarnings("unused")
    public BlutoniumDust(@Nonnull Properties properties) {
        super(properties);
    }
}