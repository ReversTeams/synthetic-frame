package net.reversteam.synthetic_frame.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class SFConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    
    // Base values
    public static final ForgeConfigSpec.BooleanValue BASE_SYNTHETIC;
    public static final ForgeConfigSpec.DoubleValue BASE_CAPACITY;
    public static final ForgeConfigSpec.DoubleValue BASE_OVERCHARGE_TIME; // minutes
    public static final ForgeConfigSpec.DoubleValue BASE_ACTIVE_DRAIN_PER_EXHAUSTION;
    public static final ForgeConfigSpec.DoubleValue BASE_PASSIVE_DRAIN_PER_TICK;

    // Exponents
    public static final ForgeConfigSpec.DoubleValue ACTIVE_DRAIN_EXPONENT;
    public static final ForgeConfigSpec.DoubleValue PASSIVE_DRAIN_EXPONENT;

    // Behaviour values
    public static final ForgeConfigSpec.LongValue HEAL_COST;
    public static final ForgeConfigSpec.DoubleValue HEAL_AMOUNT;
    public static final ForgeConfigSpec.DoubleValue LOW_CHARGE_HEAL_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue LOW_CHARGE_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue ITEM_CHARGE_RATE; // % of max charge per second

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        
        builder.comment("Base values for Synthetic Frames").push("base_values");
        BASE_SYNTHETIC = builder.comment("Should players spawn as synthetic frames by default?").define("base_synthetic", false);
        BASE_CAPACITY = builder.comment("Base capacity of a synthetic frame in FE.").defineInRange("base_capacity", 100000.0, 1.0, Double.MAX_VALUE);
        BASE_OVERCHARGE_TIME = builder.comment("Base overcharge time in minutes.").defineInRange("base_overcharge_time", 60.0, 0.0, Double.MAX_VALUE);
        BASE_ACTIVE_DRAIN_PER_EXHAUSTION = builder.comment("Base FE drained per 1.0 exhaustion.").defineInRange("base_active_drain_per_exhaustion", 100.0, 0.0, Double.MAX_VALUE);
        BASE_PASSIVE_DRAIN_PER_TICK = builder.comment("Base FE drained per tick (AFK).").defineInRange("base_passive_drain_per_tick", 2.0, 0.0, Double.MAX_VALUE);
        builder.pop();

        builder.comment("Exponents for sub-linear scaling").push("exponents");
        ACTIVE_DRAIN_EXPONENT = builder.comment("Exponent for active drain scaling (0.5 = square root).").defineInRange("active_drain_exponent", 0.5, 0.0, 1.0);
        PASSIVE_DRAIN_EXPONENT = builder.comment("Exponent for passive drain scaling (0.25 = fourth root).").defineInRange("passive_drain_exponent", 0.25, 0.0, 1.0);
        builder.pop();

        builder.comment("Behaviour values").push("behaviour_values");
        HEAL_COST = builder.comment("FE cost to heal per tick.").defineInRange("heal_cost", 20L, 0L, Long.MAX_VALUE);
        HEAL_AMOUNT = builder.comment("Amount of health healed per tick when regenerating.").defineInRange("heal_amount", 0.1, 0.0, Double.MAX_VALUE);
        LOW_CHARGE_HEAL_MULTIPLIER = builder.comment("Heal multiplier when at low charge.").defineInRange("low_charge_heal_multiplier", 0.2, 0.0, 1.0);
        LOW_CHARGE_THRESHOLD = builder.comment("Threshold for low charge (e.g., 0.10 = 10%).").defineInRange("low_charge_threshold", 0.10, 0.0, 1.0);
        ITEM_CHARGE_RATE = builder.comment("Percentage of max charge per second used to trickle-charge items.").defineInRange("item_charge_rate", 0.1, 0.0, 1.0);
        builder.pop();

        COMMON_SPEC = builder.build();
    }
}