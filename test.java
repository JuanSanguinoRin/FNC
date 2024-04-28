package programatc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class test{
    public static void main(String[] args) {
        Modelo m = new Modelo("D,E,F,G,J","a,b,c,h","D,Ea,Fb,c,1;E,E*,Ca,a,*;F,Da,Cb,b,9;X,89,89,89,1","D");
        //Modelo m = new Modelo("B,C,D,E","1,2","B,CD1,1,2;C,DE2,2;D,C1C2,2;E,BC,CD1,1","B");
        for(String a:m.generarPalabras()){
            System.out.println(a);
        }
        m.imprimirMatrix();
        //System.out.println(Arrays.toString(m.getInalcanzables()));
        //System.out.println(Arrays.toString(m.getInutiles()));
        HashMap<String, ArrayList<String>> prodMap = m.getMapaInicial();
        System.out.println("Elementos del HashMap:-------------------------");
        for (HashMap.Entry<String, ArrayList<String>> mapa : prodMap.entrySet()) {
            System.out.println("Clave: " + mapa.getKey() + ", Valor: " + mapa.getValue());
        }
    }
}