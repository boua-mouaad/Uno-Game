public abstract class Card{

        
    public enum CardColor{
        RED, BLUE, GREEN, YELLOW, WILD
    }
    public enum Cardtype{
        NUMBER, SKIP, REVERSE, DRAW_TWO, WILD, WILD_DRAW_FOUR
    }




    protected CardColor color;
    protected Cardtype type;

    public Card(CardColor color, Cardtype type){
        this.color = color;
        this.type = type;
    }
    public CardColor getColor(){
        return color;
    }
    public Cardtype getType(){
        return type;
    }
    public abstract boolean canPlayOn(Card topCard);

    @Override
    public String toString(){
        if(color == CardColor.WILD){
            return getSymbol();
        }
        return color.name().charAt(0) + getSymbol();
    }
}