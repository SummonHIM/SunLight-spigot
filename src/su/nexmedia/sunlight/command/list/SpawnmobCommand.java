package su.nexmedia.sunlight.command.list;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.CollectionsUT;
import su.nexmedia.engine.utils.LocUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.utils.SunUtils;

import java.util.Arrays;
import java.util.List;

public class SpawnmobCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "spawnmob";

    public SpawnmobCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_SPAWNMOB);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_SpawnMob_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_SpawnMob_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return SunUtils.ENTITY_TYPES.stream()
                .filter(type -> player.hasPermission(SunPerms.CMD_SPAWNMOB + "." + type)).toList();
        }
        if (i == 2) {
            return Arrays.asList("1", "2", "3", "5", "10");
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length > 2 || args.length < 1) {
            this.printUsage(sender);
            return;
        }

        String mob = args[0];
        int amount = 1;

        if (args.length == 2) {
            amount = Math.min(10, StringUT.getInteger(args[1], 0));
            if (amount == 0) {
                this.errorNumber(sender, args[1]);
                return;
            }
        }

        EntityType entityType = CollectionsUT.getEnum(mob, EntityType.class);
        if (entityType == null || !entityType.isSpawnable()) {
            this.errorType(sender, EntityType.class);
            return;
        }

        if (!sender.hasPermission(SunPerms.CMD_SPAWNMOB + "." + entityType.name().toLowerCase())) {
            this.errorPermission(sender);
            return;
        }

        Player player = (Player) sender;
        Location loc = LocUT.getCenter(player.getTargetBlock(null, 100).getRelative(BlockFace.UP).getLocation());
        for (int i = 0; i < amount; i++) {
            player.getWorld().spawnEntity(loc, entityType);
        }

        plugin.lang().Command_SpawnMob_Done
            .replace("%amount%", String.valueOf(amount))
            .replace("%type%", plugin.lang().getEnum(entityType))
            .send(sender);
    }
}
