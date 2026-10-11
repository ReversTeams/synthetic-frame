package net.reversteam.synthetic_frame.common.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.reversteam.synthetic_frame.SyntheticFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FrameDataProvider implements ICapabilitySerializable<CompoundTag> {


    // registered automatically via @AutoRegisterCapability on FrameData.
    public static final Capability<FrameData> FRAME_DATA_CAPABILITY =
            CapabilityManager.get(new CapabilityToken<>() {});

    private final FrameData instance = new FrameData();
    private final LazyOptional<FrameData> optional = LazyOptional.of(() -> instance);


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == FRAME_DATA_CAPABILITY) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    // NBT serialization

    @Override
    public CompoundTag serializeNBT() {
        return instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.deserializeNBT(nbt);
    }

    // Call this when owner entity is removed
    public void invalidate() {
        optional.invalidate();
    }

    // convenience accessor
    public static FrameData get(Player player) {
        return player.getCapability(FRAME_DATA_CAPABILITY)
                .orElseGet(() -> {
                    // This should never happen if AttachCapabilitiesEvent is wired correctly,
                    // but provides a safe fallback instead of crashing.
                    SyntheticFrame.LOGGER.warn(
                            "FrameData capability missing on player {} — returning default instance",
                            player.getName().getString()
                    );
                    return new FrameData();
                });
    }
}