package com.hasanjaved.reportmate.utility;

import android.content.Context;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
//import org.docx4j.openpackaging.exceptions.Docx4JException;
//import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
//import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
//import org.docx4j.wml.ObjectFactory;
//import org.docx4j.wml.P;
public class WordGenerator {



//    public static void createDocxWithTextsAndImages(Context context, List<String> texts, List<String> imagePaths) {
//        try {
//            // 1. Create Word document
//            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
//            ObjectFactory factory = new ObjectFactory();
//
//            // 2. Add multiple texts
//            for (String text : texts) {
//                P paragraph = factory.createP();
//                paragraph.getContent().add(
//                        factory.createR().withContent(factory.createText(text))
//                );
//                wordMLPackage.getMainDocumentPart().addObject(paragraph);
//            }
//
//            // 3. Add multiple images
//            for (String imagePath : imagePaths) {
//                File imageFile = new File(imagePath);
//                if (imageFile.exists()) {
//                    FileInputStream is = new FileInputStream(imageFile);
//                    byte[] bytes = new byte[(int) imageFile.length()];
//                    is.read(bytes);
//                    is.close();
//
//                    BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);
//
//                    int docPrId = (int) (Math.random() * 100000); // random id
//                    int cNvPrId = (int) (Math.random() * 100000); // random id
//
//                    P imageParagraph = factory.createP();
//                    imageParagraph.getContent().add(imagePart.createImageInline("Image", "Image", docPrId, cNvPrId, false));
//                    wordMLPackage.getMainDocumentPart().addObject(imageParagraph);
//                }
//            }
//
//            // 4. Save the document
//            File outputDir = new File(context.getExternalFilesDir(null), "docx_output");
//            if (!outputDir.exists()) outputDir.mkdirs();
//
//            File outputFile = new File(outputDir, "docx4j_output.docx");
//            FileOutputStream fos = new FileOutputStream(outputFile);
//            wordMLPackage.save(fos);
//            fos.close();
//
//            Toast.makeText(context, "DOCX created at: " + outputFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }

}
