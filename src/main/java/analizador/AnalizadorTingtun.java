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

    private final String mensajeFinAnalisis = "Análisis completado. ID de PDF: %d";

    @Override
    public void analizar(String pdfurl) {
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

        WebDriver driver = inicializarWebDriver();

        driver.get("https://checkers.eiii.eu/en/pdfcheck/");
        try {
            driver.findElement(By.id("premission_question")).findElement(By.tagName("button")).click();
        } catch (NoSuchElementException ex) { }
        driver.findElement(By.id("id_url")).click();
        driver.findElement(By.id("id_url")).sendKeys(pdfurl);
        driver.findElement(By.cssSelector(".align-items-end input")).click();

        extraeResultados(driver, pdfurl);

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
        driver.quit();
    }

    /**
     * Extrae los resultados de las propiedades analizadas y los almacena en la base de datos.
     * @param driver
     * @param pdfurl
     */
    private void extraeResultados(WebDriver driver, String pdfurl) {
        //System.out.println("Extrayendo resultados para PDF con ID: " + bd.idPDF);

        WebElement listaResultados = driver.findElement(By.id("rstTestlist"));
        for (WebElement webElemResultPropiedad : listaResultados.findElements(By.className("testtitle"))) {
            System.out.println("CHUCHA");
            String nombrePropiedadTesteada = webElemResultPropiedad.getText().split("\n")[1];
            System.out.println("PROPIEDAD: " + nombrePropiedadTesteada);
            try {
                webElemResultPropiedad.findElement(By.cssSelector("span[title='Failed']"));
                bd.insertResultado(nombrePropiedadTesteada, "SUSPENSO", bd.idPDF);

                System.out.println(nombrePropiedadTesteada + " - FAILED " + " - " + pdfurl);
                Log.LOGGER.info(nombrePropiedadTesteada + " - FAILED " + " - " + pdfurl);
            } catch (NoSuchElementException e1) {
                try {
                    webElemResultPropiedad.findElement(By.cssSelector("span[title='Passed']"));
                    bd.insertResultado(nombrePropiedadTesteada, "APROBADO", bd.idPDF);

                    System.out.println(nombrePropiedadTesteada + " - PASSED " + " - " + pdfurl);
                    Log.LOGGER.info(nombrePropiedadTesteada + " - PASSED " + " - " + pdfurl);
                } catch (NoSuchElementException e2) {
                    try {

                        bd.insertResultado(nombrePropiedadTesteada, "VERIF. MANUAL", bd.idPDF);

                        System.out.println(nombrePropiedadTesteada + " - NECESITA VERIFICACION MANUAL " + " - " + pdfurl);
                        Log.LOGGER.info(nombrePropiedadTesteada + " - NECESITA VERIFICACION MANUAL " + " - " + pdfurl);

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
                System.out.println("Ha ocurrido un error al intentar insertar en la BD un resultado: +" +
                        "\nID de PDF: " + bd.idPDF + "; Propiedad: " + nombrePropiedadTesteada);
                Log.LOGGER.warning("Ha ocurrido un error al intentar insertar en la BD un resultado: +" +
                        "\nID de PDF: " + bd.idPDF + "; Propiedad: " + nombrePropiedadTesteada);
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
