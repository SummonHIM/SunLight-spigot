package su.nexmedia.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class NickCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "nick";
    private final int         lengthMax;
    private final int         lengthMin;
    private final Set<String> deniedWords;

    public NickCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_NICK);

        JYML cfg = CommandConfig.getConfig();
        String path = "Settings." + NAME + ".";

        this.lengthMax = cfg.getInt("Max_Length");
        this.lengthMin = cfg.getInt("Min_Length");
        this.deniedWords = cfg.getStringSet(path + "Blacklist");
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Nick_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Nick_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            if (player.hasPermission(SunPerms.CMD_NICK_OTHERS)) {
                return PlayerUT.getPlayerNames();
            }
            else {
                return Arrays.asList(player.getName(), "<nick>");
            }
        }
        if (i == 2) {
            if (player.hasPermission(SunPerms.CMD_NICK_OTHERS)) {
                return Arrays.asList(player.getName(), "<nick>");
            }
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length > 2) {
            this.printUsage(sender);
            return;
        }

        String pName;
        String nickColor;
        if (args.length >= 2) {
            if (!sender.hasPermission(SunPerms.CMD_NICK_OTHERS)) {
                this.errorPermission(sender);
                return;
            }
            pName = args[0];
            nickColor = args[1];
        }
        else if (args.length == 1) {
            pName = sender.getName();
            nickColor = args[0];
        }
        else {
            pName = sender.getName();
            nickColor = sender.getName();
        }

        Player pTarget = plugin.getServer().getPlayer(pName);
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        nickColor = StringUT.color(nickColor);
        String nickRaw = StringUT.colorOff(nickColor);

        if (!sender.hasPermission(SunPerms.CMD_NICK_BYPASS_WORDS)) {
            // Check for bad words in nick
            for (String word : this.deniedWords) {
                if (nickRaw.contains(word) || nickColor.contains(word)) {
                    plugin.lang().Command_Nick_Error_Blacklisted.send(sender);
                    return;
                }
            }

            // Prevent to use other player's nick
            Player pNick = plugin.getServer().getPlayer(nickRaw);
            if (pNick != null) {
                if (!pNick.equals(sender)) {
                    plugin.lang().Command_Nick_Error_Blacklisted.send(sender);
                    return;
                }
            }
            else {
                // Prevent to use other players nicks in custom nick
                for (Player pNick2 : plugin.getServer().getOnlinePlayers()) {
                    if (pNick2 != null && !pNick2.equals(sender) && nickRaw.contains(pNick2.getName())) {
                        plugin.lang().Command_Nick_Error_Blacklisted.send(sender);
                        return;
                    }
                }
            }
        }

        if (!sender.hasPermission(SunPerms.CMD_NICK_BYPASS_LENGTH)) {
            if (nickRaw.length() > this.lengthMax) {
                plugin.lang().Command_Nick_Error_Long.send(sender);
                return;
            }

            if (nickRaw.length() < this.lengthMin) {
                plugin.lang().Command_Nick_Error_Short.send(sender);
                return;
            }
        }

        if (nickColor.length() > 48) {
            nickColor = nickColor.substring(0, 48);
        }

        SunUser user = plugin.getUserManager().getOrLoadUser(pTarget);
        user.setCustomNick(nickColor);
        pTarget.setDisplayName(nickColor);

        if (!sender.equals(pTarget)) {
            plugin.lang().Command_Nick_Done_Others
                .replace("%nick%", nickColor).replace("%player%", pTarget.getName())
                .send(sender);
        }

        plugin.lang().Command_Nick_Done_Self.replace("%nick%", nickColor).send(pTarget);
    }
}
