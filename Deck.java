import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> drawPile;
    private List<Card> discardPile;
    
    public Deck() {
        drawPile = new ArrayList<>();
        discardPile = new ArrayList<>();
        createDeck();
    }
    
    private void createDeck() {
        CardColor[] colors = {CardColor.RED, CardColor.YELLOW, CardColor.GREEN, CardColor.BLUE};
        
        // Number cards
        for (CardColor color : colors) {
            // One zero per color
            drawPile.add(new NumberCard(color, 0));
            
            // Two of each 1-9 per color
            for (int number = 1; number <= 9; number++) {
                drawPile.add(new NumberCard(color, number));
                drawPile.add(new NumberCard(color, number));
            }
            
            // Two of each action card per color
            drawPile.add(new SkipCard(color));
            drawPile.add(new SkipCard(color));
            drawPile.add(new ReverseCard(color));
            drawPile.add(new ReverseCard(color));
            drawPile.add(new DrawTwoCard(color));
            drawPile.add(new DrawTwoCard(color));
        }
        
        // Wild cards (4 of each)
        for (int i = 0; i < 4; i++) {
            drawPile.add(new WildCard());
            drawPile.add(new WildDrawFourCard());
        }
        
        shuffle();
        System.out.println("A deck of " + drawPile.size() + " cards has been created and shuffled.");
    }
    
    public void shuffle() {
        Collections.shuffle(drawPile);
    }
    
    public Card drawCard() {
        // If draw pile is empty, recycle discarded cards
        if (drawPile.isEmpty()) {
            if (discardPile.size() > 1) {
                Card topCard = discardPile.remove(discardPile.size() - 1);
                drawPile.addAll(discardPile);
                discardPile.clear();
                discardPile.add(topCard);
                shuffle();
                System.out.println("Draw pile has been recycled and shuffled (" + drawPile.size() + " cards).");
            } else {
                return null;
            }
        }
        return drawPile.remove(drawPile.size() - 1);
    }
    
    public List<Card> drawCards(int howMany) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < howMany; i++) {
            Card card = drawCard();
            if (card != null) {
                cards.add(card);
            } else {
                System.out.println("Warning: Not enough cards in the draw pile!");
                break;
            }
        }
        return cards;
    }
    
    public void addToDiscardPile(Card card) {
        discardPile.add(card);
    }
    
    public Card getTopDiscardCard() {
        if (discardPile.isEmpty()) {
            return null;
        }
        return discardPile.get(discardPile.size() - 1);
    }
    
    public int getCardsRemaining() {
        return drawPile.size();
    }
}