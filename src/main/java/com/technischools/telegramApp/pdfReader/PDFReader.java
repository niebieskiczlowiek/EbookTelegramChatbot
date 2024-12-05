package com.technischools.telegramApp.pdfReader;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFReader {
    public static boolean isPdfFile(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        String fileName = file.getName();
        return fileName.toLowerCase().endsWith(".pdf");
    }

    public static List<String> getFilesFromPath(String path) {
        File folder = new File(path);
        List<String> fileList = new ArrayList<>();
        File[] folderContents = folder.listFiles();
        if (folderContents == null) return null;
        for (File content : folderContents) {
            if (isPdfFile(content)) fileList.add(content.getName());
        }
        return fileList;
    }

    public static String getTextFromPDF(String path) throws IOException {
        File file = new File(path);
        PDDocument document = Loader.loadPDF(file);
        PDFTextStripper stripper = new PDFTextStripper();
        return stripper.getText(document);
    }
}
