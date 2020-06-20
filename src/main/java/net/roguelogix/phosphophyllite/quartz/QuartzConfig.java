package net.roguelogix.phosphophyllite.quartz;


import net.roguelogix.phosphophyllite.config.PhosphophylliteConfig;
import net.roguelogix.phosphophyllite.quartz_old.internal.OperationMode;
import net.roguelogix.phosphophyllite.registry.RegisterConfig;

import static net.roguelogix.phosphophyllite.Phosphophyllite.modid;

@RegisterConfig
@PhosphophylliteConfig(
        folder = modid,
        comment = "Config for the Quartz render system"
)
public class QuartzConfig {
    @PhosphophylliteConfig.Value
    OperationMode MaxOpMode = OperationMode.GL45;
}
