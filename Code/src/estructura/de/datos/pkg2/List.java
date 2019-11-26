/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package estructura.de.datos.pkg2;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Acer
 */
public class List {

    private final File file;
    RandomAccessFile raf;
    int headerSize = Integer.BYTES;
    private static int header;
    private static LinkedList list;
    public static BTree tree;
//Constructor de la lista

    public List(File file) throws IOException {
        this.file = file;
        //mira si el archivio existe
        if (file.exists() && !file.isFile()) {
            throw new IOException(file.getName() + " ERROR");
        }
        //Crea el  Random Access File
        this.raf = new RandomAccessFile(file, "rw");
        list = new LinkedList();
        // Crea el arbol B
        tree = new BTree(4);
        this.raf.seek(0);
        // Setea el header
        try {
            if (this.raf.length() > 0) {
                header = this.raf.readInt();
                if (header != -1) {
                    list.add(header);
                    Persona persona = new Persona();
                    this.raf.seek(0 + headerSize);
                    while (true) {
                        this.raf.seek((header - 1) * persona.size() + headerSize);
                        this.raf.readChar();
                        header = this.raf.readInt();
                        if (header != -1) {
                            list.add(0, header);
                        }
                    }
                }
            } else {
                this.raf.writeInt(-1);
            }
        } catch (IOException e) {

        }
    }

    //Metodo que recibe un neoRecord y un id, busca en el indice el id para luego modificarlo
    public boolean modificar(Persona persona2, int id) throws IOException {
        Index pos;
        boolean enviar;
        Index indice2 = new Index(0, id);
        //Busca si el record esta en la lista
        Node nodo = tree.search(indice2);
        pos = buscarIndex(nodo, id);
        int rrn;
        if (pos == null) {
            return false;
        } else {
            rrn = pos.getRrn();
            raf.seek((rrn - 1) * persona2.size() + headerSize);
            raf.writeChar(persona2.getMarcador());
            raf.writeInt(persona2.getReferencia());
            raf.writeInt(persona2.getId());
            raf.writeUTF(persona2.getName());
            raf.writeUTF(persona2.getBirthdate());
            raf.writeFloat(persona2.getSalary());
            tree.remove(pos);
            Index pos2 = new Index(rrn, persona2.getId());
            tree.insertNode(pos2);
            enviar = true;
        }// si el nuevo id es igual al anterior  

        return enviar;
    }
// Metodo para llenar el arbol desde zero 

    public void FillTree() {
        tree = new BTree(4);
        int rrn = 0;
        try {
            Persona persona = new Persona();
            raf.seek(0 + headerSize);
            while (true) {
                rrn++;
                persona.setMarcador(raf.readChar());
                persona.setReferencia(raf.readInt());
                persona.setId(raf.readInt());
                persona.setName(raf.readUTF());
                persona.setBirthdate(raf.readUTF());
                persona.setSalary(raf.readFloat());
                //Si la persona no se ha eliminado se agregara al file
                if (persona.getMarcador() != '*') {
                    Index index = new Index(rrn, persona.getId());
                    tree.insertNode(index);
                }
            }
        } catch (IOException e) {
        }
    }
//metodo para listar todos los records del file

    public DefaultTableModel leer(DefaultTableModel model) throws IOException {
        try {
            //ciclo While para limpiar el table
            while (model.getRowCount() > 0) {
                model.removeRow(0);
            }
            Persona persona = new Persona();
            raf.seek(0 + headerSize);
            while (true) {
                persona.setMarcador(raf.readChar());
                persona.setReferencia(raf.readInt());
                persona.setId(raf.readInt());
                persona.setName(raf.readUTF());
                persona.setBirthdate(raf.readUTF());
                persona.setSalary(raf.readFloat());
                //si la persona no esta borrada se agregara a la lista 
                if (persona.getMarcador() != '*') {
                    model.addRow(new Object[]{persona.getId(), persona.getName(), persona.getBirthdate(), persona.getSalary()});
                }
            }
        } catch (IOException e) {
        }

        return model;
    }
//metodo para introducir nuevos records en el file y en el BTree

    public boolean meter(Persona record) throws IOException {
        boolean created = false;
        Index posicion;
        //Busca si el Record ya se ha guardado antes
        Node nodo = tree.search(new Index(0, record.getId()));
        posicion = this.buscarIndex(nodo, record.getId());
        //si se ha encontrado se devolvera null de inmediato
        if (posicion != null) {
            return false;
        } else {
            try {
                raf.seek(0 + headerSize);
                while (true) {
                    if (list.isEmpty()) {
                        created = true;
                        raf.seek(file.length());
                        raf.writeChar(record.getMarcador());
                        raf.writeInt(0);
                        raf.writeInt(record.getId());
                        raf.writeUTF(record.getName());
                        raf.writeUTF(record.getBirthdate());
                        raf.writeFloat(record.getSalary());
                        tree.insertNode(new Index((int) file.length() / record.size(), record.getId()));
                        break;
                    } else {
                        raf.seek(0);
                        created = true;
                        int pos = (int) list.remove(0);
                        int ref;
                        raf.seek(record.size() * (pos - 1) + headerSize);
                        raf.readChar();
                        ref = raf.readInt();
                        raf.seek(0);
                        raf.writeInt(ref);
                        raf.seek(record.size() * (pos - 1) + headerSize);
                        raf.writeChar(record.getMarcador());
                        raf.writeInt(0);
                        raf.writeInt(record.getId());
                        raf.writeUTF(record.getName());
                        raf.writeUTF(record.getBirthdate());
                        raf.writeFloat(record.getSalary());
                        tree.insertNode(new Index(pos, record.getId()));
                        break;
                    }
                }
            } catch (IOException e) {

            }
        }
        return created;
    }
// Metodo para eliminar records Dentro del file y arbol

    public boolean delete(int id) throws IOException {
        Persona persona = new Persona();
        boolean found = false;
        Index rrn;
        try {
            Node nodo = tree.search(new Index(0, id));
            rrn = this.buscarIndex(nodo, id); //Busca si ya se ha creado el record antes
            if (rrn == null) {// si no lo encuentra devuelve false de inmediato 
                return found;
            } else {
                raf.seek((rrn.getRrn() - 1) * persona.size() + headerSize);
                persona.setMarcador(raf.readChar());
                //si la persona no esta eliminada se agrega 
                if (persona.getMarcador() != '*') {
                    found = true;
                    persona.setMarcador('*');
                    raf.seek(0);
                    header = raf.readInt();
                    if (header == -1) {
                        raf.seek(0);
                        raf.writeInt(rrn.getRrn());
                        raf.seek((rrn.getRrn() - 1) * persona.size() + headerSize);
                        raf.writeChar(persona.getMarcador());
                        raf.writeInt(header);
                        list.add(0, rrn.getRrn());
                        tree.remove(rrn);
                    } else {
                        raf.seek(0);
                        raf.writeInt(rrn.getRrn());
                        raf.seek((rrn.getRrn() - 1) * persona.size() + headerSize);
                        raf.writeChar(persona.getMarcador());
                        raf.writeInt(header);
                        list.add(0, rrn.getRrn());
                        tree.remove(rrn);
                    }
                    return found;
                }
            }

        } catch (EOFException e) {

        }
        return found;
    }
// meotod para buscar un indice en un nodo 

    public Index buscarIndex(Node nodo, int id) {
        Index temp = null;
        if (nodo != null) {
            for (Index key : nodo.keys) {
                if (key.getKey() == id) {
                    temp = key;
                    break;
                }
            }
        }
        return temp;
    }
// metodo para buscar con el arbol 

    public Persona buscar(int id) throws IOException {
        Persona persona = new Persona();// Record temporal el cual se devolvera
        //comienza a buscar si el record se ha guardado antes
        Index indice;
        Index indice2 = new Index(0, id);
        Node nodo = tree.search(indice2);
        indice = buscarIndex(nodo, id);
        //si el record se ha encontrado sera null, por lo tanto no se devolvera la persona
        if (indice == null) {
            return null;
        } else {
            int pos = indice.getRrn();
            raf.seek((pos - 1) * persona.size() + headerSize);
            persona.setMarcador(raf.readChar());
            persona.setReferencia(raf.readInt());
            persona.setId(raf.readInt());
            persona.setName(raf.readUTF());
            persona.setBirthdate(raf.readUTF());
            persona.setSalary(raf.readFloat());
            return persona;
        }

    }
//Metodo para guardar el arbol en un file

    public void guardarArbol() {
        this.FillTree();
        try {
            FileOutputStream fw = new FileOutputStream("Tree.dat");
            ObjectOutputStream bw = new ObjectOutputStream(fw);
            bw.writeObject(tree);
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {
        }
    }
//Metodo para cargar el arbol desde el archivo
    public boolean cargar() {
        FileInputStream inFile;
        try {
            inFile = new FileInputStream("Tree.dat");
            ObjectInputStream inputObject = new ObjectInputStream(inFile);
            Object objeto = inputObject.readObject();
            if (objeto instanceof BTree) {
                tree = (BTree) objeto;
                return true;
            }
        } catch (FileNotFoundException ex) {
        } catch (IOException | ClassNotFoundException ex) {
        }
        return false;
    }
}
