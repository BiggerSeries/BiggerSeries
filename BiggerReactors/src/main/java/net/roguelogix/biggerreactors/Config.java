package net.roguelogix.biggerreactors;

import net.minecraft.block.Block;
import net.minecraft.tags.ITagCollection;
import net.roguelogix.biggerreactors.classic.reactor.ReactorModeratorRegistry;
import net.roguelogix.biggerreactors.classic.turbine.TurbineCoilRegistry;
import net.roguelogix.phosphophyllite.config.PhosphophylliteConfig;
import net.roguelogix.phosphophyllite.registry.RegisterConfig;

@RegisterConfig
@PhosphophylliteConfig
public class Config {
    
    @PhosphophylliteConfig
    public static class WorldGen {
        @PhosphophylliteConfig.Value(min = 1)
        public static int YelloriteOreMaxClustersPerChunk = 5;
        @PhosphophylliteConfig.Value(min = 1)
        public static int YelloriteMaxOrePerCluster = 10;
        @PhosphophylliteConfig.Value(min = 5)
        public static int YelloriteOreMaxSpawnY = 50;
        @PhosphophylliteConfig.Value
        public static boolean EnableYelloriteGeneration = true;
    }
    
    @PhosphophylliteConfig
    public static class Reactor {
        //TODO: remove max, its only there because of the render system
        //      multiblock system can take *much* larger structures
        @PhosphophylliteConfig.Value(min = 3, max = 192)
        public static int MaxLength = 64;
        @PhosphophylliteConfig.Value(min = 3, max = 192)
        public static int MaxWidth = 64;
        @PhosphophylliteConfig.Value(min = 3, max = 256)
        public static int MaxHeight = 96;
        
        @PhosphophylliteConfig.Value(min = 0)
        public static float FuelUsageMultiplier = 1;
        @PhosphophylliteConfig.Value(min = 0)
        public static float OutputMultiplier = 1.0f;
        @PhosphophylliteConfig.Value(min = 0)
        public static float PassiveOutputMultiplier = 0.5f;
        @PhosphophylliteConfig.Value(min = 0)
        public static float ActiveOutputMultiplier = 1.0f;
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
        
        @PhosphophylliteConfig
        public static class GUI {
            @PhosphophylliteConfig.Value
            public static long HeatDisplayMax = 2000;
        }
    }
    
    @PhosphophylliteConfig
    public static class Turbine {
        @PhosphophylliteConfig.Value(min = 5, max = 192)
        public static int MaxLength = 32;
        @PhosphophylliteConfig.Value(min = 5, max = 192)
        public static int MaxWidth = 32;
        @PhosphophylliteConfig.Value(min = 4, max = 256)
        public static int MaxHeight = 48;
        
        @PhosphophylliteConfig.Value(min = 1)
        public static long TankSize = 10000;
        @PhosphophylliteConfig.Value(min = 1)
        public static long MaxFlow = 5000;
        @PhosphophylliteConfig.Value(min = 1)
        public static long FluidPerBlade = 25;
        @PhosphophylliteConfig.Value(min = 1)
        public static long SteamCondensationEnergy = 10;
        @PhosphophylliteConfig.Value(min = 1)
        public static long RotorMassPerPart = 10;
        @PhosphophylliteConfig.Value(min = 0)
        public static double MassDragMultiplier = 0.01;
        @PhosphophylliteConfig.Value(min = 0)
        public static double BladeDragMultiplier = 0.000025;
        @PhosphophylliteConfig.Value(min = 0)
        public static double CoilDragMultiplier = 1;
        @PhosphophylliteConfig.Value(min = 0)
        public static long BatterySize = 2_500_000;
    }
    @PhosphophylliteConfig
    public static class CyaniteReprocessor {
        @PhosphophylliteConfig.Value(min = 1, comment = "Max transfer rate of fluids and energy.")
        public static int TransferRate = 500;
        @PhosphophylliteConfig.Value(min = 1, comment = "Max energy capacity.")
        public static int EnergyTankCapacity = 5000;
        @PhosphophylliteConfig.Value(min = 1, comment = "Max water capacity")
        public static int WaterTankCapacity = 5000;
        @PhosphophylliteConfig.Value(min = 0, comment = "Power usage per tick of work.")
        public static int EnergyConsumptionPerTick = 1;
        @PhosphophylliteConfig.Value(min = 0, comment = "Water usage per tick of work.")
        public static int WaterConsumptionPerTick = 1;
        @PhosphophylliteConfig.Value(min = 0, comment = "Time (in ticks) it takes to complete a job.")
        public static int TotalWorkTime = 200;
    }
    
    @PhosphophylliteConfig
    public static class ReactorModeratorConfigValues {
        public enum LocationType {
            REGISTRY,
            TAG
        }
        
        @PhosphophylliteConfig.Value
        public final String location;
        @PhosphophylliteConfig.Value
        public final LocationType locationType;
        @PhosphophylliteConfig.Value
        public final float absorption;
        @PhosphophylliteConfig.Value
        public final float heatEfficiency;
        @PhosphophylliteConfig.Value
        public final float moderation;
        @PhosphophylliteConfig.Value
        public final float conductivity;
    
        @SuppressWarnings("unused")
        ReactorModeratorConfigValues() {
            location = null;
            locationType = null;
            absorption = 0;
            heatEfficiency = 0;
            moderation = 0;
            conductivity = 0;
        }
        
        public ReactorModeratorConfigValues(String location, LocationType locationType, float absorption, float heatEfficiency, float moderation, float conductivity) {
            this.location = location;
            this.locationType = locationType;
            this.absorption = absorption;
            this.heatEfficiency = heatEfficiency;
            this.moderation = moderation;
            this.conductivity = conductivity;
        }
    }
    
    // todo: blocks to add
    //      brass?
    //      fluxed electrum (lemming may port RA, unsure)
    //      thermal fluids (dont exist yet)
    @PhosphophylliteConfig.Value
    public static ReactorModeratorConfigValues[] reactorModerators = new ReactorModeratorConfigValues[]{
            new ReactorModeratorConfigValues("minecraft:air", ReactorModeratorConfigValues.LocationType.REGISTRY, 0.1f, 0.25f, 1.1f, 0.05f),
            new ReactorModeratorConfigValues("minecraft:void_air", ReactorModeratorConfigValues.LocationType.REGISTRY, 0.1f, 0.25f, 1.1f, 0.05f),
            new ReactorModeratorConfigValues("minecraft:cave_air", ReactorModeratorConfigValues.LocationType.REGISTRY, 0.1f, 0.25f, 1.1f, 0.05f),
            
            new ReactorModeratorConfigValues("minecraft:iron_block", ReactorModeratorConfigValues.LocationType.REGISTRY, 0.5f, 0.75f, 1.4f, 0.6f),
            new ReactorModeratorConfigValues("minecraft:gold_block", ReactorModeratorConfigValues.LocationType.REGISTRY, 0.52f, 0.8f, 1.45f, 2f),
            new ReactorModeratorConfigValues("minecraft:diamond_block", ReactorModeratorConfigValues.LocationType.REGISTRY, 0.55f, 0.85f, 1.5f, 3f),
            new ReactorModeratorConfigValues("minecraft:emerald_block", ReactorModeratorConfigValues.LocationType.REGISTRY, 0.55f, 0.85f, 1.5f, 2.5f),
            new ReactorModeratorConfigValues("minecraft:glass", ReactorModeratorConfigValues.LocationType.REGISTRY, 0.2f, 0.25f, 1.1f, 0.3f),
            new ReactorModeratorConfigValues("minecraft:ice", ReactorModeratorConfigValues.LocationType.REGISTRY, 0.33f, 0.33f, 1.15f, 0.1f),
            new ReactorModeratorConfigValues("minecraft:snow_block", ReactorModeratorConfigValues.LocationType.REGISTRY, 0.15f, 0.33f, 1.05f, 0.05f),
            
            new ReactorModeratorConfigValues("forge:storage_blocks/copper", ReactorModeratorConfigValues.LocationType.TAG, 0.5f, 0.75f, 1.4f, 1f),
            new ReactorModeratorConfigValues("forge:storage_blocks/osmium", ReactorModeratorConfigValues.LocationType.TAG, 0.51f, 0.77f, 1.41f, 1f),
            new ReactorModeratorConfigValues("forge:storage_blocks/bronze", ReactorModeratorConfigValues.LocationType.TAG, 0.51f, 0.77f, 1.41f, 1f),
            new ReactorModeratorConfigValues("forge:storage_blocks/zinc", ReactorModeratorConfigValues.LocationType.TAG, 0.51f, 0.77f, 1.41f, 1f),
            new ReactorModeratorConfigValues("forge:storage_blocks/aluminum", ReactorModeratorConfigValues.LocationType.TAG, 0.5f, 0.78f, 1.42f, 0.6f),
            new ReactorModeratorConfigValues("forge:storage_blocks/steel", ReactorModeratorConfigValues.LocationType.TAG, 0.5f, 0.78f, 1.42f, 0.6f),
            new ReactorModeratorConfigValues("forge:storage_blocks/invar", ReactorModeratorConfigValues.LocationType.TAG, 0.5f, 0.79f, 1.43f, 0.6f),
            new ReactorModeratorConfigValues("forge:storage_blocks/silver", ReactorModeratorConfigValues.LocationType.TAG, 0.51f, 0.79f, 1.43f, 1.5f),
            new ReactorModeratorConfigValues("forge:storage_blocks/lead", ReactorModeratorConfigValues.LocationType.TAG, 0.75f, 0.75f, 1.75f, 1.5f),
            new ReactorModeratorConfigValues("forge:storage_blocks/electrum", ReactorModeratorConfigValues.LocationType.TAG, 0.75f, 0.75f, 1.75f, 1.5f),
            new ReactorModeratorConfigValues("forge:storage_blocks/platinum", ReactorModeratorConfigValues.LocationType.TAG, 0.53f, 0.86f, 1.58f, 1.5f),
            new ReactorModeratorConfigValues("forge:storage_blocks/enderium", ReactorModeratorConfigValues.LocationType.TAG, 0.53f, 0.88f, 1.6f, 3f),
            
            new ReactorModeratorConfigValues("minecraft:water", ReactorModeratorConfigValues.LocationType.REGISTRY, 0.33f, 0.5f, 1.33f, 0.1f),
            
            new ReactorModeratorConfigValues("biggerreactors:graphite_block", ReactorModeratorConfigValues.LocationType.REGISTRY, 0.1f, 0.5f, 2f, 2f),
    };
    
    @PhosphophylliteConfig
    public static class TurbineCoilConfigValues {
        
        public enum LocationType {
            REGISTRY,
            TAG
        }
    
        @PhosphophylliteConfig.Value
        public final String location;
        @PhosphophylliteConfig.Value
        public final LocationType locationType;
        @PhosphophylliteConfig.Value
        public final double efficiency;
        @PhosphophylliteConfig.Value
        public final double extractionRate;
        @PhosphophylliteConfig.Value
        public final double bonus;
    
        @SuppressWarnings("unused")
        TurbineCoilConfigValues() {
            location = null;
            locationType = null;
            efficiency = 0;
            bonus = 0;
            extractionRate = 0;
        }
        
        public TurbineCoilConfigValues(String location, LocationType locationType, double efficiency, double extractionRate, double bonus) {
            this.location = location;
            this.locationType = locationType;
            this.efficiency = efficiency;
            this.extractionRate = extractionRate;
            this.bonus = bonus;
        }
    }
    
    
    // todo: blocks to add
    //      brass?
    //      fluxed electrum (lemming may port RA, unsure)
    @PhosphophylliteConfig.Value
    public static TurbineCoilConfigValues[] turbineCoils = new TurbineCoilConfigValues[]{
            new TurbineCoilConfigValues("minecraft:iron_block", TurbineCoilConfigValues.LocationType.REGISTRY, 0.33, 0.1, 1),
            new TurbineCoilConfigValues("minecraft:gold_block", TurbineCoilConfigValues.LocationType.REGISTRY, 0.66, 0.175, 1),
            
            new TurbineCoilConfigValues("biggerreactors:ludicrite_block", TurbineCoilConfigValues.LocationType.REGISTRY, 1.155, 0.35, 1.02),
            
            new TurbineCoilConfigValues("forge:storage_blocks/copper", TurbineCoilConfigValues.LocationType.TAG, 0.396, 0.12, 1),
            new TurbineCoilConfigValues("forge:storage_blocks/osmium", TurbineCoilConfigValues.LocationType.TAG, 0.462, 0.12, 1),
            new TurbineCoilConfigValues("forge:storage_blocks/aluminum", TurbineCoilConfigValues.LocationType.TAG, 0.495, 0.13, 1),
            new TurbineCoilConfigValues("forge:storage_blocks/steel", TurbineCoilConfigValues.LocationType.TAG, 0.495, 0.13, 1),
            new TurbineCoilConfigValues("forge:storage_blocks/invar", TurbineCoilConfigValues.LocationType.TAG, 0.495, 0.14, 1),
            new TurbineCoilConfigValues("forge:storage_blocks/silver", TurbineCoilConfigValues.LocationType.TAG, 0.561, 0.15, 1),
            new TurbineCoilConfigValues("forge:storage_blocks/electrum", TurbineCoilConfigValues.LocationType.TAG, 0.825, 0.2, 1),
            new TurbineCoilConfigValues("forge:storage_blocks/platinum", TurbineCoilConfigValues.LocationType.TAG, 0.99, 0.25, 1),
            new TurbineCoilConfigValues("forge:storage_blocks/enderium", TurbineCoilConfigValues.LocationType.TAG, 0.99, 0.3, 1.02),
    };
    
    public static void loadRegistries(ITagCollection<Block> blockTags) {
        ReactorModeratorRegistry.clearRegistry();
        for (ReactorModeratorConfigValues reactorModerator : reactorModerators) {
            ReactorModeratorRegistry.registerConfigValues(blockTags, reactorModerator);
        }
        
        TurbineCoilRegistry.clearRegistry();
        for (TurbineCoilConfigValues turbineCoil : turbineCoils) {
            TurbineCoilRegistry.registerConfigValues(blockTags, turbineCoil);
        }
    }
}
