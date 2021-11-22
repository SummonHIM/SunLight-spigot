package su.nexmedia.sunlight.modules.bans.command.api;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.bans.BanManager;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentType;

public abstract class AbstractListCommand extends SunModuleCommand<BanManager> {

    protected PunishmentType punishmentType;

    public AbstractListCommand(@NotNull BanManager banManager, @NotNull String[] aliases, @Nullable String permission,
                               @NotNull PunishmentType punishmentType) {
        super(banManager, aliases, permission);
        this.punishmentType = punishmentType;
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        this.module.getListMenu(this.punishmentType).open(player, 1);
    }
}
