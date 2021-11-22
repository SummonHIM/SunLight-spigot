package su.nexmedia.sunlight.modules.fixer.entitylimiter;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.sunlight.SunLight;

public class EntityLimitListener extends AbstractListener<SunLight> {

    private final EntityLimiterManager limiterManager;

    public EntityLimitListener(@NotNull EntityLimiterManager limiterManager) {
        super(limiterManager.plugin());
        this.limiterManager = limiterManager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChunkLoadEvent(ChunkLoadEvent e) {
        this.limiterManager.clearChunk(e.getChunk());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChunkUnloadEvent(ChunkUnloadEvent e) {
        this.limiterManager.clearChunk(e.getChunk());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntitySpawn(CreatureSpawnEvent e) {
        if (!this.limiterManager.canSpawn(e.getEntity())) {
            e.setCancelled(true);
        }
    }
}
