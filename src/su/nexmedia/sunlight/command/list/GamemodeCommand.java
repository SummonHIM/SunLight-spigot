package su.nexmedia.sunlight.command.list;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.CollectionsUT;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamemodeCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "gamemode";

    private final Map<String, GameMode> modeAlias;

    public GamemodeCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_GAMEMODE);
        this.modeAlias = new HashMap<>();

        JYML cfg = CommandConfig.getConfig();
        for (GameMode gameMode : GameMode.values()) {
            String alias = cfg.getString("Settings." + NAME + ".GameModes." + gameMode.name());
            if (alias == null) continue;

            this.modeAlias.put(alias.toLowerCase(), gameMode);
        }

    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_GameMode_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_GameMode_Desc.getMsg();
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 2 && player.hasPermission(SunPerms.CMD_GAMEMODE_OTHERS)) {
            return PlayerUT.getPlayerNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length > 2) {
            this.printUsage(sender);
            return;
        }

        String pName = sender.getName();
        GameMode mode;

        if (this.modeAlias.containsKey(label)) {
            if (args.length == 1) {
                if (!sender.hasPermission(SunPerms.CMD_GAMEMODE_OTHERS)) {
                    this.errorPermission(sender);
                    return;
                }
                pName = args[0];
            }

            mode = this.modeAlias.get(label);
        }
        // /gamemode or /gm
        else {
            if (args.length < 1) {
                this.printUsage(sender);
                return;
            }

            if (args.length == 2) {
                if (!sender.hasPermission(SunPerms.CMD_GAMEMODE_OTHERS)) {
                    this.errorPermission(sender);
                    return;
                }
                pName = args[1];
            }

            int modeNum = StringUT.getInteger(args[0], -1);
            mode = this.getModeByNum(modeNum);

            if (mode == null) mode = CollectionsUT.getEnum(args[0], GameMode.class);
        }

        if (mode == null) {
            this.printUsage(sender);
            return;
        }

        Player pTarget = plugin.getServer().getPlayer(pName);
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        if (!sender.hasPermission(SunPerms.CMD_GAMEMODE + "." + mode.name().toLowerCase())) {
            this.errorPermission(sender);
            return;
        }

        pTarget.setGameMode(mode);

        if (!sender.equals(pTarget)) {
            plugin.lang().Command_GameMode_Done_Others
                .replace("%player%", pTarget.getName())
                .replace("%gm%", plugin.lang().getEnum(mode))
                .send(sender);
        }

        plugin.lang().Command_GameMode_Done_Self
            .replace("%gm%", plugin.lang().getEnum(mode))
            .send(pTarget);
    }

    @Nullable
    private GameMode getModeByNum(int modeNum) {
        if (modeNum == 0) return GameMode.SURVIVAL;
        else if (modeNum == 1) return GameMode.CREATIVE;
        else if (modeNum == 2) return GameMode.ADVENTURE;
        else if (modeNum == 3) return GameMode.SPECTATOR;

        return null;
    }
}
