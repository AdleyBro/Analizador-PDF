package analizador;

import logger.Log;
import parametros.ParamsEjecucion;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class EjecutorAnalisis {

    private final List<Analizador> analizadores = new ArrayList<>();

    private final ExecutorService threadPool = Executors.newFixedThreadPool(ParamsEjecucion.getNumHilos());

    /**
     * A partir de los argumentos de entrada al programa, construye el ejecutor añadiendo
     * los analizadores indicados.
     * @param tiposAnalizadores Array con los nombres de cada tipo de analizador
     */
    public EjecutorAnalisis(String[] tiposAnalizadores) {
        for (Supplier<Analizador> supplier : ConstructorAnalizador.getAnalizadoresPorParams(tiposAnalizadores))
            analizadores.add(supplier.get());

        Log.LOGGER.info("Analizador listo para ser utilizado.");
    }

    /**
     * Analiza el pdf utilizando los analizadores seleccionados por el usuario a través de los argumentos del programa.
     * @param pdfurl
     */
    public void analizar(String pdfurl, int pdfId) throws IOException {
        threadPool.submit(() -> {

            for (Analizador analizador : analizadores) {
                analizador.analizar(pdfurl, pdfId);
            }
        });
    }

    public void finalizar() throws InterruptedException {
        threadPool.shutdown();
        threadPool.awaitTermination(24, TimeUnit.HOURS);
    }
}
