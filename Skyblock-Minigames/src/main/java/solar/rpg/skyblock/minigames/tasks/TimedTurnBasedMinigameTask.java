package solar.rpg.skyblock.minigames.tasks;

import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.Minigame;

import java.util.List;
import java.util.UUID;

/**
 * Supports turn-based "time attack" minigames.
 * <ul>
 * <li>Timer: counts down to zero.</li>
 * <li>Score: starts as amount of time remaining, decreases over time.</li>
 * <li>Medals: start with best medal, lose medals over time.</li>
 * <li>Gameover occurs when time runs out or when objective is completed.</li>
 * <li>Final score is always zero if time runs out.</li>
 * </ul>
 *
 * @author lavuh
 * @author JacquiRose
 * @version 3.0
 * @since 3.0
 */
public abstract class TimedTurnBasedMinigameTask extends TurnBasedMinigameTask {

    public TimedTurnBasedMinigameTask(Island owner, Minigame minigame, List<UUID> participants, MinigameController main, Difficulty difficulty, int maxGoes) {
        super(owner, minigame, participants, main, difficulty, maxGoes);
    }

    @Override
    public boolean ascendingTimer() {
        return false;
    }

    @Override
    public boolean isMedalAchieved() {
        return false;
    }

    @Override
    public int getResult() {
        return clock;
    }

    @Override
    public boolean isHighscoreCheckable() {
        return false;
    }
}
