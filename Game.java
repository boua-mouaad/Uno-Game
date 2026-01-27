import java.util.Scanner;

public class Game {
    private Deck deck;
    private Player[] players;
    private int playerCount;

    private int currentPlayerIndex;
    private boolean reverseDirection;
    private boolean skipNextPlayer;
    private int pendingDrawCards;

    private CardColor chosenColor;
    private boolean gameOver;

    private Scanner scanner;

    public Game() {
        deck = new Deck();
        players = new Player[10];  // Changed to support up to 10 players
        playerCount = 0;
        currentPlayerIndex = 0;
        reverseDirection = false;
        skipNextPlayer = false;
        pendingDrawCards = 0;
        chosenColor = null;
        gameOver = false;
    }

    public void setupGame(Scanner sc) {
        scanner = sc;

        System.out.print("Nombre de joueurs (2-10): ");
        playerCount = scanner.nextInt();
        scanner.nextLine();

        if (playerCount < 2) playerCount = 2;
        if (playerCount > 10) playerCount = 10;

        System.out.print("Nombre de bots (0-" + playerCount + "): ");
        int botCount = scanner.nextInt();
        scanner.nextLine();

        int humanCount = playerCount - botCount;

        for (int i = 0; i < humanCount; i++) {
            System.out.print("Nom du joueur " + (i + 1) + ": ");
            String name = scanner.nextLine();
            if (name.trim().isEmpty()) name = "Joueur " + (i + 1);
            players[i] = new Player(name);
        }

        for (int i = 0; i < botCount; i++) {
            players[humanCount + i] = new Bot("Bot " + (i + 1));
        }

        for (int i = 0; i < playerCount; i++) {
            players[i].addCards(deck.drawCards(7));
        }

        Card first;
        do {
            first = deck.drawCard();
        } while (first.getColor() == CardColor.WILD);

        deck.addToDiscardPile(first);
        chosenColor = first.getColor();

        System.out.println("Première carte: " + first);
    }

    public void startGame() {
        while (!gameOver) {
            playTurn();

            if (!gameOver) {
                nextPlayer();
            }
        }

        System.out.println("\nFin du jeu.");
    }

    private void playTurn() {
        Player current = players[currentPlayerIndex];
        Card top = deck.getTopDiscardCard();

        System.out.println("\n--- Tour de " + current.getName() + " ---");
        System.out.println("Carte sur table: " + top + " | Couleur: " + chosenColor);

        // Bot calls UNO if they have 1 card
        if (current instanceof Bot && current.hasUno() && !current.unoCalled) {
            current.callUno();
            System.out.println(current.getName() + " dit: UNO!");
        }

        // Check if player is skipped
        if (skipNextPlayer) {
            System.out.println(current.getName() + " est sauté.");
            skipNextPlayer = false;
            return;
        }

        // Handle pending draw cards
        if (pendingDrawCards > 0) {
            handlePendingDraw(current, top);
            return;
        }

        // Normal turn
        if (current instanceof Bot) {
            playBot((Bot) current, top);
        } else {
            playHuman(current, top);
        }
    }

    private void handlePendingDraw(Player player, Card topCard) {
        System.out.println(player.getName() + " a " + pendingDrawCards + " cartes à piocher!");
        
        // Check if player has a draw card to stack
        boolean canStack = false;
        if (pendingDrawCards % 2 == 0) {
            // Can stack with +2
            for (int i = 0; i < player.getHandSize(); i++) {
                if (player.getCard(i) instanceof DrawTwoCard) {
                    canStack = true;
                    break;
                }
            }
        }
        if (pendingDrawCards % 4 == 0) {
            // Can stack with +4
            for (int i = 0; i < player.getHandSize(); i++) {
                if (player.getCard(i) instanceof WildDrawFourCard) {
                    canStack = true;
                    break;
                }
            }
        }

        if (!canStack) {
            // Must draw cards
            System.out.println(player.getName() + " pioche " + pendingDrawCards + " cartes.");
            Card[] cards = deck.drawCards(pendingDrawCards);
            player.addCards(cards);
            
            pendingDrawCards = 0;
            skipNextPlayer = true;  // Player who draws is skipped
            return;
        }

        // Player can stack - let them choose
        if (player instanceof Bot) {
            handleBotStacking((Bot) player, topCard);
        } else {
            handleHumanStacking(player, topCard);
        }
    }

    private void handleBotStacking(Bot bot, Card topCard) {
        // Bot always stacks if possible
        int cardIndex = -1;
        
        if (pendingDrawCards % 2 == 0) {
            // Look for +2 card
            for (int i = 0; i < bot.getHandSize(); i++) {
                if (bot.getCard(i) instanceof DrawTwoCard) {
                    cardIndex = i;
                    break;
                }
            }
        }
        
        if (cardIndex == -1 && pendingDrawCards % 4 == 0) {
            // Look for +4 card
            for (int i = 0; i < bot.getHandSize(); i++) {
                if (bot.getCard(i) instanceof WildDrawFourCard) {
                    cardIndex = i;
                    break;
                }
            }
        }
        
        if (cardIndex != -1) {
            Card played = bot.playCard(cardIndex);
            deck.addToDiscardPile(played);
            
            System.out.println(bot.getName() + " joue: " + played + " pour stacker!");
            
            if (played instanceof DrawTwoCard) {
                pendingDrawCards += 2;
                System.out.println("Maintenant " + pendingDrawCards + " cartes à piocher!");
                // Player who stacks is NOT skipped
            } else if (played instanceof WildDrawFourCard) {
                pendingDrawCards += 4;
                chosenColor = bot.chooseColor();
                System.out.println("Couleur choisie: " + chosenColor);
                System.out.println("Maintenant " + pendingDrawCards + " cartes à piocher!");
                // Player who stacks is NOT skipped
            }
        } else {
            // Should not happen since canStack was true
            System.out.println(bot.getName() + " pioche " + pendingDrawCards + " cartes.");
            Card[] cards = deck.drawCards(pendingDrawCards);
            bot.addCards(cards);
            
            pendingDrawCards = 0;
            skipNextPlayer = true;
        }
    }

    private void handleHumanStacking(Player player, Card topCard) {
        player.displayHand();
        
        System.out.println("\nVous pouvez stacker avec:");
        int optionCount = 0;
        
        // Show +2 cards if applicable
        if (pendingDrawCards % 2 == 0) {
            for (int i = 0; i < player.getHandSize(); i++) {
                if (player.getCard(i) instanceof DrawTwoCard) {
                    System.out.println((optionCount + 1) + ". " + player.getCard(i) + " (stacker +2)");
                    optionCount++;
                }
            }
        }
        
        // Show +4 cards if applicable
        if (pendingDrawCards % 4 == 0) {
            for (int i = 0; i < player.getHandSize(); i++) {
                if (player.getCard(i) instanceof WildDrawFourCard) {
                    System.out.println((optionCount + 1) + ". " + player.getCard(i) + " (stacker +4)");
                    optionCount++;
                }
            }
        }
        
        System.out.println((optionCount + 1) + ". Piocher " + pendingDrawCards + " cartes");
        
        System.out.print("Votre choix (1-" + (optionCount + 1) + "): ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice >= 1 && choice <= optionCount) {
            // Find the chosen card
            int cardChoice = 0;
            Card chosenCard = null;
            int cardIndex = -1;
            
            // Find +2 cards
            if (pendingDrawCards % 2 == 0) {
                for (int i = 0; i < player.getHandSize(); i++) {
                    if (player.getCard(i) instanceof DrawTwoCard) {
                        cardChoice++;
                        if (cardChoice == choice) {
                            chosenCard = player.getCard(i);
                            cardIndex = i;
                            break;
                        }
                    }
                }
            }
            
            // Find +4 cards if not found yet
            if (chosenCard == null && pendingDrawCards % 4 == 0) {
                cardChoice = 0;
                for (int i = 0; i < player.getHandSize(); i++) {
                    if (player.getCard(i) instanceof DrawTwoCard) {
                        cardChoice++;
                    }
                }
                
                for (int i = 0; i < player.getHandSize(); i++) {
                    if (player.getCard(i) instanceof WildDrawFourCard) {
                        cardChoice++;
                        if (cardChoice == choice) {
                            chosenCard = player.getCard(i);
                            cardIndex = i;
                            break;
                        }
                    }
                }
            }
            
            if (chosenCard != null && cardIndex != -1) {
                Card played = player.playCard(cardIndex);
                deck.addToDiscardPile(played);
                
                System.out.println("Vous jouez: " + played + " pour stacker!");
                
                if (played instanceof DrawTwoCard) {
                    pendingDrawCards += 2;
                    System.out.println("Maintenant " + pendingDrawCards + " cartes à piocher!");
                    // Player who stacks is NOT skipped
                } else if (played instanceof WildDrawFourCard) {
                    pendingDrawCards += 4;
                    System.out.println("Choisir une couleur: 1-ROUGE 2-BLEU 3-VERT 4-JAUNE");
                    int c = scanner.nextInt();
                    scanner.nextLine();
                    
                    if (c == 1) chosenColor = CardColor.RED;
                    else if (c == 2) chosenColor = CardColor.BLUE;
                    else if (c == 3) chosenColor = CardColor.GREEN;
                    else chosenColor = CardColor.YELLOW;
                    
                    System.out.println("Couleur choisie: " + chosenColor);
                    System.out.println("Maintenant " + pendingDrawCards + " cartes à piocher!");
                    // Player who stacks is NOT skipped
                }
            }
        } else {
            // Choose to draw
            System.out.println(player.getName() + " pioche " + pendingDrawCards + " cartes.");
            Card[] cards = deck.drawCards(pendingDrawCards);
            player.addCards(cards);
            
            pendingDrawCards = 0;
            skipNextPlayer = true;  // Player who draws is skipped
        }
    }

    private void playBot(Bot bot, Card top) {
        Card[] playable = bot.getPlayableCards(top, chosenColor);

        if (playable.length > 0) {
            int choice = bot.chooseCardIndex(playable);
            Card chosen = playable[choice];
            int index = bot.findCardIndex(chosen);

            Card played = bot.playCard(index);
            deck.addToDiscardPile(played);

            System.out.println(bot.getName() + " joue: " + played);

            if (played.getColor() == CardColor.WILD) {
                chosenColor = bot.chooseColor();
                System.out.println("Couleur choisie: " + chosenColor);
            } else {
                chosenColor = played.getColor();
            }

            played.applyEffect(this);

            if (bot.hasWon()) {
                gameOver = true;
                System.out.println(bot.getName() + " a gagné.");
            }
        } else {
            Card c = deck.drawCard();
            bot.addCard(c);
            System.out.println(bot.getName() + " pioche: " + c);
        }
    }

    private void playHuman(Player player, Card top) {
        player.displayHand();

        Card[] playable = player.getPlayableCards(top, chosenColor);

        if (playable.length == 0) {
            Card c = deck.drawCard();
            player.addCard(c);
            System.out.println("Vous piochez: " + c);
            return;
        }

        System.out.println("Cartes jouables:");
        for (int i = 0; i < playable.length; i++) {
            System.out.println((i + 1) + ". " + playable[i]);
        }

        System.out.print("Choisir une carte (1-" + playable.length + "): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > playable.length) {
            System.out.println("Choix invalide.");
            return;
        }

        Card chosen = playable[choice - 1];
        int index = player.findCardIndex(chosen);

        Card played = player.playCard(index);
        deck.addToDiscardPile(played);
        
        System.out.println("Vous jouez: " + played);

        if (played.getColor() == CardColor.WILD) {
            System.out.println("Choisir une couleur: 1-ROUGE 2-BLEU 3-VERT 4-JAUNE");
            int c = scanner.nextInt();
            scanner.nextLine();

            if (c == 1) chosenColor = CardColor.RED;
            else if (c == 2) chosenColor = CardColor.BLUE;
            else if (c == 3) chosenColor = CardColor.GREEN;
            else chosenColor = CardColor.YELLOW;
            
            System.out.println("Couleur choisie: " + chosenColor);
        } else {
            chosenColor = played.getColor();
        }

        played.applyEffect(this);

        if (player.hasUno()) {
            System.out.print("Dire UNO ? (o/n): ");
            String ans = scanner.nextLine();
            if (ans.equalsIgnoreCase("o")) {
                player.callUno();
                System.out.println("UNO!");
            }
        }

        if (player.hasWon()) {
            gameOver = true;
            System.out.println(player.getName() + " a gagné!");
        }
    }

    private void nextPlayer() {
        if (reverseDirection) {
            currentPlayerIndex--;
        } else {
            currentPlayerIndex++;
        }

        if (currentPlayerIndex < 0) currentPlayerIndex = playerCount - 1;
        if (currentPlayerIndex >= playerCount) currentPlayerIndex = 0;
    }

    public void skipNextPlayer() {
        skipNextPlayer = true;
    }

    public void reverseDirection() {
        reverseDirection = !reverseDirection;
        System.out.println("Direction inversée!");
    }

    public void addPendingDrawCards(int n) {
        pendingDrawCards += n;
    }

    public int getPlayerCount() {
        return playerCount;
    }
}