/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package estructura.de.datos.pkg2;

import java.io.Serializable;

/**
 *
 * @author jrafa
 */
public class Index implements Serializable {
    private int rrn;
    private int key;
    public Index (){
        
    }
    public Index(int rrn, int key) {
        this.rrn = rrn;
        this.key = key;
    }

    public int getRrn() {
        return rrn;
    }

    public int getKey() {
        return key;
    }

    public void setRrn(int rrn) {
        this.rrn = rrn;
    }

    public void setKey(int key) {
        this.key = key;
    }
    
    
}
