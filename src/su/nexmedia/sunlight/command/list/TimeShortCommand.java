package su.nexmedia.sunlight.command.list;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.LocUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.config.Config;

import java.time.LocalTime;
import java.util.*;

public class TimeShortCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "time";

    private final Map<String, Long> timesMap;

    public TimeShortCommand(@NotNull SunLight plugin) {
        super(plugin, new ArrayList<>(), SunPerms.CMD_TIME);

        this.timesMap = new HashMap<>();

        JYML cfg = CommandConfig.getConfig();
        String path = "Settings." + NAME + ".";

        for (String timeLabel : cfg.getSection(path + "Values")) {
            long ticks = cfg.getLong(path + "Values." + timeLabel);
            this.timesMap.put(timeLabel.toLowerCase(), ticks);
        }

        Set<String> timeAlias = new HashSet<>(this.timesMap.keySet());
        timeAlias.addAll(Arrays.asList(CommandConfig.getAliases(NAME)));
        this.aliases = timeAlias.toArray(new String[0]);
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Time_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1 && player.hasPermission(SunPerms.CMD_TIME)) {
            return LocUT.getWorldNames();
        }
        if (i == 2 && player.hasPermission(SunPerms.CMD_TIME_SET)) {
            return Arrays.asList("<time>", "0", "18000", "24000");
        }
        return super.getTab(player, i, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        boolean isPlayer = sender instanceof Player;

        World wTarget;
        if (args.length == 1) {
            wTarget = plugin.getServer().getWorld(args[0]);
            if (wTarget == null) {
                plugin.lang().Error_NoWorld.replace("%world%", args[0]).send(sender);
                return;
            }
        }
        else {
            if (isPlayer) {
                Player pSender = (Player) sender;
                wTarget = pSender.getWorld();
            }
            else {
                wTarget = plugin.getServer().getWorlds().get(0);
            }
        }

        String shortTime = label.toLowerCase();
        if (this.timesMap.containsKey(shortTime)) {
            if (!sender.hasPermission(SunPerms.CMD_TIME + ".*") && !sender.hasPermission(SunPerms.CMD_TIME + "." + shortTime)) {
                this.errorPermission(sender);
                return;
            }

            long wTime = this.timesMap.get(shortTime);
            wTarget.setTime(wTime);

            plugin.lang().Command_Time_Done
                .replace("%world%", wTarget.getName())
                .replace("%time%", label)
                .send(sender);
        }
        else {
            if (!sender.hasPermission(SunPerms.CMD_TIME)) {
                this.errorPermission(sender);
                return;
            }

            if (args.length >= 2) {
                if (!sender.hasPermission(SunPerms.CMD_TIME_SET)) {
                    this.errorPermission(sender);
                    return;
                }
                long aTime = StringUT.getInteger(args[1], 0);
                wTarget.setTime(aTime);
            }

            long ticks = wTarget.getTime();
            double point = ticks * 3.6;
            double hours = point / 60 / 60;
            double mins = (point / 60) % 60;
            double secs = point % 60;

            LocalTime time = LocalTime.of((int) hours, (int) mins, (int) secs);
            time = time.plusHours(6);

            ILangMsg msg = args.length >= 2 ? plugin.lang().Command_Time_Done : plugin.lang().Command_Time_Info;
            msg
                .replace("%world%", wTarget.getName())
                .replace("%time%", time.format(Config.GENERAL_TIME_FORMAT))
                .replace("%ticks%", String.valueOf(ticks))
                .send(sender);
        }
    }
}
