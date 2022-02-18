package analizadorAccesibilidadPropio;

import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkInfo;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Sección "Document" que aparece al analizar un documento PDF con otro software
 * como Foxit o Adobe.
 * @author Andrei
 */
public class DocumentSection {
    
    /**
     * Comprueba si el pdf permite extraer el texto y las imágenes para ser leídas
     * por un lector con voz. Corresponde a "Indicador de permiso de accesibilidad".
     * @return true = es leible
     */
    public static boolean checkAccessPerm(PDDocument document) {
        
        return document.getCurrentAccessPermission().canExtractForAccessibility();
    }
    
    /**
     * Comprueba si el documento carece completamente de texto leíble por un lector.
     * Corresponde a "PDF de solo imagen".
     * @param document
     * @return true = solo tiene imágenes, nada de texto leible
     * @throws IOException 
     */
    public static boolean isImageOnly(PDDocument document) throws IOException {
        PDFTextStripper pdfStripper = new PDFTextStripper();
        
        return pdfStripper.getText(document).isBlank();
    }
    
    /**
     * Comprueba si el documento esté etiquetado. Corresponde a "PDF etiquetado"
     * @param document
     * @return true = está etiquetado.
     */
    public static boolean isTagged(PDDocument document) {
        PDMarkInfo mark = document.getDocumentCatalog().getMarkInfo();
        return mark != null && mark.isMarked();
    }
    
    /**
     * Lo único que hace es devolver un mensaje. El orden de lectura del texto
     * se debe comprobar manualmente.
     * @return mensaje de aviso.
     */
    public static String logicalReadingOrder() {
        return "Verifique esta comprobación de la regla manualmente. Asegúrese de que el orden de lectura mostrado " +
                "en el panel Etiquetas coincide con el orden de lectura lógico del documento.";
    }
    
    /**
     * Comprueba si el lenguaje primario del documento está indicado en el
     * documento. En tal caso, el lector de voz sabría en qué idioma debe leer
     * el documento.
     * @param document
     * @return true = el documento posee lenguaje primario.
     */
    public static boolean isPrimaryLanguageSet(PDDocument document) {
        try {
            PDDocumentCatalog catalog = document.getDocumentCatalog();
            return !catalog.getLanguage().isBlank();
        } catch(NullPointerException e) {
            return false;
        }
    }
    
    /**
     * Comprueba que el documento tenga un título.
     * @param document
     * @return true = tiene título.
     */
    public static boolean hasTitle(PDDocument document) {
        try {
            PDDocumentCatalog catalog = document.getDocumentCatalog();
            return catalog.getViewerPreferences().displayDocTitle();
        } catch (NullPointerException e) {
            return false;
        }
    }
    
    /**
     * Si el documento tiene 21 o más páginas, debe tener marcadores que se 
     * correspondan a su estructura. Esta función comprueba eso. Si tiene menos
     * de 21 páginas, devuelve siempre true.
     * @param document
     * @return true = tiene marcadores.
     */
    public static boolean hasBookmarks(PDDocument document) {
        if (document.getNumberOfPages() < 21)
            return true;
        
        try {
            PDDocumentCatalog catalog = document.getDocumentCatalog();
            return catalog.getDocumentOutline() != null;
        } catch (NullPointerException e) {
            return false;
        }
    }
    
    /**
     * Lo único que hace es devolver un mensaje. El contraste de color del texto
     * y otros elementos con respecto al fondo de la página debe revisarse
     * manualmente.
     * @return mensaje de aviso.
     */
    public static String colorContrast() {
        return "El contraste de color debe comprobarse manualmente.";
    }
    
    public static void printAccessPermissionInfo(PDDocument document) {
        AccessPermission permisos = document.getCurrentAccessPermission();
        
        System.out.println("Can assemble document: " + permisos.canAssembleDocument());
        System.out.println("Can extract content: " + permisos.canExtractContent());
        System.out.println("Can extract for accessibility " + permisos.canExtractForAccessibility());
        System.out.println("Can fill in form: " + permisos.canFillInForm());
        System.out.println("Can modify: " + permisos.canModify());
        System.out.println("Can modify annotations: " + permisos.canModifyAnnotations());
        System.out.println("Can print: " + permisos.canPrint());
        System.out.println("Can print degraded: " + permisos.canPrintDegraded());
        System.out.println("Is owner permission: " + permisos.isOwnerPermission());
        System.out.println("Is read only: " + permisos.isReadOnly());
    }
}
