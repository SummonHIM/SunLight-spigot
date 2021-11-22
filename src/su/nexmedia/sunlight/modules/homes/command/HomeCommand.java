package su.nexmedia.sunlight.modules.homes.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Constants;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.homes.Home;
import su.nexmedia.sunlight.modules.homes.HomeManager;
import su.nexmedia.sunlight.modules.homes.HomePerms;

import java.util.List;

public class HomeCommand extends SunModuleCommand<HomeManager> {

    public static final String NAME = "home";

    public HomeCommand(@NotNull HomeManager homeManager) {
        super(homeManager, CommandConfig.getAliases(NAME), HomePerms.HOMES_CMD_HOME);
    }

    @Override
    @NotNull
    public String getUsage() {
        return module.getLang().Command_Home_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return module.getLang().Command_Home_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            List<String> list = this.module.getPlayerHomeNames(player.getName());
            if (player.hasPermission(HomePerms.HOMES_CMD_HOME_OTHERS)) {
                list.addAll(PlayerUT.getPlayerNames());
            }
            return list;
        }
        if (i == 2 && player.hasPermission(HomePerms.HOMES_CMD_HOME_OTHERS)) {
            return this.module.getPlayerHomeNames(args[0]);
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length > 2) {
            this.printUsage(sender);
            return;
        }

        Player player = (Player) sender;
        String owner = sender.getName();
        String id = Constants.DEFAULT;

        if (args.length == 1) {
            id = args[0].toLowerCase();
            Home home = this.module.getPlayerHome(sender.getName(), id);

            if (home != null) {
                home.teleport(player);
                return;
            }
            else {
                id = Constants.DEFAULT;
                owner = args[0];
            }
        }
        else if (args.length == 2) {
            owner = args[0];
            id = args[1].toLowerCase();
        }

        Home home = this.module.getPlayerHome(owner, id);
        if (home == null) {
            module.getLang().Command_Home_Error_Invalid.send(sender);
            return;
        }

        if (!home.isOwner(player) &&
            !sender.hasPermission(HomePerms.HOMES_CMD_HOME_OTHERS) && !home.isInvitedPlayer(player)) {
            this.errorPermission(sender);
            return;
        }

        home.teleport(player);
    }
}
