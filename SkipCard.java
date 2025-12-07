public class SkipCard extends Card{

    public SkipCard(CardColor color){
        super(color, CardType.SKIP);
        this.color = color;
    }
    @Override
    public boolean canPlayOn(Card topCard){
        if(this.color == CardColor.WILD || this.color == topCard.getColor()){
            return true;
        }
        if(topCard instanceof SkipCard){
            return true;
        }
        return false;
    }
    @Override
    public String getSymbol(){
        return "S";
}
}