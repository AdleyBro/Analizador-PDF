package analizador;

import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

public class AnalizadorPAVE implements Analizador {

    private static final String urlHerramienta = "https://checkers.eiii.eu/en/pdfcheck/";

    @Override
    public void analizar(String pdfurl, int pdfId) {
        FirefoxOptions options = new FirefoxOptions().setHeadless(false);
        FirefoxProfile profile = new FirefoxProfile();

        options.setProfile(profile);
    }
}
