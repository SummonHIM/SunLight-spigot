package su.nexmedia.sunlight.modules.rtp.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.rtp.RtpManager;
import su.nexmedia.sunlight.modules.rtp.RtpPerms;

public class RtpCommand extends SunModuleCommand<RtpManager> {

    public static final String NAME = "rtp";

    public RtpCommand(@NotNull RtpManager rtpManager) {
        super(rtpManager, CommandConfig.getAliases(NAME), RtpPerms.CMD_RTP);
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_RTP_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        this.module.randomTp(player);
    }
}
