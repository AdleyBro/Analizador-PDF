package analizador;

import logger.Log;

public class AnalizadorTest implements Analizador {

    @Override
    public void analizar(String pdfurl) {
        Log.LOGGER.info("URL!: " + pdfurl);
        System.out.println("URL!: " + pdfurl);
    }
}
