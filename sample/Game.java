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
            String name = "" + i;
            if (role.equals("sheriff")) {
                people.add(new Player(name, "sheriff", 5));
            } else {
                people.add(new Player(name, role, 4));
            }
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

    public Player getPlayer(int num){
        if(num > 3){
            return null;
        }
        return people.get(num);
    }

    public int getPlayerIndex(Player person) {
        for (Player play : people) {
            if (play == person) {
                return people.indexOf(play);
            }
        }
        return -1;
    }

    public Player getPlayer(String name){
        for(Player p : people){
            if(p.name.equals(name)){
                return p;
            }
        }
        return null;
    }

    public void eliminatePlayers() { // returns true if the game ends
        for (Player person : people) {
            if (person.remaininghealth() <= 0) {
                if (person.getRole().equals("sheriff")) {
                    endGame(true);
                    return;
                } else {
                    int pos = people.indexOf(person);
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
                    if (players <= 1) {
                        String role = people.get(0).getRole();
                        if (role.equals("sheriff")) {
                            endGame(false);
                        } else {
                            endGame(true);
                        }
                        return;
                    }
                }
            }
        }
//        int pos = people.indexOf(person);
//        if (pos == -1) {
//            return false; // player does not exist
//        } else if (person.remaininghealth() > 0) {
//            return false; // player has some health remaining
//        } else if (person.getRole().equals("sheriff")) {
//            endGame(true);
//            return true;
//        }
//        if (pos == 0) {
//            people.get(players - 1).setNext(people.get(1));
//            people.get(1).setPrev(people.get(players - 1));
//        } else if (pos == players - 1) {
//            people.get(pos - 1).setNext(people.get(0));
//            people.get(0).setPrev(people.get(pos - 1));
//        } else {
//            people.get(pos - 1).setNext(people.get(pos + 1));
//            people.get(pos + 1).setPrev(people.get(pos - 1));
//        }
//
//        people.remove(person);
//        // or this if remove(object) doesn't work:
//        // people.remove(pos);
//        players--;
//        //if this doesn't work use this commented part
//        //people.remove(people.indexOf(person));
//        if (players <= 1) {
//            String role = people.get(0).getRole();
//            if (role.equals("sheriff")) {
//                endGame(false);
//            } else {
//                endGame(true);
//            }
//            return true;
//        }
//        return false;
    }

    public void startGame() {
        for (Player person : people) {
            // each person starts with 4 cards
            // (supposed to be varied, but fixed for simplicity)
            for (int i = 0; i < 4; i++) {
                person.addCard(cards.draw());
            }
            System.out.println(person.getName() + " is the " + person.getRole());
            System.out.println(person.getName() + "'s hand");

            for (Card card : person.hand) {
                System.out.println(card.cardeffect());
            }
            if (person.getRole().equals("sheriff")) {
                person.swapturn();
            }
        }
    }

    public String info(int index) {
        String item =
                "*#" + people.get(index).getName() +
                "*r" + people.get(index).getRole();
        item += "*h" + people.get(index).remaininghealth();
        item += "*d" + people.get(index).getRange();
        item += "*w" + people.get(index).getWeapon();
        item += "*t" + people.get(index).go();
        item += "*c";

        for (Card card : people.get(index).hand) {
            item += card.cardeffect() + "^";
        }

        return item;
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
                //if (people.get(i).getHandSize() > people.get(i).remaininghealth()) {
                    // placeholder. This isn't what actually happens
                    //people.get(i).discardIndex(0);
                //}
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

    private void endGame(boolean sheriffdied) {
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