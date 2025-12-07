public abstract class Card{

    protected CardColor color;
    protected CardType type;

    public Card(CardColor color, CardType type){
        this.color = color;
        this.type = type;
    }
    public CardColor getColor(){
        return color;
    }
    public CardType getType(){
        return type;
    }
    public abstract String getSymbol();
    public abstract boolean canPlayOn(Card topCard);

    @Override
    public String toString(){
        if(color == CardColor.WILD){
            return getSymbol();
        }
        return color.name().charAt(0) + getSymbol();
    }

}