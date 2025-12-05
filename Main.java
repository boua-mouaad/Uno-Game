
public class Main {
    
    public static void main(String[] args) {
        System.out.println("=== LANCEMENT DU JEU UNO ===\n");
        
       
        Game unoGame = new Game();
        
        unoGame.setupGame();
        
        unoGame.startGame();
        
        System.out.println("\n=== PROGRAMME TERMINE ===");
    }
}