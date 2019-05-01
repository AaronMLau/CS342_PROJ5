import java.util.ArrayList;
import java.util.Random;

public class Deck {
    private ArrayList<Card> cards;

    public Deck() {
        cards = new ArrayList<Card>();
    }

    public void populateDeck() {
        // 25 bangs
        for (int i = 0; i < 25; i++) {
            cards.add(new Card("bang"));
        }

        // 13 misses
        for (int i = 0; i < 13; i++) {
            cards.add(new Card("missed"));
        }

        // 3 schofields
        for (int i = 0; i < 3; i++) {
            cards.add(new Weapons("schofield"));
        }

        // 2 remingtons
        for (int i = 0; i < 2; i++) {
            cards.add(new Weapons("remington"));
        }

        // 1 rev carabine
        //cards.add(new Weapons("rev carabine"));

        // 1 winchester
        //cards.add(new Weapons("winchester"));

        // 4 mustangs
        for (int i = 0; i < 4; i++) {
            cards.add(new Card("mustang"));
        }

        // 4 scopes
        for (int i = 0; i < 4; i++) {
            cards.add(new Card("scope"));
        }

        // 6 beers
        for (int i = 0; i < 4; i++) {
            cards.add(new Card("beer"));
        }
    }

    public int getDeckSize() {
        return cards.size();
    }

    public Card draw() {
        Random rand = new Random();
        int pos = rand.nextInt(cards.size());
        return cards.remove(pos);
    }
}
