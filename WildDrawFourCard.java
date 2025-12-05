public WildDrawFourCard extends Card{

    public WildDrawFourCard(){
        super(CardColor.WILD, CardType.WILD_DRAW_FOUR);
    }

    @Override
    public boolean canPlayOn(Card topCard){
        return true;
    }

    @Override
    public String getSymbol(){
        return "+4";
    }
}