package su.nexmedia.sunlight.modules.bans.command.api;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.utils.Constants;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.bans.BanManager;
import su.nexmedia.sunlight.modules.bans.BanTime;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentReason;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractPunishCommand extends SunModuleCommand<BanManager> {

    protected PunishmentType type;

    public AbstractPunishCommand(@NotNull BanManager module, @NotNull String[] aliases, @Nullable String permission,
                                 @NotNull PunishmentType type) {
        super(module, aliases, permission);
        this.type = type;
    }

    @NotNull
    protected String fineUserName(@NotNull CommandSender sender, @NotNull String userName) {
        return userName;
    }

    protected boolean checkUserName(@NotNull CommandSender sender, @NotNull String userName) {
        /*SunUser user = plugin.getUserManager().getOrLoadUser(userName, false);
        if (user == null) {
            this.errorPlayer(sender);
            return false;
        }*/
        return true;
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return PlayerUT.getPlayerNames();
        }
        if (i == 2) {
            List<String> list = new ArrayList<>(Arrays.asList("-1", "15" + BanTime.MINUTES.getAlias(), "1" + BanTime.HOURS.getAlias()));
            list.addAll(this.module.getReasons().stream().map(PunishmentReason::getId).toList());
            return list;
        }
        if (i == 3) {
            return this.module.getReasons().stream().map(PunishmentReason::getId).toList();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            this.printUsage(sender);
            return;
        }

        String userName = this.fineUserName(sender, args[0]);
        if (!this.checkUserName(sender, userName)) return;

        // Parse ban time from command argument. Returns -1 if time is not parsed or negative or zero provided.
        long banTime = BanTime.parse(args.length >= 2 ? args[1] : "-1");
        // Find position where the punishment reason starts.
        int reasonIndex = banTime <= 0 ? 1 : 2;

        // Build reason text from all other arguments.
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

        this.module.punish(userName, sender, reasonMsg, banTime, this.type);
    }
}
