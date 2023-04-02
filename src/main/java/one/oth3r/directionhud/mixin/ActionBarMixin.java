package one.oth3r.directionhud.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import one.oth3r.directionhud.DirectionHUD;
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
        if (!Objects.equals(HUD.lastBar.getString().charAt(1), message.getString().charAt(1)) && message != Text.of("")) {
            //when the client is lagging, it might put the hud's message in chat, also if the first char in the hud matches the actionhud it also might mess up
            // theres probably a better way to do it, ill think bout it
            MinecraftClient client = MinecraftClient.getInstance();
            assert client.player != null;
            client.player.sendMessage(CUtl.tag(message));
        }
    }
}