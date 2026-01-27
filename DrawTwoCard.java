public class DrawTwoCard extends Card {
    public DrawTwoCard(CardColor color) {
        super(color, CardType.DRAW_TWO);
    }

    @Override
    public boolean canPlayOn(Card topCard, CardColor chosenColor) {
        CardColor activeColor = topCard.getColor() == CardColor.WILD ? chosenColor : topCard.getColor();

        if (this.color == activeColor) {
            return true;
        }

        // Can play on another DrawTwo OR on any card of same color
        return topCard instanceof DrawTwoCard;
    }

    @Override
    public String getSymbol() {
        return "+2";
    }

    @Override
    public void applyEffect(Game game) {
        game.addPendingDrawCards(2);
        System.out.println("Le prochain joueur pioche 2 cartes.");
    }
}