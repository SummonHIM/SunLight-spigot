package su.nexmedia.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.user.TeleportRequest;

import java.util.List;

public class TpdenyCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "tpdeny";

    public TpdenyCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_TPDENY);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_TpDeny_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_TpDeny_Desc.getMsg();
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
            return user.getTeleportRequests().stream().map(TeleportRequest::getSender).toList();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            this.printUsage(sender);
            return;
        }

        // Check if teleport sender is online.
        Player pAsker = plugin.getServer().getPlayer(args[0]);
        if (pAsker == null) {
            this.errorPlayer(sender);
            return;
        }

        // Get accepter user data.
        Player pAccepter = (Player) sender;
        SunUser userAccepter = plugin.getUserManager().getOrLoadUser(pAccepter);

        // Get request by a sender name.
        // Expired requests are not included and will be NULL.
        TeleportRequest requestAsker = userAccepter.getTeleportRequest(pAsker.getName());
        if (requestAsker == null) {
            plugin.lang().Command_TpDeny_Error_Empty.send(sender);
            return;
        }

        // Expire request.
        requestAsker.expire();

        // Send notifications.
        this.plugin.lang().Command_TpDeny_Done_In
            .replace("%player%", pAsker.getName()).send(sender);

        this.plugin.lang().Command_TpDeny_Done_Out
            .replace("%player%", pAccepter.getName()).send(pAsker);
    }
}
