package one.oth3r.directionhud.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.commands.Destination;
import one.oth3r.directionhud.utils.Utl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class OnPlayerChangeDimensionMixin {
    @Inject(at = @At("HEAD"), method = "worldChanged(Lnet/minecraft/server/world/ServerWorld;)V")
    public void onChangeDimension(ServerWorld world, CallbackInfo info) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        DirectionHUD.LOGGER.info(player.getName()+world.asString());
    }
}
