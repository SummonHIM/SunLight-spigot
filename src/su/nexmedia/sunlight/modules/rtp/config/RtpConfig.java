package su.nexmedia.sunlight.modules.rtp.config;

import org.bukkit.Sound;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.modules.rtp.RtpManager;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class RtpConfig {

    public static World                DEFAULT_WORLD;
    public static boolean              FORCE_TO_DEFAULT;
    public static int                  MAX_SEARCH_ATTEMPTS;
    public static Set<String>          IGNORED_BLOCKS;
    public static Map<World, double[]> COORDINATES;

    public static Sound SOUND_START;
    public static Sound SOUND_ATTEMPT;
    public static Sound SOUND_TELEPORT;

    public static String PARTICLE_TELEPORT;

    public static void load(@NotNull RtpManager rtpManager) {
        JYML cfg = rtpManager.getConfig();

        String path = "settings.";
        DEFAULT_WORLD = rtpManager.plugin().getServer().getWorld(cfg.getString(path + "Default_World", ""));
        FORCE_TO_DEFAULT = cfg.getBoolean(path + "Force_To_Default");
        MAX_SEARCH_ATTEMPTS = Math.max(1, cfg.getInt(path + "Max_Attempts", 5));
        IGNORED_BLOCKS = cfg.getStringSet(path + "Block_Blacklist");

        path = "Effects.Sounds.";
        SOUND_START = cfg.getEnum(path + "Start", Sound.class);
        SOUND_ATTEMPT = cfg.getEnum(path + "Attempt", Sound.class);
        SOUND_TELEPORT = cfg.getEnum(path + "Teleport", Sound.class);

        path = "Effects.Particles.";
        PARTICLE_TELEPORT = cfg.getString(path + "Teleport", "");

        COORDINATES = new WeakHashMap<>();
        for (String wId : cfg.getSection("Coordinates")) {
            World world = rtpManager.plugin().getServer().getWorld(wId);
            if (world == null) {
                rtpManager.error("Invalid world '" + wId + "' in coordinates.");
                continue;
            }

            path = "Coordinates." + wId + ".";
            double xMin = cfg.getDouble(path + "Min.X");
            double xMax = cfg.getDouble(path + "Max.X");
            double zMin = cfg.getDouble(path + "Min.Z");
            double zMax = cfg.getDouble(path + "Max.Z");

            COORDINATES.put(world, new double[]{xMin, xMax, zMin, zMax});
        }
    }
}
