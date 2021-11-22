package su.nexmedia.sunlight.hooks.converter;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.UserMap;
import com.earth2me.essentials.Warps;
import org.bukkit.Location;
import org.bukkit.Material;
import su.nexmedia.engine.utils.FileUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.hooks.HookId;
import su.nexmedia.sunlight.modules.homes.Home;
import su.nexmedia.sunlight.modules.homes.HomeManager;
import su.nexmedia.sunlight.modules.warps.Warp;
import su.nexmedia.sunlight.modules.warps.WarpManager;
import su.nexmedia.sunlight.modules.warps.type.WarpType;
import su.nexmedia.sunlight.user.IgnoredUser;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class EssentialsConverter {

    private static final String PREFIX = "[Essentials Converter] ";

    public static void convert() {
        SunLight plugin = SunLight.getInstance();

        Essentials essentials = (Essentials) plugin.getPluginManager().getPlugin(HookId.ESSENTIALS);
        if (essentials == null) return;

        HomeManager homeManager = plugin.getModuleCache().getHomeManager();

        UserMap essUsers = essentials.getUserMap();
        for (File file : FileUT.getFiles(essentials.getDataFolder() + "/userdata/", false)) {
            //for (UUID essUserId : essUsers.getAllUniqueUsers()) {
            UUID essUserId = UUID.fromString(file.getName().replace(".yml", ""));
            User essUser;
            try {
                essUser = essUsers.load(essUserId.toString());
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            String uuid = essUserId.toString();
            String name = essUser.getName();
            if (name == null) name = "null";

            long login = essUser.getLastLogout();
            String ip = essUser.getLastLoginAddress();
            double balance = essUser.getMoney().doubleValue();

            Map<String, Home> homes = new HashMap<>();
            if (homeManager != null && homeManager.isLoaded()) {
                for (String eHomeId : essUser.getHomes()) {
                    try {
                        Location eHomeLoc = essUser.getHome(eHomeId);
                        Home sunHome = new Home(eHomeId, name, eHomeId, Material.GRASS_BLOCK, eHomeLoc, new HashSet<>(), false);
                        homes.put(sunHome.getId(), sunHome);

                        plugin.info(PREFIX + "Home converted: " + eHomeId + " / " + uuid + " / " + name);
                    } catch (Exception e) {
                        plugin.error(PREFIX + "Home error: " + eHomeId + " / " + uuid + " / " + name);
                        e.printStackTrace();
                    }
                }
            }

            Map<String, Long> kitCooldowns = new HashMap<>();
            Map<String, Long> commandCooldowns = new HashMap<>();
            Map<String, IgnoredUser> ignoredUsers = new HashMap<>();
			/*essUser._getIgnoredPlayers().forEach(ignoredName -> {
				ignoredUsers.put(ignoredName.toLowerCase(), new IgnoredUser(ignoredName));
			});*/

            String nickName = essUser.getNickname();
            if (nickName == null) nickName = name;

            SunUser suser = new SunUser(
                plugin, essUserId, name, login, ip, nickName,
                homes, kitCooldowns, commandCooldowns,
                ignoredUsers,
                new HashMap<>(), new HashMap<>()
            );

            suser.setSettingNumber(SunUser.SETTING_BALANCE, balance);
            plugin.getData().addUser(suser);
            plugin.getUserManager().getActiveUsersMap().put(uuid, suser);

            plugin.info(PREFIX + "User converted: " + uuid + " / " + name);
        }

        WarpManager warpManager = plugin.getModuleCache().getWarpManager();
        if (warpManager != null && warpManager.isLoaded()) {
            Warps warps = essentials.getWarps();
            for (String wId : warps.getList()) {
                try {
                    Location wLoc = warps.getWarp(wId);
                    UUID lastOwner = warps.getLastOwner(wId);

                    Warp warp = new Warp(warpManager, wId, lastOwner, wLoc, WarpType.SERVER);
                    warpManager.getWarpsMap().put(warp.getId(), warp);

                    plugin.info(PREFIX + "Warp converted: " + wId);
                } catch (Exception e) {
                    plugin.info(PREFIX + "Warp error: " + wId);
                    //e.printStackTrace();
                }
            }
        }
    }
}
