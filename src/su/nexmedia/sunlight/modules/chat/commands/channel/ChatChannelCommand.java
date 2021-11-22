package su.nexmedia.sunlight.modules.chat.commands.channel;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.chat.ChatManager;
import su.nexmedia.sunlight.modules.chat.ChatPerms;

public class ChatChannelCommand extends SunModuleCommand<ChatManager> {

    public static final String NAME = "chatchannel";

    public ChatChannelCommand(@NotNull ChatManager chatManager) {
        super(chatManager, CommandConfig.getAliases(NAME), ChatPerms.CMD_CHATCHANNEL);

        this.addDefaultCommand(new HelpSubCommand<>(chatManager.plugin()));
        this.addChildren(new JoinSubCommand(chatManager));
        this.addChildren(new LeaveSubCommand(chatManager));
        this.addChildren(new SetSubCommand(chatManager));
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_Channel_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {

    }
}
