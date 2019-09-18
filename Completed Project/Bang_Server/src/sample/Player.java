package sample;

import java.util.ArrayList;

public class Player {
    String name;
    String weapon;
    String role;
    int health;
    int range;
    boolean turn;
    boolean targeted;
    boolean banged;
    ArrayList<Card> hand;
    //ArrayList<Card> equipment;
    Player next;
    Player prev;

    public Player(String name, String role, int health) {
        this.name = name;
        this.role = role;
        this.weapon = "none";
        this.health = health;
        turn = false;
        range = 1;
        banged = false;
        targeted = false;
        hand = new ArrayList<Card>();
        //equipment = new ArrayList<Card>();
        next = null; // person sitting right of you
        prev = null; // person sitting left of you
    }

    public void setNext(Player next) {
        this.next = next;
    }

    public void setPrev(Player prev) {
        this.prev = prev;
    }

    public void discardWeapon() {
        range = 1;
        weapon = "none";
    }

    private int haveCard(String effect) {
        for (Card card : hand) {
            if (card.cardeffect().equals(effect)) {
                return hand.indexOf(card);
            }
        }
        return -1;
    }

    public int remaininghealth() {
        return health;
    }

    public boolean react() {
        return targeted;
    }

    public boolean go() {
        return turn;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void discard(Card card) {
        hand.remove(card);
    }

    public void discardIndex(int i) {
        hand.remove(i);
    }

    public String getName() {
        return name;
    }

    public int getHandSize() {
        return hand.size();
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public void swapturn() {
        if (turn) {
            turn = false;
        } else {
            turn = true;
        }
    }

    public String getRole() {
        return role;
    }

    public void takedamage() {
        if (targeted) {
            health--;
            targeted = false;
        }
    }

    public void modifyBanged(boolean didBang) {
        banged = didBang;
    }

    public int getRange() {
        return range;
    }

    public String getWeapon(){return weapon;}

    private void bang(Player other) {
        if (banged) {
            // modify to display this message (or disable bang button and ignore this)
            System.out.println("Can't bang");
            return;
        }

        Player right = next;
        Player left = prev;

        if (right == other || left == other) {
            other.targeted = true;
            banged = true;
        } else if ((right.next == other || left.prev == other) && range >= 2) {
            other.targeted = true;
            banged = true;
        } else {
            // Something happened
            System.out.println("Out of reach or the player does not exist");
        }

        System.out.println("(Debugging purposes) BANG BANG");
    }

    /*private void reactToBang(boolean playedmiss) {
        if (!targeted) {
            // unnecessary probably
            System.out.println("You're not being targeted");
            return;
        }
        if (!playedmiss) {
            health--;
        }
        targeted = false;
    }*/

    public void playCard(String effect, Player target) {//Card card, Player target) {
        int cardCheck = haveCard(effect);
        if (cardCheck > -1) {
            switch (effect) {
                case "bang":
                    if (!banged && !targeted) {
                        bang(target);
                        discardIndex(cardCheck);
                    }
                case "missed":
                    if (targeted) {
                        targeted = false;
                        discardIndex(cardCheck);
                    }
                case "beer":
                    health++;
                    discardIndex(cardCheck);
                case "schofield":
                    if (!targeted) {
                        if (weapon.equals("none")) {
                            range += 1;
                        } else if (weapon.equals("remington")) {
                            range -= 1;
                        }
                        weapon = "schofield";
                        discardIndex(cardCheck);
                    }
                case "remington":
                    if (!targeted) {
                        if (weapon.equals("none")) {
                            range += 2;
                        } else if (weapon.equals("schofield")) {
                            range += 1;
                        }
                        weapon = "remington";
                        discardIndex(cardCheck);
                    }
//            case "rev carabine":
//                range += 3;
//            case "winchester":
//                range += 4;
                default:
                    System.out.println("Unknown card played");
            }
        }
    }
}