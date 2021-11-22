package su.nexmedia.sunlight.modules.menu.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.menu.MenuManager;
import su.nexmedia.sunlight.modules.menu.MenuPerms;
import su.nexmedia.sunlight.modules.menu.SunMenu;

import java.util.List;

public class MenuCommand extends SunModuleCommand<MenuManager> {

    public static final String NAME = "menu";

    public MenuCommand(@NotNull MenuManager menuManager) {
        super(menuManager, CommandConfig.getAliases(NAME), MenuPerms.CMD_MENU);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.module.getLang().Command_Menu_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_Menu_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return this.module.getMenus(player).stream().map(SunMenu::getId).toList();
        }
        if (i == 2) {
            return PlayerUT.getPlayerNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length > 2 || args.length == 0) {
            this.printUsage(sender);
            return;
        }

        if (args.length == 1 && !(sender instanceof Player)) {
            this.errorSender(sender);
            return;
        }

        Player player;
        if (args.length == 2) {
            if (!sender.hasPermission(MenuPerms.CMD_MENU_OTHERS)) {
                this.errorPermission(sender);
                return;
            }
            player = plugin.getServer().getPlayer(args[1]);
        }
        else {
            player = (Player) sender;
        }

        if (player == null) {
            this.errorPlayer(sender);
            return;
        }

        String id = args[0];
        SunMenu sunMenu = this.module.getMenuById(id);
        if (sunMenu == null) {
            this.module.getLang().Menu_Error_Invalid.send(player);
            return;
        }

        sunMenu.open(player, !sender.equals(player));

        if (!sender.equals(player)) {
            this.module.getLang().Command_Menu_Others_Done
                .replace("%menu%", sunMenu.getId()).replace("%player%", player.getName()).send(sender);
        }
    }
}
