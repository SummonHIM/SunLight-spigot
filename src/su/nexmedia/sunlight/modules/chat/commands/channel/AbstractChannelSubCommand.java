package su.nexmedia.sunlight.modules.chat.commands.channel;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.chat.ChatChannel;
import su.nexmedia.sunlight.modules.chat.ChatManager;

import java.util.List;

abstract class AbstractChannelSubCommand extends AbstractCommand<SunLight> {

    protected final ChatManager chatManager;

    public AbstractChannelSubCommand(@NotNull ChatManager chatManager, @NotNull String[] aliases, @Nullable String permission) {
        super(chatManager.plugin(), aliases, permission);
        this.chatManager = chatManager;
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    protected abstract void perform(@NotNull Player player, @NotNull ChatChannel channel);

    @Override
    public @NotNull List<@NotNull String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.chatManager.getChannelsAvailable(player).stream().map(ChatChannel::getId).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            this.printUsage(sender);
            return;
        }

        ChatChannel channel = this.chatManager.getChannel(args[1]);
        if (channel == null) {
            this.chatManager.getLang().Channel_Error_Invalid.send(sender);
            return;
        }

        Player player = (Player) sender;
        this.perform(player, channel);
    }
}
