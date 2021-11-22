package su.nexmedia.sunlight.modules.bans.command.api;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.sunlight.modules.bans.BanManager;
import su.nexmedia.sunlight.modules.bans.punishment.Punishment;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentType;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractUnPunishCommand extends AbstractPunishCommand {

    public AbstractUnPunishCommand(@NotNull BanManager module, @NotNull String[] aliases, @Nullable String permission, @NotNull PunishmentType type) {
        super(module, aliases, permission, type);
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.module.getPunishments(this.type).stream()
                .filter(Predicate.not(Punishment::isExpired)).map(Punishment::getUser).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            this.printUsage(sender);
            return;
        }

        String userName = this.fineUserName(sender, args[0]);
        if (!this.checkUserName(sender, userName)) return;

        this.module.unpunish(userName, sender, this.type);
    }
}
