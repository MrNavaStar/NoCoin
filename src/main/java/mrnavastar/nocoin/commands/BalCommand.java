package mrnavastar.nocoin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mrnavastar.nocoin.util.Balance;
import mrnavastar.nocoin.util.BetterPlayerManager;
import mrnavastar.sqlib.api.DataContainer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.util.*;

import static mrnavastar.nocoin.Nocoin.bank;

public class BalCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("bal")
                .executes(BalCommand::bal)

                .then(CommandManager.literal("top")
                        .executes(BalCommand::top)));
    }

    public static int bal(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity executor = context.getSource().getPlayer();
        DataContainer account = bank.get(executor.getUuid());

        executor.sendMessage(new LiteralText("You have $" + account.getInt("BALANCE")), false);
        return 1;
    }

    public static int top(CommandContext<ServerCommandSource> command) throws CommandSyntaxException {
        ServerPlayerEntity executor = command.getSource().getPlayer();
        List<UUID> accountIds = bank.getIdsAsUuids();
        HashMap<String, Balance> accounts = new HashMap<>();

        for (UUID uuid : accountIds) {
            accounts.put(BetterPlayerManager.getName(uuid), new Balance(bank.get(executor.getUuidAsString()).getInt("BALANCE")));
        }

        TreeMap<String, Balance> sortedAccounts = new TreeMap<>(accounts);

        for (int i = 0; i < 10; i++) {
            Map.Entry<String, Balance> account = sortedAccounts.pollFirstEntry();
            if (account != null) executor.sendMessage(new LiteralText(account.getKey() + " : $" + account.getValue().balance()), false);
            else break;
        }

        return 1;
    }
}
