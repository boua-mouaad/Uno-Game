import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Card> hand;
    private boolean unoCalled;
    
    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.unoCalled = false;
    }
    
    public void addCard(Card card) {
        hand.add(card);
        unoCalled = false;
    }
    
    public void addCards(List<Card> cards) {
        hand.addAll(cards);
        unoCalled = false;
    }
    
    public Card playCard(int cardIndex) {
        if (cardIndex >= 0 && cardIndex < hand.size()) {
            return hand.remove(cardIndex);
        }
        return null;
    }
    
    public List<Card> getPlayableCards(Card topCard) {
        List<Card> playableCards = new ArrayList<>();
        for (Card card : hand) {
            if (card.canPlayOn(topCard)) {
                playableCards.add(card);
            }
        }
        return playableCards;
    }
    
    public void callUno() {
        if (hand.size() == 1) {
            unoCalled = true;
        }
    }
    
    public boolean hasWon() {
        return hand.isEmpty();
    }
    
    public boolean hasUno() {
        return hand.size() == 1;
    }
    
    public boolean hasCalledUno() {
        return unoCalled;
    }
    
    public String getName() {
        return name;
    }
    
    public List<Card> getHand() {
        return hand;
    }
    
    public int getHandSize() {
        return hand.size();
    }
    
    public void displayHand() {
        System.out.println("\nMain de " + name + " (" + hand.size() + " cartes):");
        for (int i = 0; i < hand.size(); i++) {
            System.out.println((i + 1) + ". " + hand.get(i));
        }
    }
}