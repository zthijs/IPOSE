public class Player {

    private int score;
    private String name;

    public Player(String name, int score){
        this.score = score;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
