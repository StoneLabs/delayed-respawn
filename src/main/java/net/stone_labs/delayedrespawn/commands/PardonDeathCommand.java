package net.stone_labs.delayedrespawn.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.stone_labs.delayedrespawn.DelayedRespawn;
import net.stone_labs.delayedrespawn.deathtime.DeathTimeFile;

import java.util.Collection;

import static net.minecraft.command.argument.GameProfileArgumentType.getProfileArgument;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PardonDeathCommand
{
    private static final SimpleCommandExceptionType NO_EFFECTIVE_PARDON = new SimpleCommandExceptionType(new LiteralText("No death to pardon"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(literal("pardon-death")
                .requires((source) -> source.hasPermissionLevel(2))
                .then(argument("targets", GameProfileArgumentType.gameProfile())
                        .suggests((context, builder) ->
                                CommandSource.suggestMatching(
                                        DeathTimeFile.getInstance().getNamesInTimeout(
                                                DelayedRespawn.getDeathTimeoutLength(context.getSource().getServer())), builder))
                        .executes((context) -> pardonDeath(context.getSource(), getProfileArgument(context, "targets")))));
        dispatcher.register(literal("unpardon-death")
                .requires((source) -> source.hasPermissionLevel(2))
                .then(argument("targets", GameProfileArgumentType.gameProfile())
                        .suggests((context, builder) ->
                                CommandSource.suggestMatching(
                                        DeathTimeFile.getInstance().getPardonedNames(), builder))
                        .executes((context) -> unpardonDeath(context.getSource(), getProfileArgument(context, "targets")))));
    }

    private static int pardonDeath(ServerCommandSource source, Collection<GameProfile> targets) throws CommandSyntaxException
    {
        int i = 0;

        for (GameProfile gameProfile : targets)
        {
            if (DeathTimeFile.getInstance().containsPlayer(gameProfile))
            {
                DeathTimeFile.getInstance().pardonLastDeath(gameProfile);
                ++i;
                source.sendFeedback(new LiteralText("Pardoning the last death of " + gameProfile.getName()), true);
            }
        }

        if (i == 0)
            throw NO_EFFECTIVE_PARDON.create();
        else
            return i;
    }

    private static int unpardonDeath(ServerCommandSource source, Collection<GameProfile> targets) throws CommandSyntaxException
    {
        int i = 0;

        for (GameProfile gameProfile : targets)
        {
            if (DeathTimeFile.getInstance().containsPlayer(gameProfile))
            {
                DeathTimeFile.getInstance().unpardonLastDeath(gameProfile);
                ++i;
                source.sendFeedback(new LiteralText("Un-pardoning the last death of " + gameProfile.getName()), true);
            }
        }

        if (i == 0)
            throw NO_EFFECTIVE_PARDON.create();
        else
            return i;
    }
}