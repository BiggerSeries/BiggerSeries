package net.roguelogix.phosphophyllite.registry;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//TODO: switch to something else where these values dont have to be constant
//      works fine for other registrations, because those dont change with configs
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterOre {
    

}
