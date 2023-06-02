package one.oth3r.directionhud.fabric.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.fabric.files.PlayerData;
import one.oth3r.directionhud.fabric.utils.CTxT;
import one.oth3r.directionhud.fabric.utils.CUtl;
import one.oth3r.directionhud.fabric.utils.Loc;
import one.oth3r.directionhud.fabric.utils.Utl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class OnPlayerWorldChanged {
    @Inject(at = @At("HEAD"), method = "worldChanged(Lnet/minecraft/server/world/ServerWorld;)V")
    public void worldChangedCallback(ServerWorld world, CallbackInfo info) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (Destination.get(player).hasXYZ()) {
            Loc loc = Destination.get(player);
            String oldDIM = Utl.dim.format(world.getRegistryKey().getValue());
            if (Utl.dim.canConvert(Utl.player.dim(player),Destination.get(player).getDIM()) && PlayerData.get.dest.setting.autoconvert(player)
                    && !Utl.player.dim(player).equals(Destination.get(player).getDIM())) {
                Loc cLoc = Destination.get(player);
                cLoc.convertTo(Utl.player.dim(player));
                Destination.silentSet(player,cLoc);
                player.sendMessage(CUtl.tag().append(CUtl.lang("dest.autoconvert.dest"))
                        .append("\n ").append(CUtl.lang("dest.autoconvert.info",loc.getBadge(),cLoc.getBadge()).italic(true).color('7')).b());
            } else if (PlayerData.get.dest.setting.autoclear(player)) {
                CTxT msg = CTxT.of("").append(CUtl.lang("dest.changed.cleared.dim").color('7').italic(true))
                        .append(" ").append(CUtl.CButton.dest.set("/dest set "+loc.getXYZ()+" "+oldDIM));
                if (Utl.dim.canConvert(Utl.player.dim(player),Destination.get(player).getDIM()))
                    msg.append(" ").append(CUtl.CButton.dest.convert("/dest set "+loc.getXYZ()+" "+oldDIM+" convert"));
                Destination.clear(player, msg);
            }
        }
    }
}
