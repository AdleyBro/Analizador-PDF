package analizador;

import basedatos.ConsultasBD;
import basedatos.ConsultasBDTingtun;
import logger.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import parametros.ParamsEjecucion;

import java.sql.SQLException;

public class AnalizadorTingtun implements Analizador {
    private static final String sitioWeb = "https://checkers.eiii.eu/en/pdfcheck/";
    private ConsultasBDTingtun bd;

    @Override
    public void analizar(String pdfurl) {
        try {
            bd = new ConsultasBDTingtun(pdfurl, ParamsEjecucion.fechaHoraInicio, ParamsEjecucion.getUrlWeb());

        } catch (SQLException e) {
            System.out.println("Ha ocurrido un error al intentar insertar en la base de datos el pdf " + pdfurl);
            Log.error(e);
            return;
        }

        WebDriver driver = inicializarWebDriver();

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

    /**
     * Extrae los resultados de las propiedades analizadas y los almacena en la base de datos.
     * @param driver
     * @param pdfurl
     */
    private void extraeResultados(WebDriver driver, String pdfurl) {
        WebElement listaResultados = driver.findElement(By.id("rstTestlist"));
        for (WebElement webElemResultPropiedad : listaResultados.findElements(By.className("testtitle"))) {

            String nombrePropiedadTesteada = webElemResultPropiedad.getText().split("\n")[1];

            try {
                webElemResultPropiedad.findElement(By.cssSelector("span[title='Failed']"));
                bd.insertResultado(nombrePropiedadTesteada, "SUSPENSO", bd.idPDF);

                Log.LOGGER.info(nombrePropiedadTesteada + " - FAILED " + " - " + pdfurl);
            } catch (NoSuchElementException e1) {
                try {
                    webElemResultPropiedad.findElement(By.cssSelector("span[title='Passed']"));
                    bd.insertResultado(nombrePropiedadTesteada, "APROBADO", bd.idPDF);

                    Log.LOGGER.info(nombrePropiedadTesteada + " - PASSED " + " - " + pdfurl);
                } catch (NoSuchElementException e2) {
                    try {

                        bd.insertResultado(nombrePropiedadTesteada, "VERIF. MANUAL", bd.idPDF);
                        Log.LOGGER.info(nombrePropiedadTesteada + " - NECESITA VERIFICACION MANUAL " + " - " + pdfurl);

                    } catch (SQLException ex) {
                        Log.LOGGER.warning("Ha ocurrido un error al intentar insertar en la BD un resultado: +" +
                                "\nURL PDF: " + pdfurl + "; Propiedad: " + nombrePropiedadTesteada);
                    }
                } catch (SQLException ex) {
                    Log.LOGGER.warning("Ha ocurrido un error al intentar insertar en la BD un resultado: +" +
                            "\nURL PDF: " + pdfurl + "; Propiedad: " + nombrePropiedadTesteada);
                }
            } catch (SQLException ex) {
                Log.LOGGER.warning("Ha ocurrido un error al intentar insertar en la BD un resultado: +" +
                        "\nURL PDF: " + pdfurl + "; Propiedad: " + nombrePropiedadTesteada);
            }
        }
    }

    private WebDriver inicializarWebDriver() {
        // TODO: consultar que preferencias/opciones son buenas para el rendimiento

        String dirActual = System.getProperty("user.dir");
        System.setProperty("webdriver.gecko.driver", dirActual + "/selenium/geckodriver.exe");

        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("dom.disable_beforeunload", true);
        FirefoxOptions options = new FirefoxOptions().setHeadless(true).setProfile(profile);
        return new FirefoxDriver(options);
    }
}
