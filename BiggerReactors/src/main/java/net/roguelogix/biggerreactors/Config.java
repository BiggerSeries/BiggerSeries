package net.roguelogix.biggerreactors;

import net.roguelogix.phosphophyllite.config.PhosphophylliteConfig;
import net.roguelogix.phosphophyllite.registry.RegisterConfig;

@SuppressWarnings({"FieldMayBeFinal", "unused"})
@RegisterConfig
@PhosphophylliteConfig
public class Config {
    
    @PhosphophylliteConfig.EnableAdvanced
    @PhosphophylliteConfig.Value(hidden = true)
    private static boolean EnableAdvancedConfig = false;
    
    @PhosphophylliteConfig
    public static class WorldGen {
        @PhosphophylliteConfig.Value(range = "[1,)")
        public static int YelloriteOreMaxClustersPerChunk = 5;
        @PhosphophylliteConfig.Value(range = "[1,)")
        public static int YelloriteMaxOrePerCluster = 10;
        @PhosphophylliteConfig.Value(range = "[5,)")
        public static int YelloriteOreMaxSpawnY = 50;
        @PhosphophylliteConfig.Value
        public static boolean EnableYelloriteGeneration = true;
    }

    @PhosphophylliteConfig
    public static class Reactor {
        //TODO: remove max, its only there because of the render system
        //      multiblock system can take *much* larger structures
        @PhosphophylliteConfig.Value(range = "[3,192]")
        public static int MaxLength = 128;
        @PhosphophylliteConfig.Value(range = "[3,192]")
        public static int MaxWidth = 128;
        @PhosphophylliteConfig.Value(range = "[3,256]")
        public static int MaxHeight = 192;

        @PhosphophylliteConfig.Value(range = "(0,)")
        public static float FuelUsageMultiplier = 1;
        @PhosphophylliteConfig.Value(range = "(0,)")
        public static float OutputMultiplier = 1.0f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float PassiveOutputMultiplier = 0.5f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float ActiveOutputMultiplier = 1.0f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float AmbientTemperature = 20.0f;
        @PhosphophylliteConfig.Value(range = "[1,)", advanced = true)
        public static long PerFuelRodCapacity = 4000;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float FuelFertilityMinimumDecay = 0.1f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float FuelFertilityDecayDenominator = 20;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float FuelFertilityDecayDenominatorInactiveMultiplier = 200;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float FuelReactivity = 1.05f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static double FissionEventsPerFuelUnit = 0.01f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float FEPerRadiationUnit = 10f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float FuelPerRadiationUnit = 0.0007f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static long IrradiationDistance = 4;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float FuelHardnessDivisor = 1f;
        @PhosphophylliteConfig.Value(range = "[0,1]", advanced = true)
        public static float FuelAbsorptionCoefficient = 0.5f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float FuelModerationFactor = 1.5f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static double FEPerCentigradePerUnitVolume = 10.0f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float FuelToCasingTransferCoefficientMultiplier = 1.0f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float CasingToCoolantSystemCoefficientMultiplier = 0.6f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float HeatLossCoefficientMultiplier = 0.001f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float PassiveCoolingTransferEfficiency = 0.2f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float CasingHeatTransferCoefficient = 0.6f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static long PassiveBatteryPerExternalBlock = 10_000;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static long FuelMBPerIngot = 1000;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static long CoolantTankAmountPerExternalBlock = 100;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float CoolantBoilingPoint = 100;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float CoolantVaporizationEnergy = 4;
        @PhosphophylliteConfig.Value(range = "(0,1]", advanced = true)
        public static float RadIntensityScalingMultiplier = 0.95f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float RadIntensityScalingRateExponentMultiplier = 1.2f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float RadIntensityScalingShiftMultiplier = 1f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float RadPenaltyShiftMultiplier = 15f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float RadPenaltyRateMultiplier = 2.5f;
        @PhosphophylliteConfig.Value(range = "(0,1]", advanced = true)
        public static float FuelAbsorptionScalingMultiplier = 0.95f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float FuelAbsorptionScalingShiftMultiplier = 1f;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static float FuelAbsorptionScalingRateExponentMultiplier = 2.2f;

        @PhosphophylliteConfig
        public static class GUI {
            @PhosphophylliteConfig.Value
            public static long HeatDisplayMax = 2000;
        }
    }

    @PhosphophylliteConfig
    public static class Turbine {
        @PhosphophylliteConfig.Value(range = "[5,192]")
        public static int MaxLength = 32;
        @PhosphophylliteConfig.Value(range = "[5,192]")
        public static int MaxWidth = 32;
        @PhosphophylliteConfig.Value(range = "[4,256]")
        public static int MaxHeight = 192;

        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static long FluidPerBlade = 25;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static long FlowRatePerBlock = 500;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static long TankVolumePerBlock = 1000;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static long SteamCondensationEnergy = 10;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static long RotorMassPerPart = 10;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static double MassDragMultiplier = 0.01;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static double BladeDragMultiplier = 0.000025;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static double CoilDragMultiplier = 1;
        @PhosphophylliteConfig.Value(range = "(0,)", advanced = true)
        public static long BatterySizePerCoilBlock = 30_000;
    }
    @PhosphophylliteConfig
    public static class CyaniteReprocessor {
        @PhosphophylliteConfig.Value(range = "(0,)", comment = "Max transfer rate of fluids and energy.")
        public static int TransferRate = 500;
        @PhosphophylliteConfig.Value(range = "(0,)", comment = "Max energy capacity.")
        public static int EnergyTankCapacity = 5000;
        @PhosphophylliteConfig.Value(range = "(0,)", comment = "Max water capacity")
        public static int WaterTankCapacity = 5000;
        @PhosphophylliteConfig.Value(range = "(0,)", comment = "Power usage per tick of work.")
        public static int EnergyConsumptionPerTick = 1;
        @PhosphophylliteConfig.Value(range = "(0,)", comment = "Water usage per tick of work.")
        public static int WaterConsumptionPerTick = 1;
        @PhosphophylliteConfig.Value(range = "(0,)", comment = "Time (in ticks) it takes to complete a job.")
        public static int TotalWorkTime = 200;
    }
}
