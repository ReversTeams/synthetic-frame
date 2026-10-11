package net.reversteam.synthetic_frame.common.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.reversteam.synthetic_frame.common.capabilities.FrameData;
import net.reversteam.synthetic_frame.common.compat.CuriosHelper;

import java.util.ArrayList;
import java.util.List;

public class EnergyChargeHelper {

    public static void chargePlayerItems(Player player, FrameData frameData, int chargePerItemPerTick) {
        if (chargePerItemPerTick <= 0 || frameData.getEnergyStored() <= 0) return;

        List<ItemStack> itemsToCheck = new ArrayList<>();
        
        // Main inventory, armor, and offhand
        itemsToCheck.addAll(player.getInventory().items);
        itemsToCheck.addAll(player.getInventory().armor);
        itemsToCheck.add(player.getInventory().offhand.get(0));
        
        // Add Curios slots if the mod is loaded
        if (CuriosHelper.isCuriosLoaded()) {
            itemsToCheck.addAll(CuriosHelper.getCurioStacks(player));
        }

        for (ItemStack stack : itemsToCheck) {
            if (stack.isEmpty()) continue;
            
            // Check for Forge Energy capability
            stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energyStorage -> {
                if (energyStorage.canReceive() && energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
                    int energyNeeded = Math.min(chargePerItemPerTick, energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
                    int energyAvailable = frameData.getEnergyStored();
                    
                    if (energyAvailable > 0) {
                        int amountToTransfer = Math.min(energyNeeded, energyAvailable);
                        int received = energyStorage.receiveEnergy(amountToTransfer, false);
                        
                        if (received > 0) {
                            frameData.consumeEnergy(received);
                        }
                    }
                }
            });
            
            // Early exit if the frame's battery is depleted for this tick
            if (frameData.getEnergyStored() <= 0) break;
        }
    }
}