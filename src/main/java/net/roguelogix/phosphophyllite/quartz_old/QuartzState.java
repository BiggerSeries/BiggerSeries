package net.roguelogix.phosphophyllite.quartz_old;

import net.roguelogix.phosphophyllite.quartz_old.internal.blocks.RenderStateBuilding;

import java.util.HashMap;
import java.util.Objects;

public class QuartzState {
    
    // yes yes, you *can* poke at it, *dont*
    public final HashMap<String, String> values = new HashMap<>();
    
    // oh yes, you can use *anything* you want
    // values are matched in order
    // if one is not found, matching is *stopped* and the last found one is used
    public <T> void with(T value) {
        values.put(value.getClass().getName().toLowerCase(), value.toString().toLowerCase());
    }
    
    public static int cache(QuartzState state) {
        return RenderStateBuilding.writeStateCache(state);
    }
    
    public static QuartzState fromCache(int id) {
        return RenderStateBuilding.readStateCache(id);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuartzState that = (QuartzState) o;
        return Objects.equals(values, that.values);
    }
    
    @Override
    public int hashCode() {
        return values.hashCode();
    }
}
