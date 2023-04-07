package one.oth3r.directionhud.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.DirectionHUDClient;
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.utils.CUtl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class ActionBarMixin {
    @Inject(at = @At("HEAD"), method = "setOverlayMessage(Lnet/minecraft/text/Text;Z)V")
    private void sendMessage(Text message, boolean tinted, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClickEvent click = message.getStyle().getClickEvent();
        if (click == null || !click.getValue().equals("https://modrinth.com/mod/directionhud")) {
            if (client.player != null) {
                if (client.isInSingleplayer() && PlayerData.get.hud.state(DirectionHUD.server.getPlayerManager().getPlayer(client.player.getUuid()))) {
                    client.player.sendMessage(CUtl.tag(message));
                } else if (DirectionHUDClient.onSupportedServer && DirectionHUDClient.hudState) {
                    client.player.sendMessage(CUtl.tag(message));
                }
            }
        }
    }
}