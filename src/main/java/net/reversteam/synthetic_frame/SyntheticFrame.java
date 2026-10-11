package net.reversteam.synthetic_frame;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.reversteam.synthetic_frame.common.capabilities.FrameData;
import net.reversteam.synthetic_frame.common.capabilities.FrameDataProvider;
import net.reversteam.synthetic_frame.common.handler.PlayerFrameHandler;
import net.reversteam.synthetic_frame.common.init.ModBlockEntities;
import net.reversteam.synthetic_frame.common.init.ModBlocks;
import net.reversteam.synthetic_frame.common.init.ModItems;
import net.reversteam.synthetic_frame.common.network.SyntheticFrameNetwork;

@Mod(SyntheticFrame.MODID)
public class SyntheticFrame {
    public static final String MODID = "synthetic_frame";

    public static final Logger LOGGER = LogUtils.getLogger();

    public SyntheticFrame(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus); 

        // Register capabilities
        // modEventBus.addListener(this::registerCapabilities);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, this::attachCapabilities);

        // Register event handlers
        MinecraftForge.EVENT_BUS.register(new PlayerFrameHandler());

        MinecraftForge.EVENT_BUS.addListener((net.minecraftforge.event.entity.EntityLeaveLevelEvent event) -> {
            if (event.getEntity() instanceof net.minecraft.world.entity.player.Player player) {
                player.getCapability(FrameDataProvider.FRAME_DATA_CAPABILITY)
                        .ifPresent(data -> {
                            // If you ever store the provider reference, call invalidate() here.
                            // With ICapabilitySerializable attached via AttachCapabilitiesEvent,
                            // Forge handles most of this, but explicit invalidation is safer.
                        });
            }
        });

        SyntheticFrameNetwork.register();
    }

    @SubscribeEvent 
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(FrameData.class);
    }

    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(FrameDataProvider.FRAME_DATA_CAPABILITY).isPresent()) {
                event.addCapability(ResourceLocation.fromNamespaceAndPath(MODID, "frame_data"), new FrameDataProvider());
            }
        }
    }
}