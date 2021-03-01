package net.stone_labs.delayedrespawn;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.network.ServerPlayerEntity;
import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DeathTimeManger
{
    public static class DeathTimeEntry
    {
        public String PlayerEntityName;
        public long LastDeathTime;

        public DeathTimeEntry(String playerEntityName)
        {
            this.PlayerEntityName = playerEntityName;
            setToNow();
        }

        public void setToNow()
        {
            this.LastDeathTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        }

        public long getTimeoutSeconds()
        {
            return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - LastDeathTime;
        }
    }

    public static final String DataFilePath = "./timeouts.json";
    private static final Gson gson = new Gson();

    // Returns seconds left in ban
    public static long getTimeSinceLastDeath(ServerPlayerEntity player)
    {
        List<DeathTimeEntry> timeouts = readDeaths();
        for (DeathTimeEntry timeout : timeouts)
            if (timeout.PlayerEntityName.equals(player.getEntityName()))
                return timeout.getTimeoutSeconds();

        return Long.MAX_VALUE;
    }

    public static void createConfigIfNotExist() throws IOException
    {
        if (!Files.exists(Paths.get(DataFilePath)))
            Files.write(Paths.get(DataFilePath), gson.toJson(new ArrayList<DeathTimeEntry>()).getBytes());
    }

    public static void registerDeath(ServerPlayerEntity player)
    {
        List<DeathTimeEntry> timeouts = readDeaths();

        boolean found = false;
        for (DeathTimeEntry timeout : timeouts)
            if (timeout.PlayerEntityName.equals(player.getEntityName()))
            {
                timeout.setToNow();
                found = true;
                break;
            }

        if (!found)
            timeouts.add(new DeathTimeEntry(player.getEntityName()));

        try
        {
            Gson gson = new Gson();
            Files.write(Paths.get(DataFilePath), gson.toJson(timeouts).getBytes());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Could not write timeout file!");
        }
    }

    public static List<DeathTimeEntry> readDeaths()
    {
        try
        {
            createConfigIfNotExist();
            String file = new String(Files.readAllBytes(Paths.get(DataFilePath)));
            return gson.fromJson(file, new TypeToken<ArrayList<DeathTimeEntry>>(){}.getType());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Could not read timeout file!");
        }
    }
}
