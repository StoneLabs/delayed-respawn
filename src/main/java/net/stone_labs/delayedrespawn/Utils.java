package net.stone_labs.delayedrespawn;

public class Utils
{
    public static String FormatDuration(long seconds)
    {
        int d_seconds   = (int) (seconds % 60);
        int d_minutes   = (int) (seconds / (60L) % 60);
        int d_hours     = (int) (seconds / (60L * 60L) % 24);
        long d_days     = seconds / (60L * 60L * 24L);

        StringBuilder builder = new StringBuilder();
        if (d_days > 0)
            builder.append(d_days).append("d ");
        if (d_hours > 0)
            builder.append(d_hours).append("h ");
        if (d_minutes > 0)
            builder.append(d_minutes).append("m ");
        if (d_seconds > 0)
            builder.append(d_seconds).append("s ");

        return builder.toString().trim();
    }
}
