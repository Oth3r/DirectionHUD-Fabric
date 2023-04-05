package one.oth3r.directionhud.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import one.oth3r.directionhud.DirectionHUDClient;
import one.oth3r.directionhud.commands.HUD;
import one.oth3r.directionhud.utils.CUtl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(InGameHud.class)
public class ActionBarMixin {
    @Inject(at = @At("HEAD"), method = "setOverlayMessage(Lnet/minecraft/text/Text;Z)V")
    private void sendMessage(Text message, boolean tinted, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!client.isInSingleplayer() && !DirectionHUDClient.onSupportedServer) return;
        if (HUD.lastBar.getString().equals("") || message.getString().equals("")) return;
        if (!Objects.equals(HUD.lastBar.getString().charAt(0), message.getString().charAt(0))) {
            System.out.println(HUD.lastBar.getString());
            System.out.println(message.getString());
            //when the client is lagging, it might put the hud's message in chat, also if the first char in the hud matches the actionhud it also might mess up
            // theres probably a better way to do it, ill think bout it
            assert client.player != null;
            client.player.sendMessage(CUtl.tag(message));
        }
    }
}