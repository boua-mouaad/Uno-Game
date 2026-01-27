public class Player {
    protected String name;
    protected Card[] hand;
    protected int handSize;
    protected boolean unoCalled;

    public Player(String name) {
        this.name = name;
        this.hand = new Card[100]; // Increased size for safety
        this.handSize = 0;
        this.unoCalled = false;
    }

    public void addCard(Card card) {
        hand[handSize] = card;
        handSize++;
        unoCalled = false;
    }

    public void addCards(Card[] cards) {
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] != null) {
                addCard(cards[i]);
            }
        }
    }

    public Card playCard(int index) {
        if (index < 0 || index >= handSize) {
            return null;
        }
        
        Card c = hand[index];

        for (int i = index; i < handSize - 1; i++) {
            hand[i] = hand[i + 1];
        }

        hand[handSize - 1] = null;
        handSize--;

        return c;
    }

    public Card[] getPlayableCards(Card topCard, CardColor chosenColor) {
        Card[] temp = new Card[handSize];
        int count = 0;

        for (int i = 0; i < handSize; i++) {
            if (hand[i].canPlayOn(topCard, chosenColor)) {
                temp[count] = hand[i];
                count++;
            }
        }

        Card[] result = new Card[count];
        for (int i = 0; i < count; i++) {
            result[i] = temp[i];
        }

        return result;
    }

    public boolean hasWon() {
        return handSize == 0;
    }

    public boolean hasUno() {
        return handSize == 1;
    }

    public void callUno() {
        unoCalled = true;
    }

    public String getName() {
        return name;
    }

    public int getHandSize() {
        return handSize;
    }

    public Card getCard(int index) {
        if (index < 0 || index >= handSize) {
            return null;
        }
        return hand[index];
    }

    public int findCardIndex(Card card) {
        for (int i = 0; i < handSize; i++) {
            if (hand[i] == card) {
                return i;
            }
        }

        return -1;
    }

    public void displayHand() {
        System.out.println("\nMain de " + name + " (" + handSize + " cartes):");

        for (int i = 0; i < handSize; i++) {
            System.out.println((i + 1) + ". " + hand[i]);
        }
    }
    
    public boolean hasDrawCardToStack(int pendingDrawCards) {
        for (int i = 0; i < handSize; i++) {
            Card card = hand[i];
            if (pendingDrawCards % 2 == 0 && card instanceof DrawTwoCard) {
                return true;
            }
            if (pendingDrawCards % 4 == 0 && card instanceof WildDrawFourCard) {
                return true;
            }
        }
        return false;
    }
}