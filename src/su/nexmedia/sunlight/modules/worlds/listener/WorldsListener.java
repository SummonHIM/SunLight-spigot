package su.nexmedia.sunlight.modules.worlds.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.commands.CommandRegister;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.worlds.WorldManager;
import su.nexmedia.sunlight.modules.worlds.config.WorldsConfig;
import su.nexmedia.sunlight.modules.worlds.WorldInventory;
import su.nexmedia.sunlight.modules.worlds.WorldPerms;

import java.util.Set;

public class WorldsListener extends AbstractListener<SunLight> {

    private final WorldManager worldManager;

    public WorldsListener(@NotNull WorldManager worldManager) {
        super(worldManager.plugin());
        this.worldManager = worldManager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldInventoryChange(PlayerChangedWorldEvent e) {
        if (!WorldsConfig.INVENTORY_SPLIT_ENABLED) return;

        Player player = e.getPlayer();
        String groupTo = this.worldManager.getWorldGroup(player.getWorld());
        String groupFrom = this.worldManager.getWorldGroup(e.getFrom());

        // Do not affect snapshots for the same group
        if (groupFrom != null && groupTo != null) {
            if (groupTo.equalsIgnoreCase(groupFrom)) {
                return;
            }
        }

        WorldInventory worldInventory = this.worldManager.getWorldInventory(player);
        // And here do snapshot for current player inv for world he comes from
        if (groupFrom != null) {
            worldInventory.doSnapshot(player, groupFrom);
        }

        // And now replace it by the inventory of new world
        if (groupTo != null) {
            worldInventory.apply(player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldInventoryQuit(PlayerQuitEvent e) {
        if (!WorldsConfig.INVENTORY_SPLIT_ENABLED) return;

        Player player = e.getPlayer();
        String playerId = player.getUniqueId().toString();

        WorldInventory worldInventory = this.worldManager.getInventoryMap().remove(playerId);
        if (worldInventory != null) {
            worldInventory.save();
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onWorldCommandsBlocked(PlayerCommandPreprocessEvent e) {
        if (!WorldsConfig.COMMAND_BLOCKER_ENABLED) return;

        Player player = e.getPlayer();
        if (player.hasPermission(WorldPerms.WORLDS_BYPASS_COMMANDS)) return;

        Set<String> deniedCommands = WorldsConfig.COMMAND_BLOCKER_COMMANDS.get(player.getWorld().getName());
        if (deniedCommands == null || deniedCommands.isEmpty()) return;

        String cmd = StringUT.extractCommandName(e.getMessage());
        boolean doBlock = CommandRegister.getAliases(cmd, true).stream().anyMatch(deniedCommands::contains);

        if (doBlock) {
            worldManager.getLang().Worlds_Error_BadCommand.send(player);
            e.setCancelled(true);
        }
    }
}
