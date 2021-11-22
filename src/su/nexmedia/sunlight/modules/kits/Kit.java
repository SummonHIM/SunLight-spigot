package su.nexmedia.sunlight.modules.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractLoadableItem;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.api.manager.IPlaceholder;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.hooks.external.VaultHK;
import su.nexmedia.engine.utils.*;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.kits.editor.EditorMenuKit;
import su.nexmedia.sunlight.modules.kits.menu.KitMenuPreview;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class Kit extends AbstractLoadableItem<SunLight> implements ICleanable, IEditable, IPlaceholder {

    public static final String PLACEHOLDER_ID                  = "%kit_id%";
    public static final String PLACEHOLDER_NAME                = "%kit_name%";
    public static final String PLACEHOLDER_DESCRIPTION         = "%kit_description%";
    public static final String PLACEHOLDER_PERMISSION_REQUIRED = "%kit_permission_required%";
    public static final String PLACEHOLDER_PERMISSION_NODE     = "%kit_permission_node%";
    public static final String PLACEHOLDER_COOLDOWN            = "%kit_cooldown%";
    public static final String PLACEHOLDER_COST_MONEY          = "%kit_cost_money%";
    public static final String PLACEHOLDER_PRIORITY            = "%kit_priority%";
    public static final String PLACEHOLDER_ICON                = "%kit_icon%";
    public static final String PLACEHOLDER_COMMANDS            = "%kit_commands%";
    public static final String PLACEHOLDER_USER_ACCESS   = "%kit_user_access%";
    public static final String PLACEHOLDER_USER_COOLDOWN = "%kit_user_cooldown%";
    private final KitManager kitManager;
    private String       name;
    private List<String> description;
    private boolean      isPermission;
    private int          cooldown;
    private double       cost;
    private int          priority;
    private ItemStack    icon;
    private List<String> commands;
    private ItemStack[]  items;
    private ItemStack[]  armor;
    private KitMenuPreview preview;
    private EditorMenuKit  editor;

    // Create
    public Kit(@NotNull KitManager kitManager, @NotNull String id) {
        super(kitManager.plugin(), kitManager.getFullPath() + "kits/" + id + ".yml");
        this.kitManager = kitManager;

        this.setName(id);
        this.setDescription(new ArrayList<>());
        this.setPermissionRequired(true);
        this.setCooldown(86400);
        this.setCost(0D);
        this.setPriority(0);
        this.setIcon(new ItemStack(Material.LEATHER_CHESTPLATE));
        this.setCommands(new ArrayList<>());
        ItemStack[] items = new ItemStack[36];
        items[0] = new ItemStack(Material.GOLDEN_SWORD);
        items[1] = new ItemStack(Material.COOKED_BEEF, 16);
        items[2] = new ItemStack(Material.GOLDEN_APPLE, 4);
        this.setItems(items);
        this.setArmor(new ItemStack[4]);
    }

    // Load
    public Kit(@NotNull KitManager kitManager, @NotNull JYML cfg) {
        super(kitManager.plugin(), cfg);
        this.kitManager = kitManager;

        this.setName(cfg.getString("Name", this.getId()));
        this.setDescription(cfg.getStringList("Description"));
        this.setPermissionRequired(cfg.getBoolean("Permission_Required"));
        this.setCooldown(cfg.getInt("Cooldown"));
        this.setCost(cfg.getDouble("Cost.Money"));
        this.setPriority(cfg.getInt("Priority"));
        this.setIcon(cfg.getItem("Icon"));
        this.setCommands(cfg.getStringList("Commands"));
        this.setItems(ItemUT.fromBase64(cfg.getStringList("Items")));
        this.setArmor(ItemUT.fromBase64(cfg.getStringList("Armor")));
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        String cooldown = this.getCooldown() >= 0 ? TimeUT.formatTime(this.getCooldown() * 1000L) : plugin.lang().Other_Eternity.getMsg();
        String cost = this.getCost() > 0 ? NumberUT.format(this.getCost()) : plugin.lang().Other_Free.getMsg();

        return str -> str
            .replace(PLACEHOLDER_ID, this.getId())
            .replace(PLACEHOLDER_NAME, this.getName())
            .replace(PLACEHOLDER_DESCRIPTION, String.join("\n", this.getDescription()))
            .replace(PLACEHOLDER_PERMISSION_REQUIRED, plugin.lang().getBool(this.isPermissionRequired()))
            .replace(PLACEHOLDER_PERMISSION_NODE, KitPerms.KITS_KIT + this.getId())
            .replace(PLACEHOLDER_COOLDOWN, cooldown)
            .replace(PLACEHOLDER_COST_MONEY, cost)
            .replace(PLACEHOLDER_PRIORITY, String.valueOf(this.getPriority()))
            .replace(PLACEHOLDER_ICON, ItemUT.getItemName(this.getIcon()))
            .replace(PLACEHOLDER_COMMANDS, String.join(DELIMITER_DEFAULT, this.getCommands()))
            ;
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders(@NotNull Player player) {
        SunUser user = plugin.getUserManager().getOrLoadUser(player);
        long expireDate = user.getKitCooldown(this);
        if (expireDate == 0L) expireDate = System.currentTimeMillis();

        String strAccess = plugin.lang().getBool(this.hasPermission(player));
        String strCooldown = expireDate < 0 ? plugin.lang().Other_Eternity.getMsg() : TimeUT.formatTimeLeft(expireDate);

        return str -> this.replacePlaceholders().apply(str
            .replace(PLACEHOLDER_USER_ACCESS, strAccess)
            .replace(PLACEHOLDER_USER_COOLDOWN, strCooldown)
        );
    }

    @Override
    public void onSave() {
        cfg.set("Name", this.getName());
        cfg.set("Description", this.getDescription());
        cfg.set("Cooldown", this.getCooldown());
        cfg.set("Permission_Required", this.isPermissionRequired());
        cfg.set("Cost.Money", this.getCost());
        cfg.set("Priority", this.getPriority());
        cfg.setItem("Icon", this.getIcon());
        cfg.set("Commands", this.getCommands());
        cfg.set("Items", ItemUT.toBase64(this.getItems()));
        cfg.set("Armor", ItemUT.toBase64(this.getArmor()));
    }

    @Override
    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        if (this.preview != null) {
            this.preview.clear();
            this.preview = null;
        }
        this.commands.clear();
    }

    @NotNull
    @Override
    public EditorMenuKit getEditor() {
        if (this.editor == null) {
            this.editor = new EditorMenuKit(this);
        }
        return this.editor;
    }

    @NotNull
    public KitManager getKitManager() {
        return this.kitManager;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = StringUT.color(name);
    }

    @NotNull
    public List<String> getDescription() {
        return description;
    }

    public void setDescription(@NotNull List<String> description) {
        this.description = StringUT.color(description);
    }

    public boolean isPermissionRequired() {
        return this.isPermission;
    }

    public void setPermissionRequired(boolean isPermission) {
        this.isPermission = isPermission;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isCooldownExpirable() {
        return this.getCooldown() >= 0;
    }

    public double getCost() {
        return this.cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    public void setIcon(@NotNull ItemStack icon) {
        ItemMeta meta = icon.getItemMeta();
        if (meta != null) {
            meta.addItemFlags(ItemFlag.values());
            icon.setItemMeta(meta);
        }
        this.icon = new ItemStack(icon);
    }

    @NotNull
    public List<String> getCommands() {
        return this.commands;
    }

    public void setCommands(@NotNull List<String> commands) {
        this.commands = commands;
    }

    @NotNull
    public ItemStack[] getItems() {
        return this.items;
    }

    public void setItems(ItemStack[] items) {
        for (int count = 0; count < items.length; count++) {
            if (items[count] == null) {
                items[count] = new ItemStack(Material.AIR);
            }
        }
        this.items = items;
    }

    @NotNull
    public ItemStack[] getArmor() {
        return this.armor;
    }

    public void setArmor(ItemStack[] armor) {
        for (int count = 0; count < armor.length; count++) {
            if (armor[count] == null) {
                armor[count] = new ItemStack(Material.AIR);
            }
        }
        this.armor = armor;
    }

    public boolean hasPermission(@NotNull Player player) {
        if (!this.isPermissionRequired()) return true;
        return player.hasPermission(KitPerms.KITS_KIT + this.getId());
    }

    public void give(@NotNull Player player, boolean force) {
        SunLight plugin = this.kitManager.plugin();
        SunUser user = plugin.getUserManager().getOrLoadUser(player);

        // Check kit permission.
        if (!force && !this.hasPermission(player)) {
            kitManager.getLang().Kit_Error_NoPermission.replace(this.replacePlaceholders()).send(player);
            return;
        }

        // Check kit cooldown.
        long expireDate = user.getKitCooldown(this);
        if (!force && expireDate != 0) {
            ILangMsg msg = expireDate < 0 ? kitManager.getLang().Kit_Error_Cooldown_OneTimed : kitManager.getLang().Kit_Error_Cooldown_Expirable;
            msg.replace(this.replacePlaceholders(player)).send(player);
            return;
        }

        // Check kit money cost.
        VaultHK vault = plugin.getVault();
        double kitCost = (vault == null || player.hasPermission(KitPerms.KITS_BYPASS_COST_MONEY) || force) ? 0D : this.getCost();
        double userBalance = kitCost > 0 ? vault.getBalance(player) : 0D;
        if (userBalance < kitCost) {
            kitManager.getLang().Kit_Error_NotEnoughFunds.replace(this.replacePlaceholders()).send(player);
            return;
        }
        else if (kitCost > 0) vault.take(player, kitCost);


        // Give kit content.
        this.getCommands().forEach(cmd -> PlayerUT.execCmd(player, cmd));
        if (this.kitManager.getKeyItemBind() != null) {
            for (ItemStack item : this.getItems()) {
                ItemStack item2 = new ItemStack(item);
                DataUT.setData(item2, this.kitManager.getKeyItemBind(), player.getUniqueId().toString());
                ItemUT.addItem(player, item2);
            }
        }
        else {
            ItemUT.addItem(player, this.getItems());
        }

        int armorStart = 36;
        for (ItemStack armor2 : this.getArmor()) {
            ItemStack armor = new ItemStack(armor2);
            if (this.kitManager.getKeyItemBind() != null) {
                DataUT.setData(armor, this.kitManager.getKeyItemBind(), player.getUniqueId().toString());
            }

            ItemStack armorHas = player.getInventory().getItem(armorStart);
            if (!ItemUT.isAir(armorHas)) {
                ItemUT.addItem(player, armor);
            }
            else {
                player.getInventory().setItem(armorStart, armor);
            }
            armorStart++;
        }

        if (!force) {
            user.setKitCooldown(this);
        }
        kitManager.getLang().Kit_Notify_Give_Self.replace(this.replacePlaceholders(player)).send(player);
    }

    @NotNull
    public KitMenuPreview getPreview() {
        if (this.preview == null) {
            this.preview = new KitMenuPreview(this.plugin, this.kitManager, this);
        }
        return preview;
    }

    @Deprecated
    public void openPreview(@NotNull Player player) {

        this.preview.open(player, 1);
    }
}