package su.nexmedia.sunlight.modules.homes.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.homes.Home;
import su.nexmedia.sunlight.modules.homes.HomeManager;
import su.nexmedia.sunlight.modules.homes.event.PlayerTeleportHomeEvent;

public class HomeListener extends AbstractListener<SunLight> {

    private final HomeManager homeManager;

    public HomeListener(@NotNull HomeManager homeManager) {
        super(homeManager.plugin());
        this.homeManager = homeManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHomeRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        Home home = homeManager.getPlayerHomeRespawn(player);
        if (home == null) return;

        e.setRespawnLocation(home.getLocation());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHomeTeleport(PlayerTeleportHomeEvent e) {
        Player player = e.getPlayer();
        Home home = e.getHome();
        (home.isOwner(player) ? homeManager.getLang().Command_Home_Done_Own : homeManager.getLang().Command_Home_Done_Other)
            .replace(home.replacePlaceholders()).send(player);
    }
}
