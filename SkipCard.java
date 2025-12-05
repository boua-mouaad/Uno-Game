public SkipCard extends Card{

    public SkipCard(Cardcolor color){
        super(color, CardType.SKIP);
        this.color = color;
    }
    @Override
    public booleaen canPlayOn(Card topCard){
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