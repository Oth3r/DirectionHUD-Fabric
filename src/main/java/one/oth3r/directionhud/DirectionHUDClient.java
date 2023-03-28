package one.oth3r.directionhud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.WorldSavePath;
import one.oth3r.directionhud.commands.DestinationCommand;
import one.oth3r.directionhud.commands.DirHUDCommand;
import one.oth3r.directionhud.commands.HUDCommand;
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.files.config;

import java.io.File;

public class DirectionHUDClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DirectionHUD.isClient = true;
        ServerLifecycleEvents.SERVER_STARTED.register(s -> {
            DirectionHUD.playerManager = s.getPlayerManager();
            DirectionHUD.server = s;
            DirectionHUD.commandManager = s.getCommandManager();
            DirectionHUD.playerData = DirectionHUD.server.getSavePath(WorldSavePath.ROOT).normalize()+"/directionhud/playerdata/";
            File dir = new File(DirectionHUD.playerData);
            if (!dir.exists()) dir.mkdirs();
        });
        DirectionHUD.config = FabricLoader.getInstance().getConfigDir().toFile()+"/";
        config.load();
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerData.addPlayer(handler.player);
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PlayerData.removePlayer(handler.player);
        });
        //COMMANDS
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            DirHUDCommand.register(dispatcher);
            HUDCommand.register(dispatcher);
            DestinationCommand.register(dispatcher);
        });

        ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> minecraftServer.execute(LoopManager::tick));
    }

}
