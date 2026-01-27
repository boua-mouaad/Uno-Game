public class WildCard extends Card {
    public WildCard() {
        super(CardColor.WILD, CardType.WILD);
    }

    @Override
    public boolean canPlayOn(Card topCard, CardColor chosenColor) {
        // Wild cards can be played anytime
        return true;
    }

    @Override
    public String getSymbol() {
        return "W";
    }

    @Override
    public void applyEffect(Game game) {
        // Wild card has no draw effect, just color change
    }
}