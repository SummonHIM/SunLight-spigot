package su.nexmedia.sunlight.modules.spawn.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.spawn.Spawn;
import su.nexmedia.sunlight.modules.spawn.SpawnManager;

public class SpawnListener extends AbstractListener<SunLight> {

    private final SpawnManager spawnManager;

    public SpawnListener(@NotNull SpawnManager spawnManager) {
        super(spawnManager.plugin());
        this.spawnManager = spawnManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawnJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Spawn spawn = this.spawnManager.getSpawnByLogin(player);
        if (spawn == null) return;

        player.teleport(spawn.getLocation());
    }

    // Respawn at spawn
    @EventHandler(priority = EventPriority.NORMAL)
    public void onSpawnRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        Spawn spawn = this.spawnManager.getSpawnByDeath(player);
        if (spawn == null) return;

        e.setRespawnLocation(spawn.getLocation());
    }
}
