package su.nexmedia.sunlight.modules.afk.config;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.actions.ActionManipulator;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.afk.AfkManager;

import java.util.LinkedHashMap;
import java.util.Map;

public class AfkConfig {

    public static Map<String, Long> AFK_ENTER_TIME_GROUPS;
    public static Map<String, Long> AFK_KICK_TIME_GROUPD;
    public static int               AFK_CHECK_INTERVAL;
    public static ActionManipulator AFK_ENTER_ACTIONS;
    public static ActionManipulator AFK_EXIT_ACTIONS;

    public static void load(@NotNull AfkManager afkManager) {
        SunLight plugin = afkManager.plugin();
        JYML cfg = afkManager.getConfig();

        AFK_CHECK_INTERVAL = cfg.getInt("Check_Interval", 5);
        AFK_ENTER_TIME_GROUPS = new LinkedHashMap<>();
        AFK_KICK_TIME_GROUPD = new LinkedHashMap<>();
        for (String rank : cfg.getSection("Time_To_Afk_Per_Group")) {
            long time = cfg.getInt("Time_To_Afk_Per_Group." + rank) * 1000L;
            AFK_ENTER_TIME_GROUPS.put(rank.toLowerCase(), time);
        }
        for (String rank : cfg.getSection("Time_To_Kick_Per_Group")) {
            long time = cfg.getInt("Time_To_Kick_Per_Group." + rank) * 1000L;
            AFK_KICK_TIME_GROUPD.put(rank.toLowerCase(), time);
        }

        AFK_ENTER_ACTIONS = new ActionManipulator(plugin, cfg, "Afk_Enter_Actions");
        AFK_EXIT_ACTIONS = new ActionManipulator(plugin, cfg, "Afk_Exit_Actions");
    }
}
