package net.reversteam.synthetic_frame.common.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.reversteam.synthetic_frame.SyntheticFrame;

public class SyntheticFrameNetwork {
    private static final String PROTOCOL_VERSION = "1";
    
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(SyntheticFrame.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++, FrameDataSyncPacket.class,
                FrameDataSyncPacket::encode,
                FrameDataSyncPacket::new,
                FrameDataSyncPacket::handle);
    }

    public static void sendToPlayer(ServerPlayer player, Object packet) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}