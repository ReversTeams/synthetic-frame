package net.reversteam.synthetic_frame.common.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.energy.IEnergyStorage;
import net.reversteam.synthetic_frame.common.config.SFConfig;
import net.reversteam.synthetic_frame.common.network.FrameDataSyncPacket;
import net.reversteam.synthetic_frame.common.network.SyntheticFrameNetwork;
import net.reversteam.synthetic_frame.common.util.SyntheticFrameMath;

@AutoRegisterCapability
public class FrameData implements IEnergyStorage {
    private boolean isSynthetic = false;
    private long energy = SyntheticFrameMath.calculateEnergyForMinutes(SFConfig.BASE_CAPACITY.get().longValue(), SFConfig.BASE_OVERCHARGE_TIME.get());
    private long maxEnergy = SFConfig.BASE_CAPACITY.get().longValue();

    private boolean lastSyncedSynthetic = false;
    private long lastSyncedEnergy = -1;
    private long lastSyncedMaxEnergy = -1;

    public boolean isSynthetic() {
        return isSynthetic;
    }

    public void setSynthetic(boolean synthetic) {
        isSynthetic = synthetic;
    }

    public long getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(long maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    //
    // IEnergyStorage implementation
    //

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;
        long energyReceived = Math.min(maxEnergy - energy, Math.min(this.maxEnergy * 10, maxReceive));
        if (!simulate)
            energy += energyReceived;
        return (int) energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;
        long energyExtracted = Math.min(energy, Math.min(this.maxEnergy, maxExtract));
        if (!simulate)
            energy -= energyExtracted;
        return (int) energyExtracted;
    }

    public void consumeEnergy(long amount) {
        this.energy = Math.max(0, this.energy - amount);
    }

    @Override
    public int getEnergyStored() {
        return (int) energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) maxEnergy;
    }

    @Override
    public boolean canExtract() {
        return false;
    } // Frames only receive energy

    @Override
    public boolean canReceive() {
        return isSynthetic;
    }

    public long getEnergy() {
        return energy;
    }

    public long setEnergy(long value) {
        return energy = value;
    }

    public long addEnergy(long value) {
        return energy += value;
    }

    //
    // NBT serialization
    //

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("IsSynthetic", isSynthetic);
        tag.putLong("Energy", energy);
        tag.putLong("MaxEnergy", maxEnergy);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        this.isSynthetic = tag.getBoolean("IsSynthetic");
        this.energy = tag.getLong("Energy");
        this.maxEnergy = tag.getLong("MaxEnergy");

        // Reset sync trackers on load to force an immediate sync to the client
        this.lastSyncedEnergy = -1;
        this.lastSyncedMaxEnergy = -1;
    }

    public void syncToClient(Player player) {
        if (player.level().isClientSide || !(player instanceof ServerPlayer serverPlayer))
            return;

        // Only send a packet if the values have actually changed
        if (this.isSynthetic != this.lastSyncedSynthetic ||
                this.energy != this.lastSyncedEnergy ||
                this.maxEnergy != this.lastSyncedMaxEnergy) {

            this.lastSyncedSynthetic = this.isSynthetic;
            this.lastSyncedEnergy = this.energy;
            this.lastSyncedMaxEnergy = this.maxEnergy;

            SyntheticFrameNetwork.sendToPlayer(serverPlayer, new FrameDataSyncPacket(
                    this.isSynthetic,
                    this.getEnergyStored(),
                    this.getMaxEnergyStored()));
        }
    }
}