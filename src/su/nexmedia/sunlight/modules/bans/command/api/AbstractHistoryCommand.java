package su.nexmedia.sunlight.modules.bans.command.api;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.utils.regex.RegexUT;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.bans.BanManager;
import su.nexmedia.sunlight.modules.bans.menu.PunishmentHistoryMenu;
import su.nexmedia.sunlight.modules.bans.punishment.Punishment;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentType;

import java.util.List;

public abstract class AbstractHistoryCommand extends SunModuleCommand<BanManager> {

    protected final PunishmentType type;

    public AbstractHistoryCommand(@NotNull BanManager module,
                                  @NotNull String[] aliases, @Nullable String permission,
                                  @NotNull PunishmentType type) {
        super(module, aliases, permission);
        this.type = type;
    }

    @NotNull
    protected abstract String getPermissionOthers();

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1 && player.hasPermission(this.getPermissionOthers())) {
            return module.getPunishments(this.type).stream().map(Punishment::getUser).toList();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        // If player don't have permission to view other player's history, then deny command execution.
        if (args.length >= 1 && !player.hasPermission(this.getPermissionOthers())) {
            this.errorPermission(sender);
            return;
        }

        // Get the user name to view history of.
        String userName = args.length >= 1 ? args[0] : sender.getName();

        // If 'userName' is not an IP address, we have to check if user is exists.
        // not needed to open menu for non-existent users.
        // If 'userName' is an IP address, we can open menu even if there are no players with such IP,
        // because we can't check if this IP is valid.
        if (!RegexUT.isIpAddress(userName)) {
            SunUser user = plugin.getUserManager().getOrLoadUser(userName, false);
            if (user == null && this.module.getPunishments(userName).isEmpty()) {
                this.errorPlayer(sender);
                return;
            }
            //userName = user.getName();
        }

        PunishmentHistoryMenu historyMenu = this.module.getHistoryMenu(this.type);
        historyMenu.open(player, userName);
    }
}
