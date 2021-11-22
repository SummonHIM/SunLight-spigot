package su.nexmedia.sunlight.modules.homes.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Constants;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.homes.HomeManager;
import su.nexmedia.sunlight.modules.homes.HomePerms;

public class SetHomeCommand extends SunModuleCommand<HomeManager> {

    public static final String NAME = "sethome";

    public SetHomeCommand(@NotNull HomeManager homeManager) {
        super(homeManager, CommandConfig.getAliases(NAME), HomePerms.HOMES_CMD_SETHOME);
    }

    @Override
    @NotNull
    public String getUsage() {
        return module.getLang().Command_SetHome_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return module.getLang().Command_SetHome_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length > 1) {
            this.printUsage(sender);
            return;
        }

        Player player = (Player) sender;
        String home = args.length == 1 ? args[0].toLowerCase() : Constants.DEFAULT;
        this.module.setHome(player, home, false);
    }
}
