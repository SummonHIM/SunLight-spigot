package su.nexmedia.sunlight.modules.homes.event;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.manager.api.event.IEvent;
import su.nexmedia.sunlight.modules.homes.Home;

public class PlayerTeleportHomeEvent extends IEvent {

    private final Player player;
    private final Home   home;

    public PlayerTeleportHomeEvent(@NotNull Player player, @NotNull Home home) {
        this.player = player;
        this.home = home;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public Home getHome() {
        return home;
    }
}
