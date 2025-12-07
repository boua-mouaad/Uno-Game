public class DrawTwoCard extends Card{
    public DrawTwoCard(CardColor color){
        super(color, CardType.DRAW_TWO);
    }

    @Override
    public boolean canPlayOn(Card topCard){
        if(this.color == CardColor.WILD || this.color == topCard.getColor()){
            return true;
        }
        if(topCard instanceof DrawTwoCard){
            return true;
        }
        return false;
    }
    
    @Override
    public String getSymbol(){
        return "+2";
    }
}