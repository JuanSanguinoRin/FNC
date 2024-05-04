package programatc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class test{
    public static void main(String[] args) {
        Modelo m = new Modelo("A,B,C,D,E","1,2,3","A,B1C,DE2,F1,1;B,CDEE1,C,D,2;C,D3D1,B,2,G,?;D,12C,B3C1,2;E,B,D,?,1","A");
        //Modelo m = new Modelo("B,C,D,E","1,2","B,CD1,1,2;C,DE2,2;D,C1C2,2;E,BC,CD1,1","B");
        for(String a:m.generarPalabras()){
            System.out.println(a);
        }
        m.imprimirMatrix();
        System.out.println(Arrays.toString(m.getInalcanzables()));
        System.out.println(Arrays.toString(m.getInutiles()));
        HashMap<String, ArrayList<String>> prodMap = m.getMapaInicial();
        
        System.out.println("Elementos del HashMap:-------------------------");
        System.out.println("Solamente es el mapa inicial antes de unitarias y lambda");
        for (HashMap.Entry<String, ArrayList<String>> mapa : prodMap.entrySet()) {
            System.out.println("Clave: " + mapa.getKey() + ", Valor: " + mapa.getValue());
        }
        m.depurarFinalmente(); // llamar a este metodo para que cargue los metodos de depuracion y chomsky
        HashMap<String, ArrayList<String>> mapad = m.getMapaDepurado();
        System.out.println("Mapa depurado:-------------------------");
        for (HashMap.Entry<String, ArrayList<String>> mapa : mapad.entrySet()) {
            System.out.println("Clave: " + mapa.getKey() + ", Valor: " + mapa.getValue());
        }

        System.out.println("Mapa Chomsky:-------------------------");
        HashMap<String, ArrayList<String>> mapaC = m.getMapaChomsky();
        for (HashMap.Entry<String, ArrayList<String>> mapa : mapaC.entrySet()) {
            System.out.println("Clave: " + mapa.getKey() + ", Valor: " + mapa.getValue());
        }
        
    }
}