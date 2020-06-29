package net.roguelogix.biggerreactors.fluids;

import net.roguelogix.phosphophyllite.registry.PhosphophylliteFluid;
import net.roguelogix.phosphophyllite.registry.RegisterFluid;

@RegisterFluid(name = "fluid_irradiated_steam")
public class IrradiatedSteam extends PhosphophylliteFluid {
    
    @RegisterFluid.Instance
    public static IrradiatedSteam INSTANCE;
    
    public IrradiatedSteam(Properties properties) {
        super(properties);
    }
}
