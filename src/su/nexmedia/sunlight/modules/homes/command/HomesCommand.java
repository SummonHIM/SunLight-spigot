package su.nexmedia.sunlight.modules.homes.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.homes.HomeManager;
import su.nexmedia.sunlight.modules.homes.HomePerms;

import java.util.List;

public class HomesCommand extends SunModuleCommand<HomeManager> {

    public static final String NAME = "homes";

    public HomesCommand(@NotNull HomeManager homeManager) {
        super(homeManager, CommandConfig.getAliases(NAME), HomePerms.HOMES_CMD_HOMES);
    }

    @Override
    @NotNull
    public String getUsage() {
        return module.getLang().Command_Homes_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return module.getLang().Command_Homes_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
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
        Player player = (Player) sender;
        if (args.length >= 1 && !player.hasPermission(HomePerms.HOMES_CMD_HOMES_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        String userName = args.length >= 1 ? args[0] : null;
        if (userName != null) {
            SunUser user = plugin.getUserManager().getOrLoadUser(userName, false);
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }
            this.module.getHomesMenu().open(player, userName);
        }
        else {
            this.module.getHomesMenu().open(player, 1);
        }
    }
}
