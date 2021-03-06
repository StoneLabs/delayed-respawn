package net.stone_labs.delayedrespawn.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.stone_labs.delayedrespawn.DeathTimeManger.registerDeath;

@Mixin(ServerPlayerEntity.class)
public class PlayerDeathMixin
{
    @Inject(at = @At(value = "TAIL", target = "Lnet/minecraft/world/World;sendEntityStatus(Lnet/minecraft/entity/Entity;B)V"), method = "onDeath", cancellable = true)
    private void onPlayerDeath(DamageSource source, CallbackInfo info)
    {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        MinecraftServer server = player.getServer();
        String playerID = player.getEntityName();

        registerDeath(player);
        player.networkHandler.disconnect(new LiteralText("You died :(\nYou can reconnect once your cooldown has expired."));
    }
}
