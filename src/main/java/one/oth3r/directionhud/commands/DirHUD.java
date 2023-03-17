package one.oth3r.directionhud.commands;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.utils.CUtl;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

public class DirHUD {
    public static void UI(ServerPlayerEntity player) {

        //todo
        // maybe a command like /dirhud setdefaults
        // for client only, and sets the defaults for the mod
        // if a reload command is needed, add one
        Text msg = Text.literal("");
        msg = Text.literal("").append(msg)
                .append(Text.literal(" DirectionHUD ").setStyle(CUtl.pS()))
                .append(Text.literal("v"+DirectionHUD.VERSION).setStyle(CUtl.sS()))
                .append(Text.literal("\n                                 \n").styled(style -> style.withStrikethrough(true)))
                .append(Text.literal(" "));
        //hud
        msg = Text.literal("").append(msg).append(CUtl.CButton.dirHUD.hud())
                .append("  ");
        //dest
        msg = Text.literal("").append(msg).append(CUtl.CButton.dirHUD.dest());

        msg = Text.literal("").append(msg).append(Text.literal("\n                                 ").styled(style -> style.withStrikethrough(true)));
        player.sendMessage(msg);
    }
}
