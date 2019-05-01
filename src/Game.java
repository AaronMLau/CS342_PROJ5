import java.util.ArrayList;
import java.util.Random;

public class Game {
    private int players;
    private Deck cards;
    private ArrayList<Player> people;

    public Game(int numplayers) {
        players = numplayers;
        cards = new Deck();
        cards.populateDeck();
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

    public void eliminatePlayers() {
        for (Player person : people) {
            if (person.remaininghealth() <= 0) {
                people.remove(person);
                //if this doesn't work use this commented part
                //people.remove(people.indexOf(person));
            }
        }
    }

    public void startGame() {
        for (Player person : people) {
            // each person starts with 4 cards
            // (supposed to be varied, but fixed for simplicity)
            for (int i = 0; i < 4; i++) {
                person.addCard(cards.draw());
            }
            if (person.getRole().equals("sheriff")) {
                person.swapturn();
            }
        }
    }

    public void endTurn() {
        for (int i = 0; i < players; i++) {
            if (people.get(i).go()) {
                if (people.get(i).getHandSize() > people.get(i).remaininghealth()) {
                    people.get(i).discardIndex(0);
                }
                people.get(i).swapturn();
                if (i == players - 1) {
                    people.get(0).swapturn();
                } else {
                    people.get(i+1).swapturn();
                }
                break;
            }
        }
    }
}
