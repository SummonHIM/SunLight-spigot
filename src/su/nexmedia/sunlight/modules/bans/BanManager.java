package su.nexmedia.sunlight.modules.bans;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.data.StorageType;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.engine.utils.actions.ActionManipulator;
import su.nexmedia.engine.utils.regex.RegexUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.ModuleId;
import su.nexmedia.sunlight.modules.SunModule;
import su.nexmedia.sunlight.modules.bans.command.KickCommand;
import su.nexmedia.sunlight.modules.bans.command.ban.BanCommand;
import su.nexmedia.sunlight.modules.bans.command.ban.BanipCommand;
import su.nexmedia.sunlight.modules.bans.command.ban.UnbanCommand;
import su.nexmedia.sunlight.modules.bans.command.history.BanHistoryCommand;
import su.nexmedia.sunlight.modules.bans.command.history.MuteHistoryCommand;
import su.nexmedia.sunlight.modules.bans.command.history.WarnHistoryCommand;
import su.nexmedia.sunlight.modules.bans.command.list.BanListCommand;
import su.nexmedia.sunlight.modules.bans.command.list.MuteListCommand;
import su.nexmedia.sunlight.modules.bans.command.list.WarnListCommand;
import su.nexmedia.sunlight.modules.bans.command.mute.MuteCommand;
import su.nexmedia.sunlight.modules.bans.command.mute.UnmuteCommand;
import su.nexmedia.sunlight.modules.bans.command.warn.UnwarnCommand;
import su.nexmedia.sunlight.modules.bans.command.warn.WarnCommand;
import su.nexmedia.sunlight.modules.bans.listener.BanListener;
import su.nexmedia.sunlight.modules.bans.menu.PunishmentHistoryMenu;
import su.nexmedia.sunlight.modules.bans.menu.PunishmentListMenu;
import su.nexmedia.sunlight.modules.bans.punishment.Punishment;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentReason;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentType;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BanManager extends SunModule {

    private Map<String, Map<PunishmentType, Set<Punishment>>> punishments;

    private BanLang        lang;
    private BanDataHandler dataHandler;
    private boolean        isLocalCacheEnabled;

    private Map<PunishmentType, PunishmentHistoryMenu> historyMenuMap;
    private Map<PunishmentType, PunishmentListMenu>    listMenuMap;

    private Map<String, PunishmentReason>   reasons;
    private Set<String>                     muteBlockedCommands;
    private int                             warnMaxAmount;
    private Map<Integer, ActionManipulator> warnActionsByAmount;

    private Set<String> immunities;

    public BanManager(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getId() {
        return ModuleId.BANS;
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.4";
    }

    @Override
    public void onLoad() {
        this.lang = new BanLang(plugin, JYML.loadOrExtract(plugin, this.getPath() + "lang/messages_" + plugin.cfg().lang + ".yml"));
        this.lang.setup();

        String path = "Database.";
        try {
            this.isLocalCacheEnabled = cfg.getBoolean(path + "Enable_Local_Cache", true);
            StorageType storageType = cfg.getEnum(path + "Type", StorageType.class, StorageType.SQLITE);
            if (storageType == StorageType.SQLITE) {
                this.dataHandler = new BanDataHandler(plugin, this.getFullPath(), "bans.db");
            }
            else if (storageType == StorageType.MYSQL) {
                String host = cfg.getString(path + "MySQL.Host", "null");
                String base = cfg.getString(path + "MySQL.Base", "null");
                String login = cfg.getString(path + "MySQL.Login", "null");
                String password = cfg.getString(path + "MySQL.Password", "null");
                this.dataHandler = new BanDataHandler(plugin, host, base, login, password);
            }
            this.dataHandler.setup();
        }
        catch (SQLException e) {
            this.interruptLoad(e.getMessage());
            e.printStackTrace();
            return;
        }

        if (this.isLocalCacheEnabled) {
            this.punishments = new HashMap<>();
            for (PunishmentType type : PunishmentType.values()) {
                this.dataHandler.getPunishments(type).forEach(punishment -> {
                    String user = punishment.getUser();

                    Map<PunishmentType, Set<Punishment>> userMap = this.getPunishments(user);
                    Set<Punishment> userPunishments = this.getPunishments(user, type);
                    userPunishments.add(punishment);
                });
            }
        }

        this.reasons = new HashMap<>();
        for (String reasonId : cfg.getSection("Reasons")) {
            String path2 = "Reasons." + reasonId + ".";
            String rMessage = cfg.getString(path2 + "Message", "");
            if (rMessage.isEmpty()) continue;

            PunishmentReason reason = new PunishmentReason(reasonId, rMessage);
            this.reasons.put(reason.getId(), reason);
        }

        for (BanTime banTime : BanTime.values()) {
            cfg.addMissing("Time_Aliases." + banTime.name(), banTime.getAlias());
            banTime.setAlias(cfg.getString("Time_Aliases." + banTime.name(), banTime.getAlias()));
        }

        path = "Mute.";
        this.muteBlockedCommands = cfg.getStringSet(path + "Blocked_Commands");

        path = "Warn.";
        this.warnMaxAmount = cfg.getInt(path + "Max_Amount_Before_Reset");
        this.warnActionsByAmount = new HashMap<>();
        for (String sAmount : cfg.getSection(path + "Actions_By_Amount")) {
            int amount = StringUT.getInteger(sAmount, -1);
            if (amount <= 0) continue;

            ActionManipulator manipulator = new ActionManipulator(plugin, cfg, path + "Actions_By_Amount." + sAmount);
            this.warnActionsByAmount.put(amount, manipulator);
        }

        this.immunities = this.cfg.getStringSet("Immunity").stream().map(String::toLowerCase).collect(Collectors.toSet());

        this.historyMenuMap = new HashMap<>();
        this.listMenuMap = new HashMap<>();
        JYML listConfig = JYML.loadOrExtract(plugin, this.getPath() + "punishment.list.menu.yml");
        JYML historyConfig = JYML.loadOrExtract(plugin, this.getPath() + "punishment.history.menu.yml");
        for (PunishmentType type : PunishmentType.values()) {
            PunishmentHistoryMenu historyMenu = new PunishmentHistoryMenu(this, historyConfig, type);
            this.historyMenuMap.put(type, historyMenu);

            PunishmentListMenu listMenu = new PunishmentListMenu(this, listConfig, type);
            this.listMenuMap.put(type, listMenu);
        }

        this.plugin.getCommandRegulator().register(new KickCommand(this));
        this.plugin.getCommandRegulator().register(new BanCommand(this));
        this.plugin.getCommandRegulator().register(new BanipCommand(this));
        this.plugin.getCommandRegulator().register(new BanHistoryCommand(this));
        this.plugin.getCommandRegulator().register(new BanListCommand(this));
        this.plugin.getCommandRegulator().register(new UnbanCommand(this));
        this.plugin.getCommandRegulator().register(new MuteCommand(this));
        this.plugin.getCommandRegulator().register(new MuteHistoryCommand(this));
        this.plugin.getCommandRegulator().register(new MuteListCommand(this));
        this.plugin.getCommandRegulator().register(new UnmuteCommand(this));
        this.plugin.getCommandRegulator().register(new WarnCommand(this));
        this.plugin.getCommandRegulator().register(new WarnHistoryCommand(this));
        this.plugin.getCommandRegulator().register(new WarnListCommand(this));
        this.plugin.getCommandRegulator().register(new UnwarnCommand(this));

        this.addListener(new BanListener(this));
    }

    @Override
    public void onShutdown() {
        this.dataHandler.shutdown();
        if (this.punishments != null) {
            this.punishments.clear();
        }
        this.muteBlockedCommands.clear();
        this.immunities.clear();
        this.warnActionsByAmount.clear();
    }

    @NotNull
    public BanDataHandler getDataHandler() {
        return dataHandler;
    }

    @NotNull
    public BanLang getLang() {
        return lang;
    }

    public boolean isLocalCacheEnabled() {
        return isLocalCacheEnabled;
    }

    @NotNull
    public Collection<PunishmentReason> getReasons() {
        return this.reasons.values();
    }

    @Nullable
    public PunishmentReason getReason(@NotNull String id) {
        return this.reasons.get(id.toLowerCase());
    }

    @NotNull
    public Set<String> getMuteBlockedCommands() {
        return muteBlockedCommands;
    }

    @Nullable
    public ActionManipulator getWarnActions(int amount) {
        return this.warnActionsByAmount.get(amount);
    }

    @NotNull
    public PunishmentHistoryMenu getHistoryMenu(@NotNull PunishmentType type) {
        return this.historyMenuMap.get(type);
    }

    @NotNull
    public PunishmentListMenu getListMenu(@NotNull PunishmentType type) {
        return this.listMenuMap.get(type);
    }

    @NotNull
    public List<Punishment> getPunishments(@NotNull PunishmentType type) {
        if (this.isLocalCacheEnabled) {
            List<Punishment> list = new ArrayList<>();
            this.punishments.forEach((userName, mapType) -> {
                list.addAll(mapType.getOrDefault(type, Collections.emptySet()));
            });
            return list;
        }
        return this.dataHandler.getPunishments(type);
    }

    @NotNull
    public Map<PunishmentType, Set<Punishment>> getPunishments(@NotNull String user) {
        user = user.toLowerCase();
        if (this.isLocalCacheEnabled) return this.punishments.computeIfAbsent(user, k -> new HashMap<>());
        else return this.dataHandler.getPunishments(user);
    }

    @NotNull
    public Set<Punishment> getPunishments(@NotNull String user, @NotNull PunishmentType type) {
        return this.getPunishments(user.toLowerCase()).computeIfAbsent(type, k -> new HashSet<>());
    }

    @NotNull
    public List<Punishment> getActivePunishments(@NotNull String user, @NotNull PunishmentType type) {
        return this.getPunishments(user.toLowerCase(), type).stream()
            .filter(Predicate.not(Punishment::isExpired))
            .sorted((p1, p2) -> (int) (p2.getCreatedDate() - p1.getCreatedDate()))
            .toList();
    }

    @Nullable
    public Punishment getActivePunishment(@NotNull Player player, @NotNull PunishmentType type) {
        Punishment punishment = this.getActivePunishment(player.getName(), type);
        return punishment == null ? this.getActivePunishment(PlayerUT.getIP(player), type) : punishment;
    }

    @Nullable
    public Punishment getActivePunishment(@NotNull String user, @NotNull PunishmentType type) {
        return this.getActivePunishments(user.toLowerCase(), type).stream().findFirst().orElse(null);
    }

    public void deletePunishment(@NotNull Punishment punishment) {
        this.getPunishments(punishment.getUser(), punishment.getType()).remove(punishment);
        this.dataHandler.deletePunishment(punishment);
    }

    public boolean isMuted(@NotNull String user) {
        return this.getActivePunishment(user, PunishmentType.MUTE) != null;
    }

    public boolean isBanned(@NotNull String user) {
        return this.getActivePunishment(user, PunishmentType.BAN) != null;
    }

    public boolean canBePunished(@NotNull String user) {
        return !this.immunities.contains(user.toLowerCase());
    }

    private boolean canBePunished(@NotNull CommandSender admin, @NotNull String user) {
        if (!this.canBePunished(user)) {
            this.getLang().Error_Immune.replace("%user%", user).send(admin);
            return false;
        }
        if (user.equalsIgnoreCase(admin.getName())) {
            plugin.lang().Error_Self.send(admin);
            return false;
        }
        return true;
    }

    @NotNull
    private List<? extends Player> getPlayersToPunish(@NotNull String user) {
        return this.plugin.getServer().getOnlinePlayers().stream().filter(player -> {
            return player.getName().equalsIgnoreCase(user) || PlayerUT.getIP(player).equalsIgnoreCase(user);
        }).toList();
    }

    public void punish(
        @NotNull String user, @NotNull CommandSender punisher,
        @NotNull String reason, long banTime,
        @NotNull PunishmentType type) {

        if (!this.canBePunished(punisher, user)) return;

        // Fine user name if present.
        Player pTarget = plugin.getServer().getPlayer(user);
        if (pTarget != null) user = pTarget.getName();

        // Mutes and Bans can only have one active punishment instance, so we need to overwrite (expire) all others.
        // For warns we do the same, if max. warns amount is reached, and punish at the end of method.
        List<Punishment> punishmentsHas = this.getActivePunishments(user, type);
        int punishmentsAmount = punishmentsHas.size() + 1;
        if (type != PunishmentType.WARN || (this.warnMaxAmount >= 2 && punishmentsAmount >= this.warnMaxAmount)) {
            this.getActivePunishments(user, type).forEach(punishment -> {
                punishment.expire();
                this.dataHandler.savePunishment(punishment);
            });
        }

        // Create new punishment object and add it to the database.
        Punishment punishment = new Punishment(type, user, reason, punisher.getName(), banTime);
        this.dataHandler.addPunishment(punishment);
        if (this.isLocalCacheEnabled) {
            this.getPunishments(user, type).add(punishment);
        }

        // Send messages.
        ILangMsg msgUser = this.getLang().getForUser(punishment).replace(punishment.replacePlaceholders());
        ILangMsg msgAdmin = this.getLang().getForAdmin(punishment).replace(punishment.replacePlaceholders());
        ILangMsg msgBroadcast = this.getLang().getForBroadcast(punishment).replace(punishment.replacePlaceholders());

        msgAdmin.send(punisher);
        msgBroadcast.broadcast();

        // Notify players about their punishment.
        this.getPlayersToPunish(user).forEach(player -> {
            if (type == PunishmentType.BAN) player.kickPlayer(msgUser.normalizeLines());
            else msgUser.send(player);
        });

        // Execute warn actions at the end to keep punishment messages order.
        if (type == PunishmentType.WARN && pTarget != null) {
            ActionManipulator manipulator = this.getWarnActions(punishmentsAmount);
            if (manipulator != null) {
                manipulator.process(pTarget);
            }
        }
    }

    public void kick(@NotNull String user, @NotNull CommandSender admin, @NotNull String reason) {
        if (!this.canBePunished(admin, user)) return;

        Punishment punishment = new Punishment(PunishmentType.WARN, user, reason, admin.getName(), 0L);
        ILangMsg banMsg = this.getLang().Kick_Kicked.replace(punishment.replacePlaceholders());
        ILangMsg banAdmin = this.getLang().Kick_Done.replace(punishment.replacePlaceholders());
        ILangMsg banBroadcast = this.getLang().Kick_Broadcast.replace(punishment.replacePlaceholders());

        // Kick all users with the same IP as banned user
        this.getPlayersToPunish(user).forEach(player -> player.kickPlayer(banMsg.normalizeLines()));

        banAdmin.send(admin);
        banBroadcast.broadcast();
    }

    public void unpunish(@NotNull String userName, @NotNull CommandSender admin, @NotNull PunishmentType type) {
        boolean isIP = RegexUT.isIpAddress(userName);
        boolean hasPunishment = false;

        // When unpunishing by username, also check if the user has punishments by his IP,
        // so we can unpunish them too.
        if (type != PunishmentType.WARN && !isIP) {
            SunUser user = plugin.getUserManager().getOrLoadUser(userName, false);
            if (user != null && this.getActivePunishment(user.getIp(), type) != null) {
                this.unpunish(user.getIp(), admin, type);
                hasPunishment = true;
            }
        }

        // If used not banned by name or IP at all, then print error message.
        Punishment punishment = this.getActivePunishment(userName, type);
        if (punishment == null) {
            if (!hasPunishment) this.getLang().getForNotPunished(type).replace("%user%", userName).send(admin);
            return;
        }

        ILangMsg banAdmin = this.getLang().getForgiveForAdmin(punishment).replace(punishment.replacePlaceholders());
        ILangMsg banBroadcast = this.getLang().getForgiveForBroadcast(punishment)
            .replace("%admin%", admin.getName()).replace(punishment.replacePlaceholders());

        punishment.expire();
        this.dataHandler.savePunishment(punishment);

        banAdmin.send(admin);
        banBroadcast.broadcast();
    }


}
