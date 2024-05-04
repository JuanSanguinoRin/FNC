package programatc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Iterator;

public class Modelo {
    private String[][] matrix;
    private String[] inalcanzables;
    private String[] inutiles;
    private String noterminales;
    private String terminales;
    private String input;
    private String reglaInicial;
    private String[] terminalesvec;
    private String[] noterminalesvec;
    //primer mapa de la matrix, sin usar metodos aun
    // este no se usa para la interfaz, es un intermedio
    private HashMap<String, ArrayList<String>> mapainicial;
    //primer mapa que se va a mostrar en la interfaz, ya tiene su get y set
    private HashMap<String, ArrayList<String>> mapaDepurado;
    //segundo mapa a mostrar en la interfaz
    private HashMap<String, ArrayList<String>> mapaChomsky;
    private HashMap<String, ArrayList<String>> mapaChomskyConterminales;
    //mapa que va a contener a las nuevas Qs que se vallan creando, este se une con el mapa chomsky
    private HashMap<String, String> mapaQs = new HashMap<>();
    public Modelo() {
		
    }
    public String[] getTerminalesVec(){
        return terminalesvec;
    }

    public Modelo(String noterminales, String terminales, String input, String reglaInicial) {
        this.noterminales = noterminales;
        this.terminales = terminales;
        this.input = input;
        this.reglaInicial = reglaInicial;
    }

    // Metodo principal al que se debe llamar para que se generen las palabras
    public String[] generarPalabras() {
        matrix = generarMatrix(input);
        terminalesvec = terminales.split(",");
        terminalesvec = agregarLambda(terminalesvec, "?");
        noterminalesvec = noterminales.split(",");
        inalcanzables = depurarMatriz(terminalesvec, noterminalesvec);
        
        String[] inutilesNoTerminales = depurarNoTerminales(noterminalesvec, matrix);
		
        String[] inutilesTerminales = depurarTerminales(terminalesvec, matrix);
        terminalesvec = eliminarUltimoElemento(terminalesvec);
        inutiles = combinarArrays(inutilesNoTerminales, inutilesTerminales);
        String[] palabras = new String[10];
        for (String a:terminalesvec){
            System.out.println(a);
        }
        int control = 0;
        for (int i = 0; i < 10; i++) {
            String[] palabravec = produccionFilaAzar(matrix, reglaInicial).split("");
            palabras[i] = palabraFinal(palabravec, noterminalesvec, matrix, control);
        }
        mapainicial = convertirAHashMap(matrix);
        return palabras;
    }

    //añadir lambda como ? al final para que no las elimine
    public String[] agregarLambda(String[] miVector, String nuevoElemento){
        // Crear un nuevo vector con una longitud mayor
        String[] nuevoVector = new String[miVector.length + 1];
        
        // Copiar los elementos del vector original al nuevo vector
        System.arraycopy(miVector, 0, nuevoVector, 0, miVector.length);
        nuevoVector[nuevoVector.length - 1] = nuevoElemento;
        return nuevoVector;
    }

    //quitar lambda del ultimo elemento del vector de terminales
    public String[] eliminarUltimoElemento(String[] vector) {
        // Verificar si el vector tiene elementos
        if (vector == null || vector.length == 0) {
            return vector;
        }
        
        // Crear un nuevo vector con una longitud menor
        String[] nuevoVector = new String[vector.length - 1];
        
        // Copiar todos los elementos excepto el último
        for (int i = 0; i < nuevoVector.length; i++) {
            nuevoVector[i] = vector[i];
        }
        return nuevoVector;
    }

    // este metodo toma la entrada y la convierte en una matrix
    public String[][] generarMatrix(String input) {
        String[] sections = input.split(";");
        matrix = new String[sections.length][15];
        for (int i = 0; i < sections.length && i < matrix.length; i++) {
            String[] row = sections[i].split(",");
            int rowLength = row.length;
            if (rowLength > matrix[i].length) {
                rowLength = matrix[i].length;
            }
            System.arraycopy(row, 0, matrix[i], 0, rowLength);

            // Llenar espacios sobrantes con null
            for (int j = rowLength; j < matrix[i].length; j++) {
                matrix[i][j] = null;
            }
        }
        return matrix;
    }

    // va a buscar en el primer elemento de cada fila para saber el indice de donde
    // se encuentrea esa variable no terminal
    // es necesario para la produccion al azar de una fila en especifico
    public int indexReglaInicialMatrix(String[][] matrix, String letra) {
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][0] == null) {
                i++;
            }
            if (matrix[i][0].equals(letra)) {
                return i; // Devuelve el índice de la fila donde se encontró la letra
            }
        }
        // Si no se encuentra la letra, muestra un mensaje y detiene la ejecución
        System.out.println("Esta Regla no está en la matriz");
        System.exit(0); // Detiene la ejecución del programa
        return -1;
    }

    // devuelve una produccion de la fila donde se encuentra la letra que se le haya
    // dado
    public String produccionFilaAzar(String[][] matrix, String letra) {
        Random random = new Random();
        String p;
        int fila = indexReglaInicialMatrix(matrix, letra);
        do {
            int aux = random.nextInt(matrix[fila].length) + 1; // este mas uno es para que el la columna elegida nunca
                                                               // sea la primera
                                                               // ya que ahi siempre va a haber una variable no terminal
            if (aux == matrix[fila].length) {
                aux = aux - 1; // este menos uno es por si se pasa, no de error
            }
            p = matrix[fila][aux];
        } while (p == null);
        return p;
    }

    // comprueba si un elemento del vector a se encuentra en el vector b
    public boolean tieneNoTerminales(String[] a, String[] b) {
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < a.length; j++) {
                if (b[i].equals(a[j])) {
                    return true;
                }
            }
        }
        return false;
    }

    // Este metodo toma la produccion elegida ya separada en un vector y comprueba
    // si tiene variables no terminales hasta eliminarlas todas
    public String palabraFinal(String[] palabraActual, String[] noterminales, String[][] matrix, int control) {
        if (!tieneNoTerminales(palabraActual, noterminales) || control == 10) {
            return String.join("", palabraActual); // si no hay no terminales devolver la palabra
        } else {
            for (int i = 0; i < noterminales.length; i++) {
                for (int j = 0; j < palabraActual.length; j++) {
                    if (noterminales[i].equals(palabraActual[j])) {
                        String produccion = produccionFilaAzar(matrix, noterminales[i]);
                        palabraActual[j] = produccion;
                    }
                }
            }
            // este pedazo de codigo oculto muestra como se va formado la palabra
            /*
             * String[] a = separarVector(palabraActual);
             * String b = String.join("", a);
             * System.out.println(b);
             */
            control++; // control se asegura que la plabra no se quede infinitamente generando
            return palabraFinal(separarVector(palabraActual), noterminales, matrix, control);
        }
    }

    // cuando se reemplaza una variable no terminal por una produccion, esta va a
    // quedar como un String en esa sola posicion y
    // para el correcto funcionamiento del metodo de arriba se debe separar cada
    // letra en una posion del vector
    public String[] separarVector(String[] palabravec) {
        int longitud = 0;
        for (String s : palabravec) {
            longitud += s.length();
        }

        String[] nuevo = new String[longitud];

        int index = 0;
        for (String s : palabravec) {
            for (int i = 0; i < s.length(); i++) {
                nuevo[index] = s.substring(i, i + 1);
                index++;
            }
        }

        return nuevo;
    }

    // imprime la matriz en consola
    public void imprimirMatrix() {
        for (String[] rows : matrix) {
            System.out.println(Arrays.toString(rows));
        }
    }

    // se da el vector con las variables no terminales y se devuelven las que no
    // esten en la matriz
    public String[] depurarNoTerminales(String[] vector, String[][] matriz) {
        int tamanoListaResultados = 0;
        int fila = 0;
        for (String letra : vector) {
            if (fila < matriz.length && letra.equals(matriz[fila][0]) && !matriz[fila][0].contains("?")) {
                fila++;
            } else {
                tamanoListaResultados++;
            }
        }
    
        String[] listaResultados = new String[tamanoListaResultados];
        int indiceListaResultados = 0;
        fila = 0;
        for (String letra : vector) {
            if (fila < matriz.length && letra.equals(matriz[fila][0]) && !matriz[fila][0].contains("?")) {
                fila++;
            } else {
                listaResultados[indiceListaResultados] = letra;
                indiceListaResultados++;
            }
        }
        return listaResultados;
    }

    // se da el vector con las terminales y se devuelven las que no estan contenidas
    public String[] depurarTerminales(String[] vector, String[][] matrix) {
        int contador = 0;
        String[] arrayNoContenidos = new String[vector.length];

        for (String valor : vector) {
            boolean encontrado = false;
            for (String[] strings : matrix) {
                for (String item : strings) {
                    if (item != null && item.contains(valor)) {
                        encontrado = true;
                        break;
                    }
                }
                if (encontrado) {
                    break;
                }
            }
            if (!encontrado) {
                arrayNoContenidos[contador++] = valor;
            }
        }

        // Redimensionar el array al tamaño exacto de los elementos no contenidos
        String[] resultado = new String[contador];
        System.arraycopy(arrayNoContenidos, 0, resultado, 0, contador);
        return resultado;
    }

    // toma dos vectores y los combina en uno solo. se usa para depurar la matriz y
    // para entregar las variables inutiles mas arriba
    public String[] combinarArrays(String[] a, String[] b) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    // Elimina las producciones inalcanzables que tengan alguna variable que no este
    // contenida en las terminales y no ternminales
    public String[] depurarMatriz(String[] terminales, String[] noterminales) {
        String[] caracteres = combinarArrays(terminales, noterminales); // variables terminales y no terminales en un
                                                                        // mismo vector
        int n = matrix.length;
        int m = matrix[0].length;
        String[] eliminados = new String[n * m];

        int indexEliminados = 0; // Índice para el vector de eliminados

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                String str = matrix[i][j];

                if (str != null) {
                    boolean contieneTodas = true;

                    // Verificar si todos los caracteres del elemento de la matriz están en el
                    // vector caracteres
                    for (char c : str.toCharArray()) {
                        if (!contieneCaracter(caracteres, c)) {
                            contieneTodas = false;
                            break;
                        }
                    }

                    if (!contieneTodas) {
                        eliminados[indexEliminados++] = str; // Agregar a los eliminados
                        matrix[i][j] = null; // Vaciar la posición en la matriz
                    }
                }
            }
        }

        // Copiar los elementos eliminados al resultado final
        String[] resultadoFinal = Arrays.copyOf(eliminados, indexEliminados);

        return resultadoFinal;
    }

    // comprueba si la produccion contiene a la letra
    public boolean contieneCaracter(String[] caracteres, char c) {
        for (String s : caracteres) {
            if (s.equals(String.valueOf(c))) {
                return true;
            }
        }
        return false;
    }

    // getters y setters
    public String[] getInalcanzables() {
        return inalcanzables;
    }

    public void setInalcanzables(String[] inalcanzables) {
        this.inalcanzables = inalcanzables;
    }

    public String getNoterminales() {
        return noterminales;
    }

    public void setNoterminales(String noterminales) {
        this.noterminales = noterminales;
    }

    public String getTerminales() {
        return terminales;
    }

    public void setTerminales(String terminales) {
        this.terminales = terminales;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getReglaInicial() {
        return reglaInicial;
    }

    public void setReglaInicial(String reglaInicial) {
        this.reglaInicial = reglaInicial;
    }

    public String[] getInutiles() {
        return inutiles;
    }

    public void setInutiles(String[] inutiles) {
        this.inutiles = inutiles;
    }
    
    // --------------------------------------------------------------
    // De aqui en adelante se empezara a usar Hashmap como estructura de datos principal
    
	//llamar a este metodo en la vista para que se cargue la depuracion y chomsky
	public void depurarFinalmente(){
        ArrayList<String> varNT = new ArrayList<>(Arrays.asList(noterminalesvec));
        ArrayList<String> varT = new ArrayList<>(Arrays.asList(terminalesvec));
        mapaDepurado = depuracionTotal(convertirAHashMap(matrix), varNT, varT, reglaInicial);
        // en este momento tecnicamente esta en chomsky, pero falta convertir terminales
        mapaChomskyConterminales = chomskyEnDuos(mapaDepurado);
        mapaChomsky = reemplazarTerminales(mapaChomskyConterminales, varT);
    }

    public HashMap<String, ArrayList<String>> convertirAHashMap(String[][] matriz) {
        HashMap<String, ArrayList<String>> hashMap = new HashMap<>();

        for (String[] fila : matriz) {
            if (fila[0] != null) {
                ArrayList<String> valores = new ArrayList<>();
                for (int i = 1; i < fila.length; i++) {
                    if (fila[i] != null) {
                        valores.add(fila[i]);
                    }
                }
                hashMap.put(fila[0], valores);
            }
        }
        //mapainicial=hashMap;
        //depuracionTotal(hashMap, null, null, reglaInicial);
        return hashMap;
        
    }
    //llamar al metodo de arriba y pasarselo a este 
    public HashMap<String, ArrayList<String>> depuracionTotal(HashMap<String, ArrayList<String>> mapa,
            ArrayList<String> varNT,
            ArrayList<String> varT, String keyInicial) {

        HashMap<String, ArrayList<String>> mapaDepuradoSimple = new HashMap<>();
        mapa = eliminarRecursividad(mapa);
        mapaDepuradoSimple = mapa;
        int contadorEpsilon = 0;
        int contadorUnitarias = 0;

        for (String llave : mapaDepuradoSimple.keySet()) {
            ArrayList<String> produccion = mapaDepuradoSimple.get(llave);
            for (String cadena : produccion) {
                if (cadena.equals("?")) {
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
	
	public HashMap<String, ArrayList<String>> eliminarEpsilon(HashMap<String, ArrayList<String>> prodMap) {
        ArrayList<String> llavesConEpsilon = new ArrayList<>();

        for (String noTerminal : prodMap.keySet()) {
            ArrayList<String> produccionActual = prodMap.get(noTerminal);
            Iterator<String> iterador = produccionActual.iterator();
            while (iterador.hasNext()) {
                String cadena = iterador.next();
                if (cadena.equals("?")) {
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
                        produccion = "?";
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

    public HashMap<String, ArrayList<String>> eliminarRecursividad(HashMap<String, ArrayList<String>> producciones) {
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

    // termina depuracion y empieza chomsky

    public HashMap<String, ArrayList<String>> chomskyEnDuos(HashMap<String, ArrayList<String>> producciones) {
        
        HashMap<String,  ArrayList<String>> mapaCompletamenteEnChusmky = new HashMap<>();
        int contadorDeQ = 1;
        for (String noTerminal : producciones.keySet()){
            ArrayList<String> produccionActual = producciones.get(noTerminal);
            ArrayList<String> nuevaProduccion = new ArrayList<>();

            for (String cadena : produccionActual){
                
                if(cadena.length() > 2){
                    String variableEnChumsky = produccionChumsky(cadena, contadorDeQ);
                    nuevaProduccion.add(variableEnChumsky);
                }else{
                    nuevaProduccion.add(cadena);
                }
            }
            mapaCompletamenteEnChusmky.put(noTerminal, nuevaProduccion);
        }
        // aqui se une el mapa de las Qs con el de chomsky a medias
        for (String terminal : mapaQs.keySet()){
            String noTerminal = mapaQs.get(terminal);
            ArrayList<String> produccionNoTerminal = new ArrayList<>();
            produccionNoTerminal.add(terminal); 
            mapaCompletamenteEnChusmky.put(noTerminal, produccionNoTerminal);
        }
        
        return mapaCompletamenteEnChusmky;
    }
    //metodo que convierte cada produccion en forma de chomsky y va creando Qs y verificando si existe
    public String produccionChumsky(String produccion, int contadorGlobal){
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
                return produccionChumsky(produccionConQ, contadorGlobal);
            }//otra vez, pero ahora produccion va a tener > al final //Ya no entiendo una mondá
             else { 
                if(produccion.charAt(produccion.length() - 3) == 'Q'){

                    String llaveQ = produccion.substring(produccion.length()-5);
                    String produccionConQ;
                    if(!mapaQs.containsKey(llaveQ)){
                        String nuevaVariable = "<Q" + contadorGlobal++ + ">";
                        mapaQs.put(llaveQ, nuevaVariable);
                        produccionConQ = produccion.substring(0, produccion.length() - 5) + nuevaVariable;
                    }else{
                        produccionConQ = produccion.substring(0, produccion.length() - 5) + mapaQs.get(llaveQ);
                    }
                    return produccionChumsky(produccionConQ, contadorGlobal);
                } else {
                    String llaveQ = produccion.substring(produccion.length()-6);
                    String produccionConQ;
                    if(!mapaQs.containsKey(llaveQ)){
                        String nuevaVariable = "<Q" + contadorGlobal++ + ">";
                        mapaQs.put(llaveQ, nuevaVariable);
                        produccionConQ = produccion.substring(0, produccion.length() - 6) + nuevaVariable;
                    }else{
                        produccionConQ = produccion.substring(0, produccion.length() - 6) + mapaQs.get(llaveQ);
                    }
                    return produccionChumsky(produccionConQ, contadorGlobal);
                }
            }
        }
    }

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

    public HashMap<String, ArrayList<String>> getMapaInicial() {
        return mapainicial;
    }

    public void setMapaInicial( HashMap<String, ArrayList<String>> mapainicial) {
        this.mapainicial = mapainicial;
    }

    public HashMap<String, ArrayList<String>> getMapaDepurado() {
        return mapaDepurado;
    }

    public void setMapaDepurado( HashMap<String, ArrayList<String>> mapaDepurado) {
        this.mapaDepurado = mapaDepurado;
    }

    public HashMap<String, ArrayList<String>> getMapaChomsky() {
        return mapaChomsky;
    }

    public void setMapaChomsky( HashMap<String, ArrayList<String>> mapaChomsky) {
        this.mapaChomsky = mapaChomsky;
    }
}