public class NumberCard extends Card{
    private int number;

    public NumberCard(CardColor color, int number){
        super(color, CardType.NUMBER);
        this.number = number;
    }
    public int getNumber(){
        return number;
    }
    @Override
    public boolean canPlayOn(Card topCard){
        if(this.color == CardColor.WILD || this.color == topCard.getColor()){
            return true;
        }
        if(topCard instanceof NumberCard){
            NumberCard topNumberCard = (NumberCard) topCard;
            return this.number == topNumberCard.getNumber();
        }
        return false;
    }
    @Override
    public String getSymbol() {
        return String.valueOf(number);
    }

}