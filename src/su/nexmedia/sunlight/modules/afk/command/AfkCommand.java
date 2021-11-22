package su.nexmedia.sunlight.modules.afk.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.afk.AfkManager;
import su.nexmedia.sunlight.modules.afk.AfkPerms;

public class AfkCommand extends SunModuleCommand<AfkManager> {

    public AfkCommand(@NotNull AfkManager afkManager) {
        super(afkManager, new String[]{"afk"}, AfkPerms.CMD_AFK);
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_Afk_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        SunUser user = plugin.getUserManager().getOrLoadUser(player);
        if (!module.isAfk(user)) {
            this.module.enterAfk(player, true);
        }
        else {
            this.module.exitAfk(player);
        }
    }
}
