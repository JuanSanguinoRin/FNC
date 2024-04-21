package programatc;

import java.util.Arrays;
import java.util.Random;

public class Modelo {
    private String[][] matrix;
    private String[] inalcanzables;
    private String[] inutiles;
    private String noterminales;
    private String terminales;
    private String input;
    private String reglaInicial;

    public Modelo() {

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
        String[] terminalesvec = terminales.split(",");
        String[] noterminalesvec = noterminales.split(",");
        inalcanzables = depurarMatriz(terminalesvec, noterminalesvec);
        String[] inutilesNoTerminales = depurarNoTerminales(noterminalesvec, matrix);
        String[] inutilesTerminales = depurarTerminales(terminalesvec, matrix);
        inutiles = combinarArrays(inutilesNoTerminales, inutilesTerminales);
        String[] palabras = new String[10];
        int control = 0;
        for (int i = 0; i < 10; i++) {
            String[] palabravec = produccionFilaAzar(matrix, reglaInicial).split("");
            palabras[i] = palabraFinal(palabravec, noterminalesvec, matrix, control);
        }
        return palabras;
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
            if (fila < matriz.length && letra.equals(matriz[fila][0])) {
                fila++;
            } else {
                tamanoListaResultados++;
            }
        }

        String[] listaResultados = new String[tamanoListaResultados];
        int indiceListaResultados = 0;
        fila = 0;

        for (String letra : vector) {
            if (fila < matriz.length && letra.equals(matriz[fila][0])) {
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
}