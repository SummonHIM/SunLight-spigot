package su.nexmedia.sunlight.modules.bans.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.sunlight.modules.bans.BanManager;
import su.nexmedia.sunlight.modules.bans.punishment.Punishment;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PunishmentListMenu extends AbstractPunishmentListMenu {

    private final ItemStack icon;

    public PunishmentListMenu(@NotNull BanManager banManager, @NotNull JYML cfg, @NotNull PunishmentType punishmentType) {
        super(banManager, cfg, punishmentType);
        this.icon = cfg.getItem("Punishment.Icon");
    }

    @Override
    @NotNull
    protected List<Punishment> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.banManager.getPunishments(this.punishmentType).stream()
            .filter(Predicate.not(Punishment::isExpired))
            .sorted((p1, p2) -> (int) (p2.getCreatedDate() - p1.getCreatedDate())).toList());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull Punishment punishment) {
        ItemStack item = new ItemStack(this.icon);
        ItemUT.replace(item, punishment.replacePlaceholders());
        return item;
    }

    @Override
    public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

    }
}
