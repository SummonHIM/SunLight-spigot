package su.nexmedia.sunlight.modules.fixer.entitylimiter;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.manager.types.MobGroup;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.fixer.config.FixerConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class EntityLimiterManager extends AbstractManager<SunLight> {

    private EntityLimiterTask entityLimiterTask;

    public EntityLimiterManager(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        this.addListener(new EntityLimitListener(this));
        this.entityLimiterTask = new EntityLimiterTask(this);
        this.entityLimiterTask.start();
    }

    @Override
    protected void onShutdown() {
        if (this.entityLimiterTask != null) {
            this.entityLimiterTask.stop();
            this.entityLimiterTask = null;
        }
    }

    public void clearChunk(@NotNull Chunk chunk) {
        Map<MobGroup, Integer> chunkMobsMap = new HashMap<>();

        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Player || Hooks.isNPC(entity)) continue;

            MobGroup entityGroup = MobGroup.getMobGroup(entity);
            int groupLimit = FixerConfig.getLimit(entityGroup);
            if (groupLimit < 0) continue;

            int chunkMobs = chunkMobsMap.computeIfAbsent(entityGroup, k -> 0) + 1;
            chunkMobsMap.put(entityGroup, chunkMobs);

            if (chunkMobs > groupLimit) {
                entity.remove();
            }
        }
    }

    public boolean canSpawn(@NotNull Entity entitySrc) {
        MobGroup groupSrc = MobGroup.getMobGroup(entitySrc);
        int limit = FixerConfig.getLimit(groupSrc);
        if (limit < 0) return true;

        Chunk chunk = entitySrc.getLocation().getChunk();
        int chunkMobs = (int) Stream.of(chunk.getEntities())
            .filter(entity -> MobGroup.getMobGroup(entity) == groupSrc).count();

        return chunkMobs <= limit;
    }
}
