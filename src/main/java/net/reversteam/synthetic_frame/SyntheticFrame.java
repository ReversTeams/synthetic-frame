package net.reversteam.synthetic_frame;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SyntheticFrame.MODID)
public class SyntheticFrame {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "synthetic_frame";

    public MixinModTemplate(FMLJavaModLoadingContext context) {

    }
}
