package su.nexmedia.sunlight.command.list;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.MsgUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "near";
    private final int          radius;
    private final List<String> format;

    public NearCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_NEAR);

        JYML cfg = CommandConfig.getConfig();
        String path = "Settings." + NAME + ".";

        this.radius = cfg.getInt(path + "Radius", 100);
        this.format = StringUT.color(cfg.getStringList(path + "Format"));
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Near_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player pSender = (Player) sender;

        Map<Player, Integer> listNear = new HashMap<>();
        Location locSender = pSender.getLocation();
        for (Player pNear : plugin.getServer().getOnlinePlayers()) {
            if (pNear == null || pNear.equals(pSender)) continue;
            if (!pNear.getWorld().equals(pSender.getWorld())) continue;

            SunUser userNear = plugin.getUserManager().getOrLoadUser(pNear);
            if (userNear.isVanished() && !pSender.hasPermission(SunPerms.CMD_VANISH_BYPASS_SEE)) continue;

            int dist = (int) pNear.getLocation().distance(locSender);
            if (dist <= radius) {
                listNear.put(pNear, dist);
            }
        }

        if (listNear.isEmpty()) {
            plugin.lang().Command_Near_Error_None.replace("%radius%", radius).send(sender);
        }
        else {
            for (String line : this.format) {
                if (line.contains("%player%")) {
                    listNear.forEach((pNear, dist) -> {
                        String name = pNear.getName();
                        String pref = Hooks.getPrefix(pNear);
                        String suf = Hooks.getSuffix(pNear);

                        MsgUT.sendWithJSON(sender, line
                            .replace("%distance%", String.valueOf(dist))
                            .replace("%player%", name)
                            .replace("%prefix%", pref)
                            .replace("%suffix%", suf)
                        );
                    });
                }
                else {
                    MsgUT.sendWithJSON(sender, line.replace("%radius%", String.valueOf(radius)));
                }
            }
        }

        listNear.clear();
    }
}
