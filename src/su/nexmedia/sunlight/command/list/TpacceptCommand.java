package su.nexmedia.sunlight.command.list;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.LocUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.user.TeleportRequest;

import java.util.List;

public class TpacceptCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "tpaccept";

    public TpacceptCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_TPACCEPT);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_TpAccept_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_TpAccept_Desc.getMsg();
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
        if (args.length > 1) {
            this.printUsage(sender);
            return;
        }

        Player pAccept = (Player) sender;

        // Get accepter user data.
        SunUser userAccept = plugin.getUserManager().getOrLoadUser(pAccept);

        // Get TeleportRequest by a sender name (if provided) or just the latest one.
        // Expired requests are not included and will be NULL.
        TeleportRequest storedRequest = args.length == 0 ? userAccept.getTeleportRequest() : userAccept.getTeleportRequest(args[0]);
        if (storedRequest == null) {
            plugin.lang().Command_TpAccept_Error_Empty.send(sender);
            return;
        }

        // Check if request sender is online.
        Player pAsker = plugin.getServer().getPlayer(storedRequest.getSender());
        if (pAsker == null) {
            this.errorPlayer(sender);
            return;
        }

        // Get destination location.
        Player teleporter = storedRequest.isSummon() ? pAccept : pAsker;
        Location destination = storedRequest.isSummon() ? pAsker.getLocation() : pAccept.getLocation();
        Location ground = teleporter.isFlying() ? destination : LocUT.getFirstGroundBlock(destination);

        // Teleport player to a destination location.
        teleporter.teleport(ground);

        // Expire request.
        storedRequest.expire();

        // Send notification.
        plugin.lang().Command_TpAccept_Done
            .replace("%player%", pAsker.getName())
            .send(pAccept);
    }
}
