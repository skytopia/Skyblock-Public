package solar.rpg.skyblock.minigames.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.island.minigames.MinigameTask;
import solar.rpg.skyblock.island.minigames.Playstyle;

import java.util.List;
import java.util.UUID;

/**
 * Supports "turn-based" minigames where participants work together.
 * Actions can only be performed by whoever's turn it is.
 * Completing an action makes it someone else's turn.
 * <ul>
 * <li>Timer: counts up from zero.</li>
 * <li>Score: starts at zero, increases as objectives are finished.</li>
 * <li>Medals: earn medals as score is increased.</li>
 * <li>Gameover happens as defined in each minigame.</li>
 * </ul>
 *
 * @author lavuh
 * @author JacquiRose
 * @version 1.1
 * @since 1.0
 */
public abstract class TurnBasedMinigameTask extends MinigameTask {

    /* How many actions can be performed by one player in a single turn. */
    private final int maxActions;

    /* How long the user has to wait, in ms, before performing an action after a turn swap. */
    protected long cooldown;

    /* Set to false when making actions should be cancelled entirely. */
    protected boolean canMove;

    /* Amount of actions remaining before a turn swap. */
    private int actions;

    /* Index on the participants list denoting who's turn it is. */
    private int selected;

    /* Number of turns passed. */
    private int turns;

    TurnBasedMinigameTask(Island owner, Minigame minigame, List<UUID> participants, MinigameController main, Difficulty difficulty, int maxGoes) {
        super(owner, minigame, participants, main, difficulty);
        this.maxActions = maxGoes;
        this.actions = 1;
        this.selected = -1;
        this.turns = -1;
        this.cooldown = System.currentTimeMillis();
        this.canMove = true;

        // In turn-based minigames, breaking and placing are always disabled.
        rules.put("breaking", false);
        rules.put("placing", false);
    }

    @Override
    public boolean ascendingTimer() {
        return false;
    }

    @Override
    public boolean scoreTimer() {
        return false;
    }

    @Override
    public boolean isMedalAchieved() {
        return true;
    }

    /**
     * @return Amount of turns that have passed.
     */
    protected int getTurns() {
        return turns;
    }

    /**
     * Sets amount of turns passed to zero.
     */
    protected void resetTurns() {
        turns = 0;
    }

    /**
     * Selects the next eligible player in the participants list.
     * Makes it their turn to perform actions.
     */
    protected void selectPlayer() {
        // Skip picking someone if the minigame has ended.
        if (disqualified == null || getParticipants() == null) return;

        // Skip picking someone if there is no one available.
        if (disqualified.size() == getParticipants().size()) return;

        // End the round if the amount of overall points is zero.
        // This is only applicable to cooperative minigames.
        if (minigame.getPlaystyle() == Playstyle.COOPERATIVE)
            if (getActualResult(null) < 0) {
                stop();
                return;
            }

        // Is there more actions left? Keep going!
        if (actions > 1) {
            actions--;
            return;
        }

        // Keep a record of who was selected.
        int currentIndex = selected;
        selected = -1;
        if (currentIndex == -1)
            currentIndex = 0;

        // Find next player to select.
        while (selected == -1) {
            // Go to the next player.
            currentIndex++;
            // Are we above the maximum index? Start at 1 again.
            if (currentIndex > getParticipants().size())
                currentIndex = 1;
            // Are they eligible? Set them!
            if (!disqualified.contains(getParticipants().get(currentIndex - 1)))
                selected = currentIndex;
        }

        // Let everyone know!
        main.soundAll(getParticipants(), Sound.ENTITY_CAT_HURT, 3F);
        titleParticipants("", ChatColor.GOLD + "It is " + Bukkit.getPlayer(getParticipants().get(selected - 1)).getDisplayName() + ChatColor.GOLD + "'s turn!");
        turns++;
        canMove = true;
        actions = maxActions;
    }

    /**
     * @param check Player to check.
     * @return True if it is this participant's turn.
     */
    protected boolean isSelected(UUID check) {
        return getParticipants().get(selected - 1).equals(check);
    }

    /**
     * @return The UUID of the participant who is completing a turn.
     */
    protected UUID getSelected() {
        return getParticipants().get(selected - 1);
    }

    /**
     * @return True if the player can perform an action.
     */
    protected boolean canMove(Player check) {
        if (isValidParticipant(check.getUniqueId()))
            if (canMove && isSelected(check.getUniqueId())) {
                if (System.currentTimeMillis() < cooldown)
                    check.sendMessage(ChatColor.RED + "Please wait before selecting again!");
                else
                    return true;
            } else
                check.sendMessage(ChatColor.RED + "It is not your turn!");
        return false;
    }

    /**
     * Makes a player's turn end when they are disqualified.
     *
     * @param pl Player who was disqualfied.
     */
    @Override
    public void disqualify(Player pl) {
        if (getParticipants().get(selected - 1).equals(pl.getUniqueId()))
            selectPlayer();
        super.disqualify(pl);
    }
}
