package one.oth3r.directionhud.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import one.oth3r.directionhud.DirectionHUDClient;
import one.oth3r.directionhud.commands.HUD;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class OnClientDisconnect {
    @Inject(at = @At("HEAD"), method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V")
    private void onClientDisconnect(CallbackInfo info) {
        DirectionHUDClient.onSupportedServer = false;
        HUD.lastBar = Text.of("");
    }
}
