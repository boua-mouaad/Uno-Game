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
        
        for (CardColor color : colors) {
            drawPile.add(new NumberCard(color, 0));
            
            for (int number = 1; number <= 9; number++) {
                drawPile.add(new NumberCard(color, number));
                drawPile.add(new NumberCard(color, number));
            }
            
            drawPile.add(new SkipCard(color));
            drawPile.add(new SkipCard(color));
            drawPile.add(new ReverseCard(color));
            drawPile.add(new ReverseCard(color));
            drawPile.add(new DrawTwoCard(color));
            drawPile.add(new DrawTwoCard(color));
        }
        
        for (int i = 0; i < 4; i++) {
            drawPile.add(new WildCard());
            drawPile.add(new WildDrawFourCard());
        }
        
        shuffle();
    }
    
    public void shuffle() {
        Collections.shuffle(drawPile);
    }
    
    public Card drawCard() {
        if (drawPile.isEmpty()) {
            return null;
        }
        return drawPile.remove(drawPile.size() - 1);
    }
    
    public List<Card> drawCards(int howMany) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < howMany; i++) {
            Card card = drawCard();
            if (card != null) {
                cards.add(card);
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