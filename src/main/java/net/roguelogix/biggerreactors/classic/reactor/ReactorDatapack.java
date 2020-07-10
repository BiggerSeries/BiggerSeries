package net.roguelogix.biggerreactors.classic.reactor;

public class ReactorDatapack {

  // True if online, false if offline.
  public boolean reactorStatus;
  // True if active, false if passive.
  public boolean reactorType;

  // Energy stored in reactor.
  public long energyStored;
  // Max energy capacity of reactor.
  public long energyCapacity;

  // Case heat stored in reactor.
  public long caseHeatStored;
  // Max case heat capacity of reactor.
  public long caseHeatCapacity;

  // Core heat stored in reactor.
  public long coreHeatStored;
  // Max core heat capacity of reactor.
  public long coreHeatCapacity;

  // Fuel heat stored in reactor.
  public long fuelHeatStored;
  // Max fuel heat capacity of reactor.
  public long fuelHeatCapacity;

  // Waste stored in reactor.
  public long wasteStored;
  // Fuel stored in reactor.
  public long fuelStored;
  // Max fuel capacity of reactor.
  public long fuelCapacity;

  // Output rate of the reactor.
  public long reactorOutputRate;

  // Rate at which fuel is consumed (per tick).
  public float fuelUsageRate;
  // Rate at which reactions occur (per tick).
  public float reactivityRate;
}
