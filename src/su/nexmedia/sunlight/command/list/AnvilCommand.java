package su.nexmedia.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.List;

public class AnvilCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "anvil";

    public AnvilCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_ANVIL);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Anvil_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Anvil_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1 && player.hasPermission(SunPerms.CMD_ANVIL_OTHERS)) {
            return PlayerUT.getPlayerNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        String pName;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                this.errorSender(sender);
                return;
            }
            pName = sender.getName();
        }
        else {
            if (args.length > 1) {
                this.printUsage(sender);
                return;
            }
            if (!sender.hasPermission(SunPerms.CMD_ANVIL_OTHERS)) {
                this.errorPermission(sender);
                return;
            }
            pName = args[0];
        }

        Player pTarget = plugin.getServer().getPlayer(pName);
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        plugin.getSunNMS().virtAnvil(pTarget);

        if (!sender.equals(pTarget)) {
            plugin.lang().Command_Anvil_Done_Others.replace("%player%", pTarget.getName()).send(sender);
        }
    }
}
