package su.nexmedia.sunlight.modules.bans.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AbstractMenuAuto;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.bans.BanManager;
import su.nexmedia.sunlight.modules.bans.BanPerms;
import su.nexmedia.sunlight.modules.bans.punishment.Punishment;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentType;

public abstract class AbstractPunishmentListMenu extends AbstractMenuAuto<SunLight, Punishment> {

    protected final BanManager     banManager;
    protected final PunishmentType punishmentType;

    public AbstractPunishmentListMenu(@NotNull BanManager banManager, @NotNull JYML cfg, @NotNull PunishmentType punishmentType) {
        super(banManager.plugin(), cfg, "");
        this.banManager = banManager;
        this.punishmentType = punishmentType;
        this.title = this.title.replace("%type%", banManager.getLang().getEnum(punishmentType));
        this.objectSlots = cfg.getIntArray("Punishment.Slots");

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                this.onItemClickDefault(player, type2);
            }
        };

        for (String sId : cfg.getSection("Content")) {
            IMenuItem menuItem = cfg.getMenuItem("Content." + sId, MenuItemType.class);

            if (menuItem.getType() != null) {
                menuItem.setClick(click);
            }
            this.addItem(menuItem);
        }
    }

    @Override
    @NotNull
    protected IMenuClick getObjectClick(@NotNull Player player, @NotNull Punishment punishment) {
        return (player1, type, e) -> {
            if (e.isShiftClick()) {
                if (e.isRightClick()) {
                    if (!player1.hasPermission(BanPerms.HISTORY_REMOVE)) {
                        plugin.lang().Error_NoPerm.send(player1);
                        return;
                    }
                    this.banManager.deletePunishment(punishment);
                }
                else if (e.isLeftClick()) {
                    if (punishment.isExpired()) return;
                    if (!player1.hasPermission(BanPerms.HISTORY_EXPIRE)) {
                        plugin.lang().Error_NoPerm.send(player1);
                        return;
                    }
                    punishment.expire();
                    this.banManager.getDataHandler().savePunishment(punishment);
                }
                this.plugin.getServer().getScheduler().runTaskLater(plugin, c -> {
                    this.open(player1, this.getPage(player1));
                }, this.banManager.isLocalCacheEnabled() ? 1L : 5L);
            }
        };
    }

    @Override
    public boolean cancelClick(@NotNull SlotType slotType, int slot) {
        return true;
    }
}
