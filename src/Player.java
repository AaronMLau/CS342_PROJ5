import java.util.ArrayList;

public class Player {
    String name;
    String role;
    int health;
    int range;
    boolean targeted;
    boolean banged;
    boolean mustang;
    ArrayList<Card> hand;
    //ArrayList<Card> equipment;
    Player next;
    Player prev;

    public Player(String name, String role, int health) {
        this.name = name;
        this.role = role;
        this.health = health;
        range = 1;
        banged = false;
        targeted = false;
        mustang = false;
        hand = new ArrayList<Card>();
        //equipment = new ArrayList<Card>();
        next = null;
        prev = null;
    }

    public void setNext(Player next) {
        this.next = next;
    }

    public void setPrev(Player prev) {
        this.prev = prev;
    }

    public String getRole() {
        return role;
    }

    public boolean hasmustang() {
        return mustang;
    }

    private void losehealth() {
        health--;
    }

    private void bang(Player other) {
        banged = true;
        Player right = next;
        Player left = prev;

        if (other.hasmustang()) {
            if ((right == other || left == other) && range >= 2) {
                other.targeted = true;
            } else if ((right.next == other || left.prev == other) && range >= 3) {
                other.targeted = true;
            }
        } else {
            if (right == other || left == other) {
                other.targeted = true;
            } else if ((right.next == other || left.prev == other) && range >= 2) {
                other.targeted = true;
            }
        }
    }

    public void playCard(Card card, Player target) {
        switch (card.cardeffect()) {
            case "bang":
                bang(target);
            case "missed":
                targeted = false;
            case "beer":
                health++;
            case "mustang":
                mustang = true;
            case "schofield":
                range = 2;
            case "remington":
                range = 3;
            case "rev carabine":
                range = 4;
            case "winchester":
                range = 5;
            default:
                System.out.println("Unknown card played");
        }
    }
}
