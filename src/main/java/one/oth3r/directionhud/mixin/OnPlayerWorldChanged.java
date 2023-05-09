package one.oth3r.directionhud.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import one.oth3r.directionhud.commands.Destination;
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Loc;
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
        if (PlayerData.get.dest.setting.autoclear(player) && Destination.get(player).hasXYZ()) {
            Loc loc = Destination.get(player);
            String dim = world.getRegistryKey().getValue().getPath();
            CTxT msg = CTxT.of("").append(CUtl.lang("dest.cleared_dim").color('7').italic(true))
                    .append(" ").append(CUtl.CButton.dest.set("/dest set "+loc.getXYZ()));
            if (Utl.dim.showConvertButton(Utl.player.dim(player),dim))
                msg.append(" ").append(CUtl.CButton.dest.convert("/dest set "+loc.getXYZ()+" "+dim));
            Destination.clear(player, msg);
        }
    }
}
