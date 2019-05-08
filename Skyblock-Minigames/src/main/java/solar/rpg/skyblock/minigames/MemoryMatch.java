package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.BoardGame;
import solar.rpg.skyblock.island.minigames.FlawlessEnabled;
import solar.rpg.skyblock.island.minigames.MinigameMain;
import solar.rpg.skyblock.island.minigames.NewbieFriendly;
import solar.rpg.skyblock.island.minigames.task.Difficulty;
import solar.rpg.skyblock.island.minigames.task.LeastAttemptsMinigameTask;
import solar.rpg.skyblock.island.minigames.task.Minigame;
import solar.rpg.skyblock.util.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MemoryMatch extends Minigame implements FlawlessEnabled, BoardGame, NewbieFriendly {

    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new MemoryRun(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    public String getName() {
        return "Memory Match";
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.TOTEM);
    }

    public String[] getDescription() {
        return new String[]{"Become a mastermind.. OF LUCK!",
                ChatColor.ITALIC + "10 pairs of colors are hidden! Click to reveal them!",
                ChatColor.ITALIC + "Get a match to reveal the pair until it's over!",
                ChatColor.ITALIC + "Reveal all 10 pairs as efficiently as possible!"};
    }

    public Difficulty[] getDifficulties() {
        return new Difficulty[]{Difficulty.NORMAL, Difficulty.HARDER};
    }

    public String getSummary() {
        return "Match 10 pairs efficiently!";
    }

    public String getObjectiveWord() {
        return "guesses remaining";
    }

    public int getDuration() {
        return 240;
    }

    public int getGold() {
        return 20;
    }

    public boolean isScoreDivisible() {
        return false;
    }

    public int getFlawless() {
        return 30;
    }

    public int getMaxReward() {
        return 5000;
    }

    private class MemoryRun extends LeastAttemptsMinigameTask implements Listener {

        //What number pertains to what number ID.
        private HashMap<Block, Short> clickable;
        //If 1 and 2 are paired, they are group 1. If 7 and 10 are paired, they are group 2, and so on.
        private HashMap<Short, Short> groups;

        private Location gen;
        private Short revealed;
        private ArrayList<Short> solved;

        MemoryRun(Minigame owner, Island island, List<UUID> participants, MinigameMain main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty, 1, 40);
        }

        public void onStart() {
            clickable = new HashMap<>();
            groups = new HashMap<>();
            cooldown = System.currentTimeMillis();

            revealed = -1;
            solved = new ArrayList<>();
            gen = generateLocation(100, 20, 140, true, false);
            for (int x = 0; x <= 15; x++)
                for (int z = 0; z <= 19; z++)
                    if (gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY(), gen.getBlockZ() + z).getType() != Material.AIR) {
                        error();
                        return;
                    }
            for (int x = 0; x <= 15; x++)
                for (int z = 0; z <= 19; z++) {
                    Block bl = gen.getWorld().getBlockAt(gen.getBlockX() + x, gen.getBlockY(), gen.getBlockZ() + z);
                    bl.setType(Material.SMOOTH_BRICK);
                    placed.add(bl);
                }

            // Create a list of possible pairs, and vacant spaces on the board.
            ArrayList<Short> possiblePairs = new ArrayList<>();
            for (Short i = 1; i <= 20; i++)
                possiblePairs.add(i);

            // Create pairs from randomly selected numbers still in the lists above.
            Short pairID = 0;
            while (possiblePairs.size() > 0) {
                pairID++;
                // Get 2 random unselected pairs, and remove them.
                Short selection1 = possiblePairs.get(main.main().rng().nextInt(possiblePairs.size()));
                possiblePairs.remove(selection1);
                Short selection2 = possiblePairs.get(main.main().rng().nextInt(possiblePairs.size()));
                possiblePairs.remove(selection2);

                // Group them!
                groups.put(selection1, pairID);
                groups.put(selection2, pairID);

                registerClicks(generateGridLocation(selection1, gen), selection1);
                registerClicks(generateGridLocation(selection2, gen), selection2);
            }

            for (UUID in : getParticipants())
                Bukkit.getPlayer(in).teleport(gen.clone().add(8, 3, 5));

            selectPlayer();
        }

        /**
         * Reveals a whole square.
         */
        private void reveal(Block block) {
            if (!clickable.containsKey(block)) return;
            Short clicked = clickable.get(block);
            short clickedGroup = groups.get(clicked);

            reveal(clicked, clickedGroup);

            // Reveal the first pair, otherwise check for the match.
            if (revealed == -1) {
                revealed = clicked;
                main.soundAll(getParticipants(), Sound.ENTITY_CREEPER_DEATH, 2F);
            } else {
                Short revealedGroup = groups.get(revealed);
                final short tempRevealed = revealed;
                revealed = -1;
                if (revealedGroup == clickedGroup) {
                    main.messageAll(getParticipants(), ChatColor.GOLD + "It was a match!");
                    main.soundAll(getParticipants(), Sound.ENTITY_PLAYER_LEVELUP, 1F);
                    Utility.spawnFirework(block.getLocation().add(0.5, 1, 0.5));
                    solved.add(clickedGroup);
                } else {
                    cooldown = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3);
                    main.messageAll(getParticipants(), ChatColor.RED + "It was not a match..");
                    main.soundAll(getParticipants(), Sound.ENTITY_GHAST_DEATH, 2F);
                    Bukkit.getScheduler().runTaskLater(main.main().plugin(), () -> {
                        hide(tempRevealed);
                        hide(clicked);
                        selectPlayer();
                    }, 60L);
                }
                points--;

                // They've solved the 10 pairs.
                if (solved.size() == 10)
                    stop();
            }
        }

        /**
         * Reveals a square in-game.
         */
        private void reveal(Short revealed, Short group) {
            Location loc = generateGridLocation(revealed, gen);
            loc.getBlock().setType(Material.WOOL);
            loc.getBlock().setData(translateDyeColor(group));
            loc.add(0, 0, 1);
            loc.getBlock().setType(Material.WOOL);
            loc.getBlock().setData(translateDyeColor(group));
            loc.add(1, 0, 0);
            loc.getBlock().setType(Material.WOOL);
            loc.getBlock().setData(translateDyeColor(group));
            loc.subtract(0, 0, 1);
            loc.getBlock().setType(Material.WOOL);
            loc.getBlock().setData(translateDyeColor(group));
        }

        /**
         * Hides a visible square.
         */
        private void hide(Short clicked) {
            if (groups == null) return;
            Location loc = generateGridLocation(clicked, gen);
            loc.getBlock().setType(Material.BEDROCK);
            loc.add(0, 0, 1);
            loc.getBlock().setType(Material.BEDROCK);
            loc.add(1, 0, 0);
            loc.getBlock().setType(Material.BEDROCK);
            loc.subtract(0, 0, 1);
            loc.getBlock().setType(Material.BEDROCK);
        }

        /**
         * Registers clickable squares. (and hides them)
         */
        private void registerClicks(Location loc, Short allocation) {
            clickable.put(loc.getBlock(), allocation);
            loc.getBlock().setType(Material.BEDROCK);
            loc.add(0, 0, 1);
            clickable.put(loc.getBlock(), allocation);
            loc.getBlock().setType(Material.BEDROCK);
            loc.add(1, 0, 0);
            clickable.put(loc.getBlock(), allocation);
            loc.getBlock().setType(Material.BEDROCK);
            loc.subtract(0, 0, 1);
            clickable.put(loc.getBlock(), allocation);
            loc.getBlock().setType(Material.BEDROCK);
        }

        /**
         * Each allocation has a unique X,Y spot on the board.
         * Start at X+3, and then work your way up and right.
         */
        private Location generateGridLocation(short allocation, Location original) {
            Location result = original.clone().add(0, 0, 3);
            if (allocation >= 17) {
                result.add(0, 0, 12);
            } else if (allocation >= 13) {
                result.add(0, 0, 9);
            } else if (allocation >= 9) {
                result.add(0, 0, 6);
            } else if (allocation >= 5) {
                result.add(0, 0, 3);
            }
            return generateGridLocationPhase2(allocation, result);
        }

        /**
         * After finding the Z axis, find the X axis.
         *
         * @param allocation See above.
         * @param result     See above.
         * @return The grid location.
         */
        private Location generateGridLocationPhase2(short allocation, Location result) {
            while (allocation > 4)
                allocation -= 4;
            if (allocation == 1)
                result.add(2, 0, 0);
            else if (allocation == 2)
                result.add(5, 0, 0);
            else if (allocation == 3)
                result.add(8, 0, 0);
            else if (allocation == 4)
                result.add(11, 0, 0);
            return result;
        }

        /**
         * Translates a group ID to a dye color.
         */
        private byte translateDyeColor(short place) {
            switch (place) {
                case 1:
                    return 2;
                case 2:
                    return 6;
                case 3:
                    return 14;
                case 4:
                    return 1;
                case 5:
                    return 4;
                case 6:
                    return 5;
                case 7:
                    return 3;
                case 8:
                    return 11;
                case 9:
                    return 9;
                case 10:
                    return 10;
                default:
                    return 0;
            }
        }

        public void onFinish() {
            returnParticipants();
            clickable.clear();
            clickable = null;
        }

        public void onTick() {
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onInteract(PlayerInteractEvent event) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) return;
            if (event.getClickedBlock() == null) return;
            if (event.getClickedBlock().getType() != Material.BEDROCK) return;
            if (canMove(event.getPlayer()))
                reveal(event.getClickedBlock());
        }
    }
}
