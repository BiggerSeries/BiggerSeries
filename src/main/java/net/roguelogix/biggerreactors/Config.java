package net.roguelogix.biggerreactors;

import net.roguelogix.biggerreactors.classic.reactor.ReactorModeratorRegistry;
import net.roguelogix.phosphophyllite.config.PhosphophylliteConfig;
import net.roguelogix.phosphophyllite.registry.RegisterConfig;

@RegisterConfig
@PhosphophylliteConfig
public class Config {
    
    @PhosphophylliteConfig.Value(min = 1)
    public static int YelloriteOreMaxClustersPerChunk = 5;
    @PhosphophylliteConfig.Value(min = 1)
    public static int YelloriteMaxOrePerCluster = 10;
    @PhosphophylliteConfig.Value(min = 5)
    public static int YelloriteOreMaxSpawnY = 50;

    @PhosphophylliteConfig
    public static class Reactor {
        //TODO: remove max, its only there because of the render system
        //      multiblock system can take *much* larger structures
        @PhosphophylliteConfig.Value(min = 3, max = 32)
        public static int MaxLength = 32;
        @PhosphophylliteConfig.Value(min = 3, max = 32)
        public static int MaxWidth = 32;
        @PhosphophylliteConfig.Value(min = 3, max = 48)
        public static int MaxHeight = 48;
        
        @PhosphophylliteConfig.Value(min = 0)
        public static float FuelUsageMultiplier = 1;
        @PhosphophylliteConfig.Value(min = 0)
        public static float PowerOutputMultiplier = 0.5f;
        @PhosphophylliteConfig.Value(min = 0)
        public static float AmbientTemperature = 20.0f;
        @PhosphophylliteConfig.Value(min = 1)
        public static long PerFuelRodCapacity = 4000;
        @PhosphophylliteConfig.Value(min = 0)
        public static float FuelFertilityMinimumDecay = 0.1f;
        @PhosphophylliteConfig.Value(min = 0)
        public static float FuelFertilityDecayDenominator = 20;
        @PhosphophylliteConfig.Value(min = 0)
        public static float FuelFertilityDecayDenominatorInactiveMultiplier = 200;
        @PhosphophylliteConfig.Value(min = 0)
        public static float FuelReactivity = 1.05f;
        @PhosphophylliteConfig.Value(min = 0)
        public static double FissionEventsPerFuelUnit = 0.01f;
        @PhosphophylliteConfig.Value(min = 0)
        public static float FEPerRadiationUnit = 10f;
        @PhosphophylliteConfig.Value(min = 0)
        public static float FuelPerRadiationUnit = 0.0007f;
        @PhosphophylliteConfig.Value(min = 0)
        public static long IrradiationDistance = 4;
        @PhosphophylliteConfig.Value(min = 0)
        public static float FuelHardnessDivisor = 1f;
        @PhosphophylliteConfig.Value(min = 0, max = 1)
        public static float FuelAbsorptionCoefficient = 0.5f;
        @PhosphophylliteConfig.Value(min = 1)
        public static float FuelModerationFactor = 1.5f;
        @PhosphophylliteConfig.Value(min = 0)
        public static double FEPerCentigradePerUnitVolume = 10.0f;
        @PhosphophylliteConfig.Value(min = 0)
        public static float FuelToCasingTransferCoefficientMultiplier = 1.0f;
        @PhosphophylliteConfig.Value(min = 0)
        public static float CasingToCoolantSystemCoefficientMultiplier = 0.6f;
        @PhosphophylliteConfig.Value(min = 0)
        public static float HeatLossCoefficientMultiplier = 0.001f;
        @PhosphophylliteConfig.Value(min = 0)
        public static float PassiveCoolingTransferEfficiency = 0.2f;
        @PhosphophylliteConfig.Value(min = 0)
        public static float CasingHeatTransferCoefficient = 0.6f;
        @PhosphophylliteConfig.Value(min = 0)
        public static float FuelRodHeatTransferCoefficient = 1f;
        @PhosphophylliteConfig.Value(min = 0)
        public static long PassiveBatterySize = 10_000_000;
        @PhosphophylliteConfig.Value(min = 1)
        public static long FuelMBPerIngot = 1000;
        @PhosphophylliteConfig.Value(min = 0)
        public static long MaxActiveTankSize = 50_000;
        @PhosphophylliteConfig.Value(min = 0)
        public static long CoolantTankAmountPerExternalBlock = 100;
        @PhosphophylliteConfig.Value(min = 0)
        public static float CoolantBoilingPoint = 100;
        @PhosphophylliteConfig.Value(min = 0)
        public static float CoolantVaporizationEnergy = 4;
        @PhosphophylliteConfig.Value(min = 0, max = 1)
        public static float RadIntensityScalingMultiplier = 0.95f;
        @PhosphophylliteConfig.Value(min = 0)
        public static float RadIntensityScalingRateExponentMultiplier = 1.2f;
        @PhosphophylliteConfig.Value(min = 1)
        public static float RadIntensityScalingShiftMultiplier = 1f;
        @PhosphophylliteConfig.Value(min = 0)
        public static float RadPenaltyShiftMultiplier = 15f;
        @PhosphophylliteConfig.Value(min = 0)
        public static float RadPenaltyRateMultiplier = 2.5f;
        @PhosphophylliteConfig.Value(min = 0, max = 1)
        public static float FuelAbsorptionScalingMultiplier = 0.95f;
        @PhosphophylliteConfig.Value(min = 1)
        public static float FuelAbsorptionScalingShiftMultiplier = 1f;
        @PhosphophylliteConfig.Value(min = 0)
        public static float FuelAbsorptionScalingRateExponentMultiplier = 2.2f;
    }
    
    @PhosphophylliteConfig.Value(min = 5, max = 32)
    public static int TurbineMaxLength = 32;
    @PhosphophylliteConfig.Value(min = 5, max = 32)
    public static int TurbineMaxWidth = 32;
    @PhosphophylliteConfig.Value(min = 3, max = 48)
    public static int TurbineMaxHeight = 48;

    @PhosphophylliteConfig.Value(min = 1, comment = "Max energy transfer rate of machines.")
    public static int MachineEnergyTransferRate = 500;

    @PhosphophylliteConfig.Value(min = 1, comment = "Max energy capacity of machines.")
    public static int MachineEnergyTankCapacity = 5000;
    @PhosphophylliteConfig.Value(min = 1, comment = "Max fluid capacity of machines")
    public static int MachineFluidTankCapacity = 5000;

    @PhosphophylliteConfig
    public static class CyaniteReprocessor {
        @PhosphophylliteConfig.Value(min = 0, comment = "Power usage per tick of work.")
        public static int CyaniteReprocessorPowerConsumption = 1;
        @PhosphophylliteConfig.Value(min = 0, comment = "Water usage per tick of work.")
        public static int CyaniteReprocessorWaterConsumption = 1;
        @PhosphophylliteConfig.Value(min = 0, comment = "Time (in ticks) it takes to complete a job.")
        public static int CyaniteReprocessorWorkTime = 200;
    }

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
