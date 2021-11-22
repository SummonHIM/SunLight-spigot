package su.nexmedia.sunlight.modules.afk.task;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.manager.api.task.ITask;
import su.nexmedia.engine.utils.TimeUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.afk.AfkManager;
import su.nexmedia.sunlight.modules.afk.AfkPerms;
import su.nexmedia.sunlight.modules.afk.config.AfkConfig;

import java.util.Map;
import java.util.WeakHashMap;

public class AfkTask extends ITask<SunLight> {

    private final AfkManager            afkManager;
    private final Map<Player, Location> lastPosition;

    public AfkTask(@NotNull AfkManager afkManager) {
        super(afkManager.plugin(), AfkConfig.AFK_CHECK_INTERVAL, false);
        this.lastPosition = new WeakHashMap<>();
        this.afkManager = afkManager;
    }

    public void clearPosition(@NotNull Player player) {
        this.lastPosition.remove(player);
    }

    @NotNull
    public Location getLastPosition(@NotNull Player player) {
        return this.lastPosition.computeIfAbsent(player, k -> this.getCurrentPosition(player));
    }

    @NotNull
    public Location getCurrentPosition(@NotNull Player player) {
        return player.getLocation();
    }

    public boolean isTheSamePosition(@NotNull Player player, Location location) {
        if (this.getCurrentPosition(player).getWorld() != location.getWorld()) return false;
        return this.getCurrentPosition(player).distance(location) <= 2.5;
    }

    @Override
    public void action() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            long timeToAfk = this.afkManager.getTimeToAfk(player);
            if (timeToAfk <= 0) continue;

            if (this.isTheSamePosition(player, this.getLastPosition(player))) {
                SunUser user = plugin.getUserManager().getOrLoadUser(player);
                afkManager.setAfkTime(user, afkManager.getAfkTime(user) + AfkConfig.AFK_CHECK_INTERVAL * 1000L);

                if (!player.hasPermission(AfkPerms.BYPASS_KICK)) {
                    long kickTime = this.afkManager.getTimeToKick(player);
                    if (kickTime > 0 && afkManager.getAfkTime(user) >= kickTime) {
                        player.kickPlayer(afkManager.getLang().Afk_Kick.getMsg().replace("%time%", TimeUT.formatTime(kickTime)));
                        continue;
                    }
                }
                if (afkManager.getAfkTime(user) >= timeToAfk) {
                    this.afkManager.enterAfk(player);
                }
            }
            else {
                this.lastPosition.put(player, this.getCurrentPosition(player));
                this.afkManager.exitAfk(player);
            }
        }
    }
}
