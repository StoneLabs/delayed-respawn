package net.stone_labs.delayedrespawn;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.stone_labs.delayedrespawn.commands.PardonDeathCommand;
import net.stone_labs.delayedrespawn.commands.TimeoutsCommand;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DelayedRespawn implements DedicatedServerModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "delayedrespawn";
    public static final String MOD_NAME = "Delayed Respawn";
    public static final String VERSION = "1.4.0";

    @Override
    public void onInitializeServer()
    {
        LOGGER.log(Level.INFO, "Initialized {} version {}", MOD_NAME, VERSION);

        // Add command to display artifacts for debugging
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> PardonDeathCommand.register(dispatcher));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> TimeoutsCommand.register(dispatcher));
    }

    public static int getDeathTimeoutLength(MinecraftServer server)
    {
        return server.getGameRules().get(DelayedRespawn.DEATH_TIMEOUT).get();
    }

    public static final GameRules.Key<GameRules.IntRule> DEATH_TIMEOUT = GameRuleRegistry.register("deathTimeout", GameRules.Category.PLAYER, GameRuleFactory.createIntRule(60, 0));
}
