package solar.rpg.skyblock.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.minigames.tasks.TimeCountdownMinigameTask;
import solar.rpg.skyblock.stored.Settings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CharcoalMoon extends Minigame {

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new CharcoalMoonTask(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "Charcoal Moon";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.LAVA_BUCKET);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.ITALIC + "Wither Skeletons are invading!",
                ChatColor.ITALIC + "Kill 50 within the time limit to win!",
                "\"This is gonna be a terrible night...\""
        };
    }

    @Override
    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL, Difficulty.HARDER};
    }

    @Override
    public String getSummary() {
        return "Kill 50 Wither Skeletons!";
    }

    @Override
    public String getObjectiveWord() {
        return "seconds remaining";
    }

    @Override
    public int getDuration() {
        return 300;
    }

    @Override
    public int getMaxReward() {
        return 7500;
    }

    @Override
    public int getGold() {
        return 220;
    }

    @Override
    public boolean isScoreDivisible() {
        return false;
    }

    private class CharcoalMoonTask extends TimeCountdownMinigameTask {

        /* Entity attribute modifier that adds additional health to harder skeletons. */
        private AttributeModifier addHealth;

        /* Keeps track of the alive skeletons spawned in relation to this minigame. */
        private Set<UUID> aliveSkeletons;

        CharcoalMoonTask(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty);
            rules.put("flying", false);
            addHealth = new AttributeModifier("add_health", 10, AttributeModifier.Operation.ADD_NUMBER);
        }

        @Override
        public void onStart() {
            aliveSkeletons = new HashSet<>();
            // Set the apparent time to night for all participants.
            for (UUID part : getParticipants())
                if (Bukkit.getPlayer(part) != null)
                    Bukkit.getPlayer(part).setPlayerTime(19000, false);
        }

        @Override
        public void onTick() {
            // Has a 50% chance to spawn a skeleton every second.
            if (main.main().rng().nextBoolean()) return;
            if (getParticipants() == null) return;
            // Only a maximum amount of 15 skeletons for every participant can spawn.
            if (aliveSkeletons.size() > 15 * getParticipants().size()) return;
            for (UUID part : getParticipants()) {
                if (!isValidParticipant(part)) continue;
                Player target = Bukkit.getPlayer(part);
                WitherSkeleton skel = (WitherSkeleton) Bukkit.getWorld(Settings.ADMIN_WORLD_ID).spawnEntity(generateLocation(12, 5, target.getLocation().getBlockY(), false, true), EntityType.WITHER_SKELETON);

                // Average difficulty gives skeletons a 50% chance to be holding a stone sword.
                // Harder difficulty gives skeletons a 100% chance to be holding a diamond sword.
                if (difficulty == Difficulty.HARDER) {
                    skel.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
                    skel.getAttribute(Attribute.GENERIC_MAX_HEALTH).addModifier(addHealth);
                } else if (main.main().rng().nextBoolean())
                    skel.getEquipment().setItemInMainHand(new ItemStack(Material.STONE_SWORD));
                skel.setTarget(target);
                aliveSkeletons.add(skel.getUniqueId());
            }
        }

        @Override
        public void onFinish() {
            // Clear and remove skeleton list.
            aliveSkeletons.clear();
            aliveSkeletons = null;

            // Kill all wither skeletons on the island.
            for (WitherSkeleton skel : Bukkit.getWorld(Settings.ADMIN_WORLD_ID).getEntitiesByClass(WitherSkeleton.class))
                if (owner.isInside(skel.getLocation()))
                    skel.setHealth(0);

            // Set hidden night time back to default.
            for (UUID part : getParticipants())
                if (Bukkit.getPlayer(part) != null)
                    Bukkit.getPlayer(part).resetPlayerTime();
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            if (event.getEntity() instanceof Skeleton) {
                if (aliveSkeletons != null)
                    if (aliveSkeletons.contains(event.getEntity().getUniqueId())) {
                        event.getDrops().clear();
                        aliveSkeletons.remove(event.getEntity().getUniqueId());
                        points++;
                        main.soundAll(getParticipants(), Sound.BLOCK_LEVER_CLICK, 2F);
                        if (points == 50)
                            stop();
                        else if (points % 10 == 0)
                            titleParticipants(ChatColor.GOLD + "Progress!", ChatColor.RED + "" + points + "/50 skeletons killed!");
                    }
            }
        }

        @EventHandler
        public void onTarget(EntityTargetLivingEntityEvent event) {
            // Non-participants can't be targeted by the skeletons.
            if (event.getEntity() instanceof WitherSkeleton)
                if (aliveSkeletons.contains(event.getEntity().getUniqueId()))
                    if (!isValidParticipant(event.getTarget().getUniqueId()))
                        event.setCancelled(true);
        }

        /**
         * When a player is disqualified, reset their player time.
         *
         * @param pl Player who was disqualfied.
         */
        @Override
        public void disqualify(Player pl) {
            super.disqualify(pl);
            pl.resetPlayerTime();
        }
    }
}
