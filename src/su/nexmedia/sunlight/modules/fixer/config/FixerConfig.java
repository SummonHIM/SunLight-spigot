package su.nexmedia.sunlight.modules.fixer.config;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.manager.types.MobGroup;
import su.nexmedia.sunlight.modules.fixer.FixerManager;

import java.util.HashMap;
import java.util.Map;

public class FixerConfig {

    public static boolean FARM_KILLER_AUTO_FISHING;
    public static boolean FARM_KILLER_ENDERMITE_MINECART;

    public static boolean                ENTITY_LIMITER_ENABLED;
    public static int                    ENTITY_LIMITER_INSPECT_TIME;
    public static Map<MobGroup, Integer> ENTITY_LIMITER_LIMITS;

    public static void load(@NotNull FixerManager fixerManager) {
        JYML cfg = fixerManager.getConfig();

        String path = "Farm_Killer.";
        FARM_KILLER_AUTO_FISHING = cfg.getBoolean(path + "Fix_Auto_Fishing", true);
        FARM_KILLER_ENDERMITE_MINECART = cfg.getBoolean(path + "Fix_Endermite_Minecart", true);

        path = "Entity_Chunk_Limit.";
        if (ENTITY_LIMITER_ENABLED = cfg.getBoolean(path + "Enabled")) {
            ENTITY_LIMITER_INSPECT_TIME = cfg.getInt(path + "Inspection_Interval", 300);
            ENTITY_LIMITER_LIMITS = new HashMap<>();
            for (MobGroup mobGroup : MobGroup.values()) {
                int mobLimit = cfg.getInt(path + "Limits." + mobGroup.name(), -1);
                ENTITY_LIMITER_LIMITS.put(mobGroup, mobLimit);
            }
        }
    }

    public static int getLimit(@NotNull MobGroup mobGroup) {
        return ENTITY_LIMITER_LIMITS.getOrDefault(mobGroup, -1);
    }
}
