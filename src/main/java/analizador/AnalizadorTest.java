package analizador;

import logger.Log;

public class AnalizadorTest implements Analizador {

    @Override
    public void analizar(String pdfurl, int pdfId) {
        Log.LOGGER.info("URL!: " + pdfurl + "; ID: " + pdfId);
        System.out.println("URL!: " + pdfurl + "; ID: " + pdfId);
    }
}
