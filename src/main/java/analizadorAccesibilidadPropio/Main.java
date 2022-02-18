package analizadorAccesibilidadPropio;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdfviewer.PDFTreeModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDObjectReference;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDMarkedContent;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.text.PDFMarkedContentExtractor;
import org.apache.pdfbox.text.PDFTextStripper;

public class Main {

    public static void main(String args[]) throws IOException {

        //Loading an existing document
        File file = new File("D:\\1CarpetasVarias\\_Universidad_\\Trabajo\\Analizador PDF\\BOE.pdf");
        //File file = new File("D:\\1CarpetasVarias\\Libros\\Informática\\Procesadores del Lenguaje\\Construccion de compiladores.pdf");
        //File file = new File("D:\\1CarpetasVarias\\Libros\\Informática\\Procesadores del Lenguaje\\Compiladores principios tecnicas y herramientas.pdf");
        //File file = new File("D:\\1CarpetasVarias\\Libros\\Diseño Gráfico\\PixelLogic.pdf");
        PDDocument document = Loader.loadPDF(file);
        
        
        //printGeneralInfo(document);
        //printArbolEtiquetas(document);
        //printTodoTexto(document);
        //printXMLMetadata(document);
        //printAnotacionesPagina(document, 0); // Casi nunca devuelve nada
        //printPropiedadesPagina(document, 0); // Casi nunca devuelve nada

        //for (int i = 0; i < 242; i++)
        //    printInfoEtiquetasPagina(document, i); // Podemos obtener las etiquetas y su texto.
        //descargaImagenesPagina(document, 0);
        
        //analizadorAccesibilidadPropio.PageContentSection.isContentTagged(document, 0);
        PageContentSection.taggedAnnotations(document);
        System.out.println("-------");
        printInfoEtiquetasPagina(document, 0);
        
        //analizadorAccesibilidadPropio.DocumentSection.checkAccessPerm(document);
        //analizadorAccesibilidadPropio.DocumentSection.printAcessPermissionInfo(document);
        //System.out.println(analizadorAccesibilidadPropio.DocumentSection.isTagged(document));
        /*System.out.println(analizadorAccesibilidadPropio.DocumentSection.isImageOnly(document));
        System.out.println(analizadorAccesibilidadPropio.DocumentSection.isPrimaryLanguageSet(document));
        System.out.println(analizadorAccesibilidadPropio.DocumentSection.hasTitle(document));
        System.out.println(analizadorAccesibilidadPropio.DocumentSection.hasBookmarks(document));
        */
        
        /*
        // -------------
        // CODIGO DE ANA
        // -------------
        PDFTreeModel tree = new PDFTreeModel(document);
        PDStructureTreeRoot treeRoot = document.getDocumentCatalog().getStructureTreeRoot();
        for (Object kid : treeRoot.getKids())
        {
            PDStructureElement kidc = (PDStructureElement)kid;
            System.out.println("Type=" + kidc.getStructureType());
            for (Object kid2 : kidc.getKids())
            {
                System.out.println("  Type2=" + kidc.getStandardStructureType());
                
                try {
                    PDStructureElement kidc2 = (PDStructureElement)kid2;   
                    for (Object kid3 : kidc2.getKids())
                    {
                        System.out.println("    Type3=" + kidc2.getStandardStructureType());                   
                    }
                } catch (Exception e) {
                    PDObjectReference kidc2 = (PDObjectReference) kid2;
                    System.out.println("    Cosa=" + kidc2.getReferencedObject().toString());
                }
                
            }
        }
        
        //Instantiate PDFTextStripper class
        PDFTextStripper pdfStripper = new PDFTextStripper();
        //Retrieving text from PDF document
        String text = pdfStripper.getText(document);
        //System.out.println(text);
        PDDocumentInformation info = document.getDocumentInformation();
        PDDocumentCatalog cat = document.getDocumentCatalog();
        PDMetadata metadata = cat.getMetadata();
        System.out.println("Page Count=" + document.getNumberOfPages());
        System.out.println("Title=" + info.getTitle());
        System.out.println("Author=" + info.getAuthor());
        System.out.println("Subject=" + info.getSubject());
        System.out.println("Keywords=" + info.getKeywords());
        System.out.println("Creator=" + info.getCreator());
        System.out.println("Producer=" + info.getProducer());
        System.out.println("Creation Date=" + (info.getCreationDate()));
        System.out.println("Modification Date=" + (info.getModificationDate()));
        System.out.println("Trapped=" + info.getTrapped());   
        if (metadata != null) {
            System.out.println("Metadata=" + metadata.toString());
        }
        
        System.out.println("Type=" + treeRoot.getType());        
        
        System.out.println(document.getDocumentCatalog().getStructureTreeRoot().toString());
        
        System.out.println("Image count = " + Check.getImageCount(document));
        System.out.println("Is marked = " + Check.isMarked(document));
        System.out.println("Language = " + Check.getDocumentLanguageSet(document));  
        // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        // CODIGO DE ANA
        // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        */
        
        
        //Closing the document
        document.close();
    }
    
    /**
     * Descarga todas las imágenes de una pagina en concreto. También imprime 
     * los nombres COSName de cada imagen.
     * @param document Documento del cual extraer las imágenes.
     * @param pagina Número de la página de la cual extraer las imágenes. De 0 a n.
     * @throws IOException 
     */
    public static void descargaImagenesPagina(PDDocument document, int pagina) throws IOException {
        
        String ruta = "C:\\Users\\andro\\Documents\\";
        System.out.println("Resources pagina " + pagina);
        Iterable<COSName> nombres = document.getPage(pagina).getResources().getXObjectNames();
        for (COSName nombre : nombres) {
            PDXObject objeto = document.getPage(pagina).getResources().getXObject(nombre);
            System.out.println(objeto);
            if (objeto instanceof PDImageXObject) {
                BufferedImage imagen = ((PDImageXObject) objeto).getImage();
                File outputfile = new File(ruta + nombre.getName() + ".jpg");
                ImageIO.write(imagen, "jpg", outputfile);
            }
        }
    }
    
    /**
     * Imprime el contenido, nombre y texto de todas las etiquetas de una pagina.
     * @param document Documento a analizar.
     * @param pagina Numero de la pagina de la cual extraer las etiquetas. De 0 a n.
     * @throws IOException 
     */
    public static void printInfoEtiquetasPagina(PDDocument document, int pagina) throws IOException {
        PDFMarkedContentExtractor extractor = new PDFMarkedContentExtractor();
        extractor.processPage(document.getPage(pagina));
        
        int cantidadEtiquetas = extractor.getMarkedContents().size();
        System.out.println("Cantidad de MARKED CONTENTS en la pagina " + pagina + ": " + cantidadEtiquetas);
        System.out.println("-----------------------------------------------\n");
        
        for (int i = 0; i < cantidadEtiquetas; i++) {
            PDMarkedContent etiqueta = extractor.getMarkedContents().get(i);
            System.out.println("Contenido     : " + etiqueta.toString());
            System.out.println("Tag           : " + etiqueta.getTag());
            //if (etiqueta.getTag() == null || etiqueta.getTag().isBlank() || etiqueta.getTag().equals("Suspect"))
                //System.out.println("Contenido     : " + etiqueta.getContents().toString());
            
            // Aparentemente siempre son nulls
            //System.out.println("Actual text   : " + contenido.getActualText());
            //System.out.println("Alt. descrip. : " + contenido.getAlternateDescription());
            // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

            // getContents nos ofrece un array con cada una de las letras del texto
            //System.out.println("Contenido 0   : " + contenido.getContents().get(0)); 
            //System.out.println("Contenido 4   : " + contenido.getContents().get(4)); 
            
            //StringBuilder b = new StringBuilder();
            //etiqueta.getContents().forEach(b::append);
            //System.out.println("Texto real    : " + b.toString());
        }
    }
    
    /**
     * Imprime información general de un documento dado: numero de paginas, @n titulo, autor, asunto,
     * palabras clave, creador, productor, fecha de creación, fecha de modificación, y un booleano
     * indicando si el documento está reventado.
     * @param document Documento a analizar
     */
    public static void printGeneralInfo(PDDocument document) {
        
        PDDocumentInformation info = document.getDocumentInformation();
        System.out.println( "Page Count=" + document.getNumberOfPages() );
        System.out.println( "Title=" + info.getTitle() );
        System.out.println( "Author=" + info.getAuthor() );
        System.out.println( "Subject=" + info.getSubject() );
        System.out.println( "Keywords=" + info.getKeywords() );
        System.out.println( "Creator=" + info.getCreator() );
        System.out.println( "Producer=" + info.getProducer() );
        System.out.println( "Creation Date=" + info.getCreationDate().getTime());
        System.out.println( "Modification Date=" + info.getModificationDate().getTime());
        System.out.println( "Trapped=" + info.getTrapped() );
        System.out.println("-------------\n");
    }
    
    public static void printArbolEtiquetas(PDDocument document) {
        
        PDFTreeModel tree = new PDFTreeModel(document);
        PDStructureTreeRoot treeRoot = document.getDocumentCatalog().getStructureTreeRoot();
        
        
        for (Object kid : treeRoot.getKids()) {
            if (kid instanceof PDStructureElement)
                recPrintInfoKids((PDStructureElement) kid, 0);
            else if (kid instanceof PDObjectReference)
                recPrintInfoKids((PDObjectReference) kid, 0);
        }
    }
    
    public static void recPrintInfoKids(PDStructureElement kid, int depth) {
        
        printInfoKid(kid, depth);
        
        int numKids = kid.getKids().size();
                
        String space = new String(new char[depth]).replace('\0', ' ');
        System.out.print("\n" + space);
        System.out.print("No de kids: " + numKids + "\n-------------\n");
        
        List<Object> kids = kid.getKids();
        
        for (int i = 0; i < numKids; i++) {
            Object newKid = kids.get(i);
            if (newKid instanceof PDStructureElement)
                recPrintInfoKids((PDStructureElement) newKid, depth + 1);
            else if (newKid instanceof PDObjectReference)
                recPrintInfoKids((PDObjectReference) newKid, depth + 1);
        }
    }
    
    public static void recPrintInfoKids(PDObjectReference kid, int depth) {
        
        printInfoKid(kid, depth);
        
    }
    
    public static void printInfoKid(PDStructureElement kid, int depth) {
        try {
            String space = "\n" + new String(new char[depth]).replace('\0', ' ');
            System.out.print(space + "Clase: " + kid.getClass());
            System.out.print(space + "Title: " + kid.getTitle());
            System.out.print(space + "Structure type: " + kid.getStructureType());
            System.out.print(space + "Standard structure type: " + kid.getStandardStructureType());
            System.out.print(space + "Actual text: " + kid.getActualText());
            System.out.print(space + "Alternate description: " + kid.getAlternateDescription());
            System.out.print(space + "Element ID: " + kid.getElementIdentifier());
            System.out.print(space + "Expanded form: " + kid.getExpandedForm());
            System.out.print(space + "Language: " + kid.getLanguage());
            System.out.print(space + "------");
        } catch (Exception e) {
            System.out.println("No se pudo obtener informacion de un kid :(\n" + e);
        }
    }
    
    public static void printInfoKid(PDObjectReference kid, int depth) {
        try {
            String space = new String(new char[depth]).replace('\0', ' ');
            System.out.print(space + "Clase: " + kid.getClass());
            System.out.print(space + "COS Object: " + kid.getCOSObject());
            System.out.print(space + "------");
        } catch (Exception e) {
            System.out.println("No se pudo obtener informacion de un kid :(\n" + e);
        }
    }
    
    /**
     * Imprime todo el texto del documento.
     * @param document Documento cuyo texto queremos imprimir
     */
    public static void printTodoTexto(PDDocument document) {
        PDFTextStripper pdfStripper = new PDFTextStripper();
        
        try {
            String texto = pdfStripper.getText(document);
            System.out.println("Texto completo:\n---------------\n" + texto);
        } catch(Exception e) {
            System.out.println("No se ha podido obtener el texto :(\n" + e);
        }
    }
    
    /**
     * Imprime los metadados del documento (XML Metadata)
     * @param document Documento a analizar
     */
    public static void printXMLMetadata(PDDocument document) {
        PDDocumentCatalog catalog = document.getDocumentCatalog();
        PDMetadata metadata = catalog.getMetadata();
        
        try {
            InputStream xmlInputStream = metadata.createInputStream();
            String s = new String(xmlInputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("XML Metadata\n" + s);
        } catch (IOException e) {
            System.out.println("No se ha podido obtener el XML Metadata :(\n" + e);
        }
    }
    
    /**
     * Imprime todas las anotaciones existentes en una página de un documento.
     * @param document Documento del cual extraer las anotaciones.
     * @param pagina Numero de la pagina que queremos analizar. De 0 a n.
     */
    public static boolean printAnotacionesPagina(PDDocument document, int pagina) {
        PDPage page = document.getPage(pagina);
        int numAnotaciones = 0;
        try {
            numAnotaciones = page.getAnnotations().size();
            System.out.println("No de anotaciones: " + numAnotaciones);
            System.out.println("Anotaciones de la pagina " + pagina + ":\n");
            
            for (PDAnnotation anotacion : page.getAnnotations()) {
                System.out.println("Nombre: " + anotacion.getAnnotationName());
                System.out.println("Subtipo: " + anotacion.getSubtype());
                System.out.println("Contenido: " + anotacion.getContents());
                System.out.println("---------");
                
            }
        } catch (IOException e) {
            System.out.println("No se ha podido obtener las anotaciones de la pagina " + pagina + " :(\n" + e);
        }
        return numAnotaciones > 0;
    }
    
    /**
     * Imprime el nombre de todas las propiedades de una pagina.
     * @param document Documento a analizar.
     * @param pagina Numero de la pagina a analizar. De 0 a n.
     */
    public static void printPropiedadesPagina(PDDocument document, int pagina) {
        PDPage page = document.getPage(pagina);
        
        System.out.println("Propiedades pagina " + pagina + ":");
        
        Iterator<COSName> propertiesNames = page.getResources().getPropertiesNames().iterator();
        while (propertiesNames.hasNext())
            System.out.println("Propiedad: " + propertiesNames.next().getName());
    }
}
