public class WildCard extends Card{

    public WildCard(){
        super(CardColor.WILD, CardType.WILD);
    }

    @Override
    public boolean canPlayOn(Card topCard){
        return true;
    }

    @Override
    public String getSymbol(){
        return "W";
    }
    
}