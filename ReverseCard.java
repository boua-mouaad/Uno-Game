public ReverseCard extends Card{

    public ReverseCard (CardColor color){
        super(color, CardType.REVERSE);
}
    @Override
    public boolean canPlayOn(Card topCard){
        if(this.color == CardColor.WILD || this.color == topCard.getColor()){
            return true;
        }
        if(topCard instanceof ReverseCard){
            return true;
        }
        return false;
    }
    @Override
    public String getSymbol(){
        return "R";
    }
}