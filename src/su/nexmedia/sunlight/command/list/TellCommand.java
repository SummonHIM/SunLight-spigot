package su.nexmedia.sunlight.command.list;

import org.jetbrains.annotations.NotNull;

import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.command.IPrivateMessageCommand;
import su.nexmedia.sunlight.config.CommandConfig;

public class TellCommand extends IPrivateMessageCommand {

    public static final String NAME = "tell";

    public TellCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_TELL);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Tell_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Tell_Desc.getMsg();
    }

    @Override
    public boolean isReply() {
        return false;
    }
}
