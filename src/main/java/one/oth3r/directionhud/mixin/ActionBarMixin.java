package one.oth3r.directionhud.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
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
        if (!Objects.equals(HUD.lastBar.getString(), message.getString()) && message != Text.of("")) {
            MinecraftClient client = MinecraftClient.getInstance();
            assert client.player != null;
            client.player.sendMessage(CUtl.tag(message));
        }
    }
}
