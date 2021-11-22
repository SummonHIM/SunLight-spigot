package su.nexmedia.sunlight.modules.worlds.config;

import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.modules.worlds.WorldManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorldsConfig {

    public static final InventoryType[]             INVENTORY_SPLIT_TYPES = {InventoryType.PLAYER, InventoryType.ENDER_CHEST};
    public static       boolean                     INVENTORY_SPLIT_ENABLED;
    public static       Map<InventoryType, Boolean> INVENTORY_SPLIT_INVENTORIES;
    public static       Map<String, Set<String>>    INVENTORY_SPLIT_WORLD_GROUPS;

    public static boolean                  COMMAND_BLOCKER_ENABLED;
    public static Map<String, Set<String>> COMMAND_BLOCKER_COMMANDS;

    public static void load(@NotNull WorldManager worldManager) {
        JYML cfg = worldManager.getConfig();

        String path = "Inventory_Split.";
        if (INVENTORY_SPLIT_ENABLED = cfg.getBoolean(path + "Enabled")) {
            INVENTORY_SPLIT_INVENTORIES = new HashMap<>();
            for (InventoryType type : INVENTORY_SPLIT_TYPES) {
                INVENTORY_SPLIT_INVENTORIES.put(type, cfg.getBoolean(path + "Affected_Inventories." + type.name()));
            }
            INVENTORY_SPLIT_WORLD_GROUPS = new HashMap<>();
            for (String group : cfg.getSection(path + "World_Groups")) {
                Set<String> groupWorlds = new HashSet<>(cfg.getStringList(path + "World_Groups." + group));
                INVENTORY_SPLIT_WORLD_GROUPS.put(group, groupWorlds);
            }
        }

        path = "Command_Blocker.";
        if (COMMAND_BLOCKER_ENABLED = cfg.getBoolean(path + "Enabled")) {
            COMMAND_BLOCKER_COMMANDS = new HashMap<>();
            for (String worldName : cfg.getSection(path + "World_Commands")) {
                Set<String> wCommands = cfg.getStringSet(path + "World_Commands." + worldName);
                if (!wCommands.isEmpty()) {
                    COMMAND_BLOCKER_COMMANDS.put(worldName, wCommands);
                }
            }
        }
    }

    public static boolean isInventoryAffected(@NotNull InventoryType inventoryType) {
        return INVENTORY_SPLIT_INVENTORIES.getOrDefault(inventoryType, false);
    }
}
