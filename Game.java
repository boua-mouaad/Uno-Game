import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {
    private Deck deck;
    private List<Player> players;
    private int currentPlayerIndex;
    private boolean reverseDirection;
    private Scanner scanner;
    private boolean gameOver;
    
    public Game() {
        deck = new Deck();
        players = new ArrayList<>();
        scanner = new Scanner(System.in);
        reverseDirection = false;
        gameOver = false;
        currentPlayerIndex = 0;
    }
    
    public void setupGame() {
        System.out.println("=== JEU UNO ===\n");
        
        players.add(new Player("Joueur 1"));
        players.add(new Player("Joueur 2"));
        
        System.out.println("Joueurs créés: " + players.get(0).getName() + " et " + players.get(1).getName());
        
        for (Player player : players) {
            List<Card> initialCards = deck.drawCards(7);
            player.addCards(initialCards);
            System.out.println(player.getName() + " reçoit 7 cartes.");
        }
        
        Card firstCard;
        do {
            firstCard = deck.drawCard();
        } while (firstCard.getColor() == CardColor.WILD);
        
        deck.addToDiscardPile(firstCard);
        System.out.println("\nPremière carte: " + firstCard);
    }
    
    public void startGame() {
        System.out.println("\n=== DEBUT DU JEU ===\n");
        
        while (!gameOver) {
            playTurn();
            checkWinCondition();
            nextPlayer();
        }
        
        endGame();
    }
    
    private void playTurn() {
        Player currentPlayer = players.get(currentPlayerIndex);
        Card topCard = deck.getTopDiscardCard();
        
        System.out.println("\n=== TOUR DE " + currentPlayer.getName().toUpperCase() + " ===");
        System.out.println("Carte du dessus: " + topCard);
        
        currentPlayer.displayHand();
        
        List<Card> playableCards = currentPlayer.getPlayableCards(topCard);
        
        if (!playableCards.isEmpty()) {
            playCardFromHand(currentPlayer, playableCards);
        } else {
            drawCardForPlayer(currentPlayer);
        }
        
        if (currentPlayer.hasUno() && !currentPlayer.hasCalledUno()) {
            System.out.println("ATTENTION! Tu as une seule carte! N'oublie pas de dire UNO!");
        }
    }
    
    private void playCardFromHand(Player player, List<Card> playableCards) {
        System.out.println("\nTu peux jouer ces cartes:");
        
        for (int i = 0; i < playableCards.size(); i++) {
            System.out.println((i + 1) + ". " + playableCards.get(i));
        }
        
        System.out.print("\nChoisis une carte (1-" + playableCards.size() + ") ou tape 0 pour piocher: ");
        int choice = scanner.nextInt();
        
        if (choice == 0) {
            drawCardForPlayer(player);
        } else if (choice >= 1 && choice <= playableCards.size()) {
            Card chosenCard = playableCards.get(choice - 1);
            int cardIndexInHand = player.getHand().indexOf(chosenCard);
            Card playedCard = player.playCard(cardIndexInHand);
            deck.addToDiscardPile(playedCard);
            
            System.out.println("\n" + player.getName() + " joue: " + playedCard);
            applyCardEffect(playedCard);
            
            if (player.hasUno()) {
                System.out.print("Veux-tu dire UNO? (o/n): ");
                String answer = scanner.next();
                if (answer.equalsIgnoreCase("o")) {
                    player.callUno();
                    System.out.println("UNO!");
                }
            }
        } else {
            System.out.println("Choix invalide. Tu passes ton tour.");
        }
    }
    
    private void drawCardForPlayer(Player player) {
        System.out.println("\n" + player.getName() + " pioche une carte...");
        Card drawnCard = deck.drawCard();
        if (drawnCard != null) {
            player.addCard(drawnCard);
            System.out.println(player.getName() + " pioche: " + drawnCard);
        } else {
            System.out.println("La pioche est vide!");
        }
    }
    
    private void applyCardEffect(Card card) {
        if (card.getType() == CardType.SKIP) {
            System.out.println("EFFET: Le prochain joueur passe son tour!");
            nextPlayer();
        } else if (card.getType() == CardType.REVERSE) {
            System.out.println("EFFET: Le sens du jeu change!");
            reverseDirection = !reverseDirection;
        } else if (card.getType() == CardType.DRAW_TWO) {
            System.out.println("EFFET: Le prochain joueur pioche 2 cartes!");
            nextPlayer();
            Player nextPlayer = players.get(currentPlayerIndex);
            nextPlayer.addCards(deck.drawCards(2));
        } else if (card.getType() == CardType.WILD_DRAW_FOUR) {
            System.out.println("EFFET: Le prochain joueur pioche 4 cartes!");
            nextPlayer();
            Player nextPlayer = players.get(currentPlayerIndex);
            nextPlayer.addCards(deck.drawCards(4));
        }
    }
    
    private void nextPlayer() {
        if (reverseDirection) {
            currentPlayerIndex--;
            if (currentPlayerIndex < 0) {
                currentPlayerIndex = players.size() - 1;
            }
        } else {
            currentPlayerIndex++;
            if (currentPlayerIndex >= players.size()) {
                currentPlayerIndex = 0;
            }
        }
    }
    
    private void checkWinCondition() {
        Player currentPlayer = players.get(currentPlayerIndex);
        if (currentPlayer.hasWon()) {
            gameOver = true;
            System.out.println("\n🎉🎉🎉 " + currentPlayer.getName() + " A GAGNE ! 🎉🎉🎉");
        }
    }
    
    private void endGame() {
        System.out.println("\n=== FIN DU JEU ===");
        System.out.println("Merci d'avoir joué à UNO!");
        scanner.close();
    }
    
    public static void main(String[] args) {
        Game unoGame = new Game();
        unoGame.setupGame();
        unoGame.startGame();
    }
}