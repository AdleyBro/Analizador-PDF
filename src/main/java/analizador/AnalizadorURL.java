package analizador;

import logger.Log;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public class AnalizadorURL implements Analizador {

    private static final String sitioWeb = "https://checkers.eiii.eu/en/pdfcheck/";

    @Override
    public void analizar(String pdfurl) {
        String dirActual = System.getProperty("user.dir");
        System.setProperty("webdriver.gecko.driver", dirActual + "/selenium/geckodriver.exe");


        // TODO: abrir y cerrar el navegador firefox cada vez que queramos analizar un PDF es una puta mierda. Por ello
        // TODO: debemos crear una clase a parte que se encargue de administrar la sesión del navegador hasta que finalice
        // TODO: el proceso completo y, entonces sí, cerrar el navegador.
        
        //FirefoxOptions options = new FirefoxOptions(); //.setHeadless(true);


        //FirefoxProfile profile = new FirefoxProfile();

        WebDriver driver = new FirefoxDriver(); //new FirefoxDriver(options);
        driver.get(sitioWeb);
        /*
        //profile.setPreference("browser.download.viewableInternally.previousHandler.alwaysAskBeforeHandling.xml", false);
        //profile.setPreference("browser.download.viewableInternally.previousHandler.preferredAction.xml", 0);
        //profile.setPreference("browser.download.viewableInternally.typeWasRegistered.xml", true);
        profile.setPreference("browser.download.folderList", 2);
        //profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.download.useDownloadDir", true);
        profile.setPreference("browser.download.dir", dirActual);
        profile.setPreference("browser.helperApps.neverAsk.openFile", "application/xml");
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/xml");

        options.setProfile(profile);

        WebDriver driver = new FirefoxDriver(options);

        //WebDriverWait wait = new WebDriverWait(driver, 240);

        try {
            driver.get(sitioWeb);
            //driver.findElement(By.name("initurl")).sendKeys(paginaWeb + Keys.ENTER);
            //WebElement resultado = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.btn.view_details")));

            //resultado.click();
            //WebElement xmlElement = wait.until(presenceOfElementLocated(By.cssSelector("a.btn-primary:nth-child(2)")));
            //xmlElement.click();

        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        Log.info(pdfurl);

        driver.quit();
    }
}
