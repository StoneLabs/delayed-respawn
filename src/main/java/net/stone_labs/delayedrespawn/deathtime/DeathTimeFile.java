package net.stone_labs.delayedrespawn.deathtime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.stone_labs.delayedrespawn.DelayedRespawn;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DeathTimeFile
{
    private static final DeathTimeFile INSTANCE = new DeathTimeFile("./timeouts.json");
    public static DeathTimeFile getInstance() { return INSTANCE; }

    private List<DeathTimeEntry> entries = new ArrayList<>();

    private final Path file;
    private final transient Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public DeathTimeFile(String file)
    {
        this.file = Paths.get(file);

        if (!Files.exists(Paths.get(file)))
            write();
        else
            read();
    }

    public boolean containsPlayer(@NotNull ServerPlayerEntity player)
    {
        return containsPlayer(player.getGameProfile());
    }
    public boolean containsPlayer(GameProfile player)
    {
        return this.entries.stream().map(DeathTimeEntry::getProfile).anyMatch(x -> x.equals(player));
    }

    public Optional<DeathTimeEntry> getPlayerEntry(@NotNull ServerPlayerEntity player)
    {
        return getPlayerEntry(player.getGameProfile());
    }
    public Optional<DeathTimeEntry> getPlayerEntry(GameProfile player)
    {
        return this.entries.stream().filter(x -> x.getProfile().equals(player)).findFirst();
    }

    public Optional<Long> getSecondsSinceDeath(ServerPlayerEntity player)
    {
        Optional<DeathTimeEntry> entry = getPlayerEntry(player);
        return entry.map(DeathTimeEntry::getTimeoutSeconds);
    }

    public String[] getNames()
    {
        return this.entries.stream().map(DeathTimeEntry::getProfile).filter(Objects::nonNull).map(GameProfile::getName).toArray(String[]::new);
    }

    public void registerDeath(ServerPlayerEntity player)
    {
        Optional<DeathTimeEntry> entry = getPlayerEntry(player);

        if (entry.isPresent())
            entry.get().setToNow();
        else
            entries.add(new DeathTimeEntry(player.getGameProfile()));

        write();
    }

    private void read()
    {
        try
        {
            String content = new String(Files.readAllBytes(file));
            this.entries = gson.fromJson(content, new TypeToken<List<DeathTimeEntry>>(){}.getType());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Could not read timeout file!");
        }
    }

    private void write()
    {
        try
        {
            Files.write(file, gson.toJson(this.entries).getBytes());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Could not write timeout file!");
        }
    }
}