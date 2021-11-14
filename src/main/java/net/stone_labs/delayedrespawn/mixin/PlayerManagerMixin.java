package net.stone_labs.delayedrespawn.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.stone_labs.delayedrespawn.DelayedRespawn;
import net.stone_labs.delayedrespawn.Utils;
import net.stone_labs.delayedrespawn.deathtime.DeathTimeFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;
import java.util.Optional;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin
{
    @Inject(at = @At(value = "HEAD"), method = "checkCanJoin", cancellable = true)
    private void checkCanJoin(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Text> info)
    {
        //noinspection ConstantConditions
        PlayerManager manager = (PlayerManager)(Object)this;
        MinecraftServer server = manager.getServer();

        Optional<Integer> secondsLeft = DeathTimeFile.getInstance().getSecondsLeftInTimeout(profile, DelayedRespawn.getDeathTimeoutLength(server));
        if (secondsLeft.isPresent())
        {
            info.setReturnValue(new LiteralText(
                    String.format("You still have to wait %s before connecting.", Utils.FormatDuration(secondsLeft.get()))
            ).formatted(Formatting.RED));
            server.getPlayerManager().broadcast(new LiteralText(
                    String.format("%s tried to join the game (%s timeout left)",
                            profile.getName(), Utils.FormatDuration(secondsLeft.get()))
            ).formatted(Formatting.YELLOW), MessageType.CHAT, profile.getId());
        }
    }
}
