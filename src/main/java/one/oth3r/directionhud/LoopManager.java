package one.oth3r.directionhud;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import one.oth3r.directionhud.commands.Destination;
import one.oth3r.directionhud.commands.HUD;
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.files.config;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Loc;
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
                        && PlayerData.get.dest.setting.autoclear(player) && Destination.get(player).hasXYZ())
                    Destination.clear(player, CUtl.lang("dest.changed.cleared.reached").color('7').italic(true));
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
                Vec3d pVec = player.getPos().add(0, 1, 0);
                if (Destination.get(player).hasXYZ()) {
                    if (player.getVehicle() != null) pVec.add(0,-0.2,0);
                    Vec3d destVec = Destination.get(player).getVec3d(player).add(0.5, 0.5, 0.5);
                    if (PlayerData.get.dest.setting.particle.dest(player)) {
                        Vec3d particlePos = destVec.add(0, 3, 0);
                        double spacing = 1;
                        Vec3d segment = destVec.add(0, -3, 0).subtract(particlePos).normalize().multiply(spacing);
                        double distCovered = 0;
                        for (; distCovered <= 6; particlePos = particlePos.add(segment)) {
                            if (pVec.distanceTo(destVec) > 0.5 && pVec.distanceTo(destVec) < 50) {
                                player.getWorld().spawnParticles(player,
                                        new DustParticleEffect(new Vector3f(Vec3d.unpackRgb(
                                                Utl.color.getCodeRGB(PlayerData.get.dest.setting.particle.destcolor(player))).toVector3f()),3),
                                        true,particlePos.getX(),particlePos.getY(),particlePos.getZ(),1,0,0,0,1);
                            }
                            distCovered += spacing;
                        }
                    }
                    if (PlayerData.get.dest.setting.particle.line(player)) {
                        double distance = pVec.distanceTo(destVec);
                        Vec3d particlePos = pVec.subtract(0, 0.2, 0);
                        double spacing = 1;
                        Vec3d segment = destVec.subtract(pVec).normalize().multiply(spacing);
                        double distCovered = 0;
                        for (; distCovered <= distance; particlePos = particlePos.add(segment)) {
                            distCovered += spacing;
                            if (pVec.distanceTo(destVec) < 2) continue;
                            if (distCovered >= 50) break;
                            player.getWorld().spawnParticles(player,
                                    new DustParticleEffect(new Vector3f(Vec3d.unpackRgb(
                                            Utl.color.getCodeRGB(PlayerData.get.dest.setting.particle.linecolor(player))).toVector3f()),1),
                                    true,particlePos.getX(),particlePos.getY(),particlePos.getZ(),1,0,0,0,1);
                        }
                    }
                }
                if (PlayerData.get.dest.getTracking(player) != null && !PlayerData.get.dest.setting.track(player))
                    Destination.social.track.clear(player, CUtl.lang("dest.track.clear.tracking_off").color('7').italic(true));
                ServerPlayerEntity trackingP = Destination.social.track.getTarget(player);
                //TRACKING
                if (trackingP != null && PlayerData.get.dest.setting.track(trackingP)) {
                    //TRACKING OFFLINE MSG RESET
                    if (PlayerData.getOneTime(player,"tracking.offline") != null) {
                        player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.back")).b());
                        PlayerData.setOneTime(player,"tracking.offline",null);
                    }
                    Vec3d trackingVec = trackingP.getPos();
                    boolean particles = true;
                    //IF NOT IN SAME DIM
                    if (!Utl.player.dim(trackingP).equals(Utl.player.dim(player))) {
                        particles = false;
                        // AUTOCONVERT ON AND CONVERTIBLE
                        if (PlayerData.get.dest.setting.autoconvert(player) && Utl.dim.canConvert(Utl.player.dim(player),Utl.player.dim(trackingP))) {
                            if (PlayerData.getOneTime(player,"tracking.converted") == null) {
                                //SEND MSG IF HAVENT B4
                                player.sendMessage(CUtl.tag().append(CUtl.lang("dest.autoconvert.tracking")).append("\n ")
                                        .append(CUtl.lang("dest.autoconvert.info",
                                                        CTxT.of(Utl.dim.getName(Utl.player.dim(trackingP))).italic(true).color(Utl.dim.getHEX(Utl.player.dim(trackingP))),
                                                        CTxT.of(Utl.dim.getName(Utl.player.dim(player))).italic(true).color(Utl.dim.getHEX(Utl.player.dim(player))))
                                                .italic(true).color('7')).b());
                                PlayerData.setOneTime(player,"tracking.converted",Utl.player.dim(player));
                            }
                            particles = true;
                            Loc tLoc = new Loc(trackingP);
                            tLoc.convertTo(Utl.player.dim(player));
                            trackingVec = tLoc.getVec3d(player);
                        } else if (PlayerData.getOneTime(player,"tracking.dimension") == null) {
                            //NOT CONVERTIBLE OR AUTOCONVERT OFF -- SEND DIM MSG
                            //RESET CONVERT
                            PlayerData.setOneTime(player,"tracking.converted",null);
                            player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.dimension").append("\n ")
                                    .append(CUtl.lang("dest.track.dimension_2",
                                            CTxT.of(Utl.player.name(trackingP)).color(CUtl.sTC())).color('7').italic(true))).b());
                            PlayerData.setOneTime(player,"tracking.dimension","1");
                        }
                    } else if (PlayerData.getOneTime(player,"tracking.converted") != null) {
                        //SAME DIM & RESET CONVERT MSG
                        player.sendMessage(CUtl.tag().append(CUtl.lang("dest.autoconvert.tracking")).append("\n ")
                                .append(CUtl.lang("dest.autoconvert.info",
                                        CTxT.of(Utl.dim.getName(PlayerData.getOneTime(player,"tracking.converted"))).italic(true).color(Utl.dim.getHEX(PlayerData.getOneTime(player,"tracking.converted"))),
                                        CTxT.of(Utl.dim.getName(Utl.player.dim(player))).italic(true).color(Utl.dim.getHEX(Utl.player.dim(player))))
                                        .italic(true).color('7')).b());
                        PlayerData.setOneTime(player,"tracking.converted",null);
                    } else if (PlayerData.getOneTime(player,"tracking.dimension") != null) {
                        //SAME DIM, RESET DIM MSG
                        player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.back")).b());
                        PlayerData.setOneTime(player,"tracking.dimension",null);
                    }
                    //PARTICLES
                    if (PlayerData.get.dest.setting.particle.tracking(player) && particles) {
                        if (PlayerData.get.dest.setting.ylevel(player))
                            trackingVec = new Vec3d(trackingVec.getX(),player.getY(),trackingVec.getZ());
                        Vec3d tVec = trackingVec.add(0,1,0);
                        double distance = pVec.distanceTo(tVec);
                        Vec3d particlePos = pVec.subtract(0, 0.2, 0);
                        double spacing = 0.5;
                        //space between each particle
                        Vec3d segment = tVec.subtract(pVec).normalize().multiply(spacing);
                        double distanceCovered = 0;
                        for (; distanceCovered <= distance; particlePos = particlePos.add(segment)) {
                            distanceCovered += spacing;
                            //min particle spawning distance
                            if (pVec.distanceTo(tVec) < 2) continue;
                            //if more than x blocks away
                            if (distanceCovered >= 50) break;
                            player.getWorld().spawnParticles(player,
                                    new DustParticleEffect(new Vector3f(Vec3d.unpackRgb(
                                            Utl.color.getCodeRGB(PlayerData.get.dest.setting.particle.trackingcolor(player))).toVector3f()),0.5f),
                                    true,particlePos.getX(),particlePos.getY(),particlePos.getZ(),2,0,0,0,1);
                        }
                    }
                } else if (trackingP != null) {
                    //TRACKING PLAYER TURNED OFF TRACKING
                    Destination.social.track.clear(player, CUtl.lang("dest.track.clear.tracking_off_tracked").color('7').italic(true));
                } else if (PlayerData.getOneTime(player,"tracking.offline") == null && PlayerData.get.dest.getTracking(player) != null) {
                    //TRACKING PLAYER OFFLINE
                    player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.offline")).append(" ")
                            .append(CUtl.CButton.dest.clear()).b());
                    PlayerData.setOneTime(player,"tracking.offline","1");
                    //RESET OTHER MSGS
                    PlayerData.setOneTime(player,"tracking.converted",null);
                    PlayerData.setOneTime(player,"tracking.dimension",null);
                }
                //TRACK TIMER
                if (PlayerData.get.dest.getTrackPending(player)) {
                    if (PlayerData.get.dest.track.expire(player) == 0) {
                        player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.expired")).b());
                        PlayerData.set.dest.setTrackNull(player);
                    } else if (PlayerData.get.dest.track.expire(player) > 0) {
                        PlayerData.set.dest.track.expire(player, PlayerData.get.dest.track.expire(player) - 1);
                        if (Utl.player.getFromIdentifier(PlayerData.get.dest.track.target(player)) == null) {
                            player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.expired")).b());
                            PlayerData.set.dest.setTrackNull(player);
                        }
                    }
                }
            }
        }
    }
}
