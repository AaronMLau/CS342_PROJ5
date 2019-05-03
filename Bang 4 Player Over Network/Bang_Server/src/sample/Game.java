package sample;

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
        for (int i = 1; i <= numplayers; i++) {
            int pos = rand.nextInt(roles.size());
            String role = roles.remove(pos);
            String name = "Player " + i;
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

    public void eliminatePlayer(Player person) {
        int pos = people.indexOf(person);
        if (pos == -1) {
            return; // player does not exist
        } else if (person.getRole().equals("sheriff")) {
            endGame(true);
        }
        if (pos == 0) {
            people.get(players - 1).setNext(people.get(1));
            people.get(1).setPrev(people.get(players - 1));
        } else if (pos == players - 1) {
            people.get(pos - 1).setNext(people.get(0));
            people.get(0).setPrev(people.get(pos - 1));
        } else {
            people.get(pos - 1).setNext(people.get(pos + 1));
            people.get(pos + 1).setPrev(people.get(pos - 1));
        }

        people.remove(person);
        // or this if remove(object) doesn't work:
        // people.remove(pos);
        players--;
        //if this doesn't work use this commented part
        //people.remove(people.indexOf(person));
        if (players <= 1) {
            String role = people.get(0).getRole();
            if (role.equals("sheriff")) {
                endGame(false);
            } else {
                endGame(true);
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
                // debugging purpose
                System.out.println(person.getName() + " is the sheriff");

                person.swapturn();
            }
        }
    }

    public ArrayList<Card> showplayerhand() {
        for (Player person : people) {
            if (person.go()) {
                // debugging purpose
                System.out.println(person.getName() + "'s hand");

                return person.hand;
            }
        }
        return null;
    }

    public void endTurn() {
        for (int i = 0; i < players; i++) {
            if (people.get(i).go()) {
                if (people.get(i).getHandSize() > people.get(i).remaininghealth()) {
                    // placeholder. This isn't what actually happens
                    people.get(i).discardIndex(0);
                }
                people.get(i).swapturn();
                people.get(i).modifyBanged(false);
                if (i == players - 1) {
                    people.get(0).swapturn();
                } else {
                    people.get(i+1).swapturn();
                }
                break;
            }
        }
    }

    public void endGame(boolean sheriffdied) {
        if (!sheriffdied) {
            // placeholder. Modify to actually display this message
            System.out.println("Sheriff wins!");
        } else {
            boolean outlaws = false;
            for (Player person : people) {
                if (person.getRole().equals("outlaw")) {
                    //placeholder. Modify to actually display this message
                    System.out.println("Outlaws win!");
                    outlaws = true;
                    break;
                }
            }
            if (!outlaws) {
                // placeholder. Modify to actually display this message
                System.out.println("Renegade wins!");
            }
        }

        // insert something here to see if people want to play again or something
    }
}