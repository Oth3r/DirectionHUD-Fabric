package one.oth3r.directionhud.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import one.oth3r.directionhud.commands.Destination;
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Utl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class OnPlayerWorldChanged {
    @Inject(at = @At("HEAD"), method = "worldChanged(Lnet/minecraft/server/world/ServerWorld;)V")
    public void worldChangedCallback(ServerWorld world, CallbackInfo info) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (PlayerData.get.dest.setting.autoclear(player) && !Destination.get(player, "xyz").equals("f")) {
            String dest = Destination.get(player,"xyz");
            String dim = world.getRegistryKey().getValue().getPath();
            MutableText msg = Text.literal("").append(CUtl.lang("dest.cleared_dim").styled(style -> style
                            .withItalic(true).withColor(CUtl.TC('7'))))
                    .append(" ").append(CUtl.CButton.dest.set("/dest set "+dest));
            if (Utl.dim.showConvertButton(Utl.player.dim(player),dim)) {
                msg.append(" ")
                        .append(CUtl.CButton.dest.convert("/dest set "+dest+" "+dim));
            }
            Destination.clear(player, msg);
        }
    }
}
