/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package estructura.de.datos.pkg2;

import java.io.Serializable;

public class BTree implements Serializable {

    private static final long SerialVersionUID = 777L;

    private Node root;
    private int degrees;

    public BTree(int degrees) {
        root = null;
        this.degrees = degrees;
    }

    public Node search(Index key) {
        if (root == null) {
            return null;
        } else {
            return root.search(key);
        }
    }

    public void insertNode(Index key) {
        if (root == null) { //If the root is empty, tree is empty
            root = new Node(degrees, true); //It will create a new root
            root.keys[0] = key;//and insert the key to the root.
            root.numberKeys = 1; //there is one key in the root atm.
        } else if (root.numberKeys == 2 * degrees - 1) {//If the root is FULL
            Node s = new Node(degrees, false);//we'll make another root
            s.children[0] = root;//and the old root will become a child of the new root
            s.split(0, root);//Split the root
            int i = 0;
            if (s.keys[0].getKey() < key.getKey()) {//As it has more than two children now, this will decide where it should be
                i++;
            }
            s.children[i].insertNonFull(key);
            root = s;//replace the old root with the new one
        } else {
            root.insertNonFull(key);//just insert
        }
    }

    public void remove(Index key) {
        if (root == null) {
            System.out.println("Vacío");
        }
        root.remove(key);//Removes the key
        if (root.numberKeys == 0) {//Does it has any keys?
            Node temp = root;
            if (root.isLeaf) {//is it a leaf? (It doesn't have a child)
                root = null;
            } else {//set its child as the root.
                root = root.children[0];
            }
        }
    }

    public void print() {
        printBtree(root, "");

    }

    private void printBtree(Node node, String indent) {
        if (node == null) {
            System.out.println(indent + "The B-Tree is Empty");
        } else {
            System.out.println(indent + " ");
            String childIndent = indent + "\t";
            for (int i = node.numberKeys - 1; i >= 0; i--) {
                if (!node.isLeaf) {
                    printBtree(node.children[i], childIndent);
                }
                if (node.keys[i].getKey() > 0) {
                    System.out.println(childIndent + node.keys[i].getKey());
                }
            }
            if (!node.isLeaf) {
                printBtree(node.children[node.numberKeys], childIndent);
            }
        }
    }

    public boolean search(int n) {
        Node r = root;
        return search(r, n);
    }

    private boolean search(Node node, int value) {
        int i = 1;
        while (i <= node.numberKeys && value > node.keys[i - 1].getKey()) {
            i++;
        }
        if (i <= node.numberKeys && value == node.keys[i - 1].getKey()) {
            return true;
        }
        if (!node.isLeaf) {
            return search(node.children[i - 1], value);
        }
        return false;
    }

    public Node buscarh(Index key) {
        if (root == null) {
            return null;
        } else {
            return root.search(key);
        }
    }
}//fin

//This Btree follows the Algorithm written by Cormen.
//By no means this BTree is 100% ours -Jorge Morazán

// http://staff.ustc.edu.cn/~csli/graduate/algorithms/book6/chap19.htm