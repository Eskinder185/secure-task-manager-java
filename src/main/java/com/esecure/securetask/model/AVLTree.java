package com.esecure.securetask.model;

// Generic self-balancing AVL Tree
public class AVLTree<T extends Comparable<T>> {

    private class Node {
        T data;
        Node left, right;
        int height;

        Node(T data) {
            this.data = data;
            this.height = 1;
        }
    }

    private Node root;

    public void insert(T data) {
        root = insert(root, data);
    }

    public void delete(T data) {
        root = delete(root, data);
    }

    public boolean contains(T data) {
        return contains(root, data);
    }

    private Node insert(Node node, T data) {
        if (node == null) return new Node(data);

        if (data.compareTo(node.data) < 0) {
            node.left = insert(node.left, data);
        } else if (data.compareTo(node.data) > 0) {
            node.right = insert(node.right, data);
        } else {
            return node; // duplicate
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));
        return balance(node);
    }

    private Node delete(Node node, T data) {
        if (node == null) return null;

        if (data.compareTo(node.data) < 0) {
            node.left = delete(node.left, data);
        } else if (data.compareTo(node.data) > 0) {
            node.right = delete(node.right, data);
        } else {
            if (node.left == null || node.right == null) {
                node = (node.left != null) ? node.left : node.right;
            } else {
                Node temp = getMin(node.right);
                node.data = temp.data;
                node.right = delete(node.right, temp.data);
            }
        }

        if (node == null) return null;
        node.height = 1 + Math.max(height(node.left), height(node.right));
        return balance(node);
    }

    private boolean contains(Node node, T data) {
        if (node == null) return false;
        if (data.compareTo(node.data) < 0) return contains(node.left, data);
        if (data.compareTo(node.data) > 0) return contains(node.right, data);
        return true;
    }

    private Node getMin(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    private int height(Node node) {
        return (node == null) ? 0 : node.height;
    }

    private int getBalance(Node node) {
        return (node == null) ? 0 : height(node.left) - height(node.right);
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = 1 + Math.max(height(y.left), height(y.right));
        x.height = 1 + Math.max(height(x.left), height(x.right));

        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = 1 + Math.max(height(x.left), height(x.right));
        y.height = 1 + Math.max(height(y.left), height(y.right));

        return y;
    }

    private Node balance(Node node) {
        int balance = getBalance(node);

        if (balance > 1 && getBalance(node.left) >= 0) {
            return rotateRight(node);
        }

        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        if (balance < -1 && getBalance(node.right) <= 0) {
            return rotateLeft(node);
        }

        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }
}
