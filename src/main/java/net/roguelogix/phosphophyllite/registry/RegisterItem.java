package net.roguelogix.phosphophyllite.registry;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterItem {
    
    String name();
    
    boolean creativeTab() default true;
    
    String tag() default "";
    
    @Retention(RetentionPolicy.RUNTIME)
    @interface Instance {
    
    }
}
