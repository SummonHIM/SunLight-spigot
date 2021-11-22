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

public class DeleteHomeCommand extends SunModuleCommand<HomeManager> {

    public static final String NAME = "deletehome";

    public DeleteHomeCommand(@NotNull HomeManager homeManager) {
        super(homeManager, CommandConfig.getAliases(NAME), HomePerms.HOMES_CMD_DELHOME);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.module.getLang().Command_DeleteHome_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_DeleteHome_Desc.getMsg();
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
            if (player.hasPermission(HomePerms.HOMES_CMD_DELHOME_OTHERS)) {
                list.addAll(PlayerUT.getPlayerNames());
            }
            return list;
        }
        if (i == 2 && player.hasPermission(HomePerms.HOMES_CMD_DELHOME_OTHERS)) {
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

        String target = sender.getName();
        String id = Constants.DEFAULT;

        if (args.length == 1) {
            if (this.module.hasHome(target, args[0])) {
                id = args[0].toLowerCase();
            }
            else {
                if (!sender.hasPermission(HomePerms.HOMES_CMD_DELHOME_OTHERS)) {
                    this.errorPermission(sender);
                    return;
                }
                target = args[0];
            }
        }
        else if (args.length == 2) {
            if (!sender.hasPermission(HomePerms.HOMES_CMD_DELHOME_OTHERS)) {
                this.errorPermission(sender);
                return;
            }
            target = args[0];
            id = args[1].toLowerCase();
        }

        Home home = this.module.getPlayerHome(target, id);
        if (!this.module.deleteHome(target, id) || home == null) {
            this.module.getLang().Command_DeleteHome_Error.send(sender);
            return;
        }

        (sender instanceof Player pSender && home.isOwner(pSender) ? module.getLang().Command_DeleteHome_Done_Own : module.getLang().Command_DeleteHome_Done_Other)
            .replace(home.replacePlaceholders()).send(sender);
    }
}
