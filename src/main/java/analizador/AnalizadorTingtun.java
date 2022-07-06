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

import java.io.File;
import java.sql.SQLException;
import java.time.Duration;

public class AnalizadorTingtun implements Analizador {
    private static final String urlAnalizadorWeb = "https://checkers.eiii.eu/en/pdfcheck/";
    private ConsultasBDTingtun bd;

    private final String mensajeFinAnalisis = "Análisis completado. ID de PDF: %d";

    @Override
    public void analizar(String pdfurl, int pdfId) {

        String msg1 = "Analizando PDF " + pdfId + ": " + pdfurl;
        System.out.println(msg1);
        Log.LOGGER.info(msg1);

        try {
            bd = new ConsultasBDTingtun(false);
        } catch (SQLException ex) {
            System.out.println("Ha ocurrido un error al intentar conectar con la base de datos.");
            Log.error(ex);
            return;
        }

        WebDriver driver = iniWebDriver();

        try {
            encontrarListaResultados(driver, pdfurl);
        } catch (NoSuchElementException ex) {
            System.out.println("Ha ocurrido un error al intentar navegar por Tingtun");
            Log.error(ex);
            return;
        }

        try {
            guardarResultados(driver, pdfId);
        } catch (SQLException ex) { return; }

        finalizarConexionBD(pdfId);

        String msg2 = "Terminado: PDF " + pdfId + ": " + pdfurl;
        System.out.println(msg2);
        Log.LOGGER.info(msg2);

        driver.quit();
    }

    private void encontrarListaResultados(WebDriver driver, String pdfurl) {
        driver.get(urlAnalizadorWeb);
        driver.findElement(By.id("premission_question")).findElement(By.tagName("button")).click();
        driver.findElement(By.id("id_url")).click();
        driver.findElement(By.id("id_url")).sendKeys(pdfurl);
        driver.findElement(By.cssSelector(".align-items-end input")).click();
    }

    /**
     * Extrae los resultados de las propiedades analizadas y los almacena en la base de datos.
     * @param driver
     *
     */
    private void guardarResultados(WebDriver driver, int pdfId) throws SQLException {
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
                bd.insertResultado(nombrePropiedadTesteada, "SUSPENSO", pdfId);

            } catch (NoSuchElementException e1) {
                try {
                    webElemResultPropiedad.findElement(By.cssSelector("span[title='Passed']"));
                    bd.insertResultado(nombrePropiedadTesteada, "APROBADO", pdfId);

                } catch (NoSuchElementException e2) {
                    try {

                        bd.insertResultado(nombrePropiedadTesteada, "VERIF. MANUAL", pdfId);

                    } catch (SQLException ex) {
                        System.out.println("Ha ocurrido un error al intentar insertar en la BD un resultado: +" +
                                "\nID de PDF: " + pdfId + "; Propiedad: " + nombrePropiedadTesteada);
                        Log.LOGGER.warning("Ha ocurrido un error al intentar insertar en la BD un resultado: +" +
                                "\nID de PDF: " + pdfId + "; Propiedad: " + nombrePropiedadTesteada);
                        throw ex;
                    }
                } catch (SQLException ex) {
                    System.out.println("Ha ocurrido un error al intentar insertar en la BD un resultado: +" +
                            "\nID de PDF: " + pdfId + "; Propiedad: " + nombrePropiedadTesteada);
                    Log.LOGGER.warning("Ha ocurrido un error al intentar insertar en la BD un resultado: +" +
                            "\nID de PDF: " + pdfId + "; Propiedad: " + nombrePropiedadTesteada);
                    throw ex;
                }
            } catch (SQLException ex) {
                System.out.println("Ha ocurrido un error al intentar insertar en la BD un resultado." +
                        "\nID de PDF: " + pdfId + "; Propiedad: " + nombrePropiedadTesteada + "\n");
                Log.LOGGER.warning("Ha ocurrido un error al intentar insertar en la BD un resultado." +
                        "\nID de PDF: " + pdfId + "; Propiedad: " + nombrePropiedadTesteada + "\n");
                throw ex;
            }
        }
    }

    private void finalizarConexionBD(int pdfId) {
        try {
            bd.commit();
        } catch (SQLException e1) {
            try {
                bd.rollback();
            } catch (SQLException e2) {
                System.out.println("No se ha conseguido realizar el rollback de las transacciones. ID de PDF: " + pdfId);
                Log.error(e2);
            }
        }
    }

    private WebDriver iniWebDriver() {
        // TODO: consultar que preferencias/opciones son buenas para el rendimiento
        System.setProperty("webdriver.gecko.driver","tools/drivers/geckodriver.exe");
        //System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,"true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/dev/null");

        //String dirActual = System.getProperty("user.dir");

        FirefoxProfile profile = new FirefoxProfile();

        profile.setPreference("dom.disable_beforeunload", true);
        FirefoxOptions options = new FirefoxOptions().setHeadless(true).setProfile(profile);
        return new FirefoxDriver(options);
    }

    public static boolean existeDriverWeb() {
        String rutaDriver = "./tools/drivers/geckodriver.exe";
        File fichero = new File(rutaDriver);
        return fichero.exists() && !fichero.isDirectory();
    }
}
