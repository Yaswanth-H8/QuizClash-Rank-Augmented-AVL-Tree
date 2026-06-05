// QuizClash Leaderboard using Rank-Augmented AVL Tree
// File Name: RankAVL.java

class RankNode {
    int key, height, size;
    RankNode left, right;

    RankNode(int key) {
        this.key = key;
        height = 1;
        size = 1;
    }
}

public class RankAVL {

    // ---------------------------------
    // HEIGHT + SIZE HELPERS
    // ---------------------------------

    static int height(RankNode n) {
        return (n == null) ? 0 : n.height;
    }

    static int size(RankNode n) {
        return (n == null) ? 0 : n.size;
    }

    static void update(RankNode n) {
        if (n != null) {
            n.height = 1 + Math.max(height(n.left), height(n.right));
            n.size = 1 + size(n.left) + size(n.right);
        }
    }

    static int balanceFactor(RankNode n) {
        return (n == null) ? 0 : height(n.left) - height(n.right);
    }

    // ---------------------------------
    // RIGHT ROTATION
    // ---------------------------------

    static RankNode rotateRight(RankNode y) {

        RankNode x = y.left;
        RankNode t2 = x.right;

        x.right = y;
        y.left = t2;

        update(y);
        update(x);

        return x;
    }

    // ---------------------------------
    // LEFT ROTATION
    // ---------------------------------

    static RankNode rotateLeft(RankNode x) {

        RankNode y = x.right;
        RankNode t2 = y.left;

        y.left = x;
        x.right = t2;

        update(x);
        update(y);

        return y;
    }

    // ---------------------------------
    // INSERT
    // DESCENDING ORDER TREE
    // left > root > right
    // ---------------------------------

    static RankNode insert(RankNode root, int key) {

        if (root == null)
            return new RankNode(key);

        // DESCENDING ORDER
        if (key > root.key)
            root.left = insert(root.left, key);
        else if (key < root.key)
            root.right = insert(root.right, key);
        else
            return root;

        update(root);

        int bf = balanceFactor(root);

        // LL
        if (bf > 1 && key > root.left.key)
            return rotateRight(root);

        // RR
        if (bf < -1 && key < root.right.key)
            return rotateLeft(root);

        // LR
        if (bf > 1 && key < root.left.key) {
            root.left = rotateLeft(root.left);
            return rotateRight(root);
        }

        // RL
        if (bf < -1 && key > root.right.key) {
            root.right = rotateRight(root.right);
            return rotateLeft(root);
        }

        return root;
    }

    // ---------------------------------
    // MIN VALUE NODE
    // ---------------------------------

    static RankNode minValueNode(RankNode node) {

        RankNode curr = node;

        while (curr.left != null)
            curr = curr.left;

        return curr;
    }

    // ---------------------------------
    // DELETE
    // ---------------------------------

    static RankNode delete(RankNode root, int key) {

        if (root == null)
            return null;

        // DESCENDING ORDER
        if (key > root.key)
            root.left = delete(root.left, key);

        else if (key < root.key)
            root.right = delete(root.right, key);

        else {

            // one child or no child
            if (root.left == null || root.right == null) {

                RankNode temp;

                if (root.left != null)
                    temp = root.left;
                else
                    temp = root.right;

                if (temp == null) {
                    root = null;
                } else {
                    root = temp;
                }
            }

            else {

                RankNode temp = minValueNode(root.right);

                root.key = temp.key;

                root.right = delete(root.right, temp.key);
            }
        }

        if (root == null)
            return null;

        update(root);

        int bf = balanceFactor(root);

        // LL
        if (bf > 1 && balanceFactor(root.left) >= 0)
            return rotateRight(root);

        // LR
        if (bf > 1 && balanceFactor(root.left) < 0) {
            root.left = rotateLeft(root.left);
            return rotateRight(root);
        }

        // RR
        if (bf < -1 && balanceFactor(root.right) <= 0)
            return rotateLeft(root);

        // RL
        if (bf < -1 && balanceFactor(root.right) > 0) {
            root.right = rotateRight(root.right);
            return rotateLeft(root);
        }

        return root;
    }

    // ---------------------------------
    // RANK QUERY
    // 1 = HIGHEST SCORE
    // ---------------------------------

    static int rankOf(RankNode root, int key) {

        int rank = 1;

        while (root != null) {

            if (key == root.key) {
                return rank + size(root.left);
            }

            // go LEFT (higher scores)
            if (key < root.key) {

                rank += size(root.left) + 1;

                root = root.right;
            }

            else {
                root = root.left;
            }
        }

        return -1;
    }

    // ---------------------------------
    // PRINT TREE
    // ---------------------------------

    static void printTree(RankNode root, String indent, boolean last) {

        if (root != null) {

            System.out.print(indent);

            if (last) {
                System.out.print("R----");
                indent += "     ";
            } else {
                System.out.print("L----");
                indent += "|    ";
            }

            System.out.println(root.key + " [size=" + root.size + "]");

            printTree(root.left, indent, false);
            printTree(root.right, indent, true);
        }
    }

    // ---------------------------------
    // MAIN
    // ---------------------------------

    public static void main(String[] args) {

        int[] scores = {
                820, 540, 910, 770, 880,
                460, 990, 600, 730, 950, 510
        };

        RankNode root = null;

        // Build AVL Tree
        for (int x : scores) {
            root = insert(root, x);
        }

        System.out.println("===================================");
        System.out.println("INITIAL RANK-AUGMENTED AVL TREE");
        System.out.println("===================================\n");

        printTree(root, "", true);

        // Rank Query
        System.out.println("\nRank of 770 = " + rankOf(root, 770));

        // ---------------------------------
        // UPDATE 1
        // 540 -> 815
        // ---------------------------------

        System.out.println("\n===================================");
        System.out.println("UPDATE: 540 -> 815");
        System.out.println("===================================\n");

        root = delete(root, 540);
        root = insert(root, 815);

        printTree(root, "", true);

        // ---------------------------------
        // UPDATE 2
        // 910 -> 685
        // ---------------------------------

        System.out.println("\n===================================");
        System.out.println("UPDATE: 910 -> 685");
        System.out.println("===================================\n");

        root = delete(root, 910);
        root = insert(root, 685);

        printTree(root, "", true);

        // Complexity
        System.out.println("\n===================================");
        System.out.println("COMPLEXITY ANALYSIS");
        System.out.println("===================================\n");

        System.out.println("Insert      : O(log n)");
        System.out.println("Delete      : O(log n)");
        System.out.println("Rank Query  : O(log n)");
        System.out.println("Top-K Query : O(log n + K)");

        System.out.println("\nWhy size augmentation helps:");
        System.out.println("Each node stores subtree size,");
        System.out.println("so rank is computed during descent");
        System.out.println("without scanning all players.");

        System.out.println("\nWithout size fields:");
        System.out.println("Rank query would require full traversal");
        System.out.println("O(n) = 60,000 scans per query.");

        System.out.println("\nThis cannot sustain 2000 qps.");
    }
}
