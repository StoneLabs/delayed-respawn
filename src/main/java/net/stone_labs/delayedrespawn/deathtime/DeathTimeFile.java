package net.stone_labs.delayedrespawn.deathtime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

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
        return entry.map(DeathTimeEntry::getSecondsSinceDeath);
    }

    public void pardonLastDeath(ServerPlayerEntity player)
    {
        pardonLastDeath(player.getGameProfile());
    }
    public void pardonLastDeath(GameProfile player)
    {
        Optional<DeathTimeEntry> entry = getPlayerEntry(player);

        if (entry.isEmpty())
            return;

        entry.get().pardonLastDeath();
        write();
    }

    public void unpardonLastDeath(ServerPlayerEntity player)
    {
        unpardonLastDeath(player.getGameProfile());
    }
    public void unpardonLastDeath(GameProfile player)
    {
        Optional<DeathTimeEntry> entry = getPlayerEntry(player);

        if (entry.isEmpty())
            return;

        entry.get().unpardonLastDeath();
        write();
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

    public boolean isInTimeout(ServerPlayerEntity player, int timeout)
    {
        return isInTimeout(player, timeout, false);
    }
    public boolean isInTimeout(ServerPlayerEntity player, int timeout, boolean ignorePardon)
    {
        return isInTimeout(player.getGameProfile(), timeout, false);
    }
    public boolean isInTimeout(GameProfile player, int timeout)
    {
        return isInTimeout(player, timeout, false);
    }
    public boolean isInTimeout(GameProfile player, int timeout, boolean ignorePardon)
    {
        return getSecondsLeftInTimeout(player, timeout, ignorePardon).isPresent();
    }

    public Optional<Integer> getSecondsLeftInTimeout(ServerPlayerEntity player, int timeout)
    {
        return getSecondsLeftInTimeout(player, timeout, false);
    }
    public Optional<Integer> getSecondsLeftInTimeout(ServerPlayerEntity player, int timeout, boolean ignorePardon)
    {
        return getSecondsLeftInTimeout(player.getGameProfile(), timeout, false);
    }
    public Optional<Integer> getSecondsLeftInTimeout(GameProfile player, int timeout)
    {
        return getSecondsLeftInTimeout(player, timeout, false);
    }
    public Optional<Integer> getSecondsLeftInTimeout(GameProfile player, int timeout, boolean ignorePardon)
    {
        Optional<DeathTimeEntry> entry = getPlayerEntry(player);

        if (entry.isEmpty())
            return Optional.empty();

        if (entry.get().isPardonLastDeath() && !ignorePardon)
            return Optional.empty();

        if (entry.get().getSecondsSinceDeath() > timeout)
            return Optional.empty();

        // Since timeout >= secondsSinceDeath, the difference must be >= 0 and can
        // not be bigger than int.maxvalue the conversion is therefore safe.
        return Optional.of(timeout - (int)entry.get().getSecondsSinceDeath());
    }

    public String[] getNamesInTimeout(int timeout)
    {
        return getNamesInTimeout(timeout, false);
    }
    public String[] getNamesInTimeout(int timeout, boolean ignorePardon)
    {
        return this.entries.stream()
                .filter(x -> x.getSecondsSinceDeath() < timeout)
                .filter(x -> !x.isPardonLastDeath() || ignorePardon)
                .map(DeathTimeEntry::getProfile)
                .filter(Objects::nonNull)
                .map(GameProfile::getName)
                .toArray(String[]::new);
    }

    public String[] getPardonedNames()
    {
        return this.entries.stream()
                .filter(DeathTimeEntry::isPardonLastDeath)
                .map(DeathTimeEntry::getProfile)
                .filter(Objects::nonNull)
                .map(GameProfile::getName)
                .toArray(String[]::new);
    }

    public String[] getNames()
    {
        return this.entries.stream()
                .map(DeathTimeEntry::getProfile)
                .filter(Objects::nonNull)
                .map(GameProfile::getName)
                .toArray(String[]::new);
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