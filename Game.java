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
    private CardColor chosenColor;
    private int pendingDrawCards; // For accumulating draw cards
    private boolean skipNextPlayer;
    private boolean drawCardPlayed;
    
    public Game() {
        deck = new Deck();
        players = new ArrayList<>();
        scanner = new Scanner(System.in);
        reverseDirection = false;
        gameOver = false;
        currentPlayerIndex = 0;
        chosenColor = null;
        pendingDrawCards = 0;
        skipNextPlayer = false;
        drawCardPlayed = false;
    }
    
    public void setupGame() {
        System.out.println("=== UNO GAME ===\n");
        
        // Ask for number of players (2 to 10)
        int numberOfPlayers = 0;
        while (numberOfPlayers < 2 || numberOfPlayers > 10) {
            System.out.print("How many players? (2-10): ");
            numberOfPlayers = scanner.nextInt();
            scanner.nextLine(); // Clear buffer
            
            if (numberOfPlayers < 2 || numberOfPlayers > 10) {
                System.out.println("Invalid number. Choose between 2 and 10 players.");
            }
        }
        
        // Create players
        for (int i = 1; i <= numberOfPlayers; i++) {
            System.out.print("Name of Player " + i + ": ");
            String playerName = scanner.nextLine();
            if (playerName.trim().isEmpty()) {
                playerName = "Player " + i;
            }
            players.add(new Player(playerName));
            System.out.println(playerName + " has been added.");
        }
        
        System.out.println("\n" + players.size() + " players are ready to play!");
        
        // Deal 7 cards to each player
        for (Player player : players) {
            List<Card> initialCards = deck.drawCards(7);
            player.addCards(initialCards);
            System.out.println(player.getName() + " receives 7 cards.");
        }
        
        // Draw first card (cannot be a WILD special card)
        Card firstCard;
        do {
            firstCard = deck.drawCard();
            if (firstCard == null) {
                System.out.println("Error: not enough cards in the deck!");
                System.exit(1);
            }
        } while (firstCard.getColor() == CardColor.WILD || 
                 firstCard.getType() == CardType.DRAW_TWO ||
                 firstCard.getType() == CardType.WILD_DRAW_FOUR);
        
        deck.addToDiscardPile(firstCard);
        chosenColor = firstCard.getColor();
        System.out.println("\nFirst card: " + firstCard);
    }
    
    public void startGame() {
        System.out.println("\n=== GAME START ===\n");
        
        while (!gameOver) {
            playTurn();
            checkWinCondition();
            if (!gameOver && !drawCardPlayed) {
                nextPlayer();
            }
            drawCardPlayed = false; // Reset for next turn
        }
        
        endGame();
    }
    
    private void playTurn() {
        Player currentPlayer = players.get(currentPlayerIndex);
        Card topCard = deck.getTopDiscardCard();
        
        if (topCard == null) {
            System.out.println("Error: no card on discard pile!");
            System.exit(1);
        }
        
        System.out.println("\n=== " + currentPlayer.getName().toUpperCase() + "'S TURN ===");
        System.out.println("Top card: " + topCard);
        
        // Show current color (especially after a Wild card)
        if (chosenColor != null && topCard.getColor() == CardColor.WILD) {
            System.out.println("Current color: " + chosenColor.name());
        }
        
        // Check if player should be skipped
        if (skipNextPlayer) {
            System.out.println(currentPlayer.getName() + " is skipped!");
            skipNextPlayer = false;
            return;
        }
        
        currentPlayer.displayHand();
        
        List<Card> playableCards = getPlayableCardsForPlayer(currentPlayer, topCard);
        
        // If there are pending draw cards, check if player can stack
        if (pendingDrawCards > 0) {
            List<Card> stackableCards = getStackableCards(currentPlayer);
            if (!stackableCards.isEmpty()) {
                System.out.println("\n⚠️  There are " + pendingDrawCards + " cards to draw!");
                System.out.println("You can stack these cards to pass the penalty:");
                for (int i = 0; i < stackableCards.size(); i++) {
                    System.out.println((i + 1) + ". " + stackableCards.get(i));
                }
                System.out.print("Play a stacking card? (0 to draw, 1-" + stackableCards.size() + " to play): ");
                int choice = scanner.nextInt();
                
                if (choice > 0 && choice <= stackableCards.size()) {
                    Card chosenCard = stackableCards.get(choice - 1);
                    int cardIndexInHand = currentPlayer.getHand().indexOf(chosenCard);
                    if (cardIndexInHand != -1) {
                        Card playedCard = currentPlayer.playCard(cardIndexInHand);
                        deck.addToDiscardPile(playedCard);
                        System.out.println("\n" + currentPlayer.getName() + " plays: " + playedCard);
                        
                        // Add to pending draw cards (STACKING!)
                        if (playedCard.getType() == CardType.DRAW_TWO) {
                            pendingDrawCards += 2;
                        } else if (playedCard.getType() == CardType.WILD_DRAW_FOUR) {
                            pendingDrawCards += 4;
                        }
                        
                        System.out.println("Penalty increased to " + pendingDrawCards + " cards!");
                        
                        // If it's a Wild card, ask for a color
                        if (playedCard.getColor() == CardColor.WILD) {
                            chooseColor(currentPlayer);
                        } else {
                            chosenColor = playedCard.getColor();
                        }
                        
                        // Check for UNO
                        if (currentPlayer.hasUno()) {
                            System.out.print("Do you want to say UNO? (y/n): ");
                            scanner.nextLine(); // Clear buffer
                            String answer = scanner.nextLine();
                            if (answer.equalsIgnoreCase("y")) {
                                currentPlayer.callUno();
                                System.out.println("UNO!");
                            }
                        }
                        return;
                    }
                }
            }
            
            // Player must draw the accumulated cards
            System.out.println("\n" + currentPlayer.getName() + " must draw " + pendingDrawCards + " cards!");
            List<Card> cards = deck.drawCards(pendingDrawCards);
            currentPlayer.addCards(cards);
            System.out.println(currentPlayer.getName() + " draws " + cards.size() + " cards.");
            pendingDrawCards = 0;
            System.out.println(currentPlayer.getName() + " loses their turn.");
            return;
        }
        
        // Normal play
        if (!playableCards.isEmpty()) {
            playCardFromHand(currentPlayer, playableCards, topCard);
        } else {
            System.out.println("\nNo playable cards. You must draw.");
            drawCardForPlayer(currentPlayer);
        }
        
        if (currentPlayer.hasUno() && !currentPlayer.hasCalledUno()) {
            System.out.println("WARNING! You have only one card! Don't forget to say UNO!");
        }
    }
    
    private List<Card> getStackableCards(Player player) {
        List<Card> stackableCards = new ArrayList<>();
        
        // With house rules: +2 can stack on +2, +4 can stack on +4, +2 can stack on +4, etc.
        for (Card card : player.getHand()) {
            if (card.getType() == CardType.DRAW_TWO || card.getType() == CardType.WILD_DRAW_FOUR) {
                stackableCards.add(card);
            }
        }
        
        return stackableCards;
    }
    
    private List<Card> getPlayableCardsForPlayer(Player player, Card topCard) {
        List<Card> playableCards = new ArrayList<>();
        
        // If player is skipped, they cannot play
        if (skipNextPlayer) {
            return playableCards;
        }
        
        for (Card card : player.getHand()) {
            if (canCardBePlayed(card, topCard)) {
                playableCards.add(card);
            }
        }
        
        return playableCards;
    }
    
    private boolean canCardBePlayed(Card card, Card topCard) {
        // If top card is Wild, check against chosen color
        if (topCard.getColor() == CardColor.WILD) {
            // Wild cards can always be played
            if (card.getColor() == CardColor.WILD) {
                return true;
            }
            // Check if card matches chosen color
            return card.getColor() == chosenColor;
        } else {
            // Normal card validation
            return card.canPlayOn(topCard);
        }
    }
    
    private void playCardFromHand(Player player, List<Card> playableCards, Card topCard) {
        System.out.println("\nYou can play these cards:");
        
        for (int i = 0; i < playableCards.size(); i++) {
            System.out.println((i + 1) + ". " + playableCards.get(i));
        }
        
        System.out.print("\nChoose a card (1-" + playableCards.size() + ") or enter 0 to draw: ");
        int choice = scanner.nextInt();
        
        if (choice == 0) {
            drawCardForPlayer(player);
        } else if (choice >= 1 && choice <= playableCards.size()) {
            Card chosenCard = playableCards.get(choice - 1);
            int cardIndexInHand = player.getHand().indexOf(chosenCard);
            
            if (cardIndexInHand != -1) {
                Card playedCard = player.playCard(cardIndexInHand);
                deck.addToDiscardPile(playedCard);
                
                System.out.println("\n" + player.getName() + " plays: " + playedCard);
                
                // If it's a Wild card, ask for a color
                if (playedCard.getColor() == CardColor.WILD) {
                    chooseColor(player);
                } else {
                    chosenColor = playedCard.getColor();
                }
                
                applyCardEffect(playedCard);
                
                if (player.hasUno()) {
                    System.out.print("Do you want to say UNO? (y/n): ");
                    scanner.nextLine(); // Clear buffer
                    String answer = scanner.nextLine();
                    if (answer.equalsIgnoreCase("y")) {
                        player.callUno();
                        System.out.println("UNO!");
                    }
                }
            } else {
                System.out.println("Error: card not found in hand.");
            }
        } else {
            System.out.println("Invalid choice. You lose your turn.");
        }
    }
    
    private void chooseColor(Player player) {
        System.out.println("\n" + player.getName() + ", choose a color:");
        System.out.println("1. RED");
        System.out.println("2. BLUE");
        System.out.println("3. GREEN");
        System.out.println("4. YELLOW");
        System.out.print("Your choice (1-4): ");
        
        int colorChoice = scanner.nextInt();
        scanner.nextLine(); // Clear buffer
        
        switch (colorChoice) {
            case 1:
                chosenColor = CardColor.RED;
                System.out.println("Color chosen: RED");
                break;
            case 2:
                chosenColor = CardColor.BLUE;
                System.out.println("Color chosen: BLUE");
                break;
            case 3:
                chosenColor = CardColor.GREEN;
                System.out.println("Color chosen: GREEN");
                break;
            case 4:
                chosenColor = CardColor.YELLOW;
                System.out.println("Color chosen: YELLOW");
                break;
            default:
                chosenColor = CardColor.RED;
                System.out.println("Invalid choice. Default color: RED");
                break;
        }
    }
    
    private void drawCardForPlayer(Player player) {
        System.out.println("\n" + player.getName() + " draws a card...");
        Card drawnCard = deck.drawCard();
        if (drawnCard != null) {
            player.addCard(drawnCard);
            System.out.println(player.getName() + " draws: " + drawnCard);
            
            // Clear buffer before asking for response
            scanner.nextLine();
            
            // Check if the drawn card can be played immediately
            Card topCard = deck.getTopDiscardCard();
            if (topCard != null && pendingDrawCards == 0 && !skipNextPlayer) {
                // Check if drawn card can be played
                boolean canPlay = canCardBePlayed(drawnCard, topCard);
                
                if (canPlay) {
                    System.out.print("You can play this card immediately! Do you want to play it? (y/n): ");
                    String answer = scanner.nextLine();
                    if (answer.equalsIgnoreCase("y")) {
                        // Find the card index in hand
                        List<Card> hand = player.getHand();
                        int cardIndex = hand.size() - 1; // Last card added
                        Card playedCard = player.playCard(cardIndex);
                        deck.addToDiscardPile(playedCard);
                        System.out.println(player.getName() + " plays: " + playedCard);
                        
                        // If it's a Wild card, ask for a color
                        if (playedCard.getColor() == CardColor.WILD) {
                            chooseColor(player);
                        } else {
                            chosenColor = playedCard.getColor();
                        }
                        
                        applyCardEffect(playedCard);
                        drawCardPlayed = true; // Don't advance to next player
                        
                        // Check for UNO
                        if (player.hasUno()) {
                            System.out.print("Do you want to say UNO? (y/n): ");
                            String unoAnswer = scanner.nextLine();
                            if (unoAnswer.equalsIgnoreCase("y")) {
                                player.callUno();
                                System.out.println("UNO!");
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("No more cards in the draw pile!");
        }
    }
    
    private void applyCardEffect(Card card) {
        if (card.getType() == CardType.SKIP) {
            System.out.println("EFFECT: Next player is skipped!");
            skipNextPlayer = true;
        } else if (card.getType() == CardType.REVERSE) {
            System.out.println("EFFECT: Game direction changes!");
            reverseDirection = !reverseDirection;
            
            // With only 2 players, Reverse acts as a Skip
            if (players.size() == 2) {
                System.out.println("(With 2 players, Reverse = Skip)");
                skipNextPlayer = true;
            }
        } else if (card.getType() == CardType.DRAW_TWO) {
            System.out.println("EFFECT: Next player draws 2 cards (can be stacked)!");
            
            // HOUSE RULES: +2 can be stacked
            pendingDrawCards += 2;
        } else if (card.getType() == CardType.WILD_DRAW_FOUR) {
            System.out.println("EFFECT: Next player draws 4 cards (can be stacked)!");
            
            // HOUSE RULES: +4 can be stacked, +2 can stack on +4, etc.
            pendingDrawCards += 4;
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
            System.out.println("\n🎉🎉🎉 " + currentPlayer.getName() + " WINS ! 🎉🎉🎉");
        }
    }
    
    private void endGame() {
        System.out.println("\n=== GAME OVER ===");
        
        // Show remaining cards
        System.out.println("\nRemaining cards:");
        for (Player player : players) {
            if (!player.hasWon()) {
                System.out.println(player.getName() + ": " + player.getHandSize() + " cards");
            }
        }
        
        System.out.println("\nThank you for playing UNO!");
        scanner.close();
    }
}