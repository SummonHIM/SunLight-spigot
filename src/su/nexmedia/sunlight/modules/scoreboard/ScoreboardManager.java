package su.nexmedia.sunlight.modules.scoreboard;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.manager.api.task.ITask;
import su.nexmedia.engine.utils.Constants;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.hooks.HookId;
import su.nexmedia.sunlight.modules.ModuleId;
import su.nexmedia.sunlight.modules.SunModule;
import su.nexmedia.sunlight.modules.scoreboard.command.ScoreboardCommand;
import su.nexmedia.sunlight.modules.scoreboard.config.ScoreboardLang;
import su.nexmedia.sunlight.modules.scoreboard.listener.ScoreboardListener;
import su.nexmedia.sunlight.utils.SimpleTextAnimator;

import java.util.*;

public class ScoreboardManager extends SunModule {

    public static final String SETTING_SCOREBOARD = "SCOREBOARD";
    Map<String, SimpleTextAnimator> animationMap;
    private ScoreboardLang lang;
    private Map<String, BoardConfig> boardConfigMap;
    private Map<Player, Board> boardUserMap;
    private BoardTask          boardTask;

    public ScoreboardManager(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getId() {
        return ModuleId.SCOREBOARD;
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0";
    }

    @Override
    public void onLoad() {
        if (!Hooks.hasPlugin(HookId.PROTOCOL_LIB)) {
            this.interruptLoad("You must install ProtocolLib to use Scoreboard!");
            return;
        }

        this.lang = new ScoreboardLang(plugin, JYML.loadOrExtract(plugin, this.getPath() + "lang/messages_" + plugin.cfg().lang + ".yml"));
        this.lang.setup();

        this.boardConfigMap = new HashMap<>();
        this.boardUserMap = new WeakHashMap<>();
        this.setupAnimations();

        for (String boardId : cfg.getSection("Boards")) {
            String path2 = "Boards." + boardId + ".";

            int updateInterval = cfg.getInt(path2 + "Update_Interval", 20);
            int priority = cfg.getInt(path2 + "Priority");
            Set<String> worlds = cfg.getStringSet(path2 + "Worlds");
            Set<String> groups = cfg.getStringSet(path2 + "Groups");
            String title = cfg.getString(path2 + "Title", "");
            List<String> lines = cfg.getStringList(path2 + "Lines");

            BoardConfig boardConfig = new BoardConfig(boardId, updateInterval, priority, worlds, groups, title, lines);
            this.boardConfigMap.put(boardConfig.getId(), boardConfig);
        }

        // Register commands and tasks.
        this.plugin.getCommandRegulator().register(new ScoreboardCommand(this));

        this.addListener(new ScoreboardListener(this));

        this.boardTask = new BoardTask(this.plugin);
        this.boardTask.start();

        this.plugin.getServer().getOnlinePlayers().forEach(player -> {
            if (this.isScoreboardEnabled(player)) this.addBoard(player);
        });
    }

    private void setupAnimations() {
        JYML cfgAnim = JYML.loadOrExtract(plugin, this.getPath() + "animations.yml");

        this.animationMap = new HashMap<>();
        for (String sId : cfgAnim.getSection("")) {
            int aInter = cfgAnim.getInt(sId + ".Update_Interval_MS");
            if (aInter <= 0) continue;

            List<String> aLines = cfgAnim.getStringList(sId + ".Texts");
            SimpleTextAnimator animation = new SimpleTextAnimator(sId, aLines, aInter);
            this.animationMap.put(animation.getId(), animation);
        }
    }

    @Override
    public void onShutdown() {
        if (this.boardTask != null) {
            this.boardTask.stop();
            this.boardTask = null;
        }
        if (this.boardUserMap != null) {
            this.boardUserMap.values().forEach(Board::remove);
            this.boardUserMap.clear();
            this.boardUserMap = null;
        }
        if (this.animationMap != null) {
            this.animationMap.clear();
            this.animationMap = null;
        }
    }

    @NotNull
    public ScoreboardLang getLang() {
        return lang;
    }

    @Nullable
    public BoardConfig getBoardConfig(@NotNull Player player) {
        Set<String> groups = Hooks.getPermissionGroups(player);
        return this.boardConfigMap.values().stream()
            .filter(board -> groups.stream().anyMatch(pRank -> board.getGroups().contains(pRank)) || board.getGroups().contains(Constants.MASK_ANY))
            .filter(board -> board.getWorlds().contains(player.getWorld().getName()) || board.getWorlds().contains(Constants.MASK_ANY))
            .max(Comparator.comparingInt(BoardConfig::getPriority))
            .orElse(null);
    }

    @Nullable
    public Board getBoard(@NotNull Player player) {
        return this.boardUserMap.get(player);
    }

    public boolean hasBoard(@NotNull Player player) {
        return this.getBoard(player) != null;
    }

    public void addBoard(@NotNull Player player) {
        BoardConfig boardConfig = this.getBoardConfig(player);
        if (boardConfig == null) return;

        this.addBoard(player, boardConfig);
    }

    public void addBoard(@NotNull Player player, @NotNull BoardConfig boardConfig) {
        if (Hooks.isNPC(player)) return;
        if (this.hasBoard(player)) return;

        this.boardUserMap.computeIfAbsent(player, board -> new Board(player, this, boardConfig));
    }

    public void removeBoard(@NotNull Player player) {
        if (Hooks.isNPC(player)) return;

        Board board = this.boardUserMap.remove(player);
        if (board == null) return;

        board.remove();
    }

    public void toggleBoard(@NotNull Player player) {
        if (!this.hasBoard(player)) {
            this.addBoard(player);
        }
        else {
            this.removeBoard(player);
        }
    }

    public boolean isScoreboardEnabled(@NotNull Player player) {
        SunUser user = plugin.getUserManager().getOrLoadUser(player);
        return user.getSettingBoolean(SETTING_SCOREBOARD);
    }

    class BoardTask extends ITask<SunLight> {

        private int counter;

        BoardTask(@NotNull SunLight plugin) {
            super(plugin, 1L, true);
            this.counter = 0;
        }

        @Override
        public void action() {
            boardUserMap.forEach((player, board) -> {
                if (counter % board.getBoardConfig().getUpdateInterval() == 0) {
                    board.update(player);
                }
            });
            if (counter++ >= Short.MAX_VALUE) {
                counter = 0;
            }
        }
    }
}
