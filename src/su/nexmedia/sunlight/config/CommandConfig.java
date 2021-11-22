package su.nexmedia.sunlight.config;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.commands.CommandRegister;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommandConfig {

    public static  boolean              UNREGISTER_CONFLICTS;
    public static  Set<String>          DISABLED;
    private static JYML config;
    private static Map<String, Integer> COOLDOWNS;

    public static void load(@NotNull SunLight plugin) {
        config = JYML.loadOrExtract(plugin, "commands.yml");

        UNREGISTER_CONFLICTS = config.getBoolean("Unregister_Conflicts");
        DISABLED = config.getStringSet("Disabled");
        COOLDOWNS = new HashMap<>();
        for (String cmd : config.getSection("Cooldowns")) {
            COOLDOWNS.put(cmd, config.getInt("Cooldowns." + cmd));
        }
    }

    @NotNull
    public static JYML getConfig() {
        return config;
    }

    public static String[] getAliases(@NotNull String command) {
        if (config.addMissing("Settings." + command + ".Aliases", command)) {
            config.save();
        }
        return config.getString("Settings." + command + ".Aliases", command).trim().split(",");
    }

    public static int getCommandCooldown(@NotNull String command) {
        return COOLDOWNS.entrySet().stream().filter(entry -> {
            return CommandRegister.getAliases(command, true).contains(entry.getKey());
        }).map(Map.Entry::getValue).findFirst().orElse(0);
    }
}
