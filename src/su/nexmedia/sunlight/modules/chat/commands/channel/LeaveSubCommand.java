package su.nexmedia.sunlight.modules.chat.commands.channel;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.modules.chat.ChatChannel;
import su.nexmedia.sunlight.modules.chat.ChatManager;
import su.nexmedia.sunlight.modules.chat.ChatPerms;

class LeaveSubCommand extends AbstractChannelSubCommand {

    public LeaveSubCommand(@NotNull ChatManager chatManager) {
        super(chatManager, new String[]{"leave"}, ChatPerms.CMD_CHATCHANNEL);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.chatManager.getLang().Command_Channel_Join_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.chatManager.getLang().Command_Channel_Join_Desc.getMsg();
    }

    @Override
    protected void perform(@NotNull Player player, @NotNull ChatChannel channel) {
        this.chatManager.leaveChannel(player, channel);
    }
}
