package net.roguelogix.phosphophyllite;

import net.roguelogix.phosphophyllite.registry.RegisterConfig;

import static net.roguelogix.phosphophyllite.Phosphophyllite.modid;

// ironic
@RegisterConfig
@net.roguelogix.phosphophyllite.config.PhosphophylliteConfig(
        folder = modid,
        name = "general"
)
public class PhosphophylliteConfig {
    @net.roguelogix.phosphophyllite.config.PhosphophylliteConfig
    public static class GUI{
        @net.roguelogix.phosphophyllite.config.PhosphophylliteConfig.Value(min = 50)
        public static long UpdateIntervalMS = 200;
    }
}
