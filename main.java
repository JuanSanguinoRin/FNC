package programatc;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String[][] matrix;
        // String[] terminalesvec;
        String[] noterminalesvec, terminalesvcec;
        String terminales, noterminales, input, reglaInicial;
        // int maxproducciones, maxreglas;
        // Deshabilitado por falta de uso, por ahora
        // System.out.println("Ingrese las variables terminales separadas por comas");
        // terminales = sc.next();
        // terminalesvec = terminales.split("");

        System.out.println("Ingrese las variables no terminales separadas por comas");
        noterminales = sc.next();
        noterminalesvec = noterminales.split(",");

        // EL numero maximo de reglas es innecesario por el como funciona el metodo
        // System.out.println("Ingrese el numero maximo de reglas");
        // maxreglas = sc.nextInt();
        // El numero maximo de producciones se debe poner en caso de que alguna regla
        // tenga mas de 8 producciones
        // System.out.println("Ingrese el numero maximo de producciones");
        // maxproducciones = sc.nextInt();
        System.out.println("Ingrese la reglainicial: ");
        reglaInicial = sc.next(); // para mejor rendimiento verificar primero que la regla si existe

        System.out.println(
                "Ingrese las reglas separadas por punto y coma y las producciones separadas por comas, no ingrese la letra de la regla");
        input = sc.next();

        // Generar la Matrix con las producciones, en caso de querer n producciones
        // pasar el nuemero y modificar el metodo
        matrix = generarMatrix(input);

        // Escoger una produccion aleatoria
        String[] palabravec;
        int controlproducciones = 0;
        // Imprimir la matriz
        
         for (String[] rows : matrix) {
         System.out.println(Arrays.toString(rows));
         }
         
        for (int i = 0; i < 10; i++) {
            // se descarta el uso de una produccion al azar ya que se requiere una produccion de una fila especifica
            String a = produccionFilaAzar(matrix, reglaInicial);
            palabravec = a.split("");
            System.out.println(a);
            // palabravec = produccionAlAzar(matrix).split("");
            System.out.println(palabraFinal(palabravec, noterminalesvec, matrix, controlproducciones));
            System.out.println("--------------");
            i++;
        }
        sc.close();
    }

    /*public static int indexReglaInicial(String[] a, String b) {
        for (int i = 0; i < a.length; i++) {
            if (b.equals(a[i])) {
                return i;
            }
        }
        return -1;
    }*/
    
    // Escoge una produccion al azar de cualquier regla como punto de partida
    // pero viendolo mejor, no es necesario
    /*public static String produccionAlAzar(String[][] matrix) {
        Random random = new Random();
        int numFilas = matrix.length;
        int numColumnas = matrix[0].length;
        int fila, columna;
        String palabraCruda;
        do {
            fila = random.nextInt(numFilas);
            columna = random.nextInt(numColumnas);
            palabraCruda = matrix[fila][columna];
        } while (palabraCruda == null);
        return palabraCruda;
    }*/

   

    public static String[][] generarMatrix(String input) {
        String[] sections = input.split(";");
        String[][] matrix = new String[sections.length][8];
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

    // va a buscar en el primer elemento de cada fila para saber el indice de donde se encuentrea esa variable no terminal
    public static int indexReglaInicialMatrix(String[][] matrix, String letra){
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][0].equals(letra)) {
                return i; // Devuelve el índice de la fila donde se encontró la letra
            }
        }
        return -1;
    }

    // devuelve una produccion con el indice de la regla especificada
    public static String produccionFilaAzar(String[][] matrix, String letra) {
        Random random = new Random();
        String p;
        int fila = indexReglaInicialMatrix(matrix, letra); // añadir un if en caso de -1 para indicar que no se encontro ninguna regla con esa letra
        
        do {
            int aux = random.nextInt(matrix[fila].length) +1; // este mas uno es para que nunca busque en la primera posicion de la fila de la matriz, ya que ahi siempre va a haber una variable no terminal
            if (aux == matrix[fila].length){
                aux = aux -1; // este menos uno es por si se pasa no de error
            }
            p = matrix[fila][aux];
        } while (p == null);
        return p;
    }

    // el String b debe ser la lista de no terminales y a es el string de la palabra
    // que se escogio al azar
    public static boolean tieneNoTerminales(String[] a, String[] b) {
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < a.length; j++) {
                if (b[i].equals(a[j])) {
                    System.out.println("true");
                    return true;
                }
            }
        }
        return false;
    }

    // el String segundo debe ser la lista de no terminales y el primero la palabra
    // en vectores
    public static String palabraFinal(String[] palabraActual, String[] noterminales, String[][] matrix, int controlproducciones) {
        if (!tieneNoTerminales(palabraActual, noterminales) ) {
            return String.join("", palabraActual); // si no hay no terminales devolver la palabra
        } else {
            for (int i = 0; i < noterminales.length; i++) {
                for (int j = 0; j < palabraActual.length; j++) {
                    if (noterminales[i].equals(palabraActual[j])) { // arreglado, ahora no importa el orden
                        String produccion = produccionFilaAzar(matrix, noterminales[i]);
                        palabraActual[j] = produccion;
                    }
                }
            }
            String[] a = separarVector(palabraActual);
            String b = String.join("", a);
            System.out.println(b);
            controlproducciones++;
            return palabraFinal(separarVector(palabraActual), noterminales, matrix, controlproducciones);
        }
    }

    //
    public static String[] separarVector(String[] palabravec) {
        int longitud = 0;
        for (String s : palabravec) {
            longitud += s.length();
        }

        // Crear el nuevo array para almacenar los caracteres
        String[] nuevo = new String[longitud];

        int index = 0;
        for (String s : palabravec) {
            for (int i = 0; i < s.length(); i++) {
                nuevo[index] = s.substring(i, i + 1);
                index++;
            }
        }
        
        /*for (String character : nuevo) {
            System.out.print(character + " ");
        }*/
         
        return nuevo;
    }
}