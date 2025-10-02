import NewNodeTree.NodeTree;
import java.util.Scanner;
public class BinaryTree {
    private final NodeTree<Character> tree = new NodeTree<>();

    public BinaryTree() {
        tree.firstTree(null, 'E', 'R');
    }

    public String getUserInput(){
        Scanner input = new Scanner(System.in);
        return input.nextLine();
    }

    public String[] getDotDash()
    {
        String myString = getUserInput();
        String[] dotsAndDash = new String[myString.length()];
        for (int i = 0; i < myString.length(); i++) {
            char character = myString.charAt(i);
            dotsAndDash[i] = String.valueOf(character);
        }

        return dotsAndDash;
    }


    public void treeMovement() {
        System.out.print("Enter code (.-): ");
        String[] dotDashArray = getDotDash();

        NodeTree.TreeNode<Character> cur = tree.getRoot();
        if (cur == null) cur = tree.setRoot(null); // make sure we have a root

        for (int i = 0; i < dotDashArray.length; i++) {
            String step = dotDashArray[i];
            if (".".equals(step)) {
                NodeTree.TreeNode<Character> left = tree.getLeft(cur);
                if (left == null) left = tree.setLeft(null, cur);
                cur = left;
            } else if ("-".equals(step)) {
                NodeTree.TreeNode<Character> right = tree.getRight(cur);
                if (right == null) right = tree.setRight(null, cur);
                cur = right;
            } else {
                System.out.println("Invalid char: " + step + " (use '.' or '-')");
                return;
            }
        }

        System.out.print("Enter letter to assign here: ");
        String s = getUserInput();
        if (s == null || s.isEmpty()) {
            System.out.println("No letter provided.");
            return;
        }
        char letter = Character.toUpperCase(s.charAt(0));
        tree.setNodeValue(cur, letter);
        System.out.println("OK: stored '" + letter + "' at this path.");
    }
    // TEXT -> MORSE
    public String encodeLine(String text) {
        if (text == null) return "";
        if (tree.isEmpty()) return ""; // or throw/print an error

        StringBuilder out = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            // word boundary
            if (ch == ' ') {
                // append " / " once between words
                if (out.length() > 0) {
                    // avoid duplicate separators
                    char last = out.charAt(out.length() - 1);
                    if (last != ' ' && last != '/') out.append(' ');
                    out.append('/');
                }
                continue;
            }

            // normalize letter
            char up = Character.toUpperCase(ch);

            // find the code by recursively searching the tree
            String code = tree.encodeOne(up); // returns null if not found
            if (code == null) {
                code = "?"; // unknown character (not inserted into the tree)
            }

            // separate letters with a single space unless we just added '/'
            if (out.length() > 0) {
                char last = out.charAt(out.length() - 1);
                if (last != ' ' && last != '/') out.append(' ');
            }
            out.append(code);
        }

        return out.toString();
    }
    // MORSE -> TEXT
    public String decodeLine(String morse) {
        if (morse == null) return "";
        if (tree.isEmpty()) return ""; // or throw/print an error

        StringBuilder out = new StringBuilder();
        int i = 0, n = morse.length();

        while (i < n) {
            // skip spaces between tokens
            while (i < n && morse.charAt(i) == ' ') i++;
            if (i >= n) break;

            char ch = morse.charAt(i);

            // word boundary: '/'
            if (ch == '/') {
                if (out.length() > 0 && out.charAt(out.length() - 1) != ' ') {
                    out.append(' ');
                }
                i++;
                continue;
            }

            // invalid char (not '.', '-', '/', space) — skip it
            if (ch != '.' && ch != '-') {
                i++;
                continue;
            }

            // collect a letter token of '.' and '-'
            int start = i;
            while (i < n) {
                char c = morse.charAt(i);
                if (c == '.' || c == '-') {
                    i++;
                } else {
                    break;
                }
            }
            String token = morse.substring(start, i); // one letter in Morse

            // decode this letter via the recursive path walk
            Character val = tree.getValueByCode(token);
            out.append(val == null ? '?' : val.charValue());
            // loop will skip spaces and handle '/' at top
        }

        return out.toString();
    }

    public NewNodeTree.NodeTree<Character> getTree() { return tree; }

    // Build standard ITU Morse: A–Z and 0–9
    public void buildStandardMorse() {
        // Ensure base root exists
        if (tree.getRoot() == null) tree.setRoot(null);

        // Letters
        tree.insertByCode('A', ".-");    tree.insertByCode('B', "-...");
        tree.insertByCode('C', "-.-.");  tree.insertByCode('D', "-..");
        tree.insertByCode('E', ".");     tree.insertByCode('F', "..-.");
        tree.insertByCode('G', "--.");   tree.insertByCode('H', "....");
        tree.insertByCode('I', "..");    tree.insertByCode('J', ".---");
        tree.insertByCode('K', "-.-");   tree.insertByCode('L', ".-..");
        tree.insertByCode('M', "--");    tree.insertByCode('N', "-.");
        tree.insertByCode('O', "---");   tree.insertByCode('P', ".--.");
        tree.insertByCode('Q', "--.-");  tree.insertByCode('R', ".-.");
        tree.insertByCode('S', "...");   tree.insertByCode('T', "-");
        tree.insertByCode('U', "..-");   tree.insertByCode('V', "...-");
        tree.insertByCode('W', ".--");   tree.insertByCode('X', "-..-");
        tree.insertByCode('Y', "-.--");  tree.insertByCode('Z', "--..");

        // Digits
        tree.insertByCode('0', "-----"); tree.insertByCode('1', ".----");
        tree.insertByCode('2', "..---"); tree.insertByCode('3', "...--");
        tree.insertByCode('4', "....-"); tree.insertByCode('5', ".....");
        tree.insertByCode('6', "-...."); tree.insertByCode('7', "--...");
        tree.insertByCode('8', "---.."); tree.insertByCode('9', "----.");
    }

    // Remove letter at a Morse path; returns true if something changed
    public boolean removeAtCode(String code) {
        return tree.clearValueByCode(code);
    }






}
