package solar.rpg.skyblock.challenges.chapter3.part1;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.reward.DummyReward;
import solar.rpg.skyblock.island.chronology.reward.ItemReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

public class ForTheRecord extends Chronicle {

    public String getName() {
        return "For The Record";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new Criteria() {
            @Override
            public boolean has(Island island, Player toCheck) {
                Inventory inv = toCheck.getInventory();
                return inv.contains(Material.GREEN_RECORD)
                        && inv.contains(Material.GOLD_RECORD)
                        && inv.contains(Material.RECORD_3)
                        && inv.contains(Material.RECORD_4)
                        && inv.contains(Material.RECORD_5)
                        && inv.contains(Material.RECORD_6)
                        && inv.contains(Material.RECORD_7)
                        && inv.contains(Material.RECORD_8)
                        && inv.contains(Material.RECORD_9)
                        && inv.contains(Material.RECORD_10)
                        && inv.contains(Material.RECORD_12);
            }

            @Override
            public String[] getDescription() {
                return new String[]{"Collect every Music Disc", "(except Disc 11)"};
            }

            @Override
            public void success(Island island, Player toCheck) {
                Inventory inv = toCheck.getInventory();
                inv.remove(Material.GREEN_RECORD);
                inv.remove(Material.GOLD_RECORD);
                inv.remove(Material.RECORD_3);
                inv.remove(Material.RECORD_4);
                inv.remove(Material.RECORD_5);
                inv.remove(Material.RECORD_6);
                inv.remove(Material.RECORD_7);
                inv.remove(Material.RECORD_8);
                inv.remove(Material.RECORD_9);
                inv.remove(Material.RECORD_10);
                inv.remove(Material.RECORD_12);
            }
        }};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new DummyReward() {
                    @Override
                    public String getReward() {
                        return "Unlocks \"Creeper Parry\" Combat Skill";
                    }
                }
        };
    }

    @Override
    public Reward[] getRepeatReward() {
        return new Reward[]{
                new ItemReward(ItemUtility.createEnchantBook(Enchantment.MENDING, 1, Enchantment.PROTECTION_EXPLOSIONS, 10, Enchantment.VANISHING_CURSE, 1))
        };
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.RECORD_11, 1);
    }

    public boolean isRepeatable() {
        return true;
    }
}
