package net.roguelogix.phosphophyllite.registry;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This is specific for Phosphophyllite configs
 * All this does is tell the config loader that this is the root of a config, and to start loading from here
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterConfig {
}
