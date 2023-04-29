package one.oth3r.directionhud;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import one.oth3r.directionhud.commands.Destination;
import one.oth3r.directionhud.commands.HUD;
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.files.config;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Utl;
import org.joml.Vector3f;

public class LoopManager {
    public static int hour;
    public static int minute;
    public static long timeTicks;
    public static String weatherIcon;
    public static int rainbowF;
    private static int tickH;
    private static int tickS;
    private static int HUDRefresh;
    public static void tick() {
        tickH++;
        tickS++;
        rainbowF += 10;
        HUDRefresh++;
        if (HUDRefresh >= config.HUDRefresh) {
            HUDRefresh = 0;
            for (ServerPlayerEntity player : DirectionHUD.playerManager.getPlayerList()) {
                if (PlayerData.get.hud.state(player)) {
                    HUD.build(player);
                }
                if (Destination.getDist(player) <= PlayerData.get.dest.setting.autoclearrad(player)
                        && PlayerData.get.dest.setting.autoclear(player) && Destination.checkDestination(player))
                    Destination.clear(player, CUtl.lang("dest.cleared_reached").color('7').italic(true));
            }
        }
        if (rainbowF >= 360) rainbowF = 0;
        if (tickH >= 5) {
            ServerWorld world = DirectionHUD.server.getOverworld();
            tickH = 0;
            timeTicks = world.getTimeOfDay();
            hour = (int) ((timeTicks / 1000 + 6) % 24);
            minute = (int) ((timeTicks % 1000) * 60 / 1000);
            if (world.isRaining()) {
                String str;
                if (world.isNight()) str = "☽";
                else str = "☀";
                if (world.isThundering()) weatherIcon = str + "⛈";
                else weatherIcon = str + "🌧";
            } else if (world.isNight()) weatherIcon = "☽";
            else weatherIcon = "☀";
        }
        if (tickS >= 20) {
            tickS = 0;
            for (ServerPlayerEntity player : DirectionHUD.playerManager.getPlayerList()) {
                //PARTICLES
                if (Destination.checkDestination(player)) {
                    Vec3d PlayerLoc = player.getPos().add(0, 1, 0);
                    if (player.getVehicle() != null) PlayerLoc.add(0,-0.2,0);
                    Vec3d DestinationLoc = new Vec3d(Integer.parseInt(Destination.get(player, "x")), Integer.parseInt(Destination.get(player, "y")), Integer.parseInt(Destination.get(player, "z"))).add(0.5, 0.5, 0.5);
                    if (PlayerData.get.dest.setting.particle.dest(player)) {
                        Vec3d p1 = DestinationLoc.add(0, 3, 0);
                        double space = 1;
                        Vec3d vector = DestinationLoc.add(0, -3, 0).subtract(p1).normalize().multiply(space);
                        double covered = 0;
                        for (; covered <= 6; p1 = p1.add(vector)) {
                            if (PlayerLoc.distanceTo(DestinationLoc) > 0.5 && PlayerLoc.distanceTo(DestinationLoc) < 101) {
                                player.getWorld().spawnParticles(player, new DustParticleEffect(new Vector3f(Vec3d.unpackRgb(Utl.color.getCodeRGB(PlayerData.get.dest.setting.particle.destcolor(player))).toVector3f()), 3), true, p1.getX(), p1.getY(), p1.getZ(), 1, 0, 0, 0, 1);
                            }
                            covered += space;
                        }
                    }
                    if (PlayerData.get.dest.setting.particle.line(player)) {
                        double distance = PlayerLoc.distanceTo(DestinationLoc);
                        Vec3d p3 = PlayerLoc.subtract(0, 0.2, 0);
                        double space2 = 1;
                        Vec3d vector2 = DestinationLoc.subtract(PlayerLoc).normalize().multiply(space2);
                        double covered2 = 0;
                        for (; covered2 <= distance; p3 = p3.add(vector2)) {
                            if (PlayerLoc.distanceTo(DestinationLoc) > 2) {
                                if (covered2 > 50) {
                                    break;
                                }
                                player.getWorld().spawnParticles(player, new DustParticleEffect(new Vector3f(Vec3d.unpackRgb(Utl.color.getCodeRGB(PlayerData.get.dest.setting.particle.linecolor(player))).toVector3f()), 1), true, p3.getX(), p3.getY(), p3.getZ(), 1, 0, 0, 0, 1);
                            }
                            covered2 += space2;
                        }
                    }
                }
                //SUSPEND
                if (Destination.isPlayer(player)) {
                    //maybe in the future auto convert when player and tplayer are in overworld/nether
                    //sends a message that says "converting..." or smth
                    //too much for me rn tho
                    ServerPlayerEntity tplayer = DirectionHUD.server.getPlayerManager().getPlayer(PlayerData.get.dest.getDest(player));
                    if (!(tplayer == null || player.getWorld() == tplayer.getWorld())) {
                        Destination.suspend(player,tplayer.getName().getString(),4,CUtl.lang("dest.suspended.dimension",
                                Text.literal(tplayer.getName().getString()).setStyle(CUtl.sS())));
//                        if (Utl.dim.getPDIM(player).equals("the_end") || Utl.dim.getPDIM(tplayer).equals("the_end")) {
//                        }
                    }
                }
                //UNSUSPEND
                if (PlayerData.get.dest.getSuspendedState(player)) {
                    ServerPlayerEntity tplayer = DirectionHUD.server.getPlayerManager().getPlayer(PlayerData.get.dest.suspended.target(player));
                    if (!(tplayer == null || player.getWorld() != tplayer.getWorld())) {
                        Destination.silentSetPlayer(player, tplayer);
                        PlayerData.set.dest.setSuspendedNull(player);
                        player.sendMessage(CUtl.tag().append(CUtl.lang("dest.unsuspended")).b());
                    }
                }
                //TRACK TIMER
                if (PlayerData.get.dest.getTrackingPending(player)) {
                    if (PlayerData.get.dest.track.expire(player) == 0) {
                        player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.expired")).b());
//                        Player pl = Bukkit.getPlayer(playerdata.getString(player, "destination.track.target"));
//                        if (pl != null) pl.sendMessage(CUtl.tag("&fThe tracking request has expired!"));
                        PlayerData.set.dest.setTrackNull(player);
                    } else if (PlayerData.get.dest.track.expire(player) > 0)
                        PlayerData.set.dest.track.expire(player, PlayerData.get.dest.track.expire(player) - 1);
                }
                //SUSPEND TIMER
                if (PlayerData.get.dest.getSuspendedState(player)) {
                    if (PlayerData.get.dest.suspended.expire(player) == 0) {
                        player.sendMessage(CUtl.tag().append(CUtl.lang("dest.suspended.expired")).b());
                        PlayerData.set.dest.setSuspendedNull(player);
                    } else if (PlayerData.get.dest.suspended.expire(player) > 0)
                        PlayerData.set.dest.suspended.expire(player, (PlayerData.get.dest.suspended.expire(player) - 1));
                }
            }
        }
    }
}
