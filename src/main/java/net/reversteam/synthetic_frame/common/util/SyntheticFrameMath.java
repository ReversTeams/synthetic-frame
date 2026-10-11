package net.reversteam.synthetic_frame.common.util;

import net.reversteam.synthetic_frame.common.config.SFConfig;

public class SyntheticFrameMath {

    static double calculateCapacityRatio(long maxEnergy) {
        return Math.max(1.0, (double) maxEnergy / SFConfig.BASE_CAPACITY.get());
    }

    // Passive drain
    public static long calculatePassiveDrain(long maxEnergy) {
        double capacityRatio = calculateCapacityRatio(maxEnergy);
        double passiveMultiplier = Math.pow(capacityRatio, SFConfig.PASSIVE_DRAIN_EXPONENT.get());
        return (long) Math.max(1, SFConfig.BASE_PASSIVE_DRAIN_PER_TICK.get() * passiveMultiplier);
        // Note: Returns FE per tick. Multiply by 20 for FE per second.
    }

    // Active drain (exhaustion hunger equivalent)
    public static long calculateActiveDrain(long maxEnergy, float exhaustion) {
        if (exhaustion <= 0.0f)
            return 0;
        double capacityRatio = calculateCapacityRatio(maxEnergy);
        double activeMultiplier = Math.pow(capacityRatio, SFConfig.ACTIVE_DRAIN_EXPONENT.get());
        return (long) (exhaustion * SFConfig.BASE_ACTIVE_DRAIN_PER_EXHAUSTION.get() * activeMultiplier);
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