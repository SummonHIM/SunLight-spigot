package su.nexmedia.sunlight.command.list;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CondenseCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "condense";

    public CondenseCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_CONDENSE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Condense_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        boolean done = false;
        Set<Material> userItems = new HashSet<>();

        // Put materials to set to avoid duplicates and 'double' converts
        for (ItemStack userItem : player.getInventory().getContents()) {
            if (ItemUT.isAir(userItem)) continue;
            userItems.add(userItem.getType());
        }

        for (Material userMaterial : userItems) {
            ItemStack userItem = new ItemStack(userMaterial);
            int amountPerCraft = 0;

            ItemStack recipeResult = null;

            Iterator<Recipe> iter = plugin.getServer().recipeIterator();

            Label_Recipe:
            while (iter.hasNext()) {
                Recipe r = iter.next();
                if (!(r instanceof ShapedRecipe)) continue;

                ShapedRecipe sr = (ShapedRecipe) r;
                Collection<ItemStack> recipeItems = sr.getIngredientMap().values();

                // Only 'cuboid' crafts.
                String[] shape = sr.getShape();
                if (shape.length < 2) continue;
                for (String line : shape) {
                    if (line.length() != shape.length) continue Label_Recipe;
                }

                // Check for same ingredients
                int amountPerRecipe = 0;
                for (ItemStack srcItem : recipeItems) {
                    if (ItemUT.isAir(srcItem)) continue;
                    if (!srcItem.isSimilar(userItem)) continue Label_Recipe;

                    amountPerRecipe += srcItem.getAmount();
                }

                // Get the greater recipe
                if (amountPerRecipe > amountPerCraft) {
                    amountPerCraft = amountPerRecipe;
                    recipeResult = r.getResult();
                }
            }

            // Check for valid recipe
            if (amountPerCraft <= 1 || recipeResult == null) {
                continue;
            }

            int amountUserHas = this.countPlayerItems(player, userItem);
            int amountCraftCan = (int) ((double) amountUserHas / (double) amountPerCraft);
            int amountCraftMin = recipeResult.getAmount();

            if (amountCraftCan < amountCraftMin) {
                plugin.lang().Command_Condense_Error_NotEnought
                    .replace("%amount%", String.valueOf(amountPerCraft))
                    .replace("%item-from%", ItemUT.getItemName(userItem))
                    .replace("%item-result%", ItemUT.getItemName(recipeResult))
                    .send(sender);
                continue;
            }

            for (int craft = 0; craft < amountCraftCan; craft++) {
                this.takePlayerItems(player, userItem, amountPerCraft);
                ItemUT.addItem(player, recipeResult);
            }

            plugin.lang().Command_Condense_Done
                .replace("%item-from%", ItemUT.getItemName(userItem))
                .replace("%item-result%", ItemUT.getItemName(recipeResult))
                .replace("%amount-from%", String.valueOf(amountCraftCan * amountPerCraft))
                .replace("%amount-result%", String.valueOf(amountCraftMin * amountCraftCan))
                .send(sender);
            done = true;
        }

        if (!done) {
            plugin.lang().Command_Condense_Error_Nothing.send(sender);
        }
    }

    @Deprecated
    private int countPlayerItems(@NotNull Player player, @NotNull ItemStack item) {
        int balance = 0;

        for (int i = 0; i < 36; i++) {
            ItemStack it = player.getInventory().getItem(i);
            if (it == null) continue;
            if (item.isSimilar(it)) {
                balance += it.getAmount();
            }
        }

        return balance;
    }

    @Deprecated
    private void takePlayerItems(@NotNull Player player, @NotNull ItemStack item, int amount) {
        int count = 0;

        for (int i = 0; i < 36; i++) {
            ItemStack it = player.getInventory().getItem(i);
            if (it == null) continue;
            if (item.isSimilar(it)) {
                int stackAmount = it.getAmount();
                if (count + stackAmount > amount) {
                    int diff = (count + stackAmount) - amount;
                    it.setAmount(diff);
                    return;
                }
                player.getInventory().removeItem(it);
                if ((count += stackAmount) == amount) {
                    return;
                }
            }
        }
    }
}
