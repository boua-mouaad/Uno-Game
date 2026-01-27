public class WildDrawFourCard extends Card {
    public WildDrawFourCard() {
        super(CardColor.WILD, CardType.WILD_DRAW_FOUR);
    }

    @Override
    public boolean canPlayOn(Card topCard, CardColor chosenColor) {
        // Wild Draw Four can be played anytime (no restrictions)
        return true;
    }

    @Override
    public String getSymbol() {
        return "+4";
    }

    @Override
    public void applyEffect(Game game) {
        game.addPendingDrawCards(4);
        System.out.println("Le prochain joueur pioche 4 cartes.");
    }
}