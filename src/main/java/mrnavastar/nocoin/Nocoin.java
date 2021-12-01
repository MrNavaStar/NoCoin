package mrnavastar.nocoin;

import mrnavastar.nocoin.commands.BalCommand;
import mrnavastar.nocoin.commands.PayCommand;
import mrnavastar.sqlib.api.DataContainer;
import mrnavastar.sqlib.api.Table;
import mrnavastar.sqlib.api.databases.SQLiteDatabase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.PlayerManager;
import net.minecraft.util.UserCache;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.util.UUID;

public class Nocoin implements ModInitializer {

    public static String MODID = "NoCoin";
    public static PlayerManager playerManager;
    public static UserCache userCache;
    public static Table bank;

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing...");

        SQLiteDatabase database = new SQLiteDatabase("NoCoin", FabricLoader.getInstance().getGameDir().toString());
        bank = database.createTable("bank");

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            BalCommand.register(server.getCommandManager().getDispatcher());
            PayCommand.register(server.getCommandManager().getDispatcher());

            playerManager = server.getPlayerManager();
            userCache = server.getUserCache();
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            UUID uuid = handler.getPlayer().getUuid();

            if (!bank.contains(uuid)) {
                DataContainer account = bank.createDataContainer(uuid);
                account.put("BALANCE", 100);
            }
        });
    }

    public static void log(Level level, String message){
        LogManager.getLogger().log(level, "[" + MODID + "] " + message);
    }
}
