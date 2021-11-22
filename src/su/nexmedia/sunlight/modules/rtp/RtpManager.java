package su.nexmedia.sunlight.modules.rtp;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.EffectUT;
import su.nexmedia.engine.utils.LocUT;
import su.nexmedia.engine.utils.MsgUT;
import su.nexmedia.engine.utils.NumberUT;
import su.nexmedia.engine.utils.random.Rnd;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.ModuleId;
import su.nexmedia.sunlight.modules.SunModule;
import su.nexmedia.sunlight.modules.rtp.command.RtpCommand;
import su.nexmedia.sunlight.modules.rtp.config.RtpConfig;
import su.nexmedia.sunlight.modules.rtp.config.RtpLang;

import java.util.Map;
import java.util.WeakHashMap;

public class RtpManager extends SunModule {

    private RtpLang lang;

    public RtpManager(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getId() {
        return ModuleId.RTP;
    }

    @Override
    @NotNull
    public String getVersion() {
        return "2.0";
    }

    @Override
    public void onLoad() {
        this.lang = new RtpLang(plugin, JYML.loadOrExtract(plugin, this.getPath() + "lang/messages_" + plugin.cfg().lang + ".yml"));
        this.lang.setup();
        RtpConfig.load(this);

        this.plugin.getCommandRegulator().register(new RtpCommand(this));
    }

    @Override
    public void onShutdown() {

    }

    @NotNull
    public RtpLang getLang() {
        return lang;
    }

    public void randomTp(@NotNull Player player) {
        if (Finder.IN_TELEPORT.containsKey(player)) {
            this.getLang().Teleport_Error_AlreadyIn.send(player);
            return;
        }

        World world = player.getWorld();
        if (!RtpConfig.COORDINATES.containsKey(world)) {
            world = RtpConfig.FORCE_TO_DEFAULT ? RtpConfig.DEFAULT_WORLD : null;
        }
        if (world == null) {
            this.getLang().Teleport_Error_World.send(player);
            return;
        }

        Finder finder = new Finder(player, world, RtpConfig.MAX_SEARCH_ATTEMPTS);
        finder.start();
    }

    public boolean isValidBlock(@NotNull Block block) {
        return !RtpConfig.IGNORED_BLOCKS.contains(block.getType().name());
    }

    class Finder {

        public static final Map<Player, Finder> IN_TELEPORT = new WeakHashMap<>();

        private final Player player;
        private final int    attempts;
        private final World  world;

        public Finder(@NotNull Player player, @NotNull World world, int attempts) {
            this.player = player;
            this.world = world;
            this.attempts = attempts;
            IN_TELEPORT.put(this.player, this);
        }

        public void start() {
            if (RtpConfig.SOUND_START != null) {
                MsgUT.sound(this.player, RtpConfig.SOUND_START);
            }
            this.find(0);
        }

        private void find(int attempt) {
            if (this.player == null || this.world == null) return;

            double[] coords = RtpConfig.COORDINATES.get(this.world);
            if (attempt >= this.attempts || coords == null) {
                getLang().Teleport_Notify_Failure.send(player);
                IN_TELEPORT.remove(player);
                return;
            }

            if (attempt > 0 && RtpConfig.SOUND_ATTEMPT != null) {
                MsgUT.sound(this.player, RtpConfig.SOUND_ATTEMPT);
            }

            getLang().Teleport_Notify_Search
                .replace("%attempt_current%", attempt + 1).replace("%attempt_max%", this.attempts)
                .send(player);

            double locX = Rnd.getDouble(coords[0], coords[1]);
            double locZ = Rnd.getDouble(coords[2], coords[3]);

            Block ground = this.world.getHighestBlockAt((int) locX, (int) locZ);
            Block above = ground.getRelative(BlockFace.UP);
            Location location = LocUT.getCenter(above.getLocation());

            if ((above.isEmpty() || !above.getType().isSolid()) && world.getWorldBorder().isInside(location)) {
                if (ground.getType().isSolid() && !ground.isEmpty() && isValidBlock(ground)) {
                    this.player.teleport(location);

                    if (RtpConfig.SOUND_TELEPORT != null) {
                        MsgUT.sound(this.player, RtpConfig.SOUND_TELEPORT);
                    }
                    EffectUT.playEffect(location, RtpConfig.PARTICLE_TELEPORT, 0.25, 0.25, 0.25, 0.1, 50);

                    String x = NumberUT.format(location.getX());
                    String y = NumberUT.format(location.getY());
                    String z = NumberUT.format(location.getZ());

                    getLang().Teleport_Notify_Done
                        .replace("%x%", x).replace("%y%", y).replace("%z%", z)
                        .send(player);

                    IN_TELEPORT.remove(this.player);
                    return;
                }
            }

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                this.find(attempt + 1);
            }, 100L);
        }
    }
}
