package model.characters;

public class StrikesTracker {
    private int strikes;
    private final int maxStrikes;

    public StrikesTracker(int maxStrikes) {
        this.maxStrikes = maxStrikes;
        this.strikes = maxStrikes; // Start with max Strikes
    }

    public void loseStrike() {
        if (strikes > 0) {
            strikes--;
            System.out.println("Strike lost! Strikes remaining: " + strikes);
            if (isGameOver()) {
                System.out.println("Game Over!");
                // game over here
            }
        }
    }

    public void gainStrike() {
        if (strikes < maxStrikes) {
            strikes++;
            System.out.println("Extra Strike gained! Strikes now: " + strikes);
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

