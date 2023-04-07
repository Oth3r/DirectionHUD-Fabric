package one.oth3r.directionhud.commands;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.files.LangReader;
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.files.config;
import one.oth3r.directionhud.utils.CUtl;

public class DirHUD {
    public static void setDefaults(ServerPlayerEntity player) {
        config.DESTAutoClear = PlayerData.get.dest.setting.autoclear(player);
        config.DESTAutoClearRad = PlayerData.get.dest.setting.autoclearrad(player);
        config.DESTDestParticles = PlayerData.get.dest.setting.particle.dest(player);
        config.DESTDestParticleColor = PlayerData.get.dest.setting.particle.destcolor(player);
        config.DESTLineParticles = PlayerData.get.dest.setting.particle.line(player);
        config.DESTLineParticleColor = PlayerData.get.dest.setting.particle.linecolor(player);
        config.DESTYLevel = PlayerData.get.dest.setting.ylevel(player);
        config.DESTSend = PlayerData.get.dest.setting.send(player);
        config.DESTTrack = PlayerData.get.dest.setting.track(player);

        config.HUD24HR = PlayerData.get.hud.setting.time24h(player);
        config.HUDCompass = PlayerData.get.hud.module.compass(player);
        config.HUDCoordinates = PlayerData.get.hud.module.coordinates(player);
        config.HUDDistance = PlayerData.get.hud.module.distance(player);
        config.HUDDirection = PlayerData.get.hud.module.direction(player);
        config.HUDDestination = PlayerData.get.hud.module.destination(player);
        config.HUDTime = PlayerData.get.hud.module.time(player);
        config.HUDWeather = PlayerData.get.hud.module.weather(player);

        config.HUDEnabled = PlayerData.get.hud.state(player);
        config.HUDOrder = PlayerData.get.hud.order(player);

        config.HUDPrimaryColor = HUD.color.getHUDColors(player)[0];
        config.HUDPrimaryBold = HUD.color.getHUDBold(player,1);
        config.HUDPrimaryItalics = HUD.color.getHUDItalics(player, 1);

        config.HUDSecondaryColor = HUD.color.getHUDColors(player)[1];
        config.HUDSecondaryBold = HUD.color.getHUDBold(player, 2);
        config.HUDSecondaryItalics = HUD.color.getHUDItalics(player, 2);
        config.save();
        player.sendMessage(CUtl.tag(CUtl.lang("dirhud.defaults.set")));
    }
    public static void resetDefaults(ServerPlayerEntity player) {
        config.resetDefaults();
        player.sendMessage(CUtl.tag(CUtl.lang("dirhud.defaults.reset")));
    }
    public static void defaults(ServerPlayerEntity player) {
        MutableText msg = Text.literal(" ").append(CUtl.lang("dirhud.ui.defaults").setStyle(CUtl.pS()))
                .append(Text.literal("\n                                 \n").styled(style -> style.withStrikethrough(true)))
                .append(" ")
                .append(CUtl.button(CUtl.SBtn("dirhud.defaults.set"),CUtl.HEX(CUtl.c.set),1,"/dirhud defaults set",
                        CUtl.lang("button.dirhud.defaults.set.hover")))
                .append("  ")
                .append(CUtl.button(CUtl.SBtn("dirhud.defaults.reset"),CUtl.TC('c'),1,"/dirhud defaults reset",
                        CUtl.lang("button.dirhud.defaults.reset.hover")))
                .append("  ")
                .append(CUtl.CButton.back("/dirhud"))
                .append(Text.literal("\n                                 ").styled(style -> style.withStrikethrough(true)));
        player.sendMessage(msg);
    }
    public static void reload(ServerPlayerEntity player) {
        if (player == null) {
            if (DirectionHUD.config == null) DirectionHUD.config = FabricLoader.getInstance().getConfigDir().toFile()+"/";
            config.load();
            LangReader.loadLanguageFile();
            DirectionHUD.LOGGER.info(CUtl.lang("dirhud.reload", CUtl.lang("dirhud.reload_2")).getString());
            return;
        }
        if (!player.hasPermissionLevel(2)) return;
        MutableText msg = CUtl.tag(CUtl.lang("dirhud.reload",
                CUtl.lang("dirhud.reload_2").setStyle(CUtl.C('a'))));
        if (DirectionHUD.config == null) DirectionHUD.config = FabricLoader.getInstance().getConfigDir().toFile()+"/";
        LangReader.loadLanguageFile();
        config.load();
        player.sendMessage(msg);
    }
    public static void UI(ServerPlayerEntity player) {
        MutableText msg = Text.literal("")
                .append(Text.literal(" DirectionHUD ").setStyle(CUtl.pS()))
                .append(Text.literal("v"+DirectionHUD.VERSION).setStyle(CUtl.sS()))
                .append(Text.literal("\n                                 \n").styled(style -> style.withStrikethrough(true)))
                .append(Text.literal(" "));
        //hud
        if (config.HUDEditing) msg.append(CUtl.CButton.dirHUD.hud()).append("  ");
        //dest
        msg.append(CUtl.CButton.dirHUD.dest());
        if (!DirectionHUD.server.isRemote()) {
            msg.append("\n\n ").append(CUtl.CButton.dirHUD.defaults());
        } else if (player.hasPermissionLevel(2)) {
            msg.append("\n\n ").append(CUtl.CButton.dirHUD.reload());
        }
        msg.append(Text.literal("\n                                 ").styled(style -> style.withStrikethrough(true)));
        player.sendMessage(msg);
    }
}
