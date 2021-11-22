package su.nexmedia.sunlight.modules.afk.listener;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.event.PlayerPrivateMessageEvent;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.afk.AfkManager;

public class AfkListener extends AbstractListener<SunLight> {

    private final AfkManager afkManager;

    public AfkListener(@NotNull AfkManager afkManager) {
        super(afkManager.plugin());
        this.afkManager = afkManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAfkSms(PlayerPrivateMessageEvent e) {
        if (!(e.getTarget() instanceof Player geter)) return;

        SunUser userGeter = plugin.getUserManager().getOrLoadUser(geter);
        if (afkManager.isAfk(userGeter)) {
            CommandSender from = e.getSender();
            this.afkManager.getLang().Afk_TellNotify.replace("%player%", geter.getName()).send(from);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAfkQuit(PlayerQuitEvent e) {
        this.afkManager.exitAfk(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAfkQuit(PlayerDeathEvent e) {
        this.afkManager.exitAfk(e.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAfkInteract(PlayerInteractEvent e) {
        if (e.useInteractedBlock() == Event.Result.DENY) return;
        if (e.useItemInHand() == Event.Result.DENY) return;

        Player player = e.getPlayer();
        this.afkManager.exitAfk(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAfkChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.afkManager.exitAfk(player));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAfkDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        this.afkManager.exitAfk(player);
    }
}
