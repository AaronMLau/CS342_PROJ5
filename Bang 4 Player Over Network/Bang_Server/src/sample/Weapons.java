package sample;

public class Weapons extends Card {
    private int reach;

    public Weapons(String effect) {
        super(effect);
        switch (effect) {
            case "schofield":
                reach = 2;
            case "remington":
                reach = 3;
//            case "rev carabine":
//                reach = 4;
//            case "winchester":
//                reach = 5;
            default:
                // unknown weapon, default range
                reach = 1;
        }
    }

    public int getReach() {
        return reach;
    }
}