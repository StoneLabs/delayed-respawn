package net.stone_labs.delayedrespawn.deathtime;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerPlayerEntity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

public class DeathTimeEntry
{
    private final UUID uuid;
    private final String name;
    private long lastDeath;

    public DeathTimeEntry(GameProfile profile)
    {
        this.uuid = profile.getId();
        this.name = profile.getName();
        setToNow();
    }

    public GameProfile getProfile()
    {
        return new GameProfile(uuid, name);
    }

    public long getLastDeath()
    {
        return lastDeath;
    }

    protected void setToNow()
    {
        this.lastDeath = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

    public long getTimeoutSeconds()
    {
        return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - lastDeath;
    }

    public boolean equalsPlayer(ServerPlayerEntity player)
    {
        return this.getProfile().equals(player.getGameProfile());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeathTimeEntry that = (DeathTimeEntry) o;
        return Objects.equals(uuid, that.uuid) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(uuid, name);
    }
}
