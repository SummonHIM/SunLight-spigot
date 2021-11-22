package su.nexmedia.sunlight.command.list;

import org.jetbrains.annotations.NotNull;

import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.command.IPrivateMessageCommand;
import su.nexmedia.sunlight.config.CommandConfig;

public class ReplyCommand extends IPrivateMessageCommand {

    public static final String NAME = "reply";

    public ReplyCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_REPLY);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Reply_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Reply_Desc.getMsg();
    }

    @Override
    public boolean isReply() {
        return true;
    }
}
