package analizador;

import basedatos.ConsultasBDTingtun;
import logger.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import parametros.ParamsEjecucion;

import java.sql.SQLException;
import java.time.Duration;

public class AnalizadorTingtun implements Analizador {
    private static final String urlAnalizadorWeb = "https://checkers.eiii.eu/en/pdfcheck/";
    private ConsultasBDTingtun bd;

    private final String mensajeFinAnalisis = "Análisis completado. ID de PDF: %d";

    @Override
    public void analizar(String pdfurl) {

        Log.LOGGER.info("Analizando " + pdfurl);

        insertarPDFenBD(pdfurl);

        WebDriver driver = inicializarWebDriver();

        encontrarListaResultados(driver, pdfurl);

        extraerResultados(driver);

        finalizarConexionBD();

        driver.quit();
    }

    private void encontrarListaResultados(WebDriver driver, String pdfurl) {
        driver.get(urlAnalizadorWeb);
        try {
            driver.findElement(By.id("premission_question")).findElement(By.tagName("button")).click();
        } catch (NoSuchElementException ex) { }
        driver.findElement(By.id("id_url")).click();
        driver.findElement(By.id("id_url")).sendKeys(pdfurl);
        driver.findElement(By.cssSelector(".align-items-end input")).click();
    }

    /**
     * Extrae los resultados de las propiedades analizadas y los almacena en la base de datos.
     * @param driver
     *
     */
    private void extraerResultados(WebDriver driver) {
        //System.out.println("Extrayendo resultados para PDF con ID: " + bd.idPDF);

        WebElement listaResultados;
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            listaResultados = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul[id='rstTestlist']")));
        } catch (NoSuchElementException ex) {
            System.out.println("Ha ocurrido un error en la página de Tingtun.");
            Log.error(ex);
            return;
        }

        for (WebElement webElemResultPropiedad : listaResultados.findElements(By.className("testtitle"))) {

            String nombrePropiedadTesteada = webElemResultPropiedad.getText().split("\n")[1];
            try {
                webElemResultPropiedad.findElement(By.cssSelector("span[title='Failed']"));
                bd.insertResultado(nombrePropiedadTesteada, "SUSPENSO", bd.idPDF);

            } catch (NoSuchElementException e1) {
                try {
                    webElemResultPropiedad.findElement(By.cssSelector("span[title='Passed']"));
                    bd.insertResultado(nombrePropiedadTesteada, "APROBADO", bd.idPDF);

                } catch (NoSuchElementException e2) {
                    try {

                        bd.insertResultado(nombrePropiedadTesteada, "VERIF. MANUAL", bd.idPDF);

                    } catch (SQLException ex) {
                        System.out.println("Ha ocurrido un error al intentar insertar en la BD un resultado: +" +
                                "\nID de PDF: " + bd.idPDF + "; Propiedad: " + nombrePropiedadTesteada);
                        Log.LOGGER.warning("Ha ocurrido un error al intentar insertar en la BD un resultado: +" +
                                "\nID de PDF: " + bd.idPDF + "; Propiedad: " + nombrePropiedadTesteada);
                    }
                } catch (SQLException ex) {
                    System.out.println("Ha ocurrido un error al intentar insertar en la BD un resultado: +" +
                            "\nID de PDF: " + bd.idPDF + "; Propiedad: " + nombrePropiedadTesteada);
                    Log.LOGGER.warning("Ha ocurrido un error al intentar insertar en la BD un resultado: +" +
                            "\nID de PDF: " + bd.idPDF + "; Propiedad: " + nombrePropiedadTesteada);
                }
            } catch (SQLException ex) {
                System.out.println("Ha ocurrido un error al intentar insertar en la BD un resultado." +
                        "\nID de PDF: " + bd.idPDF + "; Propiedad: " + nombrePropiedadTesteada + "\n");
                Log.LOGGER.warning("Ha ocurrido un error al intentar insertar en la BD un resultado." +
                        "\nID de PDF: " + bd.idPDF + "; Propiedad: " + nombrePropiedadTesteada + "\n");
            }
        }
    }

    private void finalizarConexionBD() {
        try {
            bd.commit();
            Log.LOGGER.info(mensajeFinAnalisis.formatted(bd.idPDF));
            System.out.println(mensajeFinAnalisis.formatted(bd.idPDF));
        } catch (SQLException e1) {
            try {
                bd.rollback();
            } catch (SQLException e2) {
                System.out.println("No se ha conseguido realizar el rollback de las transacciones. ID de PDF: " + bd.idPDF);
                Log.error(e2);
            }
        }
    }

    private WebDriver inicializarWebDriver() {
        // TODO: consultar que preferencias/opciones son buenas para el rendimiento
        System.setProperty("webdriver.gecko.driver","src/main/resources/drivers/geckodriver.exe");
        //System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,"true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/dev/null");

        //String dirActual = System.getProperty("user.dir");
        System.setProperty("webdriver.gecko.driver", "src/main/resources/drivers/geckodriver.exe");

        FirefoxProfile profile = new FirefoxProfile();

        profile.setPreference("dom.disable_beforeunload", true);
        FirefoxOptions options = new FirefoxOptions().setHeadless(true).setProfile(profile);
        return new FirefoxDriver(options);
    }

    private void insertarPDFenBD(String pdfurl) {
        try {
            bd = new ConsultasBDTingtun(false);

            bd.idPDF = bd.insertPDF(pdfurl, ParamsEjecucion.fechaHoraInicio, ParamsEjecucion.getUrlWeb());

        } catch (SQLException e) {
            System.out.println("Ha ocurrido un error al intentar insertar en la base de datos el pdf " + pdfurl);
            Log.error(e);
            try {
                bd.rollback();
            } catch (SQLException ex) {
                System.out.println("No se ha conseguido realizar el rollback de las transacciones. ID de PDF: " + bd.idPDF);
                Log.error(ex);
            }
            return;
        }
    }
}
