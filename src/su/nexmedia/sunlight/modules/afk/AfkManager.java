package su.nexmedia.sunlight.modules.afk;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.TimeUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.ModuleId;
import su.nexmedia.sunlight.modules.SunModule;
import su.nexmedia.sunlight.modules.afk.command.AfkCommand;
import su.nexmedia.sunlight.modules.afk.config.AfkConfig;
import su.nexmedia.sunlight.modules.afk.config.AfkLang;
import su.nexmedia.sunlight.modules.afk.event.PlayerAfkEvent;
import su.nexmedia.sunlight.modules.afk.listener.AfkListener;
import su.nexmedia.sunlight.modules.afk.task.AfkTask;

public class AfkManager extends SunModule {

    public static final String SETTING_AFK      = "AFK";
    public static final String SETTING_AFK_TIME = "AFK_TIME";
    private AfkLang lang;
    private AfkTask afkTask;

    public AfkManager(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getId() {
        return ModuleId.AFK;
    }

    @Override
    @NotNull
    public String getVersion() {
        return "2.0";
    }

    @Override
    public void onLoad() {
        this.lang = new AfkLang(plugin, JYML.loadOrExtract(plugin, this.getPath() + "lang/messages_" + plugin.cfg().lang + ".yml"));
        this.lang.setup();
        AfkConfig.load(this);

        this.plugin.getCommandRegulator().register(new AfkCommand(this));
        this.addListener(new AfkListener(this));

        this.afkTask = new AfkTask(this);
        this.afkTask.start();
    }

    @Override
    public void onShutdown() {
        if (this.afkTask != null) {
            this.afkTask.stop();
            this.afkTask = null;
        }

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            this.exitAfk(player);
        }
    }

    @NotNull
    public AfkLang getLang() {
        return lang;
    }

    public boolean isAfk(@NotNull SunUser user) {
        return user.getSettingBoolean(SETTING_AFK);
    }

    public void setAfk(@NotNull SunUser user, boolean isAfk) {
        user.setSettingBoolean(SETTING_AFK, isAfk);
    }

    public long getAfkTime(@NotNull SunUser user) {
        return (long) user.getSettingNumber(SETTING_AFK_TIME);
    }

    public void setAfkTime(@NotNull SunUser user, long afkTime) {
        user.setSettingNumber(SETTING_AFK_TIME, (double) afkTime);
    }

    public void exitAfk(@NotNull Player player) {
        if (Hooks.isNPC(player)) return;

        SunUser user = plugin.getUserManager().getOrLoadUser(player);
        if (!this.isAfk(user)) {
            this.setAfkTime(user, 0);
            return;
        }

        PlayerAfkEvent event = new PlayerAfkEvent(player, user, false);
        plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        this.getLang().Afk_Exit
            .replace("%player%", player.getName())
            .replace("%time%", TimeUT.formatTime(this.getAfkTime(user)))
            .broadcast();
        AfkConfig.AFK_EXIT_ACTIONS.process(player);

        this.setAfk(user, false);
        this.setAfkTime(user, 0);
    }

    public void enterAfk(@NotNull Player player) {
        this.enterAfk(player, false);
    }

    public void enterAfk(@NotNull Player player, boolean isForced) {
        if (Hooks.isNPC(player)) return;

        SunUser user = plugin.getUserManager().getOrLoadUser(player);
        if (this.isAfk(user)) return;

        PlayerAfkEvent event = new PlayerAfkEvent(player, user, true);
        plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        AfkConfig.AFK_ENTER_ACTIONS.process(player);
        this.setAfk(user, true);
        if (isForced) {
            this.afkTask.clearPosition(player);
        }

        this.getLang().Afk_Enter.replace("%player%", player.getName()).broadcast();
    }

    public long getTimeToAfk(@NotNull Player player) {
        return Hooks.getGroupValueLong(player, AfkConfig.AFK_ENTER_TIME_GROUPS, true);
    }

    public long getTimeToKick(@NotNull Player player) {
        return Hooks.getGroupValueLong(player, AfkConfig.AFK_KICK_TIME_GROUPD, true);
    }
}
