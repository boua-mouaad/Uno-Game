public Wild extends Card{

    public Wild(){
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