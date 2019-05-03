import java.util.ArrayList;

public class Player {
    String name;
    String role;
    int health;
    int range;
    boolean turn;
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
        turn = false;
        range = 1;
        banged = false;
        targeted = false;
        mustang = false;
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

    public int remaininghealth() {
        return health;
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

    public boolean hasmustang() {
        return mustang;
    }

    private void losehealth() {
        health--;
    }

    public void modifyBanged(boolean didBang) {
        banged = didBang;
    }

    private void bang(Player other) {
        if (banged) {
            // modify to display this message (or disable bang button and ignore this)
            System.out.println("Can't bang");
            return;
        }
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

    private void reactToBang(boolean playedmiss) {
        if (!targeted) {
            // unnecessary probably
            System.out.println("You're not being targeted");
            return;
        }
        if (!playedmiss) {
            health--;
        }
    }

    public void playCard(Card card, Player target) {
        switch (card.cardeffect()) {
            case "bang":
                bang(target);
            case "missed":
                reactToBang(true);
            case "beer":
                health++;
            case "mustang":
                mustang = true;
            case "scope":
                range += 1;
            case "schofield":
                range += 1;
            case "remington":
                range += 2;
//            case "rev carabine":
//                range += 3;
//            case "winchester":
//                range += 4;
            default:
                System.out.println("Unknown card played");
        }
    }
}

/*
Bang! pseduo code:

 - Global enum whosTurn

Player{
method turn{ //Things you can do on a turn
 - endTurn()
 - viewCards()
 - viewRole()
 - viewHealth()
 - viewRange()
 - viewEnemyHealth(right or left)
 - playCard(Card c)
 - addHealth()
 - die()
}

method shootResponse{ //when someone shoots you
 - PlayCard(Card c - Miss)
 - LoseHealth(amount)
}

method playCard{
 - bang(target) -> shootResponse(target)
	- error check range
 - beer() -> addHealth(this player)
 - miss(target Shooter) ->

 *- removeCard(Card c, this player)
}
}
*/