package mrnavastar.nocoin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mrnavastar.nocoin.util.BetterPlayerManager;
import mrnavastar.sqlib.api.DataContainer;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.UUID;

import static mrnavastar.nocoin.Nocoin.bank;

public class PayCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("pay")

                .then(CommandManager.argument("player", StringArgumentType.string()).suggests((commandContext, suggestionsBuilder) -> {
                    ArrayList<ServerPlayerEntity> onlinePlayers = BetterPlayerManager.getOnlinePlayers();
                    onlinePlayers.remove(commandContext.getSource().getPlayer());
                    return CommandSource.suggestMatching(onlinePlayers.stream().map(PlayerEntity::getEntityName), suggestionsBuilder);
                })
                .then(CommandManager.argument("amount", IntegerArgumentType.integer())
                        .executes(context -> pay(context, StringArgumentType.getString(context, "player"), IntegerArgumentType.getInteger(context, "amount"))))
                ));
    }

    public static int pay(CommandContext<ServerCommandSource> context, String player, int amount) throws CommandSyntaxException {
        ServerPlayerEntity executor = context.getSource().getPlayer();
        UUID target = BetterPlayerManager.getUuid(player);

        if (target != null) {
            if (!target.equals(executor.getUuid())) {
                DataContainer executorAccount = bank.get(executor.getUuid());
                DataContainer targetAccount = bank.get(target);

                int executorBal = executorAccount.getInt("BALANCE");
                int targetBal = targetAccount.getInt("BALANCE");

                if (executorBal >= amount) {
                    executorAccount.put("BALANCE", executorBal - amount);
                    targetAccount.put("BALANCE", targetBal + amount);

                } else executor.sendMessage(new LiteralText("You do not have enough money"), false);
            } else executor.sendMessage(new LiteralText("You cannot pay yourself"), false);
        } else executor.sendMessage(new LiteralText("That player does not have a bank account"), false);
        return 1;
    }
}