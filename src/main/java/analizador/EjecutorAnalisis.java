package analizador;

import analizador.Analizador;
import logger.Log;

import java.util.*;

public class EjecutorAnalisis {
    private static final Map<Character, Analizador> tablaAnalizadores = inicializarTablaAnalizadores();

    private final List<Analizador> analizadores = new ArrayList<>();

    /**
     * A partir de los argumentos de entrada al programa, construye el ejecutor añadiendo
     * los analizadores indicados.
     * @param args Debe ser una única cadena de caracteres, los cuales serán claves del HashMap
     * {@link EjecutorAnalisis#tablaAnalizadores}
     */
    public EjecutorAnalisis(String[] args) {
        if (args.length < 1)
            analizadores.add(tablaAnalizadores.get('u'));
        else {
            boolean esValido = false;
            String arg = "";
            for (int i = 0; !esValido && i < args.length; i++) {
                arg = args[i];
                esValido = arg.startsWith("-");
            }

            if (esValido)
                construirAnalizadores(arg.substring(1));
            else
                analizadores.add(tablaAnalizadores.get('u'));
        }
    }

    public void analizar(String pdfurl) {
        for (Analizador analizador : analizadores) {
            analizador.analizar(pdfurl);
        }
    }

    /**
     * Todos los tipos de analizadores existentes deben registrarse dentro de esta función. Esta función
     * se utiliza exclusivamente para inicializar el atributo estático {@link EjecutorAnalisis#tablaAnalizadores}.
     * @return HashMap con todos los tipos de analizadores existentes.
     */
    private static Map<Character, Analizador> inicializarTablaAnalizadores() {
        Map<Character, Analizador> tabla = new HashMap<>();

        tabla.put('a', new AnalizadorArchivo());
        tabla.put('u', new AnalizadorURL());
        tabla.put('t', new AnalizadorTest());

        return tabla;
    }

    private void construirAnalizadores(String tipoAnalisis) {
        Set<Character> repetidos = new HashSet<>();

        for (char tipo : tipoAnalisis.toCharArray()) {
            if (tablaAnalizadores.containsKey(tipo)) {
                if (repetidos.contains(tipo))
                    continue;
                else
                    repetidos.add(tipo);

                analizadores.add(tablaAnalizadores.get(tipo));
            }
        }
    }
}
