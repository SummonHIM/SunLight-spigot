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

public class TeleportCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "teleport";

    public TeleportCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_TELEPORT);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Tp_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Tp_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1 || (i == 2 && player.hasPermission(SunPerms.CMD_TELEPORT_OTHERS))) {
            return PlayerUT.getPlayerNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length > 2 || args.length < 1) {
            this.printUsage(sender);
            return;
        }
        if (args.length == 1 && !(sender instanceof Player)) {
            this.errorSender(sender);
            return;
        }

        if (args.length == 1) {
            Player pFrom = (Player) sender;
            Player pTo = plugin.getServer().getPlayer(args[0]);

            if (pTo == null) {
                this.errorPlayer(sender);
                return;
            }

            pFrom.teleport(pTo);
            plugin.lang().Command_Tp_Done_Self.replace("%player%", pTo.getName()).send(pFrom);
        }
        else if (args.length == 2) {
            if (!sender.hasPermission(SunPerms.CMD_TELEPORT_OTHERS)) {
                this.errorPermission(sender);
                return;
            }

            Player pFrom = plugin.getServer().getPlayer(args[0]);
            Player pTo = plugin.getServer().getPlayer(args[1]);

            if (pFrom == null || pTo == null) {
                this.errorPlayer(sender);
                return;
            }

            pFrom.teleport(pTo);

            plugin.lang().Command_Tp_Done_Others
                .replace("%who%", pFrom.getName()).replace("%to%", pTo.getName())
                .send(sender);
        }
    }
}
