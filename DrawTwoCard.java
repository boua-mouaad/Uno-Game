public DrawtwoCard extends Card{
    public DrawtwoCard(CardColor color){
        super(color, Cardtype.DRAW_TWO);
    }

    @Override
    public boolean canPlayOn(Card topCard){
        if(this.color == CardColor.WILD || this.color == topCard.getColor()){
            return true;
        }
        if(topCard instanceof DrawtwoCard){
            return true;
        }
        return false;
    }
    
    @Override
    public String getSymbol(){
        return "+2";
    }
}