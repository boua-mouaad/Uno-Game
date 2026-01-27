import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("=== JEU UNO ===");
        
        Game game = new Game();
        game.setupGame(sc);
        game.startGame();

        sc.close();
    }
}