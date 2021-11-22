package su.nexmedia.sunlight.modules.chat.rule;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.engine.utils.actions.ActionManipulator;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.chat.ChatManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChatRuleConfig {

    private final ActionManipulator     punishmentActions;
    private final Map<String, ChatRule> rules;

    public ChatRuleConfig(@NotNull ChatManager chatManager, @NotNull JYML cfg) {
        SunLight plugin = chatManager.plugin();

        this.punishmentActions = new ActionManipulator(plugin, cfg, "Punishment.Custom_Actions");
        this.rules = new HashMap<>();
        for (String ruleId : cfg.getSection("Rules")) {
            String path2 = "Rules." + ruleId + ".";

            String rRegex = cfg.getString(path2 + "Pattern");
            if (rRegex == null || rRegex.isEmpty()) continue;

            ChatRule.Type rAction = cfg.getEnum(path2 + "Action", ChatRule.Type.class);
            if (rAction == null) continue;

            String rReplace = StringUT.color(cfg.getString(path2 + "Replace_With", ""));
            Set<String> rIgnored = cfg.getStringSet(path2 + "Ignored_Words");

            this.rules.put(ruleId, new ChatRule(ruleId, rRegex, rAction, rReplace, rIgnored));
        }
    }

    @NotNull
    public Map<String, ChatRule> getRules() {
        return rules;
    }

    @NotNull
    public ActionManipulator getPunishmentActions() {
        return punishmentActions;
    }

    public void punish(@NotNull Player player) {
        this.punishmentActions.process(player);
    }
}
