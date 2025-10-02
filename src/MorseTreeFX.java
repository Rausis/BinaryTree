import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import NewNodeTree.NodeTree;

/** Visualizador JavaFX da Árvore Binária de Código Morse (composição). */
public class MorseTreeFX extends Application {

    private final BinaryTree app = new BinaryTree();
    private final Pane drawPane = new Pane();

    // constantes de layout
    private static final double NODE_RADIUS = 16;
    private static final double V_GAP = 70;
    private static final double H_START_GAP = 240;

    @Override
    public void start(Stage stage) {
        // ---- Controles superiores ----
        TextField tfLetter = new TextField();
        tfLetter.setPromptText("Letra (A-Z)");
        tfLetter.setPrefColumnCount(4);

        TextField tfCode = new TextField();
        tfCode.setPromptText("Código (.-)");
        tfCode.setPrefColumnCount(10);

        Button btnInsert = new Button("Inserir");
        btnInsert.setOnAction(e -> {
            String codigo = tfCode.getText().trim();
            String s = tfLetter.getText().trim();
            if (s.isEmpty() || codigo.isEmpty()) { alerta("Informe a letra e o código Morse."); return; }
            char ch = Character.toUpperCase(s.charAt(0));
            if (!validaMorse(codigo)) { alerta("O código deve conter apenas '.' e '-'"); return; }
            app.getTree().insertByCode(ch, codigo);
            tfLetter.clear();
            tfCode.clear();
            redesenhar();
        });

        TextField tfEncode = new TextField();
        tfEncode.setPromptText("Texto para codificar");
        TextField tfEncoded = new TextField();
        tfEncoded.setPromptText("Resultado em Morse");
        tfEncoded.setEditable(false);
        Button btnEncode = new Button("Codificar →");
        btnEncode.setOnAction(e -> tfEncoded.setText(app.encodeLine(tfEncode.getText())));

        TextField tfDecode = new TextField();
        tfDecode.setPromptText("Morse para decodificar");
        TextField tfDecoded = new TextField();
        tfDecoded.setPromptText("Resultado em texto");
        tfDecoded.setEditable(false);
        Button btnDecode = new Button("Decodificar →");
        btnDecode.setOnAction(e -> tfDecoded.setText(app.decodeLine(tfDecode.getText())));

        HBox insertRow = new HBox(10, new Label("Inserir:"), tfLetter, tfCode, btnInsert);
        HBox encodeRow = new HBox(10, new Label("Codificar:"), tfEncode, btnEncode, tfEncoded);
        HBox decodeRow = new HBox(10, new Label("Decodificar:"), tfDecode, btnDecode, tfDecoded);

        // --- Construir + Remover ---
        Button btnBuild = new Button("Construir Árvore Morse");
        btnBuild.setOnAction(e -> { app.buildStandardMorse(); redesenhar(); });

        TextField tfRemove = new TextField();
        tfRemove.setPromptText("Código para remover (.-)");
        tfRemove.setPrefColumnCount(14);

        Button btnRemove = new Button("Remover");
        btnRemove.setOnAction(e -> {
            String codigo = tfRemove.getText().trim();
            if (codigo.isEmpty()) { alerta("Informe o código Morse a remover."); return; }
            for (int i = 0; i < codigo.length(); i++) {
                char ch = codigo.charAt(i);
                if (ch != '.' && ch != '-') { alerta("O código deve conter apenas '.' e '-'"); return; }
            }
            boolean ok = app.removeAtCode(codigo);
            if (!ok) alerta("Nada para remover em: " + codigo);
            tfRemove.clear();
            redesenhar();
        });

        HBox buildRow = new HBox(10, btnBuild, new Label("Remover:"), tfRemove, btnRemove);

        VBox controls = new VBox(8, insertRow, encodeRow, decodeRow, buildRow);
        controls.setPadding(new Insets(10));

        // ---- Área de desenho ----
        drawPane.setMinSize(800, 500);
        drawPane.setPrefSize(1000, 650);
        ScrollPane scroll = new ScrollPane(drawPane);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);

        BorderPane root = new BorderPane();
        root.setTop(controls);
        root.setCenter(scroll);

        Scene scene = new Scene(root, 1000, 720);
        stage.setTitle("Visualizador da Árvore Morse");
        stage.setScene(scene);
        stage.show();

        redesenhar();
        drawPane.widthProperty().addListener((obs, o, n) -> redesenhar());
    }

    private void redesenhar() {
        drawPane.getChildren().clear();
        NodeTree<Character> t = app.getTree();
        NodeTree.TreeNode<Character> root = t.getRoot();
        if (root == null) root = t.setRoot(null);
        double width = Math.max(drawPane.getWidth(), 800);
        double startX = width / 2.0;
        double startY = 60;
        desenhaNoRec(root, startX, startY, H_START_GAP);
    }

    private void desenhaNoRec(NodeTree.TreeNode<Character> node, double x, double y, double hGap) {
        if (node == null) return;

        if (node.left != null) {
            double lx = x - hGap, ly = y + V_GAP;
            desenhaLigacao(x, y, lx, ly, ".");
            desenhaNoRec(node.left, lx, ly, hGap / 1.8);
        }
        if (node.right != null) {
            double rx = x + hGap, ry = y + V_GAP;
            desenhaLigacao(x, y, rx, ry, "-");
            desenhaNoRec(node.right, rx, ry, hGap / 1.8);
        }
        desenhaNo(x, y, app.getTree().getNodeValue(node));
    }

    private void desenhaLigacao(double x1, double y1, double x2, double y2, String etiqueta) {
        Line line = new Line(x1, y1 + NODE_RADIUS, x2, y2 - NODE_RADIUS);
        line.setStroke(Color.GRAY);
        line.setStrokeWidth(2);
        drawPane.getChildren().add(line);

        double mx = (x1 + x2) / 2.0, my = (y1 + y2) / 2.0;
        Text t = new Text(mx - 4, my - 4, etiqueta);
        t.setFill(Color.DARKGRAY);
        t.setFont(Font.font(12));
        drawPane.getChildren().add(t);
    }

    private void desenhaNo(double x, double y, Character valor) {
        Circle c = new Circle(x, y, NODE_RADIUS);
        c.setFill(Color.WHITESMOKE);
        c.setStroke(Color.DARKSLATEGRAY);
        c.setStrokeWidth(2);
        drawPane.getChildren().add(c);

        String label = (valor == null) ? "•" : String.valueOf(valor);
        Text t = new Text(x - 5, y + 5, label);
        t.setFont(Font.font(14));
        drawPane.getChildren().add(t);
    }

    private boolean validaMorse(String s) {
        if (s == null || s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch != '.' && ch != '-') return false;
        }
        return true;
    }

    private void alerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
