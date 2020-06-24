package net.roguelogix.biggerreactors;

import net.roguelogix.biggerreactors.classic.reactor.ReactorModeratorRegistry;
import net.roguelogix.phosphophyllite.config.PhosphophylliteConfig;
import net.roguelogix.phosphophyllite.registry.RegisterConfig;

@RegisterConfig
@PhosphophylliteConfig
public class Config {

    @PhosphophylliteConfig.Value(min = 1)
    public static final int YelloriteOreMaxClustersPerChunk = 5;
    @PhosphophylliteConfig.Value(min = 1)
    public static final int YelloriteMaxOrePerCluster = 10;
    @PhosphophylliteConfig.Value(min = 5)
    public static final int YelloriteOreMaxSpawnY = 50;


    //TODO: remove max, its only there because of the render system
    //      multiblock system can take *much* larger structures
    @PhosphophylliteConfig.Value(min = 3, max = 32)
    public static int ReactorMaxLength = 32;
    @PhosphophylliteConfig.Value(min = 3, max = 32)
    public static int ReactorMaxWidth = 32;
    @PhosphophylliteConfig.Value(min = 3, max = 48)
    public static int ReactorMaxHeight = 48;

    @PhosphophylliteConfig.Value(min = 1)
    public static float fuelUsageMultiplier;

    @PhosphophylliteConfig.Value(min = 5, max = 32)
    public static int TurbineMaxLength = 32;
    @PhosphophylliteConfig.Value(min = 5, max = 32)
    public static int TurbineMaxWidth = 32;
    @PhosphophylliteConfig.Value(min = 3, max = 48)
    public static int TurbineMaxHeight = 48;

    @PhosphophylliteConfig
    public static class ReactorModeratorConfigValues {
        @PhosphophylliteConfig.Value
        public final String location;
        @PhosphophylliteConfig.Value
        public final float absorption;
        @PhosphophylliteConfig.Value
        public final float heatEfficiency;
        @PhosphophylliteConfig.Value
        public final float moderation;
        @PhosphophylliteConfig.Value
        public final float conductivity;

        public ReactorModeratorConfigValues(String location, float absorption, float heatEfficiency, float moderation, float conductivity) {
            this.location = location;
            this.absorption = absorption;
            this.heatEfficiency = heatEfficiency;
            this.moderation = moderation;
            this.conductivity = conductivity;
        }
    }

    @PhosphophylliteConfig.Value
    public static ReactorModeratorConfigValues[] reactorModerators = new ReactorModeratorConfigValues[]{
            new ReactorModeratorConfigValues("minecraft:air", 0.1f, 0.25f, 1.1f, 0.05f),

            new ReactorModeratorConfigValues("minecraft:iron_block", 0.5f, 0.75f, 1.4f, 0.6f),
            new ReactorModeratorConfigValues("minecraft:gold_block", 0.52f, 0.8f, 1.45f, 2f),
            new ReactorModeratorConfigValues("minecraft:diamond_block", 0.55f, 0.85f, 1.5f, 3f),
            new ReactorModeratorConfigValues("minecraft:emerald_block", 0.55f, 0.85f, 1.5f, 2.5f),
            new ReactorModeratorConfigValues("minecraft:glass", 0.2f, 0.25f, 1.1f, 0.3f),
            new ReactorModeratorConfigValues("minecraft:ice", 0.33f, 0.33f, 1.15f, 0.1f),
            new ReactorModeratorConfigValues("minecraft:snow_block", 0.15f, 0.33f, 1.05f, 0.05f),

            new ReactorModeratorConfigValues("minecraft:water", 0.33f, 0.5f, 1.33f, 0.1f),

            new ReactorModeratorConfigValues("biggerreactors:graphite_block", 0.1f, 0.5f, 2f, 2f),
    };


    @PhosphophylliteConfig.OnLoad
    public static void onLoad() {
        for (ReactorModeratorConfigValues reactorModerator : reactorModerators) {
            ReactorModeratorRegistry.registerBlock(reactorModerator.location, reactorModerator.absorption, reactorModerator.heatEfficiency, reactorModerator.moderation, reactorModerator.conductivity);
        }
    }
}
