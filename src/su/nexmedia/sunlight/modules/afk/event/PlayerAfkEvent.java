package su.nexmedia.sunlight.modules.afk.event;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.manager.api.event.ICancellableEvent;
import su.nexmedia.sunlight.data.SunUser;

public class PlayerAfkEvent extends ICancellableEvent {

    private final Player  player;
    private final SunUser user;
    private final boolean isAfk;

    public PlayerAfkEvent(
        @NotNull Player player,
        @NotNull SunUser user,
        boolean isAfk
    ) {
        this.player = player;
        this.user = user;
        this.isAfk = isAfk;
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @NotNull
    public SunUser getUser() {
        return this.user;
    }

    public boolean isAfk() {
        return this.isAfk;
    }
}
