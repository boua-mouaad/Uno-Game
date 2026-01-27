public class ReverseCard extends Card {
    public ReverseCard(CardColor color) {
        super(color, CardType.REVERSE);
    }

    @Override
    public boolean canPlayOn(Card topCard, CardColor chosenColor) {
        CardColor activeColor = topCard.getColor() == CardColor.WILD ? chosenColor : topCard.getColor();

        if (this.color == activeColor) {
            return true;
        }

        // Can play on another Reverse card OR on any card of same color
        return topCard instanceof ReverseCard;
    }

    @Override
    public String getSymbol() {
        return "R";
    }

    @Override
    public void applyEffect(Game game) {
        game.reverseDirection();

        if (game.getPlayerCount() == 2) {
            // In 2-player game, reverse acts like skip
            game.skipNextPlayer();
        }
    }
}