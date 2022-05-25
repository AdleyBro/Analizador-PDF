package analizador;

import logger.Log;

import java.util.*;

public class ConstructorAnalizador {
    private static final Map<String, Analizador> tablaAnalizadores = inicializarTablaAnalizadores();
    private static final String nombreAnalizadorPredet = "tingtun";

    /**
     * Todos los tipos de analizadores existentes deben registrarse dentro de esta función. Esta función
     * se utiliza exclusivamente para inicializar el atributo estático {@link ConstructorAnalizador#tablaAnalizadores}.
     * @return HashMap con todos los tipos de analizadores existentes.
     */
    private static Map<String, Analizador> inicializarTablaAnalizadores() {
        Map<String, Analizador> tabla = new HashMap<>();

        tabla.put("tingtun", new AnalizadorTingtun());
        tabla.put("pave", new AnalizadorPAVE());
        tabla.put("test", new AnalizadorTest());

        return tabla;
    }

    public static List<Analizador> construirAnalizadores(String[] tiposAnalizadores) throws NoSuchElementException {
        Set<String> repetidos = new HashSet<>();
        List<Analizador> analizadores = new ArrayList<>();

        for (String tipo : tiposAnalizadores) {
            if (repetidos.contains(tipo))
                continue;
            else
                repetidos.add(tipo);

            analizadores.add(tablaAnalizadores.get(tipo));
        }
        return analizadores;
    }

    public static Set<String> getTiposValidos() {
        return tablaAnalizadores.keySet();
    }
    public static String getAnalizadorPredet() { return nombreAnalizadorPredet; }
}
