package su.nexmedia.sunlight.modules.bans.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Constants;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.bans.BanManager;
import su.nexmedia.sunlight.modules.bans.BanPerms;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentReason;

import java.util.List;

public class KickCommand extends SunModuleCommand<BanManager> {

    public static final String NAME = "kick";

    public KickCommand(@NotNull BanManager banManager) {
        super(banManager, CommandConfig.getAliases(NAME), BanPerms.CMD_KICK);
    }

    @Override
    @NotNull
    public String getUsage() {
        return module.getLang().Command_Kick_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return module.getLang().Command_Kick_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player p, int i, @NotNull String[] args) {
        if (i == 1) {
            return PlayerUT.getPlayerNames();
        }
        if (i == 2) {
            return this.module.getReasons().stream().map(PunishmentReason::getId).toList();
        }
        return super.getTab(p, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            this.printUsage(sender);
            return;
        }

        Player player = plugin.getServer().getPlayer(args[0]);
        if (player == null) {
            this.errorPlayer(sender);
            return;
        }

        int reasonIndex = 1;
        String reasonMsg;
        PunishmentReason reason = args.length > reasonIndex ? this.module.getReason(args[reasonIndex]) : this.module.getReason(Constants.DEFAULT);

        if (reason != null) {
            reasonMsg = reason.getMessage();
        }
        else {
            StringBuilder reasonBuilder = new StringBuilder();
            if (args.length > reasonIndex) {
                for (int index = reasonIndex; index < args.length; index++) {
                    reasonBuilder.append(args[index]).append(" ");
                }
            }
            reasonMsg = reasonBuilder.toString().trim();
        }

        this.module.kick(player.getName(), sender, reasonMsg);
    }
}
