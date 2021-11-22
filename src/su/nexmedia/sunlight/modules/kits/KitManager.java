package su.nexmedia.sunlight.modules.kits;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.editor.EditorUtils;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.DataUT;
import su.nexmedia.engine.utils.regex.RegexUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.ModuleId;
import su.nexmedia.sunlight.modules.SunModule;
import su.nexmedia.sunlight.modules.kits.command.KitCommand;
import su.nexmedia.sunlight.modules.kits.command.KitEditorCommand;
import su.nexmedia.sunlight.modules.kits.command.KitPreviewCommand;
import su.nexmedia.sunlight.modules.kits.editor.EditorHandlerKit;
import su.nexmedia.sunlight.modules.kits.editor.EditorMenuKitList;
import su.nexmedia.sunlight.modules.kits.listener.KitListener;
import su.nexmedia.sunlight.modules.kits.menu.KitMenuList;

import java.util.*;

public class KitManager extends SunModule {

    public static final String YML_KITS_MENU    = "kitlist.menu.yml";
    public static final String YML_PREVIEW_MENU = "preview.menu.yml";
    private KitLang lang;
    private Map<String, Kit> kits;
    private KitMenuList       kitsMenu;
    private EditorMenuKitList editor;
    private NamespacedKey keyItemBind;

    public KitManager(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getId() {
        return ModuleId.KITS;
    }

    @Override
    @NotNull
    public String getVersion() {
        return "2.0";
    }

    @Override
    public void onLoad() {
        this.plugin.getConfigManager().extractFullPath(this.getFullPath() + "kits", "yml");
        this.lang = new KitLang(plugin, JYML.loadOrExtract(plugin, this.getPath() + "lang/messages_" + plugin.cfg().lang + ".yml"));
        this.lang.setup();

        this.kits = new /*Linked*/HashMap<>();
        this.kitsMenu = new KitMenuList(this.plugin, this);

        if (this.cfg.getBoolean("Kit.Bind_Items_To_Player")) {
            this.keyItemBind = new NamespacedKey(plugin, "kit_item_owner");
        }

        //Map<String, Kit> kitsRaw = new HashMap<>();
        for (JYML cfg2 : JYML.loadAll(this.getFullPath() + "kits", false)) {
            try {
                Kit kit = new Kit(this, cfg2);
                //kitsRaw.put(kit.getId(), kit);
                this.kits.put(kit.getId(), kit);
            } catch (Exception ex) {
                plugin.error("Could not to load kit: '" + cfg2.getFile().getName() + "'");
                ex.printStackTrace();
            }
        }

        // Sort kits by priority.
		/*this.kits = kitsRaw.entrySet().stream().sorted((en1, en2) -> {
			return en2.getValue().getPriority() - en1.getValue().getPriority();
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (from, to) -> to, LinkedHashMap::new));*/
        this.info("Loaded " + this.getKits().size() + " kits.");

        this.plugin.getCommandRegulator().register(new KitCommand(this));
        this.plugin.getCommandRegulator().register(new KitEditorCommand(this));
        this.plugin.getCommandRegulator().register(new KitPreviewCommand(this));

        this.plugin.getSunEditorHandler().addInputHandler(Kit.class, new EditorHandlerKit(this));
        this.addListener(new KitListener(this));
    }

    @Override
    public void onShutdown() {
        this.plugin.getSunEditorHandler().removeInputHandler(Kit.class);
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        if (this.kitsMenu != null) {
            this.kitsMenu.clear();
            this.kitsMenu = null;
        }
        if (this.kits != null) {
            this.kits.values().forEach(Kit::clear);
            this.kits.clear();
            this.kits = null;
        }
    }

    @NotNull
    public KitLang getLang() {
        return lang;
    }

    @NotNull
    public KitMenuList getKitsMenu() {
        return this.kitsMenu;
    }

    @NotNull
    public EditorMenuKitList getEditor() {
        if (this.editor == null) {
            this.editor = new EditorMenuKitList(this);
        }
        return editor;
    }

    @Nullable
    public NamespacedKey getKeyItemBind() {
        return this.keyItemBind;
    }

    @Nullable
    public Kit getKitById(@NotNull String id) {
        return this.kits.get(id.toLowerCase());
    }

    @NotNull
    public Collection<Kit> getKits() {
        return this.kits.values().stream().sorted(Comparator.comparingInt(Kit::getPriority)).toList();
    }

    @NotNull
    public List<String> getKitIds() {
        return new ArrayList<>(this.kits.keySet());
    }

    @NotNull
    public List<String> getKitIds(@NotNull Player player) {
        return this.getKits().stream().filter(kit -> kit.hasPermission(player)).map(Kit::getId).toList();
    }

    public void delete(@NotNull Kit kit) {
        if (kit.getFile().delete()) {
            kit.clear();
            this.kits.remove(kit.getId());
        }
    }

    public boolean create(@NotNull Player player, @NotNull String id) {
        id = EditorUtils.fineId(id);

        if (!RegexUT.matchesEnRu(id)) {
            EditorUtils.errorCustom(player, plugin.lang().Error_InvalidName.replace("%name%", id).getMsg());
            return false;
        }
        if (this.isKitExists(id)) {
            EditorUtils.errorCustom(player, this.getLang().Editor_Error_AlreadyExists.getMsg());
            return false;
        }

        Kit kit = new Kit(this, id);
        kit.save();
        this.kits.put(kit.getId(), kit);
        return true;
    }

    public boolean isKitExists(@NotNull String id) {
        return this.getKitById(id) != null;
    }

    public boolean isBindedTo(@NotNull ItemStack item, @NotNull LivingEntity entity) {
        String uuidRaw = DataUT.getStringData(item, this.keyItemBind);
        if (uuidRaw == null) return true;

        UUID uuid = UUID.fromString(uuidRaw);
        return entity.getUniqueId().equals(uuid);
    }
}
