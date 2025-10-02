import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        BinaryTree app = new BinaryTree();   // your class using composition
        Scanner sc = new Scanner(System.in);


        System.out.println("Letters separated by spaces; words by '/'.");
        System.out.println("Exemplo: .... . .-.. .-.. --- / .-- --- .-. .-.. -..");
        System.out.println("Exemplo encode: HELLO WORLD");

        while (true) {
            System.out.println("\nMenu");
            System.out.println("1 - Inserir (código Morse -> letra)");
            System.out.println("2 - Codificar (texto -> Morse)");
            System.out.println("3 - Decodificar (Morse -> texto)");
            System.out.println("0 - Sair");
            System.out.print("> ");

            String opt = sc.nextLine().trim();

            if ("1".equals(opt)) {
                app.treeMovement();

            } else if ("2".equals(opt)) {
                System.out.print("Texto para codificar: ");
                String text = sc.nextLine();
                String morse = app.encodeLine(text);
                System.out.println("Morse: " + morse);

            } else if ("3".equals(opt)) {
                System.out.print("Morse para decodificar: ");
                String morse = sc.nextLine();
                String text = app.decodeLine(morse);
                System.out.println("Texto: " + text);

            } else if ("0".equals(opt)) {
                System.out.println("Bye!");
                break;

            } else {
                System.out.println("Opção inválida.");
            }
        }
    }
}
