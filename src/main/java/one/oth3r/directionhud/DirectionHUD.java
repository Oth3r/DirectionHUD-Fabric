package one.oth3r.directionhud;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import one.oth3r.directionhud.commands.DestinationCommand;
import one.oth3r.directionhud.commands.DirHUDCommand;
import one.oth3r.directionhud.commands.HUDCommand;
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.files.config;
import one.oth3r.directionhud.utils.Utl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DirectionHUD {
	public static Map<ServerPlayerEntity, Boolean> players = new HashMap<>();
	public static final Logger LOGGER = LogManager.getLogger("DirectionHUD");
	public static final String MOD_ID = "directionhud";
	public static final Version VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getVersion();
	public static boolean isClient;
	public static String playerData;
	public static String configFile;
	public static PlayerManager playerManager;
	public static MinecraftServer server;
	public static CommandManager commandManager;
	public static void initializeCommon() {
		configFile = FabricLoader.getInstance().getConfigDir().toFile()+"/";
		config.load();
		ServerLifecycleEvents.SERVER_STARTED.register(s -> {
			DirectionHUD.playerManager = s.getPlayerManager();
			DirectionHUD.server = s;
			DirectionHUD.commandManager = s.getCommandManager();
			if (isClient) playerData = DirectionHUD.server.getSavePath(WorldSavePath.ROOT).normalize()+"/directionhud/playerdata/";
			else playerData = FabricLoader.getInstance().getConfigDir().toFile()+"/directionhud/playerdata/";
			Utl.dim.dimsToMap();
			Path dirPath = Paths.get(DirectionHUD.playerData);
			try {
				Files.createDirectories(dirPath);
			} catch (IOException e) {
				System.out.println("Failed to create playerdata directory: " + e.getMessage());
			}
		});
		//PLAYER
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			PacketBuilder packet = new PacketBuilder("On DirectionHUD supported server!");
			packet.sendToPlayer(PacketBuilder.INITIALIZATION_PACKET, handler.player);
			PlayerData.addPlayer(handler.player);
			DirectionHUD.players.put(handler.player,false);
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			PlayerData.removePlayer(handler.player);
			DirectionHUD.players.remove(handler.player);
		});
		//PACKETS
		ServerPlayNetworking.registerGlobalReceiver(PacketBuilder.INITIALIZATION_PACKET,
				(server, player, handler, buf, responseSender) -> server.execute(() -> {
					DirectionHUD.players.put(player,true);
					PacketBuilder packet = new PacketBuilder(PlayerData.get.hud.state(player)+"");
					packet.sendToPlayer(PacketBuilder.HUD_STATE,player);
				}));
		//COMMANDS
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			DirHUDCommand.register(dispatcher);
			HUDCommand.register(dispatcher);
			DestinationCommand.register(dispatcher);
		});
		//LOOP
		ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> minecraftServer.execute(LoopManager::tick));
	}
}
