package net.roguelogix.phosphophyllite.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static net.roguelogix.phosphophyllite.config.ConfigFormat.JSON5;
import static net.roguelogix.phosphophyllite.config.ConfigType.COMMON;


@Retention(RetentionPolicy.RUNTIME)
public @interface PhosphophylliteConfig {
    
    String name() default "";
    
    String folder() default "";
    
    String comment() default "";
    
    ConfigFormat format() default JSON5;
    
    ConfigType type() default COMMON;
    
    boolean reloadable() default false;
    
    @Retention(RetentionPolicy.RUNTIME)
    @interface Value {
        
        String comment() default "";
        
        boolean commentDefaultValue() default true;
        
        // only used for numbers
        String range() default "(,)";
        
        // only used for enums
        String[] allowedValues() default {};
        
        boolean advanced() default false;
        
        boolean hidden() default false;
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @interface EnableAdvanced {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @interface PreLoad {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @interface Load {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @interface PostLoad {
    }
}

