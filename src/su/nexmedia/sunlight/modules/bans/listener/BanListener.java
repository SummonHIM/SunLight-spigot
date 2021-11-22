package su.nexmedia.sunlight.modules.bans.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.commands.CommandRegister;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.bans.BanManager;
import su.nexmedia.sunlight.modules.bans.punishment.Punishment;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentType;

import java.util.Set;

public class BanListener extends AbstractListener<SunLight> {

    private final BanManager banManager;

    public BanListener(@NotNull BanManager banManager) {
        super(banManager.plugin());
        this.banManager = banManager;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        String name = e.getName();

        Punishment ban = banManager.getActivePunishment(name, PunishmentType.BAN);
        if (ban == null) ban = banManager.getActivePunishment(PlayerUT.getIP(e.getAddress()), PunishmentType.BAN);
        if (ban == null) return;

        ILangMsg banMsg = banManager.getLang().getForUser(ban).replace(ban.replacePlaceholders());
        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, banMsg.normalizeLines());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onMuteChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String name = player.getName();

        Punishment punishment = this.banManager.getActivePunishment(player, PunishmentType.MUTE);
        if (punishment == null) return;

        e.setCancelled(true);
        e.getRecipients().clear();

        this.banManager.getLang().getForUser(punishment).replace(punishment.replacePlaceholders()).send(player);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onMuteCommand(PlayerCommandPreprocessEvent e) {
        if (this.banManager.getMuteBlockedCommands().isEmpty()) return;

        Player player = e.getPlayer();
        String name = player.getName();

        Punishment punishment = this.banManager.getActivePunishment(player, PunishmentType.MUTE);
        if (punishment == null) return;

        String command = StringUT.extractCommandName(e.getMessage());
        Set<String> aliases = CommandRegister.getAliases(command, true);

        if (aliases.stream().anyMatch(alias -> this.banManager.getMuteBlockedCommands().contains(alias))) {
            e.setCancelled(true);
            this.banManager.getLang().getForUser(punishment).replace(punishment.replacePlaceholders()).send(player);
        }
    }
}
