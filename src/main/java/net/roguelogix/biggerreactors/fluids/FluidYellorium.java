package net.roguelogix.biggerreactors.fluids;

import net.minecraft.fluid.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.roguelogix.phosphophyllite.registry.PhosphophylliteFluid;
import net.roguelogix.phosphophyllite.registry.RegisterFluid;

@RegisterFluid(name = "fluid_yellorium", registerBucket = true)
public class FluidYellorium extends PhosphophylliteFluid {
    
    @RegisterFluid.Instance
    public static FluidYellorium INSTANCE;
    
    public FluidYellorium(Properties properties) {
        super(properties);
    }
}
