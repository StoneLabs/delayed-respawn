package net.stone_labs.delayedrespawn.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.stone_labs.delayedrespawn.DelayedRespawn;
import net.stone_labs.delayedrespawn.Utils;
import net.stone_labs.delayedrespawn.deathtime.DeathTimeFile;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TimeoutsCommand
{
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(literal("timeouts")
                .requires((source) -> source.hasPermissionLevel(2))
                .then(argument("page", IntegerArgumentType.integer(0))
                        .executes((context) -> list(context.getSource(), IntegerArgumentType.getInteger(context, "page"))))
                .executes((context) -> list(context.getSource(), 1)));
    }

    private static class TableEntry
    {
        String name;
        int secondsLeft;
        boolean isPardon;

        public TableEntry(String name, int secondsLeft, boolean isPardon)
        {
            this.name = name;
            this.secondsLeft = secondsLeft;
            this.isPardon = isPardon;
        }

        public void buildRow(StringBuilder builder, int index)
        {
            builder.append("\"\n- %2d: \", {\"text\":\"%s\"}, "
                    .formatted(index, name));

            if (secondsLeft > 0)
                builder.append("{\"text\":\" %s left\",\"color\":\"green\"}, ".formatted(Utils.FormatDuration(secondsLeft)));
            else
                builder.append("{\"text\":\" 0s left\",\"color\":\"red\"}, ");

            if (isPardon)
                builder.append("{\"text\":\" (PARDONED)\",\"color\":\"yellow\"}, ");

            builder.append("\" \", ");
        }
    }

    private static int list(ServerCommandSource source, int page)
    {
        int timeout = DelayedRespawn.getDeathTimeoutLength(source.getServer());

        // Name -> (timeout left, pardoned)
        List<TableEntry> data = DeathTimeFile.getInstance().getEntries().stream()
                .map(x -> new TableEntry(x.getProfile().getName(),
                        (int) Math.max(0, timeout - x.getSecondsSinceDeath()),
                        x.isPardonLastDeath()))
                .collect(Collectors.toList());

        source.sendFeedback(listPage(data, page), false);
        return 0;
    }

    public static int pageLimit = 8;

    private static Text listPage(List<TableEntry> data, int page)
    {
        StringBuilder builder = new StringBuilder();

        builder.append("[\"\nThe following entries were found:\", ");
        for (int i = pageLimit * (page - 1); i < page * pageLimit; i++)
        {
            if (i < data.size())
            {
                data.get(i).buildRow(builder, i);
            }
            else
                builder.append("\"\n- %2d: \", ".formatted(i));
        }

        int maxPage = (data.size() - 1) / pageLimit + 1;
        if (page == 1)
            builder.append("\"\n<<<<\", ");
        else
            builder.append("\"\n\", {\"text\":\"<<<<\",\"underlined\":true,\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/timeouts %d\"}}, ".formatted(page - 1));
        builder.append("\" | Page %d of %d | \", ".formatted(page, maxPage));
        if (page == maxPage)
            builder.append("\">>>>\", ");
        else
            builder.append(" {\"text\":\">>>>\",\"underlined\":true,\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/timeouts %d\"}}, ".formatted(page + 1));

        builder.append("\"\"]");
        //return new LiteralText(builder.toString());
        return Text.Serializer.fromJson(builder.toString());
    }
}
