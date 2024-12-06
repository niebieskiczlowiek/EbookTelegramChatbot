package com.technischools.telegramApp.utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
* Custom class that provides methods for reading and manipulating PDF files.
* */
public class PDFReader {
    /*
    * check if given file is a PDF
    * */
    public static boolean isPdfFile(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        String fileName = file.getName();
        return fileName.toLowerCase().endsWith(".pdf");
    }

    /*
    * gets a list of all PDF files from a given path
    * if the given path is a direct path to a PDF, it returns a list with just that one file
    * */
    public static List<PDDocument> getFilesFromPath(String path) throws IOException {
        File file = new File(path);
        List<PDDocument> fileList = new ArrayList<>();
        boolean isFile = file.isFile();

        if (isFile && isPdfFile(file)) {
            PDDocument document = Loader.loadPDF(file);
            fileList.add(document);
        }

        File[] folderContents = file.listFiles();
        if (folderContents == null) return null;
        for (File folderFile : folderContents) {
            if (isPdfFile(folderFile)) fileList.add(Loader.loadPDF(folderFile));
        }
        return fileList;
    }

    /*
    * Allows to retrieve the text of a PDF file from path, file or document
    * */
    public static String getTextFromPDF(String path) throws IOException {
        File file = new File(path);
        return getTextFromPDF(file);
    }
    public static String getTextFromPDF(File file) throws IOException {
        PDDocument document = Loader.loadPDF(file);
        PDFTextStripper stripper = new PDFTextStripper();
        return stripper.getText(document);
    }
    public static String getTextFromPDF(PDDocument document) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        return stripper.getText(document);
    }

    /*
    * Splits PDF specified in path or directly from document into a list of pages
    * */
    public static List<PDDocument> splitIntoPages(String path) throws IOException {
        File file = new File(path);
        PDDocument document = Loader.loadPDF(file);
        Splitter splitter = new Splitter();
        return splitter.split(document);
    }
    public static List<PDDocument> splitIntoPages(PDDocument document) throws IOException {
        Splitter splitter = new Splitter();
        return splitter.split(document);
    }
    public static List<PDDocument> splitIntoPages(List<PDDocument> documents) {
        List<PDDocument> pages = new ArrayList<>();
        documents.forEach(p -> {
            try {
                List<PDDocument> documentPages = PDFReader.splitIntoPages(p);
                pages.addAll(documentPages);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return pages;
    }

    /*
    * finds relevant pages based on a user query
    * */
    public static List<PDDocument> findRelevantPages(List<PDDocument> pages, String query) throws IOException {
        List<PDDocument> relevantPages = new ArrayList<>();
        List<String> keywords = NLPUtils.extractKeywords(query);
        for (PDDocument page : pages) {
            String pageText = getTextFromPDF(page);
            boolean containsKeyword = keywords.stream().anyMatch(pageText::contains);
            if (containsKeyword) relevantPages.add(page);
        }
        return relevantPages;
    }
}
