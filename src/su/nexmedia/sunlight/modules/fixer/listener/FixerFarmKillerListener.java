package su.nexmedia.sunlight.modules.fixer.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.fixer.FixerManager;
import su.nexmedia.sunlight.modules.fixer.config.FixerConfig;

public class FixerFarmKillerListener extends AbstractListener<SunLight> {

    //private final FixerManager fixerManager;

    public FixerFarmKillerListener(@NotNull FixerManager fixerManager) {
        super(fixerManager.plugin());
        //this.fixerManager = fixerManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFarmKillerEnderEndermite(EntityTargetEvent e) {
        if (!FixerConfig.FARM_KILLER_ENDERMITE_MINECART) return;

        Entity enderman = e.getEntity();
        if (enderman instanceof Enderman) {
            Entity target = e.getTarget();
            if (target instanceof Endermite && target.isInsideVehicle()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFarmKillerFishingAuto(PlayerInteractEvent e) {
        if (!FixerConfig.FARM_KILLER_AUTO_FISHING) return;
        if (e.useInteractedBlock() == Event.Result.DENY) return;
        if (e.useItemInHand() == Event.Result.DENY) return;

        Block block = e.getClickedBlock();
        if (block == null) return;

        Player player = e.getPlayer();
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();
        if (!this.isFishingRod(main) && !this.isFishingRod(off)) return;

        Material blockType = block.getType();
        if (!blockType.isInteractable() && blockType.isSolid()) return;

        player.getNearbyEntities(16D, 16D, 16).stream()
            .filter(entity -> entity.getType() == EntityType.FISHING_HOOK).map(entity -> (FishHook) entity)
            .filter(fishHook -> fishHook.getShooter() instanceof Player && fishHook.getShooter().equals(player))
            .forEach(Entity::remove);
    }

    private boolean isFishingRod(@NotNull ItemStack item) {
        return !ItemUT.isAir(item) && item.getType() == Material.FISHING_ROD;
    }
}
