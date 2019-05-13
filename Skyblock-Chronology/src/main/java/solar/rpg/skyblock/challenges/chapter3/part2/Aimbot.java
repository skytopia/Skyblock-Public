package solar.rpg.skyblock.challenges.chapter3.part2;

import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import solar.rpg.skyblock.event.PlayerHeadshotMonsterEvent;
import solar.rpg.skyblock.island.chronology.Chronicle;
import solar.rpg.skyblock.island.chronology.Live;
import solar.rpg.skyblock.island.chronology.criteria.Criteria;
import solar.rpg.skyblock.island.chronology.criteria.DummyCrit;
import solar.rpg.skyblock.island.chronology.reward.GadgetReward;
import solar.rpg.skyblock.island.chronology.reward.Reward;
import solar.rpg.skyblock.util.ItemUtility;

import java.util.HashMap;
import java.util.UUID;

public class Aimbot extends Chronicle implements Live {

    private final HashMap<UUID, Integer> count = new HashMap<>();

    public String getName() {
        return "Aimbot";
    }

    public Criteria[] getCriteria() {
        return new Criteria[]{new DummyCrit("Land 15+ Headshots on Monsters", "(from 25+ blocks away)")};
    }

    public Reward[] getReward() {
        return new Reward[]{
                new GadgetReward("Homing Arrow")
        };
    }

    public ItemStack getIcon() {
        return ItemUtility.createTippedArrow(PotionEffectType.SATURATION, 1, 0);
    }

    public boolean isRepeatable() {
        return false;
    }

    @EventHandler
    public void onDamage(PlayerHeadshotMonsterEvent event) {
        if (event.getPlayer().getLocation().distanceSquared(event.getMonster().getLocation()) >= 625)
            if (count.containsKey(event.getPlayer().getUniqueId())) {
                int amt = count.get(event.getPlayer().getUniqueId());
                if (amt + 1 == 15)
                    main.challenges().complete(event.getPlayer(), this);
                count.put(event.getPlayer().getUniqueId(), amt + 1);
            } else
                count.put(event.getPlayer().getUniqueId(), 1);
    }
}
