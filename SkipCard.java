public class SkipCard extends Card {
    public SkipCard(CardColor color) {
        super(color, CardType.SKIP);
    }

    @Override
    public boolean canPlayOn(Card topCard, CardColor chosenColor) {
        CardColor activeColor = topCard.getColor() == CardColor.WILD ? chosenColor : topCard.getColor();

        if (this.color == activeColor) {
            return true;
        }

        // Can play on another Skip card OR on any card of same color
        return topCard instanceof SkipCard;
    }

    @Override
    public String getSymbol() {
        return "S";
    }

    @Override
    public void applyEffect(Game game) {
        game.skipNextPlayer();
        System.out.println("Le prochain joueur est sauté.");
    }
}