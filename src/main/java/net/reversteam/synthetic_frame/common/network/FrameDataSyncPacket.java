package net.reversteam.synthetic_frame.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.reversteam.synthetic_frame.common.capabilities.FrameDataProvider;
import java.util.function.Supplier;

public class FrameDataSyncPacket {
    private final boolean isSynthetic;
    private final long energy;
    private final long maxEnergy;

    public FrameDataSyncPacket(boolean isSynthetic, long energy, long maxEnergy) {
        this.isSynthetic = isSynthetic;
        this.energy = energy;
        this.maxEnergy = maxEnergy;
    }

    public FrameDataSyncPacket(FriendlyByteBuf buf) {
        this.isSynthetic = buf.readBoolean();
        this.energy = buf.readLong();
        this.maxEnergy = buf.readLong();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isSynthetic);
        buf.writeLong(energy);
        buf.writeLong(maxEnergy);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            var mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.getCapability(FrameDataProvider.FRAME_DATA_CAPABILITY).ifPresent(data -> {
                    data.setSynthetic(this.isSynthetic);
                    data.setEnergy(this.energy);
                    data.setMaxEnergy(this.maxEnergy);
                });
            }
        });
        context.setPacketHandled(true);
    }
}