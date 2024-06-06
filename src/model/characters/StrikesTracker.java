package model.characters;

public class StrikesTracker {
    private int strikes;
    private final int maxStrikes;

    public StrikesTracker(int maxStrikes) {
        this.maxStrikes = maxStrikes;
        this.strikes = maxStrikes; // Start with max Strikes
    }


    /**
     * Method to lose a strike, should be called only from Person class!
     */
    void loseStrike() {
        if (strikes > 0) {
            strikes--;
        }
    }

    public void gainStrike(int amount) {
        if (strikes < maxStrikes) {
            strikes += amount;
        }
    }
    public boolean isGameOver() {
        return strikes <= 0;
    }
    public void resetStrikes() {
        strikes = maxStrikes;
    }

    public int getStrikes() {
        return strikes;
    }
}

