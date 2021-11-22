package su.nexmedia.sunlight.data;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUserDataHandler;
import su.nexmedia.engine.data.DataTypes;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.user.IgnoredUser;
import su.nexmedia.sunlight.data.serialize.HomeSerializer;
import su.nexmedia.sunlight.modules.homes.Home;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class SunUserData extends AbstractUserDataHandler<SunLight, SunUser> {

    private static SunUserData instance;

    private final Function<ResultSet, SunUser> FUNC_USER;

    protected SunUserData(@NotNull SunLight plugin) throws SQLException {
        super(plugin);

        this.FUNC_USER = (rs) -> {
            try {
                UUID uuid = UUID.fromString(rs.getString(COL_USER_UUID));
                String name = rs.getString(COL_USER_NAME);
                long lastOnline = rs.getLong(COL_USER_LAST_ONLINE);

                String ip = rs.getString("ip");
                String nick = rs.getString("nickname");
                Map<String, Home> homes = gson.fromJson(rs.getString("homes"), new TypeToken<Map<String, Home>>() {
                }.getType());
                Map<String, Long> kitCooldowns = gson.fromJson(rs.getString("kitCooldowns"), new TypeToken<Map<String, Long>>() {
                }.getType());
                Map<String, Long> commandCooldowns = gson.fromJson(rs.getString("commandCooldowns"), new TypeToken<Map<String, Long>>() {
                }.getType());
                Map<String, IgnoredUser> ignoredUsers = gson.fromJson(rs.getString("ignoredUsers"), new TypeToken<Map<String, IgnoredUser>>() {
                }.getType());
                Map<String, Boolean> settingsBool = gson.fromJson(rs.getString("settingsBool"), new TypeToken<Map<String, Boolean>>() {
                }.getType());
                Map<String, Double> settingsNum = gson.fromJson(rs.getString("settingsNum"), new TypeToken<Map<String, Double>>() {
                }.getType());

                return new SunUser(
                    plugin, uuid, name, lastOnline,
                    ip,
                    nick,
                    homes,
                    kitCooldowns,
                    commandCooldowns,
                    ignoredUsers,
                    settingsBool, settingsNum
                );
            } catch (SQLException ex) {
                return null;
            }
        };
    }

    @NotNull
    public static synchronized SunUserData getInstance() throws SQLException {
        if (instance == null) {
            instance = new SunUserData(SunLight.getInstance());
        }
        return instance;
    }

    @Override
    @NotNull
    protected GsonBuilder registerAdapters(@NotNull GsonBuilder builder) {
        return super.registerAdapters(builder)
            .registerTypeAdapter(Home.class, new HomeSerializer())
            ;
    }

    @Override
    @NotNull
    protected LinkedHashMap<String, String> getColumnsToCreate() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("ip", DataTypes.STRING.build(this.dataType, 24));
        map.put("nickname", DataTypes.STRING.build(this.dataType, 48));
        map.put("homes", DataTypes.STRING.build(this.dataType));
        map.put("kitCooldowns", DataTypes.STRING.build(this.dataType));
        map.put("commandCooldowns", DataTypes.STRING.build(this.dataType));
        map.put("ignoredUsers", DataTypes.STRING.build(this.dataType));
        map.put("settingsBool", DataTypes.STRING.build(this.dataType));
        map.put("settingsNum", DataTypes.STRING.build(this.dataType));
        return map;
    }

    @Override
    @NotNull
    protected LinkedHashMap<String, String> getColumnsToSave(@NotNull SunUser user) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("ip", user.getIp());
        map.put("nickname", user.getCustomNick());
        map.put("homes", this.gson.toJson(user.getHomes()));
        map.put("kitCooldowns", this.gson.toJson(user.getKitCooldowns()));
        map.put("commandCooldowns", this.gson.toJson(user.getCommandCooldowns()));
        map.put("ignoredUsers", this.gson.toJson(user.getIgnoredUsers()));
        map.put("settingsBool", this.gson.toJson(user.getSettingsBoolean()));
        map.put("settingsNum", this.gson.toJson(user.getSettingsNumber()));
        return map;
    }

    @Override
    @NotNull
    protected Function<ResultSet, SunUser> getFunctionToUser() {
        return this.FUNC_USER;
    }

    @NotNull
    public Map<String, Double> getUserBalance() {
        Map<String, Double> map = new HashMap<>();
        String sql = "SELECT `name`, `settingsNum` FROM " + this.tableUsers;

        try (Statement ps = this.getConnection().createStatement()) {
            ResultSet rs = ps.executeQuery(sql);
            while (rs.next()) {
                String name = rs.getString("name");
                Map<String, Double> settingsNum = gson.fromJson(rs.getString("settingsNum"), new TypeToken<Map<String, Double>>() {
                }.getType());

                double balance = settingsNum.getOrDefault(SunUser.SETTING_BALANCE, 0D);

                map.put(name, balance);
            }
            return map;
        } catch (SQLException e) {
            plugin.error("SQL Error!");
            e.printStackTrace();
            return map;
        }
    }
}
