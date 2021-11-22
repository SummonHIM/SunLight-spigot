package su.nexmedia.sunlight.command.list;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.*;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.afk.AfkManager;

import java.util.List;

public class PlayerInfoCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "playerinfo";

    private final List<String> formatOnline;
    private final List<String> formatOffline;

    public PlayerInfoCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_PLAYERINFO);

        JYML cfg = CommandConfig.getConfig();
        String path = "Settings." + NAME + ".";

        this.formatOnline = StringUT.color(cfg.getStringList(path + "Format_Online"));
        this.formatOffline = StringUT.color(cfg.getStringList(path + "Format_Offline"));
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_PlayerInfo_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_PlayerInfo_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return PlayerUT.getPlayerNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            this.printUsage(sender);
            return;
        }

        SunUser user = plugin.getUserManager().getOrLoadUser(args[0], false);
        if (user == null) {
            this.errorPlayer(sender);
            return;
        }

        Player pTarget = user.getPlayer();
        boolean isOnline = pTarget != null;
        List<String> format = isOnline ? this.formatOnline : this.formatOffline;

        AfkManager afkManager = plugin.getModuleCache().getAfkManager();

        for (String line : format) {

            if (afkManager != null) {
                line = line
                    .replace("%user_is_afk%", plugin.lang().getBool(afkManager.isAfk(user)))
                    .replace("%user_afk_time%", TimeUT.formatTime(afkManager.getAfkTime(user)));
            }

            line = line
                .replace("%user_name%", user.getName())
                .replace("%user_ip%", user.getIp())
                .replace("%user_last_online%", TimeUT.formatTimeLeft(System.currentTimeMillis(), user.getLastOnline()))
                .replace("%user_online_time%", TimeUT.formatTimeLeft(System.currentTimeMillis(), user.getLastOnline()))
            ;

            if (isOnline) {
                if (Hooks.hasPlaceholderAPI()) line = PlaceholderAPI.setPlaceholders(pTarget, line);
                line = line
                    .replace("%player_z%", NumberUT.format(pTarget.getLocation().getZ()))
                    .replace("%player_y%", NumberUT.format(pTarget.getLocation().getY()))
                    .replace("%player_x%", NumberUT.format(pTarget.getLocation().getX()))
                    .replace("%player_world%", pTarget.getWorld().getName())
                    .replace("%player_op%", plugin.lang().getBool(pTarget.isOp()))
                    .replace("%player_can_fly%", plugin.lang().getBool(pTarget.getAllowFlight()))
                    .replace("%player_flying%", plugin.lang().getBool(pTarget.isFlying()))
                    .replace("%player_food%", String.valueOf(pTarget.getFoodLevel()))
                    .replace("%player_saturation%", String.valueOf(pTarget.getSaturation()))
                    .replace("%player_health_max%", NumberUT.format(EntityUT.getAttribute(pTarget, Attribute.GENERIC_MAX_HEALTH)))
                    .replace("%player_health%", NumberUT.format(pTarget.getHealth()))
                    .replace("%player_gamemode%", plugin.lang().getEnum(pTarget.getGameMode()))
                    .replace("%player_display_name%", pTarget.getDisplayName())
                    .replace("%player_name%", pTarget.getName());
            }

            sender.sendMessage(line);
        }
    }
}
