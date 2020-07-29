package net.roguelogix.phosphophyllite;

import net.roguelogix.phosphophyllite.registry.RegisterConfig;

// ironic
@RegisterConfig
@net.roguelogix.phosphophyllite.config.PhosphophylliteConfig
public class PhosphophylliteConfig {
    @net.roguelogix.phosphophyllite.config.PhosphophylliteConfig
    public static class GUI{
        @net.roguelogix.phosphophyllite.config.PhosphophylliteConfig.Value(min = 50)
        public static long UpdateIntervalMS = 200;
    }
}
