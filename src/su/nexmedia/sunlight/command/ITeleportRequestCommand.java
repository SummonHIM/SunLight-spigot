package su.nexmedia.sunlight.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.engine.utils.TimeUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.command.list.TpaCommand;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.event.PlayerTeleportRequestEvent;
import su.nexmedia.sunlight.user.IgnoredUser;
import su.nexmedia.sunlight.user.TeleportRequest;
import su.nexmedia.sunlight.data.SunUser;

import java.util.List;

public abstract class ITeleportRequestCommand extends GeneralCommand<SunLight> {

    protected int timeout;

    public ITeleportRequestCommand(@NotNull SunLight plugin, @NotNull String[] aliases, @NotNull String permission) {
        super(plugin, aliases, permission);

        JYML cfg = CommandConfig.getConfig();
        this.timeout = cfg.getInt("Settings." + TpaCommand.NAME + ".Request_Timeout", 30);
    }

    public abstract boolean isSummon();

    @NotNull
    public abstract ILangMsg getNotifyFromMessage();

    @NotNull
    public abstract ILangMsg getNotifyToMessage();

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
        if (args.length != 1) {
            this.printUsage(sender);
            return;
        }

        Player pAccepter = plugin.getServer().getPlayer(args[0]);
        if (pAccepter == null) {
            this.errorPlayer(sender);
            return;
        }

        SunUser userAccepter = plugin.getUserManager().getOrLoadUser(pAccepter);

        // Check if 'accepter' disaled requests so request should be declined.
        if (!userAccepter.isTeleportRequestsEnabled()) {
            plugin.lang().Command_TeleportRequest_Error_Disabled
                .replace("%player%", pAccepter.getName())
                .send(sender);
            return;
        }

        // Check if 'accepter' is ignoring 'sended' so request should be declined.
        IgnoredUser ignoredUser = userAccepter.getIgnoredUser(sender.getName());
        if (ignoredUser != null && ignoredUser.isIgnoreTeleportRequests()) {
            plugin.lang().User_Ignore_TeleportRequest.send(sender);
            return;
        }

        // Check if user already sent request to this player and it's not expired yet.
        // So players can't spam requests while there is active one.
        TeleportRequest requestHas = userAccepter.getTeleportRequest(sender.getName());
        if (requestHas != null && !requestHas.isExpired()) {
            plugin.lang().Command_TeleportRequest_Error_Cooldown
                .replace("%time%", TimeUT.formatTimeLeft(requestHas.getExpireDate(), System.currentTimeMillis()))
                .send(sender);
            return;
        }

        // Create a TeleportRequest object.
        TeleportRequest request = new TeleportRequest(sender.getName(), pAccepter.getName(), this.isSummon(), this.timeout);

        // Call a custom TeleportRequest event, so other plugins or modules can handle it.
        PlayerTeleportRequestEvent eventTeleport = new PlayerTeleportRequestEvent(request);
        plugin.getPluginManager().callEvent(eventTeleport);
        if (eventTeleport.isCancelled()) return;

        // Add TeleportRequest object to user teleport requests list.
        userAccepter.addTeleportRequest(request, false);

        // Send teleport notifications.
        this.getNotifyToMessage().replace("%player%", sender.getName()).send(pAccepter);
        this.getNotifyFromMessage().replace("%player%", pAccepter.getName()).send(sender);
    }
}
