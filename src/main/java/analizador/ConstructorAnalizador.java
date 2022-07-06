package analizador;

import java.util.*;
import java.util.function.Supplier;

public class ConstructorAnalizador {
    private static final Map<String, Supplier<Analizador>> tablaAnalizadores = inicializarTablaAnalizadores();
    private static final String nombreAnalizadorPredet = "tingtun";

    /**
     * Todos los tipos de analizadores existentes deben registrarse dentro de esta función. Esta función
     * se utiliza exclusivamente para inicializar el atributo estático {@link ConstructorAnalizador#tablaAnalizadores}.
     * @return HashMap con todos los tipos de analizadores existentes.
     */
    private static Map<String, Supplier<Analizador>> inicializarTablaAnalizadores() {
        Map<String, Supplier<Analizador>> tabla = new HashMap<>();

        tabla.put("tingtun", AnalizadorTingtun::new);
        tabla.put("pave", AnalizadorPAVE::new);
        tabla.put("test", AnalizadorTest::new);

        return tabla;
    }


    /**
     *
     * @param tiposAnalizadores especificados por los parámetros de entrada introducidos por el usuario
     * @return Lista de Suppliers de cada tipo de analizador requerido por el usuario
     * @throws NoSuchElementException
     */
    public static List<Supplier<Analizador>> getAnalizadoresPorParams(String[] tiposAnalizadores) throws NoSuchElementException {
        Set<String> repetidos = new HashSet<>();
        List<Supplier<Analizador>> analizadores = new ArrayList<>();

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
