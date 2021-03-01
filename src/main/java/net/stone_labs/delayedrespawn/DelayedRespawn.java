package net.stone_labs.delayedrespawn;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import static net.stone_labs.delayedrespawn.DeathTimeManger.getTimeSinceLastDeath;
import static net.stone_labs.delayedrespawn.DeathTimeManger.readDeathTimeoutConfig;

public class DelayedRespawn implements DedicatedServerModInitializer
{
    public static class PlayerJoinEvent implements ServerPlayConnectionEvents.Join
    {
        @Override
        public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server)
        {
            long secondsSinceDeath = getTimeSinceLastDeath(handler.player);
            long secondsTillReconnect = readDeathTimeoutConfig();
            if (secondsSinceDeath < secondsTillReconnect)
            {
                long timeout = secondsTillReconnect - secondsSinceDeath;
                handler.disconnect(new LiteralText(
                        String.format("You still have to wait %dh %dm %ds before connecting.",
                                timeout / 60 / 60, timeout / 60 % 60, timeout % 60)
                ).formatted(Formatting.RED));
            }
        }
    }

    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "delayedrespawn";
    public static final String MOD_NAME = "Delayed Respawn";
    public static final String VERSION = "1.0.0";

    @Override
    public void onInitializeServer()
    {
        ServerPlayConnectionEvents.JOIN.register(new PlayerJoinEvent());
        LOGGER.log(Level.INFO, "Initialized {} version {}", MOD_NAME, VERSION);
    }
}
