package net.stone_labs.delayedrespawn.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.stone_labs.delayedrespawn.DelayedRespawn;
import net.stone_labs.delayedrespawn.deathtime.DeathTimeFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerDeathMixin
{
    @Inject(at = @At(value = "TAIL"), method = "onDeath", cancellable = true)
    private void onPlayerDeath(DamageSource source, CallbackInfo info)
    {
        //noinspection ConstantConditions
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        MinecraftServer server = player.getServer();

        assert server != null;

        DeathTimeFile.getInstance().registerDeath(player);
        player.networkHandler.disconnect(Text.literal("You died :(\nYou can reconnect once your cooldown has expired."));

        long timeout = DelayedRespawn.getDeathTimeoutLength(server);
        server.getPlayerManager().broadcast(Text.literal(
                String.format("%s died and has to take %sh %sm %ss timeout",
                        player.getEntityName(), timeout / 60 / 60, timeout / 60 % 60, timeout % 60)
        ).formatted(Formatting.RED), false);
    }
}
