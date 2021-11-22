package su.nexmedia.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.List;

public class SpeedCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "speed";

    public SpeedCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_SPEED);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Speed_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Speed_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 2 && player.hasPermission(SunPerms.CMD_SPEED_OTHERS)) {
            return PlayerUT.getPlayerNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            this.printUsage(sender);
            return;
        }

        String name = sender.getName();
        if (args.length == 2) {
            if (!sender.hasPermission(SunPerms.CMD_SPEED_OTHERS)) {
                this.errorPermission(sender);
                return;
            }
            name = args[1];
        }

        float speed = (float) Math.min(10, StringUT.getDouble(args[0], 0));
        if (speed == 0) {
            this.errorNumber(sender, args[0]);
            return;
        }

        Player player = plugin.getServer().getPlayer(name);
        if (player == null) {
            this.errorPlayer(sender);
            return;
        }

        boolean isFly = player.isFlying();
        boolean isBypass = true;

        if (isFly) {
            player.setFlySpeed(this.getRealMoveSpeed(speed, isFly, isBypass));

            plugin.lang().Command_Speed_Done_Self_Fly
                .replace("%speed%", String.valueOf(speed))
                .send(player);

            if (!player.equals(sender)) {
                plugin.lang().Command_Speed_Done_Others_Fly
                    .replace("%player%", player.getName())
                    .replace("%speed%", String.valueOf(speed))
                    .send(sender);
            }
        }
        else {
            player.setWalkSpeed(this.getRealMoveSpeed(speed, isFly, isBypass));
            plugin.lang().Command_Speed_Done_Self_Walk
                .replace("%speed%", String.valueOf(speed))
                .send(player);

            if (!player.equals(sender)) {
                plugin.lang().Command_Speed_Done_Others_Walk
                    .replace("%player%", player.getName())
                    .replace("%speed%", String.valueOf(speed))
                    .send(sender);
            }
        }
    }

    private float getRealMoveSpeed(float userSpeed, boolean isFly, boolean isBypass) {
        float defaultSpeed = isFly ? 0.1F : 0.2F;
        float maxSpeed = 1.0F;
        if (!isBypass) {
            maxSpeed = (float) (isFly ? 10 : 10);
        }
        if (userSpeed < 1.0F) {
            return defaultSpeed * userSpeed;
        }
        float ratio = (userSpeed - 1.0F) / 9.0F * (maxSpeed - defaultSpeed);
        return ratio + defaultSpeed;
    }
}
