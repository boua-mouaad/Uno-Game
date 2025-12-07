public class Main {
    public static void main(String[] args) {
        System.out.println("--- LAUNCHING UNO GAME ---\n");
        
        Game unoGame = new Game();
        unoGame.setupGame();
        unoGame.startGame();
        
        System.out.println("\n--- PROGRAM ENDED ---");
    }
}