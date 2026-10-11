package net.reversteam.synthetic_frame.common.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.reversteam.synthetic_frame.SyntheticFrame;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
            .create(ForgeRegistries.BLOCK_ENTITY_TYPES, SyntheticFrame.MODID);

//     public static final RegistryObject<BlockEntityType<FrameCabinetBlockEntity>> FRAME_CABINET = BLOCK_ENTITIES
//             .register("frame_cabinet_be",
//                     () -> BlockEntityType.Builder.of(
//                             FrameCabinetBlockEntity::new,
//                             ModBlocks.FRAME_CABINET.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
