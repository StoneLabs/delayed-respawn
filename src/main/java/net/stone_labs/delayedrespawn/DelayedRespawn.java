package net.stone_labs.delayedrespawn;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;
import net.stone_labs.delayedrespawn.deathtime.DeathTimeFile;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DelayedRespawn implements DedicatedServerModInitializer
{
    public static class PlayerJoinEvent implements ServerPlayConnectionEvents.Join
    {
        @Override
        public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server)
        {
            long secondsSinceDeath = DeathTimeFile.getInstance().getSecondsSinceDeath(handler.player).orElse(Long.MAX_VALUE);
            int secondsTillReconnect = DelayedRespawn.getDeathTimeoutLength(server);

            if (secondsSinceDeath < secondsTillReconnect)
            {
                long timeout = secondsTillReconnect - secondsSinceDeath;
                handler.disconnect(new LiteralText(
                        String.format("You still have to wait %dh %dm %ds before connecting.",
                                timeout / 60 / 60, timeout / 60 % 60, timeout % 60)
                ).formatted(Formatting.RED));
                server.getPlayerManager().broadcast(new LiteralText(
                        String.format("%s still has to wait %dh %dm %ds before connecting.",
                                handler.player.getEntityName(), timeout / 60 / 60, timeout / 60 % 60, timeout % 60)
                ).formatted(Formatting.RED), MessageType.CHAT, handler.player.getUuid());
            }
        }
    }

    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "delayedrespawn";
    public static final String MOD_NAME = "Delayed Respawn";
    public static final String VERSION = "1.2.0";

    @Override
    public void onInitializeServer()
    {
        ServerPlayConnectionEvents.JOIN.register(new PlayerJoinEvent());
        LOGGER.log(Level.INFO, "Initialized {} version {}", MOD_NAME, VERSION);
    }

    public static int getDeathTimeoutLength(MinecraftServer server)
    {
        return server.getGameRules().get(DelayedRespawn.DEATH_TIMEOUT).get();
    }

    public static final GameRules.Key<GameRules.IntRule> DEATH_TIMEOUT = GameRuleRegistry.register("deathTimeout", GameRules.Category.PLAYER, GameRuleFactory.createIntRule(60, 0));
}
