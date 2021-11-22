package su.nexmedia.sunlight.modules.bans;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractDataHandler;
import su.nexmedia.engine.data.DataTypes;
import su.nexmedia.engine.utils.CollectionsUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.bans.punishment.Punishment;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class BanDataHandler extends AbstractDataHandler<SunLight> {

    protected static final String TABLE_BANS  = "sunlight_bans_bans";
    protected static final String TABLE_MUTES = "sunlight_bans_mutes";
    protected static final String TABLE_WARNS = "sunlight_bans_warns";

    private static final Function<ResultSet, Punishment> FUNC_GET_PUNISH = ((rs) -> {
        try {
            PunishmentType type = CollectionsUT.getEnum(rs.getString("type"), PunishmentType.class);
            if (type == null) return null;

            UUID id = UUID.fromString(rs.getString("pid"));
            String user = rs.getString("user");
            String reason = rs.getString("reason");
            String admin = rs.getString("admin");
            long created = rs.getLong("created");
            long expired = rs.getLong("expired");

            return new Punishment(id, type, user, reason, admin, created, expired);
        } catch (SQLException e) {
            return null;
        }
    });

    protected BanDataHandler(@NotNull SunLight plugin, @NotNull String host, @NotNull String base, @NotNull String login, @NotNull String password) {
        super(plugin, host, base, login, password);
    }

    protected BanDataHandler(@NotNull SunLight plugin, @NotNull String filePath, @NotNull String fileName) throws SQLException {
        super(plugin, filePath, fileName);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        LinkedHashMap<String, String> bansMap = new LinkedHashMap<>();
        bansMap.put("pid", DataTypes.STRING.build(this.dataType, 36));
        bansMap.put("type", DataTypes.STRING.build(this.dataType, 12));
        bansMap.put("user", DataTypes.STRING.build(this.dataType, 32));
        bansMap.put("reason", DataTypes.STRING.build(this.dataType, 128));
        bansMap.put("admin", DataTypes.STRING.build(this.dataType, 32));
        bansMap.put("created", DataTypes.LONG.build(this.dataType));
        bansMap.put("expired", DataTypes.LONG.build(this.dataType));

        this.createTable(TABLE_BANS, bansMap);
        this.createTable(TABLE_MUTES, bansMap);
        this.createTable(TABLE_WARNS, bansMap);
    }

    @Override
    protected void onShutdown() {
        super.onShutdown();
    }

    @NotNull
    public Set<Punishment> getPunishments(@NotNull String user, @NotNull PunishmentType type) {
        return this.getPunishments(user).computeIfAbsent(type, k -> new HashSet<>());
    }

    @NotNull
    public Map<PunishmentType, Set<Punishment>> getPunishments(@NotNull String user) {
        Map<String, String> whereMap = new HashMap<>();
        whereMap.put("user", user);

        Map<PunishmentType, Set<Punishment>> map = new HashMap<>();
        for (PunishmentType type : PunishmentType.values()) {
            map.put(type, new HashSet<>(this.getDatas(this.getTable(type), whereMap, FUNC_GET_PUNISH, -1)));
        }
        return map;
    }

    @NotNull
    public List<Punishment> getPunishments(@NotNull PunishmentType type) {
        return this.getDatas(this.getTable(type), Collections.emptyMap(), FUNC_GET_PUNISH, -1);
    }

    @NotNull
    private String getTable(@NotNull PunishmentType punishmentType) {
        return switch (punishmentType) {
            case BAN -> TABLE_BANS;
            case MUTE -> TABLE_MUTES;
            case WARN -> TABLE_WARNS;
        };
    }

    public void savePunishment(@NotNull Punishment punishment) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        //map.put("pid", punishment.getId().toString());
        map.put("type", punishment.getType().name());
        map.put("user", punishment.getUser());
        map.put("reason", punishment.getReason());
        map.put("admin", punishment.getAdmin());
        map.put("created", String.valueOf(punishment.getCreatedDate()));
        map.put("expired", String.valueOf(punishment.getExpireDate()));

        Map<String, String> whereMap = new HashMap<>();
        whereMap.put("pid", punishment.getId().toString());

        this.plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            this.saveData(this.getTable(punishment.getType()), map, whereMap);
        });
    }

    public void addPunishment(@NotNull Punishment punishment) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("pid", punishment.getId().toString());
        map.put("type", punishment.getType().name());
        map.put("user", punishment.getUser());
        map.put("reason", punishment.getReason());
        map.put("admin", punishment.getAdmin());
        map.put("created", String.valueOf(punishment.getCreatedDate()));
        map.put("expired", String.valueOf(punishment.getExpireDate()));

        this.plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            this.addData(this.getTable(punishment.getType()), map);
        });
    }

    public void deletePunishment(@NotNull Punishment punishment) {
        String table = this.getTable(punishment.getType());
        String sql = "DELETE FROM " + table + " WHERE `pid` = '" + punishment.getId() + "'";
        this.plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            this.executeSQL(sql);
        });
    }
}
