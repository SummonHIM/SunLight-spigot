package su.nexmedia.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;

import java.util.List;

public class VanishCommand extends GeneralCommand<SunLight> implements ICleanable {

    public static final String NAME = "vanish";
    private VanishListener vanishListener;

    public VanishCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_VANISH);

        (this.vanishListener = new VanishListener(plugin)).registerListeners();
    }

    @Override
    public void clear() {
        if (this.vanishListener != null) {
            this.vanishListener.unregisterListeners();
            this.vanishListener = null;
        }
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Vanish_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player pTarget = (Player) sender;
        SunUser user = plugin.getUserManager().getOrLoadUser(pTarget);

        user.setVanished(!user.isVanished());
        this.vanish(pTarget, user.isVanished());

        plugin.lang().Command_Vanish_Toggle
            .replace("%state%", plugin.lang().getOnOff(user.isVanished()))
            .send(sender);
    }

    private void vanish(@NotNull Player pTarget, boolean isVanished) {
        for (Player pServer : plugin.getServer().getOnlinePlayers()) {
            if (pServer == null) continue;

            if (isVanished) {
                if (!pServer.hasPermission(SunPerms.CMD_VANISH_BYPASS_SEE)) {
                    pServer.hidePlayer(plugin, pTarget);
                }
            }
            else {
                pServer.showPlayer(plugin, pTarget);
            }
        }
    }

    static class VanishListener extends AbstractListener<SunLight> {

        public VanishListener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onJoin(PlayerJoinEvent e) {
            Player pJoin = e.getPlayer();
            for (Player pServer : plugin.getServer().getOnlinePlayers()) {
                if (pServer.equals(pJoin)) continue;

                SunUser userServer = plugin.getUserManager().getOrLoadUser(pServer);
                if (userServer.isVanished()) {
                    pJoin.hidePlayer(plugin, pServer);
                }
            }
        }
    }
}
