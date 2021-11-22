package su.nexmedia.sunlight.modules.enhancements.module.chairs;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.event.entity.EntityDismountEvent;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.LocUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.enhancements.EnhancementManager;
import su.nexmedia.sunlight.modules.enhancements.module.chairs.command.ChairsCommand;
import su.nexmedia.sunlight.modules.enhancements.module.chairs.command.SitCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChairsManager extends AbstractManager<SunLight> {

    public static final  String USER_SETTING_CHAIRS = "CHAIRS";
    private static final String CHAIR_HOLDER_META   = "CHAIR_HOLDER";
    private final EnhancementManager enhancementManager;
    private Set<String>            chairMaterials;
    private Map<Block, ArmorStand> chairHolders;

    public ChairsManager(@NotNull EnhancementManager enhancementManager) {
        super(enhancementManager.plugin());
        this.enhancementManager = enhancementManager;
    }

    @Override
    public void onLoad() {
        JYML cfg = JYML.loadOrExtract(plugin, this.enhancementManager.getPath() + "chairs.yml");

        this.chairHolders = new HashMap<>();
        this.chairMaterials = cfg.getStringSet("Chair_Blocks");

        this.addListener(new Listener(this.plugin));
        this.plugin.getCommandRegulator().register(new ChairsCommand(this.enhancementManager));
        this.plugin.getCommandRegulator().register(new SitCommand(this.enhancementManager, this));
    }

    @Override
    public void onShutdown() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            this.standUp(player, true);
        }
        if (this.chairHolders != null) {
            this.chairHolders.values().forEach(Entity::remove);
            this.chairHolders.clear();
            this.chairHolders = null;
        }
        if (this.chairMaterials != null) {
            this.chairMaterials.clear();
            this.chairMaterials = null;
        }
    }

    @NotNull
    private ArmorStand createChairHolder(@NotNull Player player, @NotNull Block chair) {
        Location playerLoc = player.getEyeLocation();
        playerLoc.setPitch(0.0f);

        Vector vector;
        BlockData blockData = chair.getBlockData();
        if (blockData instanceof Stairs) {
            BlockFace facing = ((Stairs) blockData).getFacing();
            Location blockLoc = chair.getLocation();
            Location faceLoc = chair.getRelative(facing.getOppositeFace()).getLocation();
            vector = faceLoc.clone().subtract(blockLoc.toVector()).toVector();
        }
        else {
            vector = playerLoc.getBlock().getLocation().toVector().subtract(chair.getLocation().toVector());
        }

        Location holderLoc = this.getHolderLocation(chair).setDirection(vector);
        ArmorStand armorStand = player.getWorld().spawn(holderLoc, ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);
        armorStand.setCollidable(false);
        armorStand.setRemoveWhenFarAway(true);
        armorStand.setMetadata(CHAIR_HOLDER_META, new FixedMetadataValue(plugin, chair.getLocation()));
        this.chairHolders.put(chair, armorStand);

        return armorStand;
    }

    @NotNull
    private Location getHolderLocation(@NotNull Block chair) {
        Location seat = LocUT.getCenter(chair.getLocation(), false);
        double dY = 0.25D;
        double height = chair.getBlockData() instanceof Stairs ? 0.5D : chair.getBoundingBox().getHeight();

        BlockData blockData = chair.getBlockData();
        if (blockData instanceof Slab slab) {
            if (slab.getType() == Slab.Type.TOP) {
                height *= 2;
            }
        }

        double normalized = -(dY - height);
        return seat.add(0, normalized, 0);
    }

    @Nullable
    private ArmorStand getChairHolder(@NotNull Player player) {
        if (!this.isSit(player)) return null;
        return (ArmorStand) player.getVehicle();
    }

    private void standUp(@NotNull Player player, boolean off) {
        this.standUp(player, null, off);
    }

    private void standUp(@NotNull Player player, @Nullable ArmorStand stand, boolean off) {
        if (stand == null) stand = this.getChairHolder(player);
        if (stand == null || !stand.hasMetadata(CHAIR_HOLDER_META)) return;

        Location chairBlockLocation = (Location) stand.getMetadata(CHAIR_HOLDER_META).get(0).value();
        if (chairBlockLocation == null) return;

        Block chairBlock = chairBlockLocation.getBlock();
        if (!this.chairHolders.containsKey(chairBlock)) return;

        if (!player.isDead() && !off) {
            Location holderLocation = stand.getLocation();
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                holderLocation.setDirection(player.getLocation().getDirection());
                player.teleport(holderLocation.add(0, 1, 0));
            });
        }

        stand.remove();
        this.chairHolders.remove(chairBlock);
    }

    public void sitPlayer(@NotNull Player player, @NotNull Block chair) {
        if (this.isOccupied(chair)) {
            return;
        }

        SunUser user = plugin.getUserManager().getOrLoadUser(player);
        if (!user.getSettingBoolean(USER_SETTING_CHAIRS)) return;

        ArmorStand fakeSeat = this.createChairHolder(player, chair);
        fakeSeat.addPassenger(player);
    }

    private boolean isSit(@NotNull Player player) {
        Entity holder = player.getVehicle();
        return holder != null && holder.hasMetadata(CHAIR_HOLDER_META);
    }

    private boolean isOccupied(@NotNull Block chair) {
        return this.chairHolders.containsKey(chair);
    }

    private boolean isChair(@NotNull Block chair) {
        if (!chair.getRelative(BlockFace.UP).isEmpty()) {
            return false;
        }

        Material chairType = chair.getType();
        return this.chairMaterials.contains(chairType.name());
    }

    class Listener extends AbstractListener<SunLight> {

        public Listener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.NORMAL)
        public void onChairsEnterRightClick(PlayerInteractEvent e) {
            if (e.useInteractedBlock() == Result.DENY) return;
            if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
            if (e.getHand() != EquipmentSlot.HAND) return;
            if (e.getBlockFace() == BlockFace.DOWN) return;

            Player p = e.getPlayer();
            if (p.isSneaking()) return;

            Block block = e.getClickedBlock();
            if (block == null || !isChair(block)) return;

            if (p.getLocation().distance(LocUT.getCenter(block.getLocation())) >= 2D) {
                return;
            }

            // Stop sit if player is building something over "chair" blocks.
            ItemStack item = e.getItem();
            if (item != null && item.getType().isBlock()) return;

            sitPlayer(p, block);
        }

        @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
        public void onChairsLeaveDismount(EntityDismountEvent e) {
            if (!(e.getEntity() instanceof Player player)) {
                return;
            }
            if (!(e.getDismounted() instanceof ArmorStand stand)) {
                return;
            }

            standUp(player, stand, false);
        }

        @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
        public void onChairsLeaveChangeGameMode(PlayerGameModeChangeEvent e) {
            if (e.getNewGameMode() == GameMode.SPECTATOR) {
                standUp(e.getPlayer(), false);
            }
        }

        @EventHandler(priority = EventPriority.NORMAL)
        public void onChairsLeaveDeath(PlayerDeathEvent e) {
            Player player = e.getEntity();
            standUp(player, true);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onChairsLeaveQuit(PlayerQuitEvent event) {
            Player player = event.getPlayer();
            standUp(player, false);
        }

        @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
        public void onChairsProtectBlockBreak(BlockBreakEvent e) {
            if (isOccupied(e.getBlock())) {
                e.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
        public void onChairsProtectBlockExplode(BlockExplodeEvent e) {
            e.blockList().removeIf(ChairsManager.this::isOccupied);
        }

        @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
        public void onChairsProtectEntityExplode(EntityExplodeEvent e) {
            e.blockList().removeIf(ChairsManager.this::isOccupied);
        }

        @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
        public void onChairsProtectPistonExtend(BlockPistonExtendEvent e) {
            if (e.getBlocks().stream().anyMatch(ChairsManager.this::isOccupied)) {
                e.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
        public void onChairsProtectPistonRetract(BlockPistonRetractEvent e) {
            if (e.getBlocks().stream().anyMatch(ChairsManager.this::isOccupied)) {
                e.setCancelled(true);
            }
        }
    }
}
