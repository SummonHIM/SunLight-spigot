package su.nexmedia.sunlight.modules.scoreboard.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.scoreboard.Board;
import su.nexmedia.sunlight.modules.scoreboard.BoardConfig;
import su.nexmedia.sunlight.modules.scoreboard.ScoreboardManager;

public class ScoreboardListener extends AbstractListener<SunLight> {

    private final ScoreboardManager scoreboardManager;

    public ScoreboardListener(@NotNull ScoreboardManager scoreboardManager) {
        super(scoreboardManager.plugin());
        this.scoreboardManager = scoreboardManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBoardWorldChange(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        if (!this.scoreboardManager.isScoreboardEnabled(player)) return;

        Board board = this.scoreboardManager.getBoard(player);
        BoardConfig boardHas = board != null ? board.getBoardConfig() : null;
        BoardConfig boardNew = this.scoreboardManager.getBoardConfig(player);

        if (boardHas == null || boardNew == null) return;
        if (boardHas.equals(boardNew)) return;

        this.scoreboardManager.removeBoard(player);
        this.scoreboardManager.addBoard(player, boardNew);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBoardJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!this.scoreboardManager.isScoreboardEnabled(player)) return;

        this.scoreboardManager.addBoard(player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBoardQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        this.scoreboardManager.removeBoard(player);
    }
}
