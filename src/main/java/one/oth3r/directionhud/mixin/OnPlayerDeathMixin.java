package one.oth3r.directionhud.mixin;

import one.oth3r.directionhud.commands.Destination;
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.files.config;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Loc;
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
        Loc loc = new Loc(player);
        Destination.lastdeath.add(player, loc);
        CTxT msg = CUtl.tag().append(CUtl.lang("dest.lastdeath.save"))
                .append(" ").append(loc.getBadge())
                .append(" ").append(CUtl.CButton.dest.set("/dest set "+loc.getXYZ()));
        if (Utl.dim.showConvertButton(Utl.dim.format(player.getSpawnPointDimension().getValue()),loc.getDIM()))
            msg.append(" ").append(CUtl.CButton.dest.convert("/dest set "+loc.getXYZ()+" "+loc.getDIM()));
        player.sendMessage(msg.b());
    }
}
