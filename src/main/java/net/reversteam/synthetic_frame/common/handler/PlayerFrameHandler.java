package net.reversteam.synthetic_frame.common.handler;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.reversteam.synthetic_frame.common.capabilities.FrameData;
import net.reversteam.synthetic_frame.common.capabilities.FrameDataProvider;
import net.reversteam.synthetic_frame.common.config.SFConfig;
import net.reversteam.synthetic_frame.common.util.EnergyChargeHelper;
import net.reversteam.synthetic_frame.common.util.SyntheticFrameMath;
import net.reversteam.synthetic_frame.mixin.FoodDataAccessor;

public class PlayerFrameHandler {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Player player = event.player;
        if (player.level().isClientSide) return;

        FrameData data = FrameDataProvider.get(player);
        if (!data.isSynthetic()) return;

        long maxEnergy = data.getMaxEnergy();
        long currentEnergy = data.getEnergy();

        // Passive draw (AFK Drain)
        long passiveDrain = SyntheticFrameMath.calculatePassiveDrain(maxEnergy);
        if (currentEnergy > 0) {
            data.consumeEnergy(passiveDrain);
        }

        // Active draw (from vanilla exhaustion sources)
        FoodData foodData = player.getFoodData();
        FoodDataAccessor accessor = (FoodDataAccessor) foodData;
        float exhaustion = accessor.getExhaustionLevel();
        
        if (exhaustion > 0.0f) {
            long activeDrain = SyntheticFrameMath.calculateActiveDrain(maxEnergy, exhaustion);
            data.consumeEnergy(activeDrain);

            accessor.setExhaustionLevel(0.0f);
        }

        // Recalculate after drains
        currentEnergy = data.getEnergy();
        float energyPercent = maxEnergy > 0 ? (float) currentEnergy / maxEnergy : 0.0f;

        // Map to vanilla hunger behaviours
        if (energyPercent == 0.0f) {
            // Starving: 0 food causes native starvation damage
            foodData.setFoodLevel(0);
            foodData.setSaturation(0.0f);
        } else if (energyPercent <= SFConfig.LOW_CHARGE_THRESHOLD.get()) {
            // Low Hunger: <= 6 food prevents sprinting and natural regeneration
            foodData.setFoodLevel(6);
            foodData.setSaturation(0.0f);
        } else {
            // Normal/Full: 20 food allows full sprinting and regeneration
            foodData.setFoodLevel(20);
            foodData.setSaturation(5.0f);
        }

        // Regenerate Health using energy
        if (player.getHealth() < player.getMaxHealth() && currentEnergy > 0) {
            boolean notLowCharge = energyPercent > SFConfig.LOW_CHARGE_THRESHOLD.get();

            player.heal((float)(SFConfig.HEAL_AMOUNT.get() * (notLowCharge ? 1.0 : SFConfig.LOW_CHARGE_HEAL_MULTIPLIER.get())));
            data.consumeEnergy(SFConfig.HEAL_COST.get());
        }

        // Scale Max Health based on Max Energy Capacity
        // Base 20 HP + 1 HP per 1000 FE capacity above the base value
        float targetMaxHealth = 20.0f + Math.max(0, (float)((maxEnergy / 1000.0f) - SFConfig.BASE_CAPACITY.get()));
        AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);

        if (maxHealthAttr != null && Math.abs(maxHealthAttr.getBaseValue() - targetMaxHealth) > 0.1f) {
            double oldMax = maxHealthAttr.getBaseValue();
            maxHealthAttr.setBaseValue(targetMaxHealth);
            // Optionally scale current health proportionally to prevent sudden "healing" or "damage" on capacity change
            if (oldMax > 0) {
                double healthRatio = player.getHealth() / oldMax;
                player.setHealth((float) Math.min(
                    player.getHealth() + (targetMaxHealth - oldMax) * healthRatio, 
                    targetMaxHealth
                ));
            }
        }

        // Trickle-charge items in inventory and curios
        int chargePerItemPerTick = Math.max(1, (int) Math.ceil(maxEnergy * SFConfig.ITEM_CHARGE_RATE.get() / 20.0));
        EnergyChargeHelper.chargePlayerItems(player, data, chargePerItemPerTick);

        data.syncToClient(player);
    }
}