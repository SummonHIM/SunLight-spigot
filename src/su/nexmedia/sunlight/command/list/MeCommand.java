package su.nexmedia.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.List;

public class MeCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "me";

    private final String format;

    public MeCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_ME);

        JYML cfg = CommandConfig.getConfig();
        String path = "Settings." + NAME + ".";
        this.format = StringUT.color(cfg.getString(path + "Format", ""));
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Me_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Me_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            this.printUsage(sender);
            return;
        }

        StringBuilder msgBuild = new StringBuilder();
        for (String arg : args) {
            msgBuild.append(arg).append(" ");
        }
        String message = StringUT.colorOff(msgBuild.toString().trim());
        String name = sender.getName();
        String format = this.format.replace("%message%", message).replace("%player%", name);

        this.plugin.getServer().getOnlinePlayers().forEach(player -> player.sendMessage(format));
    }
}
