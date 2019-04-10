package Files;

/**
 * Represents the actual line in the text file managed by the high score
 * file manager.
 * 
 * @author Matthew Babel
 */
public class HighScore {
    
    private String line;
    private final String NAME;
    private final int SCORE;
    
    /**
     * Makes a new line with a score and name.
     * @param s the score
     * @param n the name
     */
    public HighScore(int s, String n) {
        SCORE = s;
        NAME = n;
        line = String.valueOf(SCORE)+"_" + NAME;
    }
    
    /**
     * Sets the line to given string.
     * @param s the new line
     */
    public void setLine(String s) {
        line = s;
    }
    
    /**
     * Gets the line.
     * @return line
     */
    public String getLine() {
        return line;
    }
    
    /**
     * Gets the score.
     * @return score
     */
    public int getScore() {
        return SCORE;
    }
}
