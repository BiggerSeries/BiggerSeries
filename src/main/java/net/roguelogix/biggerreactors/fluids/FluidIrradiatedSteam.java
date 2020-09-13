package net.roguelogix.biggerreactors.fluids;

import net.roguelogix.phosphophyllite.registry.PhosphophylliteFluid;
import net.roguelogix.phosphophyllite.registry.RegisterFluid;

@RegisterFluid(name = "fluid_irradiated_steam")
public class FluidIrradiatedSteam extends PhosphophylliteFluid {
    
    @RegisterFluid.Instance
    public static FluidIrradiatedSteam INSTANCE;
    
    public FluidIrradiatedSteam(Properties properties) {
        super(properties);
    }
}
