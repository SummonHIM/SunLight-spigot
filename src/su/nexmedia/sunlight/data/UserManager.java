package su.nexmedia.sunlight.data;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUserManager;
import su.nexmedia.engine.api.data.event.EngineUserLoadEvent;
import su.nexmedia.engine.api.data.event.EngineUserUnloadEvent;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.engine.utils.TimeUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.user.IgnoredUser;

public class UserManager extends AbstractUserManager<SunLight, SunUser> {

    public UserManager(@NotNull SunLight plugin) {
        super(plugin, plugin);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        this.addListener(new UserListener(this.plugin));
    }

    @Override
    protected void onShutdown() {
        super.onShutdown();
    }

    @Override
    @NotNull
    protected SunUser createData(@NotNull Player player) {
        return new SunUser(plugin, player);
    }

    class UserListener extends AbstractListener<SunLight> {

        public UserListener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.NORMAL)
        public void onUserLoad(EngineUserLoadEvent<SunLight, SunUser> e) {
            if (!e.getPlugin().equals(this.plugin)) return;

            SunUser user = e.getUser();
            Player player = user.getPlayer();
            if (player != null) {
                player.setDisplayName(user.getCustomNick());
                user.setIp(PlayerUT.getIP(player));
            }
        }

        @EventHandler(priority = EventPriority.NORMAL)
        public void onUserUnload(EngineUserUnloadEvent<SunLight, SunUser> e) {
            if (!e.getPlugin().equals(this.plugin)) return;

            e.getUser().clear();
        }

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onUserIgnoreChat(AsyncPlayerChatEvent e) {
            Player pSender = e.getPlayer();
            SunUser userSender = getOrLoadUser(pSender);

            e.getRecipients().removeIf(pReceiver -> {
                SunUser userReceiver = getOrLoadUser(pReceiver);

                IgnoredUser ignoredUser = userReceiver.getIgnoredUser(pSender);
                if (ignoredUser == null) return false;

                return ignoredUser.isIgnoreChatMessages();
            });
        }

        @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
        public void onUserCommandCooldown(PlayerCommandPreprocessEvent e) {
            Player player = e.getPlayer();
            if (player.hasPermission(SunPerms.CMD_BYPASS_COOLDOWN)) return;

            SunUser user = getOrLoadUser(player);
            String cmd = StringUT.extractCommandName(e.getMessage());
            if (user.isCommandOnCooldown(cmd)) {
                e.setCancelled(true);
                long date = user.getCommandCooldown(cmd);
                String time = TimeUT.formatTimeLeft(date, System.currentTimeMillis());

                (date < 0 ? plugin.lang().Generic_Command_Cooldown_Onetime : plugin.lang().Generic_Command_Cooldown_Default)
                    .replace("%time%", time).replace("%cmd%", "/" + cmd)
                    .send(player);
                return;
            }

            user.setCommandCooldown(cmd, CommandConfig.getCommandCooldown(cmd));
        }
    }
}
