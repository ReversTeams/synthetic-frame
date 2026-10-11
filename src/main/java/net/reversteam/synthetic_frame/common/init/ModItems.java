package net.reversteam.synthetic_frame.common.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.reversteam.synthetic_frame.SyntheticFrame;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            SyntheticFrame.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
