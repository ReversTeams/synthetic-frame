package net.reversteam.synthetic_frame.common.util;

public class SyntheticFrameMath {

    // TODO load from config

    // Base values
    public static final boolean BASE_SYNTHETIC = false;
    public static final double BASE_CAPACITY = 100_000.0;
    public static final double BASE_OVERCHARGE_TIME = 60; // minutes
    public static final double BASE_ACTIVE_DRAIN_PER_EXHAUSTION = 100.0;
    public static final double BASE_PASSIVE_DRAIN_PER_TICK = 2.0;

    // Exponents for sub-linear scaling
    public static final double ACTIVE_DRAIN_EXPONENT = 0.5; // Square root
    public static final double PASSIVE_DRAIN_EXPONENT = 0.25; // Fourth root

    // Behaviour values
    public static final long HEAL_COST = 20;
    public static final float HEAL_AMOUNT = 0.1f;
    public static final float LOW_CHARGE_HEAL_MULTIPLIER = 0.2f;
    public static final float LOW_CHARGE_THRESHOLD = 0.10f; // <10%

    static double calculateCapacityRatio(long maxEnergy) {
        return Math.max(1.0, (double) maxEnergy / BASE_CAPACITY);
    }

    // Passive drain
    public static long calculatePassiveDrain(long maxEnergy) {
        double capacityRatio = calculateCapacityRatio(maxEnergy);
        double passiveMultiplier = Math.pow(capacityRatio, PASSIVE_DRAIN_EXPONENT);
        return (long) Math.max(1, BASE_PASSIVE_DRAIN_PER_TICK * passiveMultiplier);
        // Note: Returns FE per tick. Multiply by 20 for FE per second.
    }

    // Active drain (exhaustion hunger equivalent)
    public static long calculateActiveDrain(long maxEnergy, float exhaustion) {
        if (exhaustion <= 0.0f)
            return 0;
        double capacityRatio = calculateCapacityRatio(maxEnergy);
        double activeMultiplier = Math.pow(capacityRatio, ACTIVE_DRAIN_EXPONENT);
        return (long) (exhaustion * BASE_ACTIVE_DRAIN_PER_EXHAUSTION * activeMultiplier);
    }

    // Time you can last with a full charge (minutes)
    public static double calculateTimeWithEnergy(long maxEnergy, double minutes) {
        long passiveDrainPerTick = calculatePassiveDrain(maxEnergy);
        return (passiveDrainPerTick > 0)
            ? (maxEnergy / (double) passiveDrainPerTick) / 1200.0 // 1 minute = 20 ticks/sec * 60 seconds = 1200 ticks
            : 0.0;
    }
    public static double calculateTimeWithEnergy(long maxEnergy) {
        return calculateTimeWithEnergy(maxEnergy, maxEnergy);
    }

    // Charge needed to last `minutes`
    public static long calculateEnergyForMinutes(long maxEnergy, double minutes) {
        long passiveDrainPerTick = calculatePassiveDrain(maxEnergy);
        long ticksPerMinute = 1200; // 20 ticks/sec * 60 seconds
        return (long) (passiveDrainPerTick * ticksPerMinute * minutes);
    }
}