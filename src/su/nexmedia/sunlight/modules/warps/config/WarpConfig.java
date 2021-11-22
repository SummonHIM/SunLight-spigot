package su.nexmedia.sunlight.modules.warps.config;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.modules.warps.WarpManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WarpConfig {

    public static boolean WARP_TELEPORT_COST_MONEY_TO_OWNER;

    public static Set<String>          WARP_SET_WORLD_BLACKLIST;
    public static Map<String, Integer> WARP_SET_AMOUNT_PER_GROUP;

    public static double RATING_SCALE_OF;
    public static long   RATING_VISIT_COOLDOWN;
    public static long   RATING_VISIT_MAXIMUM;

    public static int DESCRIPTION_LINE_LENGTH;
    public static int DESCRIPTION_LINE_AMOUNT;

    public static void load(@NotNull WarpManager warpManager) {
        JYML cfg = warpManager.getConfig();

        WARP_TELEPORT_COST_MONEY_TO_OWNER = cfg.getBoolean("Warp.Teleport.Cost.Money.Transfer_To_Owner");
        WARP_SET_WORLD_BLACKLIST = cfg.getStringSet("Warp.Set.World_Blacklist");
        WARP_SET_AMOUNT_PER_GROUP = new HashMap<>();
        for (String rank : cfg.getSection("Warp.Set.Amount_Per_Group")) {
            WARP_SET_AMOUNT_PER_GROUP.put(rank.toLowerCase(), cfg.getInt("Warp.Set.Amount_Per_Group." + rank));
        }

        RATING_SCALE_OF = cfg.getDouble("Rating.Scale_Of", 100D);
        RATING_VISIT_COOLDOWN = cfg.getLong("Rating.Visit_Count_Cooldown", 3600L);
        RATING_VISIT_MAXIMUM = cfg.getLong("Rating.Visit_Max_Value", 10000L);

        DESCRIPTION_LINE_LENGTH = cfg.getInt("Editor.Description.Max_Line_Length", 50);
        DESCRIPTION_LINE_AMOUNT = cfg.getInt("Editor.Description.Max_Line_Amount", 5);
    }
}
