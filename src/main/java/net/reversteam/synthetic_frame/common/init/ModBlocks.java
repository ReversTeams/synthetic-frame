package net.reversteam.synthetic_frame.common.init;

import java.util.function.Supplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.reversteam.synthetic_frame.SyntheticFrame;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            SyntheticFrame.MODID);

    // public static final RegistryObject<Block> FRAME_CABINET = registerBlock("frame_cabinet",
    //     () -> new FrameCabinetBlock(BlockBehaviour.Properties.of()
    //             .sound(SoundType.METAL)));

    // public static final RegistryObject<Block> FRAME_DATA_CABLE = registerBlock("data_cable",
    //         () -> new Block(BlockBehaviour.Properties.of()
    //                 .sound(SoundType.METAL)));

    // 3. Helper method to automatically register the Block and its accompanying
    // BlockItem
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    // 4. Helper method to tie a BlockItem to the registered block
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    // 5. Called in your main class constructor to link this register to the mod's
    // event bus
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
