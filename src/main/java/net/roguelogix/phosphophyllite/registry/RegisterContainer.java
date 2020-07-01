package net.roguelogix.phosphophyllite.registry;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterContainer {
    
    String name();
    
    @Retention(RetentionPolicy.RUNTIME)
    @interface Instance {
    
    }
}
