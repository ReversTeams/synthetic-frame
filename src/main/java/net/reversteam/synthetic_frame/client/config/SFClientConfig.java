package net.reversteam.synthetic_frame.client.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class SFClientConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;
    
    public enum TextDisplayMode {
        NONE,
        VALUE_MAX,
        VALUE_ONLY
    }

    public static final ForgeConfigSpec.EnumValue<TextDisplayMode> ENERGY_BAR_TEXT_MODE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        
        builder.comment("Client-side display settings for the Synthetic Frame energy bar").push("display");
        ENERGY_BAR_TEXT_MODE = builder
            .comment("How to display text on the energy bar.")
            .defineEnum("energy_bar_text_mode", TextDisplayMode.VALUE_MAX);
        builder.pop();

        CLIENT_SPEC = builder.build();
    }
}