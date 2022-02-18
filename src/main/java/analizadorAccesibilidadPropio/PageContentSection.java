package analizadorAccesibilidadPropio;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDMarkedContent;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.text.PDFMarkedContentExtractor;

/**
 * Sección "Page Content" que aparece al analizar un documento PDF con otro 
 * software como Foxit o Adobe.
 * @author Andrei
 */
public class PageContentSection {
    
    /**
     * TODO: no está todavía hecho
     * Comprueba que todos los elementos del documento estén etiquetados.
     * @return true = todos los elementos están etiquetados.
     */
    public static boolean isContentTagged(PDDocument document, int page) throws IOException {
        System.out.println(new String(document.getPage(page).getContents().readAllBytes(), "UTF-8"));
        return true;
    }
    
    public static boolean taggedAnnotations(PDDocument document) throws IOException {
        for (PDAnnotation an : document.getPage(0).getAnnotations()) {
            System.out.println("Name: " + an.getAnnotationName());
            System.out.println("Flags: " + Integer.toBinaryString(an.getAnnotationFlags()));
            System.out.println("Contents: " + an.getContents());
            System.out.println("COS object: " + an.getCOSObject());
        }
        return true;
    }
}
