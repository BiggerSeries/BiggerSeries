package net.roguelogix.biggerreactors.items.tools;

import net.minecraft.item.Item;
import net.roguelogix.biggerreactors.items.ingots.CyaniteIngot;
import net.roguelogix.phosphophyllite.registry.RegisterItem;

@RegisterItem(name = "debug_tool")
public class DebugTool extends Item {

    @RegisterItem.Instance
    public static DebugTool INSTANCE;

    public DebugTool(Properties properties) {
        super(properties);
    }
}
