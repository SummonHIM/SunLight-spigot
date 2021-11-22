package su.nexmedia.sunlight.command.list;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "list";

    private final Map<String, String> formatGroup;
    private final List<String>        formatList;

    public ListCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_LIST);

        // Map for list group format
        JYML cfg = CommandConfig.getConfig();
        String path = "Settings." + NAME + ".";

        this.formatGroup = new HashMap<>();
        for (String rank : cfg.getSection(path + "Format_Group")) {
            String path2 = path + "Format_Group." + rank;
            String format = StringUT.color(cfg.getString(path2, ""));
            this.formatGroup.put(rank.toLowerCase(), format);
        }
        this.formatList = StringUT.color(cfg.getStringList(path + "Format_List"));
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_List_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        int onlineTotal = plugin.getServer().getOnlinePlayers().size();

        for (String line : this.formatList) {

            if (line.startsWith("%") && line.endsWith("%")) {
                String rank = line.replace("%", "");
                String rankFormat = this.getRankFormat(rank);
                if (rankFormat.isEmpty()) continue;

                line = rankFormat;
            }

            MsgUT.sendWithJSON(sender, line.replace("%total%", String.valueOf(onlineTotal)));
        }
    }

    @NotNull
    private String getRankFormat(@NotNull String rank) {
        String format = this.formatGroup.get(rank.toLowerCase());
        if (format == null) return "";

        StringBuilder buildUsers = new StringBuilder();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            String pRank = Hooks.getPermGroup(player);
            if (!pRank.equalsIgnoreCase(rank)) continue;

            if (buildUsers.length() > 0) {
                buildUsers.append(", ");
            }
            buildUsers.append(player.getDisplayName());
        }
        if (buildUsers.length() == 0) return "";

        String rankUsers = buildUsers.toString();
        return format.replace("%players%", rankUsers);
    }
}
