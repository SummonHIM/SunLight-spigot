package su.nexmedia.sunlight.modules.kits.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.kits.KitManager;

public class KitListener extends AbstractListener<SunLight> {

    private final KitManager kitManager;

    public KitListener(@NotNull KitManager kitManager) {
        super(kitManager.plugin());
        this.kitManager = kitManager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBindItemPickup(EntityPickupItemEvent e) {
        if (this.kitManager.getKeyItemBind() == null) return;

        ItemStack item = e.getItem().getItemStack();
        LivingEntity entity = e.getEntity();
        if (!this.kitManager.isBindedTo(item, entity)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBindItemClick(InventoryClickEvent e) {
        if (this.kitManager.getKeyItemBind() == null) return;

        ItemStack item = e.getCurrentItem();
        if (item == null || ItemUT.isAir(item)) return;

        Player player = (Player) e.getWhoClicked();
        if (!this.kitManager.isBindedTo(item, player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBindItemDispense(BlockDispenseArmorEvent e) {
        if (this.kitManager.getKeyItemBind() == null) return;

        ItemStack item = e.getItem();
        LivingEntity entity = e.getTargetEntity();
        if (!this.kitManager.isBindedTo(item, entity)) {
            e.setCancelled(true);
        }
    }
}
