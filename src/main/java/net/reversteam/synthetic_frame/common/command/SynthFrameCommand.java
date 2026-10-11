package net.reversteam.synthetic_frame.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.reversteam.synthetic_frame.common.capabilities.FrameData;
import net.reversteam.synthetic_frame.common.capabilities.FrameDataProvider;
import net.reversteam.synthetic_frame.common.util.SyntheticFrameMath;

public class SynthFrameCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("synthframe")
            .requires(source -> source.hasPermission(2))
            
            .then(Commands.literal("setSynthetic")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> setSynthetic(ctx, EntityArgument.getPlayer(ctx, "player"), BoolArgumentType.getBool(ctx, "value")))
                    )
                )
            )
            
            .then(Commands.literal("data")
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(ctx -> checkData(ctx, EntityArgument.getPlayer(ctx, "player")))
                )
            )
            
            .then(Commands.literal("charge")
                .then(Commands.argument("action", StringArgumentType.word())
                    .suggests((ctx, builder) -> { builder.suggest("add"); builder.suggest("set"); return builder.buildFuture(); })
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("amount", LongArgumentType.longArg(0))
                            .executes(ctx -> charge(ctx, EntityArgument.getPlayer(ctx, "player"), StringArgumentType.getString(ctx, "action"), LongArgumentType.getLong(ctx, "amount")))
                        )
                    )
                )
            )

            .then(Commands.literal("maxCharge")
                .then(Commands.argument("action", StringArgumentType.word())
                    .suggests((ctx, builder) -> { builder.suggest("add"); builder.suggest("set"); return builder.buildFuture(); })
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("amount", LongArgumentType.longArg(0))
                            .executes(ctx -> maxCharge(ctx, EntityArgument.getPlayer(ctx, "player"), StringArgumentType.getString(ctx, "action"), LongArgumentType.getLong(ctx, "amount")))
                        )
                    )
                )
            )

            .then(Commands.literal("overcharge")
                .then(Commands.argument("action", StringArgumentType.word())
                    .suggests((ctx, builder) -> { builder.suggest("add"); builder.suggest("set"); return builder.buildFuture(); })
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("minutes", DoubleArgumentType.doubleArg(0.0))
                            .executes(ctx -> overcharge(ctx, EntityArgument.getPlayer(ctx, "player"), StringArgumentType.getString(ctx, "action"), DoubleArgumentType.getDouble(ctx, "minutes")))
                        )
                    )
                )
            )
        );
    }

    private static int setSynthetic(CommandContext<CommandSourceStack> ctx, ServerPlayer player, boolean value) {
        FrameData data = FrameDataProvider.get(player);
        data.setSynthetic(value);
        data.syncToClient(player);
        ctx.getSource().sendSuccess(() -> Component.literal("Set synthetic status of " + player.getName().getString() + " to " + value), true);
        return 1;
    }

    private static int checkData(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        FrameData data = FrameDataProvider.get(player);
        long maxEnergy = data.getMaxEnergy();
        long passiveDrainPerTick = SyntheticFrameMath.calculatePassiveDrain(maxEnergy);
        long passiveDrainPerSec = passiveDrainPerTick * 20;

        // Calculate full charge duration in minutes
        // 1 minute = 20 ticks/sec * 60 seconds = 1200 ticks
        double minutes = SyntheticFrameMath.calculateTimeWithEnergy(maxEnergy);

        Component msg = Component.literal(String.format(
            "FrameData for %s:\n" +
            " - Synthetic: %b\n" +
            " - Energy: %d / %d FE\n" +
            " - Passive Drain: %d FE/tick (%d FE/sec)\n" +
            " - Full Charge Duration: ~%.1f minutes (AFK)",
            player.getName().getString(), 
            data.isSynthetic(), 
            data.getEnergy(), 
            maxEnergy,
            passiveDrainPerTick,
            passiveDrainPerSec,
            minutes
        ));
        
        ctx.getSource().sendSuccess(() -> msg, false);
        return 1;
    }

    private static int charge(CommandContext<CommandSourceStack> ctx, ServerPlayer player, String action, long amount) {
        FrameData data = FrameDataProvider.get(player);
        
        if (!data.isSynthetic()) {
            ctx.getSource().sendFailure(Component.literal("Target player is not a synthetic frame! Use /synthframe setSynthetic first."));
            return 0;
        }

        if (action.equalsIgnoreCase("add")) {
            data.receiveEnergy((int) Math.min(amount, Integer.MAX_VALUE), false); // Uses IEnergyStorage logic
            ctx.getSource().sendSuccess(() -> Component.literal(
                "Added " + amount + " FE to " + player.getName().getString() + " (Now: " + data.getEnergy() + " / " + data.getMaxEnergy() + " FE)"
            ), true);
        } else if (action.equalsIgnoreCase("set")) {
            data.setEnergy(Math.min(amount, Integer.MAX_VALUE));
            ctx.getSource().sendSuccess(() -> Component.literal(
                "Set energy of " + player.getName().getString() + " to " + amount + " FE (Now: " + data.getEnergy() + " / " + data.getMaxEnergy() + " FE)"
            ), true);
        } else {
            ctx.getSource().sendFailure(Component.literal("Invalid action. Use 'add' or 'set'."));
            return 0;
        }

        data.syncToClient(player);
        return 1;
    }

    private static int maxCharge(CommandContext<CommandSourceStack> ctx, ServerPlayer player, String action, long amount) {
        FrameData data = FrameDataProvider.get(player);
        
        if (!data.isSynthetic()) {
            ctx.getSource().sendFailure(Component.literal("Target player is not a synthetic frame! Use /synthframe setSynthetic first."));
            return 0;
        }

        if (action.equalsIgnoreCase("add")) {
            long newMax = data.getMaxEnergy() + amount;
            data.setMaxEnergy(newMax);
            ctx.getSource().sendSuccess(() -> Component.literal(
                "Added " + amount + " FE to max capacity of " + player.getName().getString() + " (Now: " + data.getEnergy() + " / " + data.getMaxEnergy() + " FE)"
            ), true);
        } else if (action.equalsIgnoreCase("set")) {
            data.setMaxEnergy(amount);
            ctx.getSource().sendSuccess(() -> Component.literal(
                "Set max capacity of " + player.getName().getString() + " to " + amount + " FE (Now: " + data.getEnergy() + " / " + data.getMaxEnergy() + " FE)"
            ), true);
        } else {
            ctx.getSource().sendFailure(Component.literal("Invalid action. Use 'add' or 'set'."));
            return 0;
        }

        data.syncToClient(player);
        return 1;
    }

    private static int overcharge(CommandContext<CommandSourceStack> ctx, ServerPlayer player, String action, double minutes) {
        FrameData data = FrameDataProvider.get(player);
        
        if (!data.isSynthetic()) {
            ctx.getSource().sendFailure(Component.literal("Target player is not a synthetic frame! Use /synthframe setSynthetic first."));
            return 0;
        }

        // Calculate FE based on the player's specific max energy passive drain rate
        long energyFromMinutes = SyntheticFrameMath.calculateEnergyForMinutes(data.getMaxEnergy(), minutes);

        if (action.equalsIgnoreCase("add")) {
            data.addEnergy(energyFromMinutes);
            ctx.getSource().sendSuccess(() -> Component.literal(
                String.format("Added %.1f minutes of overcharge (%d FE) to %s (Now: %d / %d FE)", 
                    minutes, energyFromMinutes, player.getName().getString(), data.getEnergy(), data.getMaxEnergy())
            ), true);
        } else if (action.equalsIgnoreCase("set")) {
            data.setEnergy(energyFromMinutes);
            ctx.getSource().sendSuccess(() -> Component.literal(
                String.format("Set overcharge of %s to %.1f minutes (%d FE) (Now: %d / %d FE)", 
                    player.getName().getString(), minutes, energyFromMinutes, data.getEnergy(), data.getMaxEnergy())
            ), true);
        } else {
            ctx.getSource().sendFailure(Component.literal("Invalid action. Use 'add' or 'set'."));
            return 0;
        }

        data.syncToClient(player);
        return 1;
    }
}