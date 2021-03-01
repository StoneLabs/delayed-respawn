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

public class MidnightRespawn implements DedicatedServerModInitializer
{
    public final static long waitTime = 60;

    public class PlayerJoinEvent implements ServerPlayConnectionEvents.Join
    {
        @Override
        public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server)
        {
            long secondsSinceDeath = getTimeSinceLastDeath(handler.player);
            if (secondsSinceDeath < waitTime)
            {
                long timeout = waitTime - secondsSinceDeath;
                handler.disconnect(new LiteralText(
                        String.format("You still have to wait %dh %dm %ds before connecting.",
                                timeout / 60 / 60, timeout / 60 % 60, timeout % 60)
                ).formatted(Formatting.RED));
            }
        }
    }

    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "midnightrespawn";
    public static final String MOD_NAME = "Midnight Respawn";
    public static final String VERSION = "1.0.0";

    @Override
    public void onInitializeServer()
    {
        ServerPlayConnectionEvents.JOIN.register(new PlayerJoinEvent());
        LOGGER.log(Level.INFO, "Initialized {} version {}", MOD_NAME, VERSION);
    }
}
