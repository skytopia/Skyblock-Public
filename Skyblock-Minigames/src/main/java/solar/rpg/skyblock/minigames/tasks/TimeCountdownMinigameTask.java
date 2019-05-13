package solar.rpg.skyblock.minigames.tasks;

import solar.rpg.skyblock.controllers.MinigameController;
import solar.rpg.skyblock.island.Island;
import solar.rpg.skyblock.island.minigames.Difficulty;
import solar.rpg.skyblock.island.minigames.Minigame;
import solar.rpg.skyblock.island.minigames.MinigameTask;

import java.util.List;
import java.util.UUID;

/**
 * Supports "time-attack" minigames.
 * <ul>
 * <li>Timer: counts down to zero.</li>
 * <li>Score: starts as amount of time remaining, decreases over time.</li>
 * <li>Medals: start with best medal, lose medals over time.</li>
 * <li>Gameover occurs when objective has been completed.</li>
 * <li>Final score is always zero if time runs out.</li>
 * </ul>
 *
 * @author lavuh
 * @author JacquiRose
 * @version 3.0
 * @since 3.0
 */
public class TimeCountdownMinigameTask extends MinigameTask {

    public TimeCountdownMinigameTask(Island owner, Minigame minigame, List<UUID> participants, MinigameController main, Difficulty difficulty) {
        super(owner, minigame, participants, main, difficulty);
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

    @Override
    public boolean isNoScoreIfOutOfTime() {
        return true;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onFinish() {
    }

    @Override
    public void onTick() {
    }
}
