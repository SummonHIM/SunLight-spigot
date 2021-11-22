package su.nexmedia.sunlight.command.list;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;

import java.util.List;
import java.util.Set;

public class BackCommand extends GeneralCommand<SunLight> implements ICleanable {

    public static final String NAME = "back";

    private final Set<String>     excludedWorlds;
    private       CommandListener commandListener;

    public BackCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_BACK);

        JYML config = CommandConfig.getConfig();
        String path = "Settings." + NAME + ".";

        this.excludedWorlds = config.getStringSet(path + "Disabled_Worlds");

        (this.commandListener = new CommandListener(plugin)).registerListeners();
    }

    @Override
    public void clear() {
        if (this.commandListener != null) {
            this.commandListener.unregisterListeners();
            this.commandListener = null;
        }
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Back_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Back_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1 && player.hasPermission(SunPerms.CMD_BACK_OTHERS)) {
            return PlayerUT.getPlayerNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        String pName = sender.getName();
        if (args.length == 1) {
            if (!sender.hasPermission(SunPerms.CMD_BACK_OTHERS)) {
                this.errorPermission(sender);
                return;
            }
            pName = args[0];
        }

        Player pTarget = plugin.getServer().getPlayer(pName);
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        SunUser userTarget = plugin.getUserManager().getOrLoadUser(pTarget);

        Location back = userTarget.getPreviousLocation();
        if (back == null) {
            plugin.lang().Command_Back_Error_Empty.send(sender);
            return;
        }

        if (!pTarget.hasPermission(SunPerms.CMD_BACK_BYPASS_WORLDS)) {
            World worldBack = back.getWorld();
            if (worldBack != null && this.excludedWorlds.contains(worldBack.getName())) {
                plugin.lang().Command_Back_Error_BadWorld.send(pTarget);
                return;
            }
        }

        pTarget.teleport(back);
        plugin.lang().Command_Back_Done.send(sender);
    }

    private static class CommandListener extends AbstractListener<SunLight> {

        CommandListener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        public void onCommandBackTeleport(PlayerTeleportEvent e) {
            Player player = e.getPlayer();
            if (Hooks.isNPC(player)) return;

            Location locFrom = e.getFrom();
            Location locTo = e.getTo();
            if (locTo == null) return;

            World worldFrom = locFrom.getWorld();
            World worldTo = locTo.getWorld();
            if (worldFrom != null && worldFrom.equals(worldTo)) {
                if (locFrom.distance(locTo) <= 5) return;
            }

            SunUser user = plugin.getUserManager().getOrLoadUser(player);
            user.setPreviousLocation(e.getFrom());
        }
    }
}
