/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package estructura.de.datos.pkg2;

import java.lang.instrument.Instrumentation;

/**
 *
 * @author Acer
 */
public class Persona {

    // char[] nombre = new char[40];
    int id;
    // char[] birthdate = new char[9];
    float salary;
    String name;
    String birthdate;
    char marcador;
    private int referencia;
    public Instrumentation instrumentation;

    public Persona() {
    }

    public int getReferencia() {
        return referencia;
    }

    public void setReferencia(int referencia) {
        this.referencia = referencia;
    }

    public Persona(int id, float salary, String name, String birthdate) {
        this.id = id;
        this.salary = salary;
        this.name = name;
        this.marcador = '-';
        this.birthdate = birthdate;
        this.referencia = 0;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public char getMarcador() {
        return marcador;
    }

    public void setMarcador(char marcador) {
        this.marcador = marcador;
    }

    @Override
    public String toString() {
        return "Persona{" + "id=" + id + ", salary=" + salary + ", name=" + name + ", birthdate=" + birthdate + ", marcador=" + marcador + '}';
    }

    int size() {
        return 68;
        //return 2 * (40+1+10) + 9;
    }

    public String getName() {
        for (int i = name.length(); i < 40; i++) {
            name += " ";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*public Persona(int id, float salary, char[] nombre, char[] birthdate) {
        this.id = id;
        this.salary = salary;
        this.nombre = nombre;
        this.birthdate = birthdate;
    }*/

 /*  public char[] getNombre() {
        return nombre;
    }

    public void setNombre(char[] nombre) {
        this.nombre = nombre;
    }*/
    public long getObjectSize(Object o) {
        return instrumentation.getObjectSize(o);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /*  public char[] getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(char[] birthdate) {
        this.birthdate = birthdate;
    }
     */
    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

}
