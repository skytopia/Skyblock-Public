package solar.rpg.skyblock.minigames;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.*;
import solar.rpg.skyblock.minigames.tasks.LeastAttemptsMinigameTask;
import solar.rpg.skyblock.util.Utility;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MemoryMatch extends Minigame implements FlawlessEnabled, BoardGame, NewbieFriendly {

    @Override
    public void start(Island island, List<UUID> participants, Difficulty difficulty) {
        main.getActiveTasks().add(new MemoryMatchTask(this, island, participants, main, difficulty).start());
        getRunning().add(island);
    }

    @Override
    public String getName() {
        return "Memory Match";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.TOTEM);
    }

    @Override
    public String[] getDescription() {
        return new String[]{"Become a mastermind.. OF LUCK!",
                ChatColor.ITALIC + "10 data of colors are hidden! Click to move them!",
                ChatColor.ITALIC + "Get a match to move the pair until it's over!",
                ChatColor.ITALIC + "Reveal all 10 data as efficiently as possible!"};
    }

    @Override
    public Difficulty[] getDifficulties() {
        //TODO: Harder, add more dimensions.
        return new Difficulty[]{Difficulty.NORMAL};
    }

    @Override
    public String getSummary() {
        return "Match 10 data efficiently!";
    }

    @Override
    public String getObjectiveWord() {
        return "guesses remaining";
    }

    @Override
    public int getDuration() {
        return 240;
    }

    @Override
    public int getGold() {
        return 20;
    }

    @Override
    public boolean isScoreDivisible() {
        return false;
    }

    @Override
    public int getFlawless() {
        return 30;
    }

    @Override
    public int getMaxReward() {
        return 5000;
    }

    private class MemoryMatchTask extends LeastAttemptsMinigameTask {

        /* Maps clickable blocks to their corresponding grid index. */
        private HashMap<Block, Short> clickable;

        /* Maps an index to a pair number. */
        private HashMap<Short, Short> groups;

        /* When making the first move, this stores the grid index of that square. */
        private Short revealed;

        /* List of solved data.*/
        private Set<Short> solved;

        MemoryMatchTask(Minigame owner, Island island, List<UUID> participants, MinigameController main, Difficulty difficulty) {
            super(island, owner, participants, main, difficulty, 1, 40);
        }

        @Override
        public void onStart() {
            clickable = new HashMap<>();
            groups = new HashMap<>();
            cooldown = System.currentTimeMillis();

            revealed = -1;
            solved = new HashSet<>();
            gen = generateLocation(100, 20, 140, true, false);
            if (!isEmpty(gen, 15, 6, 19)) {
                error();
                return;
            }

            makePlatform(gen, 15, 19, Material.SMOOTH_BRICK);

            // Adds all possible indexes to an array.
            ArrayList<Short> possiblePairs = new ArrayList<>();
            for (Short i = 1; i <= 20; i++)
                possiblePairs.add(i);

            // Create data from randomly selected numbers still in the lists above.
            Short pairID = 0;
            while (possiblePairs.size() > 0) {
                pairID++;
                // Get 2 random unselected data, and remove them.
                Short selection1 = possiblePairs.get(main.main().rng().nextInt(possiblePairs.size()));
                possiblePairs.remove(selection1);
                Short selection2 = possiblePairs.get(main.main().rng().nextInt(possiblePairs.size()));
                possiblePairs.remove(selection2);

                // Group them!
                groups.put(selection1, pairID);
                groups.put(selection2, pairID);

                registerClicks(generateGridLocation(selection1), selection1);
                registerClicks(generateGridLocation(selection2), selection2);
            }

            // Teleports players on to the platform.
            for (UUID in : getParticipants())
                Bukkit.getPlayer(in).teleport(gen.clone().add(8, 3, 5));

            selectPlayer();
        }

        /**
         * Reveals a hidden square to show its color.
         * If this is the first move, it leaves it revealed.
         * If this is the second move, it checks for a match.
         * If it is a match, the user gets another turn and the tiles stay revealed.
         * If it isn't a match, the tiles get re-hidden.
         *
         * @param block The block selected.
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

                // They've solved the 10 data.
                if (solved.size() == 10)
                    stop();
            }
        }

        /**
         * Reveals a tile on the board.
         *
         * @param revealed The grid index that got revealed.
         * @param group    The group this index belongs to.
         */
        private void reveal(Short revealed, Short group) {
            Location loc = generateGridLocation(revealed);
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
         * Hides a tile on the board.
         *
         * @param clicked The grid index of the tile to hide.
         */
        private void hide(Short clicked) {
            if (groups == null) return;
            Location loc = generateGridLocation(clicked);
            loc.getBlock().setType(Material.BEDROCK);
            loc.add(0, 0, 1);
            loc.getBlock().setType(Material.BEDROCK);
            loc.add(1, 0, 0);
            loc.getBlock().setType(Material.BEDROCK);
            loc.subtract(0, 0, 1);
            loc.getBlock().setType(Material.BEDROCK);
        }

        /**
         * Registers clickable tile blocks (and hides them).
         *
         * @param loc        Corner block of the tile to register.
         * @param allocation The grid index of this tile.
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
         * Takes a grid index allocation and returns a corner
         * block of the appropriate tile.
         * Each allocation has a unique X,Y spot on the board.
         *
         * @param allocation The grid index of this tile.
         */
        private Location generateGridLocation(short allocation) {
            Location result = gen.clone().add(0, 0, 3);

            // Find X axis.
            if (allocation >= 17) result.add(0, 0, 12);
            else if (allocation >= 13) result.add(0, 0, 9);
            else if (allocation >= 9) result.add(0, 0, 6);
            else if (allocation >= 5) result.add(0, 0, 3);

            // Find X axis.
            while (allocation > 4)
                allocation -= 4;
            if (allocation == 1) result.add(2, 0, 0);
            else if (allocation == 2) result.add(5, 0, 0);
            else if (allocation == 3) result.add(8, 0, 0);
            else if (allocation == 4) result.add(11, 0, 0);

            return result;
        }

        /**
         * Translates a group ID to a dye color data value.
         */
        private byte translateDyeColor(short group) {
            switch (group) {
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

        @Override
        public void onFinish() {
            returnParticipants();
            clickable.clear();
            clickable = null;
        }

        @Override
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
