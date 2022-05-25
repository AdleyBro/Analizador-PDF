package analizador;

import logger.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.List;
import java.util.logging.Logger;

public class AnalizadorTingtun implements Analizador {
    private static final String sitioWeb = "https://checkers.eiii.eu/en/pdfcheck/";

    @Override
    public void analizar(String pdfurl) {
        String dirActual = System.getProperty("user.dir");
        System.setProperty("webdriver.gecko.driver", dirActual + "/selenium/geckodriver.exe");

        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("dom.disable_beforeunload", true);
        FirefoxOptions options = new FirefoxOptions().setHeadless(true).setProfile(profile);


        // TODO: consultar que preferencias/opciones son buenas para el rendimiento

        WebDriver driver = new FirefoxDriver(options);
        driver.get("https://checkers.eiii.eu/en/pdfcheck/");

        driver.findElement(By.id("premission_question")).findElement(By.tagName("button")).click();

        driver.findElement(By.id("id_url")).click();
        driver.findElement(By.id("id_url")).sendKeys(pdfurl);
        driver.findElement(By.cssSelector(".align-items-end input")).click();

        extraeResultados(driver, pdfurl);

        driver.quit();

        Log.LOGGER.info("Terminado: " + pdfurl);
        System.out.println("Terminado: " + pdfurl);
    }

    private void extraeResultados(WebDriver driver, String pdfurl) {
        WebElement listaResultados = driver.findElement(By.id("rstTestlist"));
        for (WebElement webElemResultPropiedad : listaResultados.findElements(By.className("testtitle"))) {

            String nombrePropiedadTesteada = webElemResultPropiedad.getText().split("\n")[1];

            try {
                webElemResultPropiedad.findElement(By.cssSelector("span[title='Failed']"));
                Log.LOGGER.info(nombrePropiedadTesteada + " - FAILED " + " - " + pdfurl);
            } catch (NoSuchElementException e1) {
                try {
                    webElemResultPropiedad.findElement(By.cssSelector("span[title='Passed']"));
                    Log.LOGGER.info(nombrePropiedadTesteada + " - PASSED " + " - " + pdfurl);
                } catch (NoSuchElementException e2) {
                    Log.LOGGER.info(nombrePropiedadTesteada + " - NECESITA VERIFICACION MANUAL " + " - " + pdfurl);
                }
            }
        }
    }
}
