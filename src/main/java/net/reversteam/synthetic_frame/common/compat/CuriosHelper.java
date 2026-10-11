package net.reversteam.synthetic_frame.common.compat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.reversteam.synthetic_frame.SyntheticFrame;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;

public class CuriosHelper {

    public static boolean isCuriosLoaded() {
        return ModList.get().isLoaded("curios");
    }

    public static List<ItemStack> getCurioStacks(LivingEntity entity) {
        List<ItemStack> curioStacks = new ArrayList<>();
        if (!isCuriosLoaded()) return curioStacks;

        try {
            CuriosApi.getCuriosInventory(entity).ifPresent(inventory -> {
                inventory.getCurios().values().forEach(stackHandler -> {
                    for (int i = 0; i < stackHandler.getSlots(); i++) {
                        ItemStack stack = stackHandler.getStacks().getStackInSlot(i);
                        if (!stack.isEmpty()) {
                            curioStacks.add(stack);
                        }
                    }
                });
            });
        } catch (Exception e) {
            // Fail silently if Curios API changes or an unexpected error occurs
            SyntheticFrame.LOGGER.warn("Failed to access Curios inventory", e);
        }

        return curioStacks;
    }
}