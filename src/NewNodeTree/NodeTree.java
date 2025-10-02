package NewNodeTree;

public class NodeTree<E> {

    public static final class TreeNode<T> {
        T value;
        public TreeNode<T> left;
        public TreeNode<T> right;
        TreeNode<T> parent;
        TreeNode(T value) { this.value = value; }
    }

    private TreeNode<E> root;
    private int size;

    public NodeTree() { }

    // --- Core ---
    public TreeNode<E> getRoot() { return root; }
    public int getSize() { return size; }
    public boolean isEmpty() { return root == null; }

    // Build a tiny tree:     a
    //                      /   \
    //                     c     b
    public TreeNode<E> firstTree(E element1, E element2, E element3) {
        TreeNode<E> a = new TreeNode<E>(element1);
        TreeNode<E> b = new TreeNode<E>(element2);
        TreeNode<E> c = new TreeNode<E>(element3);

        a.right = b;  b.parent = a;
        a.left  = c;  c.parent = a;

        root = a;
        size = 3;
        return root;
    }

    // --- Accessors (take NODE, not value) ---
    public TreeNode<E> getLeft(TreeNode<E> node) {
        return (node == null) ? null : node.left;
    }

    public TreeNode<E> getRight(TreeNode<E> node) {
        return (node == null) ? null : node.right;
    }

    // --- Mutators: attach by parent NODE ---
    public TreeNode<E> setLeft(E element, TreeNode<E> parent) {
        if (parent == null) return null;
        TreeNode<E> child = new TreeNode<E>(element);
        child.parent = parent;
        parent.left = child;
        size++;
        return child;
    }

    public TreeNode<E> setRight(E element, TreeNode<E> parent) {
        if (parent == null) return null;
        TreeNode<E> child = new TreeNode<E>(element);
        child.parent = parent;
        parent.right = child;
        size++;
        return child;
    }

    // Remove ONLY if node is a leaf (simple starter; expand later)
    public void removeLeaf(TreeNode<E> node) {
        if (node == null) return;
        if (node.left != null || node.right != null) return; // not a leaf
        if (node.parent == null) { // it's the root
            root = null;
            size = 0;
            return;
        }
        TreeNode<E> p = node.parent;
        if (p.left == node) p.left = null;
        if (p.right == node) p.right = null;
        node.parent = null;
        size--;
    }

    // Simple helper to create/set the root
    public TreeNode<E> setRoot(E value) {
        root = new TreeNode<E>(value);
        size = 1;
        return root;
    }
    public void setNodeValue(TreeNode<E> node, E value) {
        if (node != null) node.value = value;
    }
    // MORSE -> VALUE (e.g., ".-" -> 'A')
    public E getValueByCode(CharSequence code) {
        if (code == null) throw new IllegalArgumentException("code == null");
        if (root == null) return null;
        TreeNode<E> n = getRec(root, code, 0);
        return (n == null) ? null : n.value;
    }

    private TreeNode<E> getRec(TreeNode<E> node, CharSequence code, int i) {
        if (node == null) return null;
        if (i == code.length()) return node;
        char ch = code.charAt(i);
        if (ch == '.') return getRec(node.left,  code, i + 1);
        if (ch == '-') return getRec(node.right, code, i + 1);
        // invalid character
        return null;
    }
    // VALUE -> MORSE (e.g., 'A' -> ".-")
    public String encodeOne(E target) {
        if (root == null) return null;
        return encodeRec(root, target, "");
    }


    private String encodeRec(TreeNode<E> node, E target, String path) {
        if (node == null) return null;

        boolean eq = (target == null) ? (node.value == null)
                : target.equals(node.value);
        if (eq) return path;

        // try left ('.')
        String leftPath = encodeRec(node.left, target, path + ".");
        if (leftPath != null) return leftPath;

        // try right ('-')
        return encodeRec(node.right, target, path + "-");
    }

    public E getNodeValue(TreeNode<E> node) { return (node == null) ? null : node.value; }



    // Ensure there is a root placeholder
    private void ensureRoot() {
        if (root == null) {
            root = new TreeNode<E>(null);
            size = 1;
        }
    }


    public TreeNode<E> insertByCode(E value, String code) {
        if (code == null) throw new IllegalArgumentException("code == null");
        ensureRoot();
        return insertRec(root, code, 0, value);
    }

    private TreeNode<E> insertRec(TreeNode<E> node, String code, int i, E value) {
        if (i == code.length()) {      // reached destination
            node.value = value;        // just set value; children (if any) remain intact
            return node;
        }
        char ch = code.charAt(i);
        if (ch == '.') {
            if (node.left == null) setLeft(null, node);   // uses your existing setter (updates size/parent)
            return insertRec(node.left, code, i + 1, value);
        } else if (ch == '-') {
            if (node.right == null) setRight(null, node); // uses your existing setter (updates size/parent)
            return insertRec(node.right, code, i + 1, value);
        } else {
            throw new IllegalArgumentException("Invalid code char: '" + ch + "' (use '.' or '-')");
        }
    }
    // Clear only the value at a path ('.' left, '-' right). Returns true if node exists.
    public boolean clearValueByCode(String code) {
        if (code == null) return false;
        if (root == null) return false;
        TreeNode<E> n = getRec(root, code, 0);
        if (n == null) return false;
        n.value = null;
        pruneIfEmpty(n);
        return true;
    }

    // Prune upward any now-empty leaf nodes (no value, no children)
    private void pruneIfEmpty(TreeNode<E> n) {
        while (n != null && n.value == null && n.left == null && n.right == null) {
            TreeNode<E> p = n.parent;
            if (p == null) { // removing root
                root = null;
                size = 0;
                return;
            }
            if (p.left == n) p.left = null;
            else if (p.right == n) p.right = null;
            size--;
            n = p;
        }
    }






}


