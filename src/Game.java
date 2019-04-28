import java.util.ArrayList;
import java.util.Random;

public class Game {
    private int players;
    private ArrayList<Player> people;

    public Game(int numplayers) {
        players = numplayers;
        people = new ArrayList<Player>();
        ArrayList<String> roles = new ArrayList<String>();

        // this is hard coded for only 4 players
        roles.add("sheriff");
        roles.add("renegade");
        roles.add("outlaw");
        roles.add("outlaw");

        Random rand = new Random();
        for (int i = 0; i < numplayers; i++) {
            int pos = rand.nextInt(roles.size());
            String role = roles.remove(pos);
            String name = "Player" + i;
            people.add(new Player(name, role, 4));
        }

        for (int i = 0; i < numplayers; i++) {
            if (i == 0) {
                people.get(i).setPrev(people.get(numplayers - 1));
            } else {
                people.get(i).setPrev(people.get(i - 1));
            }

            if (i == numplayers - 1) {
                people.get(i).setNext(people.get(0));
            } else {
                people.get(i).setNext(people.get(i + 1));
            }
        }
    }


}
