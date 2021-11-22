package su.nexmedia.sunlight.command;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.Constants;
import su.nexmedia.engine.utils.MsgUT;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.command.list.SocialSpyCommand;
import su.nexmedia.sunlight.command.list.TellCommand;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.event.PlayerPrivateMessageEvent;
import su.nexmedia.sunlight.user.IgnoredUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IPrivateMessageCommand extends GeneralCommand<SunLight> {

    private static final Map<String, String> LAST_MESSAGE = new HashMap<>();
    private static final String PLACEHOLDER_MESSAGE      = "%message%";
    private static final String PLACEHOLDER_NAME         = "%player_name%";
    private static final String PLACEHOLDER_DISPLAY_NAME = "%player_display_name%";
    private final Sound soundIn;
    private final Sound soundOut;
    private final String formatTo;
    private final String formatFrom;
    private final String formatSpy;

    public IPrivateMessageCommand(@NotNull SunLight plugin, @NotNull String[] aliases, @NotNull String permission) {
        super(plugin, aliases, permission);

        JYML cfg = CommandConfig.getConfig();
        String path = "Settings." + TellCommand.NAME + ".";
        this.soundIn = cfg.getEnum(path + "Incoming", Sound.class, Sound.BLOCK_NOTE_BLOCK_BELL);
        this.soundOut = cfg.getEnum(path + "Outgoing", Sound.class, Sound.UI_BUTTON_CLICK);

        this.formatFrom = StringUT.color(cfg.getString(path + "Format.From", ""));
        this.formatTo = StringUT.color(cfg.getString(path + "Format.To", ""));
        this.formatSpy = StringUT.color(cfg.getString("Settings." + SocialSpyCommand.NAME + ".Format", ""));
    }

    public abstract boolean isReply();

    @Override
    public final boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public final List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (!this.isReply()) {
            if (i == 1) {
                return PlayerUT.getPlayerNames();
            }
        }
        return super.getTab(player, i, args);
    }

    @Override
    public final void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        String receiverName;

        if (this.isReply()) {
            // Check if /reply contains any text.
            if (args.length == 0) {
                this.printUsage(sender);
                return;
            }
            // For player sender get the last sender from user data.
            // For console get from the local field.
            receiverName = LAST_MESSAGE.get(sender.getName());
            if (receiverName == null) {
                plugin.lang().Command_Reply_Error_Empty.send(sender);
                return;
            }
        }
        else {
            // Check if /tell contains player name and text.
            if (args.length < 2) {
                this.printUsage(sender);
                return;
            }
            // Get player name from the command arguments.
            receiverName = args[0];
        }

        // Get receiver by a player name or just Console.
        boolean isToConsole = receiverName.equalsIgnoreCase(Constants.CONSOLE);
        CommandSender receiver = isToConsole ? plugin.getServer().getConsoleSender() : plugin.getServer().getPlayer(receiverName);
        if (receiver == null) {
            this.errorPlayer(sender);
            return;
        }

        // Merge words (arguments) into one String.
        StringBuilder msgBuild = new StringBuilder();
        for (int i = (this.isReply() ? 0 : 1); i < args.length; i++) {
            msgBuild.append(args[i]);
            msgBuild.append(" ");
        }

        String message = StringUT.colorOff(msgBuild.toString().trim());

        // Prevent to exploit JSON messages.
        if (MsgUT.isJSON(message)) {
            this.errorPermission(sender);
            return;
        }

        String nameFrom = sender.getName();
        String nameTo = receiver.getName();

        // Check if receiver has blocked messages from sender.
        Player pReceiver = receiver instanceof Player ? (Player) receiver : null;
        if (pReceiver != null) {
            SunUser userGeter = plugin.getUserManager().getOrLoadUser(pReceiver);
            IgnoredUser ignoredUser = userGeter.getIgnoredUser(nameFrom);
            if (ignoredUser != null && ignoredUser.isIgnorePrivateMessages()) {
                plugin.lang().User_Ignore_PrivateMessage.send(sender);
                return;
            }
        }

        // Call custom plugin event.
        PlayerPrivateMessageEvent eventSms = new PlayerPrivateMessageEvent(sender, receiver, message);
        plugin.getPluginManager().callEvent(eventSms);
        if (eventSms.isCancelled()) return;

        // Make /reply work for receiver too
        if (pReceiver != null) {
            nameTo = pReceiver.getDisplayName();

            if (this.soundIn != null) {
                MsgUT.sound(pReceiver, this.soundIn);
            }
        }

        // Make /reply work for sender too
        if (sender instanceof Player pSender) {
            nameFrom = pSender.getDisplayName();

            if (this.soundOut != null) {
                MsgUT.sound(pSender, this.soundOut);
            }
        }

        LAST_MESSAGE.put(sender.getName(), receiver.getName());
        LAST_MESSAGE.put(receiver.getName(), sender.getName());

        MsgUT.sendWithJSON(receiver, this.formatFrom
            .replace(PLACEHOLDER_MESSAGE, message)
            .replace(PLACEHOLDER_DISPLAY_NAME, nameFrom).replace(PLACEHOLDER_NAME, sender.getName()));

        MsgUT.sendWithJSON(sender, this.formatTo
            .replace(PLACEHOLDER_MESSAGE, message)
            .replace(PLACEHOLDER_DISPLAY_NAME, nameTo).replace(PLACEHOLDER_NAME, receiver.getName()));

        String spy = this.formatSpy
            .replace(PLACEHOLDER_MESSAGE, message)
            .replace("%from%", sender.getName())
            .replace("%to%", receiver.getName());

        for (Player pServer : plugin.getServer().getOnlinePlayers()) {
            if (pServer.equals(sender) || pServer.equals(receiver)) continue;
            if (!pServer.hasPermission(SunPerms.CMD_SOCIALSPY)) continue;

            SunUser userSpy = plugin.getUserManager().getOrLoadUser(pServer);
            if (userSpy.isSocialSpy()) {
                pServer.sendMessage(spy);
            }
        }
    }
}
