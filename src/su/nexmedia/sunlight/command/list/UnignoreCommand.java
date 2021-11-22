package su.nexmedia.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.user.IgnoredUser;

import java.util.List;

public class UnignoreCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "unignore";

    public UnignoreCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_UNIGNORE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_UnIgnore_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_UnIgnore_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            SunUser user = plugin.getUserManager().getOrLoadUser(player);
            return user.getIgnoredUsers().values().stream().map(IgnoredUser::getName).toList();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            this.printUsage(sender);
            return;
        }

        SunUser userExcuse = plugin.getUserManager().getOrLoadUser(args[0], false);
        if (userExcuse == null) {
            this.errorPlayer(sender);
            return;
        }

        Player pBanner = (Player) sender;
        SunUser userBanner = plugin.getUserManager().getOrLoadUser(pBanner);

        if (!userBanner.removeIgnoredUser(userExcuse.getName())) {
            plugin.lang().Command_UnIgnore_Error_Already
                .replace("%player%", userExcuse.getName())
                .send(sender);
        }
        else {
            plugin.lang().Command_UnIgnore_Done.
                replace("%player%", userExcuse.getName())
                .send(sender);
        }
    }
}
