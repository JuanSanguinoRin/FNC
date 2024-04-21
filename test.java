package programatc;

import java.util.Arrays;

public class test{
    public static void main(String[] args) {
        Modelo m = new Modelo("D,E,F,G,J","a,b,c,h","D,Ea,Fb,c,1;E,E*,Ca,a,*;F,Da,Cb,b,9;X,89,89,89,1","D");
        //Modelo m = new Modelo("B,C,D,E","1,2","B,CD1,1,2;C,DE2,2;D,C1C2,2;E,BC,CD1,1","B");
        for(String a:m.generarPalabras()){
            System.out.println(a);
        }
        m.imprimirMatrix();
        System.out.println(Arrays.toString(m.getInalcanzables()));
        System.out.println(Arrays.toString(m.getInutiles()));
    }
}