package net.roguelogix.phosphophyllite.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static net.roguelogix.phosphophyllite.config.ConfigFormat.ROTN;
import static net.roguelogix.phosphophyllite.config.ConfigType.COMMON;


@Retention(RetentionPolicy.RUNTIME)
public @interface PhosphophylliteConfig {
    
    String name() default "";
    
    String folder() default "";
    
    String comment() default "";
    
    ConfigFormat format() default ROTN;
    
    ConfigType type() default COMMON;
    
    boolean reloadable() default false;
    
    @Retention(RetentionPolicy.RUNTIME)
    @interface Value {
        
        String comment() default "";
        
        // only used for numbers
        long max() default Long.MAX_VALUE;
        
        long min() default Long.MIN_VALUE;
        
        // only used for enums
        String[] allowedValues() default {};
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @interface OnLoad {
    }
}

