/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.edu.ufps.trabajochomsky;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EjercicioTc {
    ArrayList<String> terminales;
    ArrayList<String> noTerminales;
    String inicial;
    int numProducciones;
    HashMap<String, ArrayList<String>> prodMap;

    public EjercicioTc() {
        terminales = new ArrayList<>();
        noTerminales = new ArrayList<>();
        inicial = "";
        numProducciones = 0;
        prodMap = new HashMap<>();
      
    }

    public boolean almacenarTerminales(String cadena) {
        try {
            String[] vt2 = cadena.split(" ");
            for (String letra : vt2) {
                terminales.add(letra);
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public boolean almacenarNoTerminales(String cadena) {
        
        try {
            String[] vnt2 = cadena.split(" ");
            for (String letra : vnt2) {
                noTerminales.add(letra);
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public boolean almacenarProd(String cadena) {
        try {
            String[] p2 = cadena.split("-");
            if (p2.length > 1) {
                String[] reglas = p2[1].split("/");
                ArrayList<String> texto = new ArrayList<>();
                for (String s : reglas) {
                    texto.add(s);
                }
                prodMap.put(p2[0], texto);
                return true;
            } else {
                throw new IllegalArgumentException("Ingrese la cadena con el formato estipulado.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<String> detectarVariablesInalcanzables(HashMap<String, ArrayList<String>> producciones,
            ArrayList<String> varNT, ArrayList<String> varT) {
        ArrayList<String> inalcanzables = new ArrayList<>();
        ArrayList<String> llavesDeclaradas = new ArrayList<>();
        HashSet<String> declaradas = new HashSet<>();

        llavesDeclaradas.addAll(producciones.keySet());
        inalcanzables = noLLaves(llavesDeclaradas, varNT);

        for (ArrayList<String> produccion : producciones.values()) {
            for (String cadena : produccion) {
                for (int i = 0; i < cadena.length(); i++) {
                    String simbolo = String.valueOf(cadena.charAt(i));
                    declaradas.add(simbolo);
                }
            }
        }
        // aqui cambie una monda
        for (String variable : declaradas) {
            if (!variable.equals("@") && !varT.contains(variable) && !varNT.contains(variable)
                    && !inalcanzables.contains(variable)) {
                inalcanzables.add(variable);
            }
        }
        return inalcanzables;
    }

    public static ArrayList<String> noLLaves(ArrayList<String> llaves, ArrayList<String> noTerminales) {
        HashSet<String> llavesSet = new HashSet<>(llaves);
        ArrayList<String> noTerminalesNoEnLlaves = new ArrayList<>();

        for (String noTerminal : noTerminales) {
            if (!llavesSet.contains(noTerminal)) {
                noTerminalesNoEnLlaves.add(noTerminal);
            }
        }

        return noTerminalesNoEnLlaves;
    }

    public ArrayList<String> detectarVariablesInutiles(HashMap<String, ArrayList<String>> producciones,
            ArrayList<String> varT, ArrayList<String> varNT, String inicial) {
        ArrayList<String> inutiles = new ArrayList<>();

        HashSet<String> usadas = new HashSet<>();
        usadas.add(inicial);

        for (String noTerminal : producciones.keySet()) {
            ArrayList<String> produccionActual = producciones.get(noTerminal);
            for (String cadena : produccionActual) {
                for (int i = 0; i < cadena.length(); i++) {
                    String simbolo = String.valueOf(cadena.charAt(i));
                    if (!noTerminal.equals(simbolo)) {
                        usadas.add(simbolo);
                    }
                }
            }
        }

        for (String variable : varT) {
            if (!usadas.contains(variable) && !inutiles.contains(variable)) {
                inutiles.add(variable);
            }
        }

        for (String variable : varNT) {
            if (!usadas.contains(variable) && !inutiles.contains(variable)) {
                inutiles.add(variable);
            }
        }

        return inutiles;
    }

    public String mostrarVariables(ArrayList<String> lista) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < lista.size(); i++) {
            stringBuilder.append(lista.get(i));
            if (i < lista.size() - 1) {
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.toString();
    }

    public HashMap<String, ArrayList<String>> depurarMatriz(HashMap<String, ArrayList<String>> producciones,
            ArrayList<String> varT, ArrayList<String> varNT,
            ArrayList<String> inutiles, String inicial, ArrayList<String> inalcanzable) {
        HashMap<String, ArrayList<String>> produccionesDepuradas = new HashMap<>();
        for (String noTerminal : producciones.keySet()) {
            if (noTerminal.equals(inicial) || (varNT.contains(noTerminal) && !inutiles.contains(noTerminal))) {
                ArrayList<String> produccion = producciones.get(noTerminal);
                ArrayList<String> produccionDepurada = new ArrayList<>();

                for (String cadena : produccion) {
                    if (esValido(cadena, varT, varNT, inalcanzable)) {
                        produccionDepurada.add(cadena);
                    }
                }
                produccionesDepuradas.put(noTerminal, produccionDepurada);
            }
        }

        return produccionesDepuradas;
    }

    public static boolean esValido(String simbolo, ArrayList<String> varT, ArrayList<String> varNT,
            ArrayList<String> inalcanzable) {
        for (int i = 0; i < simbolo.length(); i++) {
            char caracter = simbolo.charAt(i);
            String caracterStr = String.valueOf(caracter);
            if (!caracterStr.equals("@") && !varT.contains(caracterStr) && !varNT.contains(caracterStr)
                    || inalcanzable.contains(caracterStr)) {
                return false;
            }
        }
        return true;
    }

    public String mostrarProposicionesDepuradas(HashMap<String, ArrayList<String>> produccionesDepuradas) {
        StringBuilder stringBuilder = new StringBuilder();

        for (HashMap.Entry<String, ArrayList<String>> mapa : produccionesDepuradas.entrySet()) {
            stringBuilder.append("Clave: ").append(mapa.getKey()).append(", Valor: ");

            ArrayList<String> valores = mapa.getValue();
            for (int i = 0; i < valores.size(); i++) {
                stringBuilder.append(valores.get(i));
                if (i < valores.size() - 1) {
                    stringBuilder.append("/");
                }
            }

            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    public ArrayList<String> lenguaje(String simbIni, ArrayList<String> varT, ArrayList<String> varNT,
            HashMap<String, ArrayList<String>> reglas, int profundidadMaxima) {
        HashSet<String> palabrasSet = new HashSet<>();
        expandir(simbIni, palabrasSet, "", varT, varNT, reglas, profundidadMaxima);
        return new ArrayList<>(palabrasSet);
    }

    private void expandir(String simbolo, HashSet<String> palabras, String actual, ArrayList<String> varT,
            ArrayList<String> varNT, HashMap<String, ArrayList<String>> reglas, int profundidadMaxima) {
        if (simbolo.isEmpty() || profundidadMaxima == 0) {
            palabras.add(actual);
            return;
        }

        for (int i = 0; i < simbolo.length(); i++) {
            char c = simbolo.charAt(i);
            String cStr = String.valueOf(c);
            if (varT.contains(cStr)) {
                expandir(simbolo.substring(1), palabras, actual + cStr, varT, varNT, reglas, profundidadMaxima);
            } else if (varNT.contains(cStr) && reglas.containsKey(cStr)) {
                ArrayList<String> producciones = reglas.get(cStr);
                for (String produccion : producciones) {
                    expandir(produccion + simbolo.substring(1), palabras, actual, varT, varNT, reglas,
                            profundidadMaxima - 1);
                }
            }
        }
    }

    public HashMap<String, ArrayList<String>> eliminarUnitarias(HashMap<String, ArrayList<String>> prodMap,
            ArrayList<String> varNT) {
        HashMap<String, ArrayList<String>> prodSinUnitarias = new HashMap<>();
        for (String key : prodMap.keySet()) {
            ArrayList<String> producciones = prodMap.get(key);
            ArrayList<String> produccionesSinUnitarias = new ArrayList<>();
            for (String produccion : producciones) {
                if (produccion.length() == 1 && varNT.contains(produccion)) {
                    ArrayList<String> copia = prodMap.get(produccion);
                    for (String copy : copia) {
                        if (!varNT.contains(copy)) {
                            produccionesSinUnitarias.add(copy);
                        }
                    }
                } else {
                    produccionesSinUnitarias.add(produccion);
                }
            }
            ArrayList<String> produccionesListas = removeDuplicates(produccionesSinUnitarias);
            prodSinUnitarias.put(key, produccionesListas);
        }
        return prodSinUnitarias;
    }

    public  ArrayList<String> removeDuplicates(ArrayList<String> arr) {

        HashSet<String> uniqueSet = new HashSet<>(arr);
        return new ArrayList<>(uniqueSet);
    }

    public HashMap<String, ArrayList<String>> eliminarEpsilon(HashMap<String, ArrayList<String>> prodMap) {
        ArrayList<String> llavesConEpsilon = new ArrayList<>();

        for (String noTerminal : prodMap.keySet()) {
            ArrayList<String> produccionActual = prodMap.get(noTerminal);
            Iterator<String> iterador = produccionActual.iterator();
            while (iterador.hasNext()) {
                String cadena = iterador.next();
                if (cadena.equals("@")) {
                    llavesConEpsilon.add(noTerminal);
                    iterador.remove();

                }
            }
        }

        if (llavesConEpsilon.isEmpty()) {
            return prodMap;
        }

        for (String llave : llavesConEpsilon) {
            for (String key : prodMap.keySet()) {
                ArrayList<String> producciones = prodMap.get(key);
                ArrayList<String> produccionesSinEpsilon = new ArrayList<>();

                for (String produccion : producciones) {
                    if (produccion.length() == 1 && produccion.contains(llave)) {
                        produccion = "@";
                        produccionesSinEpsilon.add(produccion);
                    } else if (produccion.contains(llave)) {
                        ArrayList<String> combinaciones = todasLasCombinaciones(produccion, llave);
                        produccionesSinEpsilon.addAll(combinaciones);
                    } else {
                        produccionesSinEpsilon.add(produccion);
                    }
                }
                ArrayList<String> produccionesListas = removeDuplicates(produccionesSinEpsilon);
                prodMap.put(key, produccionesListas);
            }
        }

        return eliminarEpsilon(prodMap);
    }

    public ArrayList<String> todasLasCombinaciones(String s, String charABorrar) {
        ArrayList<String> resultado = new ArrayList<>();
        generarCombinaciones(s, charABorrar, 0, "", resultado);
        resultado.remove("");
        return resultado;
    }

    private void generarCombinaciones(String s, String charABorrar, int indice, String combinacionActual,
            ArrayList<String> resultado) {
        if (indice == s.length()) {
            // Caso base: se llegó al final de la cadena
            resultado.add(combinacionActual);
            return;
        }

        // Incluir el carácter actual
        generarCombinaciones(s, charABorrar, indice + 1, combinacionActual + s.charAt(indice), resultado);

        // Excluir el carácter actual si coincide con el carácter a borrar
        if (s.substring(indice, indice + 1).equals(charABorrar)) {
            generarCombinaciones(s, charABorrar, indice + 1, combinacionActual, resultado);
        }
    }


    //mondas para hacer todo en chomsky
    public boolean verificar_fnc_hashmap(HashMap<String, ArrayList<String>> prodMap) {
        for (String key : prodMap.keySet()) {
            ArrayList<String> producciones = prodMap.get(key);
            if (!verificar_fnc_lista(producciones)) {
                return false;
            }
        }
        return true;
    }

    public boolean verificar_fnc_lista(ArrayList<String> lista) {
        for (String termino : lista) {
            if (!verificar_termino(termino)) {
                return false;
            }
        }
        return true;
    } 

    public boolean verificar_termino(String str) {
        if (str.length() > 2) {
            return false;
        }
        return true;
    }

    
    public HashMap<String, ArrayList<String>> chomsky(HashMap<String, ArrayList<String>> mapa) {
        HashMap<String, ArrayList<String>> mapaChomsky = new HashMap<>(mapa);
        int contador = 1;
        boolean cambio = true;
        while (cambio) {
            cambio = false;
            HashMap<String, ArrayList<String>> copiaMapaChomsky = new HashMap<>(mapaChomsky);
            mapaChomsky.clear();
            for (String key : copiaMapaChomsky.keySet()) {
                ArrayList<String> producciones = copiaMapaChomsky.get(key);
                ArrayList<String> nuevaRegla = new ArrayList<>();
                for (int i = 0; i < producciones.size(); i++) {
                    ArrayList<String> reglaq = new ArrayList<>();
                    if (!verificar_termino(producciones.get(i))) {
                        String nuevoTermino = producciones.get(i).substring(1, producciones.get(i).length());
                        reglaq.add(nuevoTermino);
                        mapaChomsky.put("Q" + contador, reglaq);
                        nuevaRegla.add(producciones.get(i).substring(0, 1) + "Q" + contador);
                        contador++;
                        cambio = true;
                    } else {
                        nuevaRegla.add(producciones.get(i));
                    }
                }
                mapaChomsky.put(key, nuevaRegla);
            }
            if (!cambio) {
                break;
            }
        }
        return mapaChomsky;
    }
    
    
    

    public HashMap<String, ArrayList<String>> depuracionTotal(HashMap<String, ArrayList<String>> mapa,
            ArrayList<String> varNT,
            ArrayList<String> varT, String keyInicial) {

        HashMap<String, ArrayList<String>> mapaDepuradoSimple = new HashMap<>();
        mapa = eliminarRrecursividad(mapa);
        ArrayList<String> inutiles = detectarVariablesInutiles(mapa, varNT, varT, keyInicial);
        ArrayList<String> inalcanzables = detectarVariablesInalcanzables(mapa, varNT, varT);
        
        mapaDepuradoSimple = depurarMatriz(mapa, varT, varNT, inutiles, keyInicial, inalcanzables);
        terminales.removeIf(inutiles::contains);
        terminales.removeIf(inalcanzables::contains);
        int contadorEpsilon = 0;
        int contadorUnitarias = 0;

        for (String llave : mapaDepuradoSimple.keySet()) {
            ArrayList<String> produccion = mapaDepuradoSimple.get(llave);
            for (String cadena : produccion) {
                if (cadena.equals("@")) {
                    contadorEpsilon++;
                }
                if (cadena.length() == 1) {
                    contadorUnitarias++;
                }
            }
        }
        if (contadorEpsilon >= contadorUnitarias) {
            mapaDepuradoSimple = eliminarEpsilon(mapaDepuradoSimple);
            mapaDepuradoSimple = eliminarUnitarias(mapaDepuradoSimple, varNT);
            mapaDepuradoSimple = eliminarEpsilon(mapaDepuradoSimple);
            mapaDepuradoSimple = eliminarUnitarias(mapaDepuradoSimple, varNT);
        } else {
            mapaDepuradoSimple = eliminarUnitarias(mapaDepuradoSimple, varNT);
            mapaDepuradoSimple = eliminarEpsilon(mapaDepuradoSimple);
            mapaDepuradoSimple = eliminarUnitarias(mapaDepuradoSimple, varNT);
            mapaDepuradoSimple = eliminarEpsilon(mapaDepuradoSimple);
        }
        
        return mapaDepuradoSimple;
    }

    public HashMap<String, ArrayList<String>> eliminarRrecursividad(HashMap<String, ArrayList<String>> producciones) {
        HashMap<String, ArrayList<String>> mapaSinRecursividad = new HashMap<>();
        HashMap<String, ArrayList<String>> mapaSemiDepurado = new HashMap<>();
        ArrayList<String> keyRecursivas = new ArrayList<>();
        int contadorNoRecursividad = 0;

        for (String llave : producciones.keySet()) {
            ArrayList<String> produccion = producciones.get(llave);
            contadorNoRecursividad = 0;
            for (String cadena : produccion) {
                if (!cadena.contains(llave)) {
                    contadorNoRecursividad++;
                }
            }
            if (contadorNoRecursividad > 0) {
                mapaSinRecursividad.put(llave, produccion);
            } else {
                keyRecursivas.add(llave);
            }
        }

        if (keyRecursivas.isEmpty()) {
            return mapaSinRecursividad;
        } else {
            for (String llave : keyRecursivas) {
                for (String noTerminal : mapaSinRecursividad.keySet()) {
                    ArrayList<String> produccion = producciones.get(noTerminal);
                    ArrayList<String> produccionDepurada = new ArrayList<>();

                    for (String cadena : produccion) {
                        if (!cadena.contains(llave)) {
                            produccionDepurada.add(cadena);
                        }
                    }
                    mapaSemiDepurado.put(noTerminal, produccionDepurada);
                }
            }
        }
        return mapaSemiDepurado;
    }

        
     public ObservableList<String> cargarListViewPalabras(ArrayList<String> palabras) {
        ObservableList<String> items = FXCollections.observableArrayList();
        items.addAll(palabras);
        return items;
    } 


      //DE AQUI HACIA ABAJO EMPIEZA CHOMSKY
    // Reducir producciones




    //------------------METODOS APARTE PARA CHUMSKY QUE DEBERIAN SERVIR PERO AUN NO LO SE ----------------------

    public HashMap<String, ArrayList<String>> reemplazarTerminales(HashMap<String, ArrayList<String>> producciones, ArrayList<String> varT ){
        HashMap<String, String> reemplazos = new HashMap<>();
        HashMap<String, ArrayList<String>> produccionesReemplazadas = new HashMap<>();
        int contador = 0;

        for (String terminal : varT) {
            if(!reemplazos.containsKey(terminal)){
                String nuevaVariable = "<X" + contador++ + ">";
                reemplazos.put(terminal, nuevaVariable);
            }
        }

        for (String noTerminal : producciones.keySet()) {
            ArrayList<String> produccionActual = producciones.get(noTerminal);
            ArrayList<String> nuevaProduccion = new ArrayList<>();

            for (String cadena : produccionActual) {
                
                if(cadena.length() > 1){
                    StringBuilder nuevaCadena = new StringBuilder();

                    for (int i = 0; i < cadena.length(); i++) {
                        String simbolo1 = String.valueOf(cadena.charAt(i));
                        if(simbolo1.equals("<")){
                            if(cadena.charAt(i+3) == '>'){
                                nuevaCadena.append(cadena.substring(i, i+3));
                                i=i+3;
                            } else {
                                nuevaCadena.append(cadena.substring(i, i+4));
                                i=i+4;
                            }
                            String simbolo2 = String.valueOf(cadena.charAt(i));
                            if (reemplazos.containsKey(simbolo2)) {
                                nuevaCadena.append(reemplazos.get(simbolo2));
                            } else {
                                nuevaCadena.append(simbolo2);
                            }
                        }else{
                            if (reemplazos.containsKey(simbolo1)) {
                                nuevaCadena.append(reemplazos.get(simbolo1));
                            } else {
                                nuevaCadena.append(simbolo1);
                            }
                        }
                    }
                    nuevaProduccion.add(nuevaCadena.toString());
                }else{
                    nuevaProduccion.add(cadena);
                }
            }
            produccionesReemplazadas.put(noTerminal, nuevaProduccion);
        }

        for (String terminal : reemplazos.keySet()) {
            String noTerminal = reemplazos.get(terminal);
            ArrayList<String> produccionNoTerminal = new ArrayList<>();
            produccionNoTerminal.add(terminal); 
            produccionesReemplazadas.put(noTerminal, produccionNoTerminal);
        }

        return produccionesReemplazadas;
    }
    

    //binarios con mendoza y juan

public HashMap<String, ArrayList<String>> chomskyEnDuos(HashMap<String, ArrayList<String>> producciones) {
        
        HashMap<String,  ArrayList<String>> mapaCompletamenteEnChusmky = new HashMap<>();

        for (String noTerminal : producciones.keySet()){
            ArrayList<String> produccionActual = producciones.get(noTerminal);
            ArrayList<String> nuevaProduccion = new ArrayList<>();

            for (String cadena : produccionActual){
                
                if(cadena.length() > 2){
                    String variableEnChumsky = produccionChumsky(cadena);
                    nuevaProduccion.add(variableEnChumsky);
                }else{
                    nuevaProduccion.add(cadena);
                }
            }
            mapaCompletamenteEnChusmky.put(noTerminal, nuevaProduccion);
        }

        for (String terminal : mapaQs.keySet()){
            String noTerminal = mapaQs.get(terminal);
            ArrayList<String> produccionNoTerminal = new ArrayList<>();
            produccionNoTerminal.add(terminal); 
            mapaCompletamenteEnChusmky.put(noTerminal, produccionNoTerminal);
        }
        
        return mapaCompletamenteEnChusmky;
    }

    HashMap<String, String> mapaQs = new HashMap<>();
    int contadorGlobal = 1;

    public String produccionChumsky(String produccion){
        if(produccion.charAt(1) == '<'){
            return produccion;
        } else{
            // primera vez que entra
            if (produccion.charAt(produccion.length() - 1) != '>'){
                String llaveQ = produccion.substring(produccion.length()-2);
                String produccionConQ;
                if(!mapaQs.containsKey(llaveQ)){
                    String nuevaVariable = "<Q" + contadorGlobal++ + ">";
                    mapaQs.put(llaveQ, nuevaVariable);
                    produccionConQ = produccion.substring(0, produccion.length() - 2) + nuevaVariable;
                }else{
                    produccionConQ = produccion.substring(0, produccion.length() - 2) + mapaQs.get(llaveQ);
                }
                return produccionChumsky(produccionConQ);
            }//otra vez, pero ahora produccion va a tener > al final //Ya no entiendo una mondá
             else { 
                if(contadorGlobal < 10 || produccion.charAt(produccion.length() - 3) == 'Q'){

                    String llaveQ = produccion.substring(produccion.length()-5);
                    //puede que no sirva por el -5
                    String produccionConQ;
                    if(!mapaQs.containsKey(llaveQ)){
                        String nuevaVariable = "<Q" + contadorGlobal++ + ">";
                        mapaQs.put(llaveQ, nuevaVariable);
                        produccionConQ = produccion.substring(0, produccion.length() - 5) + nuevaVariable;
                    }else{
                        produccionConQ = produccion.substring(0, produccion.length() - 5) + mapaQs.get(llaveQ);
                    }
                    return produccionChumsky(produccionConQ);
                } else {
                    String llaveQ = produccion.substring(produccion.length()-6);
                    
                    //puede que no sirva por el -5
                    String produccionConQ;
                    if(!mapaQs.containsKey(llaveQ)){
                        String nuevaVariable = "<Q" + contadorGlobal++ + ">";
                        mapaQs.put(llaveQ, nuevaVariable);
                        produccionConQ = produccion.substring(0, produccion.length() - 6) + nuevaVariable;
                    }else{
                        produccionConQ = produccion.substring(0, produccion.length() - 6) + mapaQs.get(llaveQ);
                    }
                    return produccionChumsky(produccionConQ);
                }
            }
        }
    }

    

    public java.util.ArrayList<java.lang.String> getNoTerminales() {
        return this.noTerminales;
    }// end method getNoTerminales

    // *SET Method Propertie noTerminales/
    public void setNoTerminales(java.util.ArrayList<java.lang.String> noTerminales) {
        this.noTerminales = noTerminales;
    }// end method setNoTerminales

    // *GET Method Propertie prodMap/
    public java.util.HashMap<java.lang.String, java.util.ArrayList<java.lang.String>> getProdMap() {
        return this.prodMap;
    }// end method getProdMap

    // *SET Method Propertie prodMap/
    public void setProdMap(java.util.HashMap<java.lang.String, java.util.ArrayList<java.lang.String>> prodMap) {
        this.prodMap = prodMap;
    }// end method setProdMap

    public String getInicial() {
        return this.inicial;
    }// end method getInicial

    public void setInicial(String inicial) {
        this.inicial = inicial;
    }// end method setInicial

    public int getNumProducciones() {
        return this.numProducciones;
    }// end method getNumProducciones

    // *SET Method Propertie numProducciones/
    public void setNumProducciones(int numProducciones) {
        this.numProducciones = numProducciones;
    }// end method setNumProducciones

    // *SET Method Propertie terminales/
    public void setTerminales(java.util.ArrayList<java.lang.String> terminales) {
        this.terminales = terminales;
    }// end method setTerminales

    // *GET Method Propertie terminales/
    public java.util.ArrayList<java.lang.String> getTerminales() {
        return this.terminales;
    }

}


