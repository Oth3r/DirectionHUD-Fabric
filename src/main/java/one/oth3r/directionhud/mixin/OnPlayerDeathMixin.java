package one.oth3r.directionhud.mixin;

import one.oth3r.directionhud.commands.Destination;
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.files.config;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Utl;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class OnPlayerDeathMixin {
    @Inject(at = @At("HEAD"), method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V")
    public void onDeathCallback(DamageSource source, CallbackInfo onDeathCallbackInfoReturnable) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (!config.deathsaving || !PlayerData.get.dest.setting.lastdeath(player)) return;
        Destination.lastdeath.add(player, Utl.player.dim(player), Utl.player.XYZ(player));
        CTxT msg = CUtl.tag().append(CUtl.lang("dest.lastdeath.save"))
                .append(" ").append(CUtl.xyzBadge(Utl.player.XYZ(player),Utl.player.dim(player),null,null))
                .append(" ").append(CUtl.CButton.dest.set("/dest set "+Utl.player.XYZ(player)));
        if (Utl.dim.showConvertButton(Utl.dim.format(player.getSpawnPointDimension().getValue()),Utl.player.dim(player)))
            msg.append(" ").append(CUtl.CButton.dest.convert("/dest set "+Utl.player.XYZ(player)+" "+Utl.player.dim(player)));
        player.sendMessage(msg.b());
    }
}
