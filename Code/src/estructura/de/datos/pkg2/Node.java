/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package estructura.de.datos.pkg2;

import java.io.Serializable;

/**
 *
 * @author Acer
 */
public class Node implements Serializable {

    private static final long SerialVersionUID = 777L;

    public Index keys[];  // An array of keys
    public int degrees;      // Minimum degree (defines the range for number of keys)
    public Node children[]; // An array of child pointers
    public int numberKeys;     // Current number of keys
    public boolean isLeaf; // Is true when node is leaf. Otherwise false

    public Node() {

    }

    public Node(int degrees, boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.degrees = degrees;
        keys = new Index[2 * degrees - 1];
        children = new Node[2 * degrees];
        numberKeys = 0;
    }

    public Node search(Index key) {
        int i = 0;
        while ((i < numberKeys) && (key.getKey() > keys[i].getKey())) {
            i++;
        }

        if (i < this.numberKeys && keys[i].getKey() == key.getKey()) {
            return this;
        }

        if (isLeaf) {//It doesn't have any children.
            return null;
        }
        return children[i].search(key);

    }

    public void split(int i, Node newNode) {
        Node neoNode = new Node(degrees, newNode.isLeaf);//This node will store degrees-1 keys of the newNode
        neoNode.numberKeys = degrees - 1;
        //It will copy degrees-1 keys and children (if it has any)
        for (int j = 0; j < degrees - 1; j++) {
            neoNode.keys[j] = newNode.keys[j + degrees];
        }
        if (!newNode.isLeaf) {
            for (int j = 0; j < degrees; j++) {
                neoNode.children[j] = newNode.children[j + degrees];
            }
        }

        newNode.numberKeys = degrees - 1;//Reduce the number of Keys

        for (int j = numberKeys; j >= i + 1; j--) {
            children[j + 1] = children[j];//Create space for the new child
        }
        children[i + 1] = neoNode;//The new child will be in this node
        for (int j = numberKeys - 1; j >= i; j--) {
            keys[j + 1] = keys[j];//Find location of new key and move all greater keys one space ahead
        }
        keys[i] = newNode.keys[degrees - 1];//Copy the middle key of the newNode to this node
        numberKeys = numberKeys + 1; //the numer of keys has increased
    }

    public void insertNonFull(Index k) {
        int i = numberKeys - 1;//Rightmost element
        if (isLeaf) {
            while (i >= 0 && keys[i].getKey() > k.getKey()) {
                keys[i + 1] = keys[i];//It will move every key greater than k and leave a space to k in the correct place
                i--;
            }
            keys[i + 1] = k;//insert k in the correct place
            numberKeys = numberKeys + 1;
        } else {
            while (i >= 0 && keys[i].getKey() > k.getKey()) {//which child is going to have k
                i--;
            }
            if (children[i + 1].numberKeys == 2 * degrees - 1) {//if the children are full
                split(i + 1, children[i + 1]);//this will promote the child that is in the middle.
                if (keys[i + 1].getKey() < k.getKey()) {
                    i++;//This will decide which child is going to have k
                }
            }
            children[i + 1].insertNonFull(k);//recursive, inserts k in the correct child
        }
    }

    public int findKey(Index k) {
        int i = 0;
        while ((i < numberKeys) && keys[i].getKey() < k.getKey()) {
            i++;
        }
        return i;
    }

    public void remove(Index k) {
        int i = findKey(k);//finds where the key is
        //The key is in the present node
        if (i < numberKeys && keys[i].getKey() == k.getKey()) {
            if (isLeaf) {
                removeFromLeaf(i);
            } else {
                removeFromNonLeaf(i);
            }
        } else {
            if (isLeaf) {//it means that it is not in the tree.
                System.out.println("La llave no existe");
            }
            boolean flag = false;
            if (i == numberKeys) {//This proves that the key is present in the subtree
                flag = true;
            }
            if (children[i].numberKeys < degrees) {
                fill(i);
            }
            if (flag && i > numberKeys) {
                children[i - 1].remove(k);
            } else {
                children[i].remove(k);
            }
        }
    }//fin remove

    private void removeFromLeaf(int i) {
        for (int j = i + 1; j < numberKeys; j++) {
            if (i - 1 > 0) {
                keys[i - 1] = keys[i];//Moves all the keys one space backwards
            } else {
                keys[i] = keys[i];
            }
        }
        numberKeys--;
    }

    private void removeFromNonLeaf(int i) {
        Index k = keys[i];
        if (children[i].numberKeys >= degrees) {//removes  the predecessor
            Index predecessor = getPredecessor(i);
            keys[i] = predecessor;
            children[i + 1].remove(predecessor);
        } else if (children[i + 1].numberKeys >= degrees) {//removes  the sucessor
            Index sucessor = getSucessor(i);
            keys[i] = sucessor;
            children[i + 1].remove(sucessor);
        } else {
            merge(i);//if there is no suc or pred merge k and all of the children
            children[i].remove(k);
        }
    }

    private Index getPredecessor(int i) {
        Node temp = children[i];
        while (!temp.isLeaf) {
            temp = temp.children[temp.numberKeys];
        }
        return temp.keys[temp.numberKeys - 1];
    }

    private Index getSucessor(int i) {
        Node temp = children[i + 1];
        while (!temp.isLeaf) {
            temp = temp.children[0];
        }
        return temp.keys[0];
    }

    private void fill(int i) {
        if (i != 0 && children[i - 1].numberKeys >= degrees) {//LEFT BROTHER GIVE ME
            borrowFromLeft(i);
        } else if (i != numberKeys && children[i + 1].numberKeys >= degrees) {//RIGHT BRO MY LEFT BRO IS POOR
            borrowFromRight(i);
        } else if (i != numberKeys) {//DAD WHY MY BROTHERS ARE POOR? WE SHOULD MERGE IMO
            merge(i);
        } else {
            merge(i - 1);
        }
    }

    private void borrowFromLeft(int i) {
        Node son = children[i];
        Node brother = children[i - 1];
        //Last key from brother -> to parent -> son
        for (int j = son.numberKeys - 1; j >= 0; j--) {
            son.keys[j + 1] = son.keys[j];
        }
        if (!son.isLeaf) {
            for (int j = son.numberKeys; j >= 0; j--) {
                son.children[j + 1] = son.children[j];
            }
        }
        son.keys[0] = keys[i - 1];
        if (!isLeaf) {
            son.children[0] = brother.children[brother.numberKeys];
        }
        keys[i - 1] = brother.keys[brother.numberKeys - 1];
        son.numberKeys += 1;
        brother.numberKeys -= 1;
    }

    private void borrowFromRight(int i) {
        Node son = children[i];
        Node brother = children[i + 1];
        //brother->parent->son
        son.keys[son.numberKeys] = keys[i];
        if (!son.isLeaf) {
            son.children[son.numberKeys + 1] = brother.children[0];
        }
        keys[i] = brother.keys[0];
        for (int j = 1; j < brother.numberKeys; j++) {
            brother.keys[j - 1] = brother.keys[j];
        }
        if (!brother.isLeaf) {
            for (int j = 1; j < brother.numberKeys; j++) {
                brother.children[j - 1] = brother.children[j];
            }
        }
        son.numberKeys += 1;
        brother.numberKeys -= 1;
    }

    private void merge(int i) {
        Node son = children[i];
        Node brother = children[i + 1];
        son.keys[degrees - 1] = keys[i];
        for (int j = 0; j < brother.numberKeys; j++) {
            son.children[j + degrees] = brother.children[j];
        }
        if (!son.isLeaf) {
            for (int j = 0; j < brother.numberKeys; j++) {
                son.children[j + degrees] = brother.children[j];
            }
        }
        for (int j = i + 1; j < numberKeys; j++) {
            keys[j - 1] = keys[j];
        }
        for (int j = i + 2; j <= numberKeys; j++) {
            children[j - 1] = children[j];
        }
        son.numberKeys += brother.numberKeys + 1;
        numberKeys--;
        brother = null;
    }

}//fin all

//This Btree follows the Algorithm written by Cormen.
//By no means this BTree is 100% ours -Jorge Morazán

// http://staff.ustc.edu.cn/~csli/graduate/algorithms/book6/chap19.htm
