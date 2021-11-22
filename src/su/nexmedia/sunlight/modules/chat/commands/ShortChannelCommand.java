package su.nexmedia.sunlight.modules.chat.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.chat.ChatChannel;
import su.nexmedia.sunlight.modules.chat.ChatManager;
import su.nexmedia.sunlight.modules.chat.ChatPerms;

import java.util.stream.Stream;

public class ShortChannelCommand extends SunModuleCommand<ChatManager> {

    private final ChatChannel channel;

    public ShortChannelCommand(@NotNull ChatManager chatManager, @NotNull ChatChannel channel) {
        super(chatManager, new String[]{channel.getCommandAlias()}, ChatPerms.CMD_CHANNEL + channel.getId());
        this.channel = channel;
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.module.getLang().Command_ShortChannel_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_ShortChannel_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            this.module.setChannel(player, this.channel);
            return;
        }

        String message = String.join(" ", Stream.of(args).toList());
        String prefix = this.channel.getMessagePrefix();
        player.chat(prefix + message);
    }
}
