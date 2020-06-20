package net.roguelogix.phosphophyllite;

import net.minecraftforge.fml.common.Mod;
import net.roguelogix.phosphophyllite.config.ConfigLoader;
import net.roguelogix.phosphophyllite.quartz.Quartz;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Phosphophyllite.modid)
public class Phosphophyllite {
    public static final String modid = "phosphophyllite";

    public static final Logger LOGGER = LogManager.getLogger("Phosphophyllite/Main");

    public Phosphophyllite() {
        Quartz.onModLoad();
    }
}
