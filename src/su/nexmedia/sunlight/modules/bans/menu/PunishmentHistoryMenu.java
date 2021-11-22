package su.nexmedia.sunlight.modules.bans.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.bans.BanManager;
import su.nexmedia.sunlight.modules.bans.punishment.Punishment;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PunishmentHistoryMenu extends AbstractPunishmentListMenu {

    private final ItemStack iconActual;
    private final ItemStack iconExpired;

    private final Map<String, String> openOthers;

    public PunishmentHistoryMenu(@NotNull BanManager banManager, @NotNull JYML cfg, @NotNull PunishmentType punishmentType) {
        super(banManager, cfg, punishmentType);
        this.openOthers = new HashMap<>();

        this.iconActual = cfg.getItem("Punishment.Item_Actual");
        this.iconExpired = cfg.getItem("Punishment.Item_Expired");
    }

    public void open(@NotNull Player player, @NotNull String userOther) {
        this.openOthers.put(player.getName(), userOther);
        this.open(player, 1);
    }

    @NotNull
    private String getUser(@NotNull Player player) {
        return this.openOthers.getOrDefault(player.getName(), player.getName());
    }

    @Override
    @NotNull
    protected List<Punishment> getObjects(@NotNull Player player) {
        // userName can be as player name and as IP address.
        String userName = this.getUser(player);
        // SunUser is needed to add punishments for user's IP address (if there are any)
        SunUser user = plugin.getUserManager().getOrLoadUser(userName, false);

        // Get punishments for raw 'userName', it work for both player name and IP address.
        List<Punishment> list = new ArrayList<>(banManager.getPunishments(userName, this.punishmentType));
        // If user of our 'userName' is present, then also display all punishments of his IP address.
        if (user != null) list.addAll(banManager.getPunishments(user.getIp(), this.punishmentType));

        return new ArrayList<>(list.stream()
            .sorted((p1, p2) -> (int) (p2.getCreatedDate() - p1.getCreatedDate())).toList());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull Punishment punishment) {
        ItemStack item = new ItemStack(punishment.isExpired() ? this.iconExpired : this.iconActual);
        ItemUT.replace(item, punishment.replacePlaceholders());
        return item;
    }

    @Override
    public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

    }

    @Override
    public void onClose(@NotNull Player player, @NotNull InventoryCloseEvent e) {
        super.onClose(player, e);
        this.openOthers.remove(player.getName());
    }
}
