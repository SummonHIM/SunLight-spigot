package su.nexmedia.sunlight.modules.fixer.entitylimiter;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.manager.api.task.ITask;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.fixer.config.FixerConfig;

public class EntityLimiterTask extends ITask<SunLight> {

    private final EntityLimiterManager limiterManager;

    public EntityLimiterTask(@NotNull EntityLimiterManager limiterManager) {
        super(limiterManager.plugin(), FixerConfig.ENTITY_LIMITER_INSPECT_TIME, false);
        this.limiterManager = limiterManager;
    }

    @Override
    public void action() {
        for (World world : plugin.getServer().getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                limiterManager.clearChunk(chunk);
            }
        }
    }
}
