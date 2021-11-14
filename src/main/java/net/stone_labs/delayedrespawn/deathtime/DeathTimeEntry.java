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
    private boolean pardonLastDeath = false;

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

    public long getLastDeathEpoch()
    {
        return lastDeath;
    }

    public LocalDateTime getLastDeath()
    {
        return LocalDateTime.ofEpochSecond(getLastDeathEpoch(), 0, ZoneOffset.UTC);
    }

    public boolean isPardonLastDeath()
    {
        return pardonLastDeath;
    }

    protected void pardonLastDeath()
    {
        this.pardonLastDeath = true;
    }

    protected void unpardonLastDeath()
    {
        this.pardonLastDeath = false;
    }

    protected void setToNow()
    {
        this.lastDeath = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        this.pardonLastDeath = false;
    }

    public long getSecondsSinceDeath()
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
