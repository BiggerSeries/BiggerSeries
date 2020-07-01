package net.roguelogix.phosphophyllite.registry;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//TODO: switch to something else where these values dont have to be constant
//      works fine for other registrations, because those dont change with configs
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterOre {
    
    /**
     * How many blocks are in each vein.
     */
    int size() default 8;
    
    /**
     * How many times per chunk this ore attempts to spawn.
     */
    int count() default 20;
    
    /**
     * The top spawn offset.
     */
    int offset() default 0;
    
    /**
     * The lowest Y level to spawn at (the bottom spawn offset).
     */
    int minLevel() default 0;
    
    /**
     * The highest Y level to spawn at.
     */
    int maxLevel() default 64;
    
    /**
     * If true, this ore will spawn in netherrack, rather than stone.
     */
    boolean isNetherOre() default false;
    
    /**
     * What biomes this ore can spawn in, by key/name. Defaults to ALL biomes, if omitted.
     *
     * @see net.minecraft.world.biome.Biomes for a list.
     */
    String[] spawnBiomes() default {};
}
