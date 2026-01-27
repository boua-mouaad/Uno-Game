public class NumberCard extends Card {
    private int number;

    public NumberCard(CardColor color, int number) {
        super(color, CardType.NUMBER);
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public boolean canPlayOn(Card topCard, CardColor chosenColor) {
        CardColor activeColor = topCard.getColor() == CardColor.WILD ? chosenColor : topCard.getColor();

        if (this.color == activeColor) {
            return true;
        }

        if (topCard instanceof NumberCard) {
            NumberCard n = (NumberCard) topCard;
            return this.number == n.number;
        }

        return false;
    }

    @Override
    public String getSymbol() {
        return String.valueOf(number);
    }

    @Override
    public void applyEffect(Game game) {
        // Number cards have no effect
    }
}