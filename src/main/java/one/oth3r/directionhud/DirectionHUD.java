package one.oth3r.directionhud;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DirectionHUD {
	public static final Logger LOGGER = LogManager.getLogger("DirectionHUD");
	public static final String MOD_ID = "directionhud";
	public static final Version VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getVersion();

	public static boolean isClient;
	public static String playerData;
	public static String config;
	public static PlayerManager playerManager;
	public static MinecraftServer server;
	public static CommandManager commandManager;
}
