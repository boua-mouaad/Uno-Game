public class Deck {
    private Card[] drawPile;
    private int drawPileSize;

    private Card[] discardPile;
    private int discardPileSize;

    public Deck() {
        drawPile = new Card[108];
        discardPile = new Card[108];
        drawPileSize = 0;
        discardPileSize = 0;
        createDeck();
    }

    private void createDeck() {
        CardColor[] colors = {CardColor.RED, CardColor.YELLOW, CardColor.GREEN, CardColor.BLUE};

        for (int i = 0; i < colors.length; i++) {
            CardColor color = colors[i];

            drawPile[drawPileSize++] = new NumberCard(color, 0);

            for (int n = 1; n <= 9; n++) {
                drawPile[drawPileSize++] = new NumberCard(color, n);
                drawPile[drawPileSize++] = new NumberCard(color, n);
            }

            for (int k = 0; k < 2; k++) {
                drawPile[drawPileSize++] = new SkipCard(color);
                drawPile[drawPileSize++] = new ReverseCard(color);
                drawPile[drawPileSize++] = new DrawTwoCard(color);
            }
        }

        for (int i = 0; i < 4; i++) {
            drawPile[drawPileSize++] = new WildCard();
            drawPile[drawPileSize++] = new WildDrawFourCard();
        }

        shuffle();
    }

    private void shuffle() {
        for (int i = drawPileSize - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));

            Card tmp = drawPile[i];
            drawPile[i] = drawPile[j];
            drawPile[j] = tmp;
        }
    }

    public Card drawCard() {
        if (drawPileSize == 0) {
            reshuffleFromDiscard();
        }

        if (drawPileSize == 0) {
            return null;
        }

        drawPileSize--;
        Card c = drawPile[drawPileSize];
        drawPile[drawPileSize] = null;
        return c;
    }

    public Card[] drawCards(int n) {
        Card[] res = new Card[n];

        for (int i = 0; i < n; i++) {
            res[i] = drawCard();
        }

        return res;
    }

    public void addToDiscardPile(Card c) {
        discardPile[discardPileSize] = c;
        discardPileSize++;
    }

    public Card getTopDiscardCard() {
        if (discardPileSize == 0) return null;
        return discardPile[discardPileSize - 1];
    }

    private void reshuffleFromDiscard() {
        if (discardPileSize <= 1) return;

        Card top = discardPile[discardPileSize - 1];
        discardPileSize--;

        for (int i = 0; i < discardPileSize; i++) {
            drawPile[drawPileSize] = discardPile[i];
            drawPileSize++;
            discardPile[i] = null;
        }

        discardPileSize = 0;
        shuffle();

        discardPile[discardPileSize] = top;
        discardPileSize++;
    }
    
    public int getDrawPileSize() {
        return drawPileSize;
    }
}