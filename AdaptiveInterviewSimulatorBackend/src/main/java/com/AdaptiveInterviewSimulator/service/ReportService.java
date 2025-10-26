package com.AdaptiveInterviewSimulator.service;

import com.AdaptiveInterviewSimulator.model.Session;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class ReportService {

    public byte[] generateSessionPdf(Session s) throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.LETTER);
        doc.addPage(page);

        PDPageContentStream cs = new PDPageContentStream(doc, page);
        PDType0Font font = PDType0Font.load(doc, PDType1Font.HELVETICA.getFontDescriptor().getFontFile().createInputStream()); // fallback
        float y = page.getMediaBox().getUpperRightY() - 50;

        cs.beginText();
        cs.setFont(font, 18);
        cs.newLineAtOffset(50, y);
        cs.showText("Adaptive Interview Simulator — Session Report");
        cs.endText();

        y -= 30;
        DateTimeFormatter f = DateTimeFormatter.ISO_LOCAL_DATE;
        cs.beginText();
        cs.setFont(font, 12);
        cs.newLineAtOffset(50, y);
        cs.showText("User: " + s.getUserId() + "    Date: " + s.getStartedAt().toString());
        cs.endText();

        y -= 30;
        for (var sq : s.getQuestions()) {
            cs.beginText();
            cs.setFont(font, 12);
            cs.newLineAtOffset(50, y);
            cs.showText("Q: " + sq.getPromptText());
            cs.endText();
            y -= 16;
            for (var ans : sq.getAnswers()) {
                if (ans.getAnalysis() != null) {
                    cs.beginText();
                    cs.setFont(font, 10);
                    cs.newLineAtOffset(60, y);
                    String t = "A: " + (ans.getAnswerText() != null ? ans.getAnswerText().replaceAll("\\s+"," ") : "");
                    if (t.length() > 120) t = t.substring(0,120) + "...";
                    cs.showText(t);
                    cs.endText();
                    y -= 14;
                    cs.beginText();
                    cs.setFont(font, 10);
                    cs.newLineAtOffset(60, y);
                    String meta = String.format("Content:%d Clarity:%d Confidence:%d",
                            ans.getAnalysis().getContentScore(),
                            ans.getAnalysis().getClarityScore(),
                            ans.getAnalysis().getConfidenceScore());
                    cs.showText(meta);
                    cs.endText();
                    y -= 20;
                    if (y < 80) { cs.close(); page = new PDPage(); doc.addPage(page); cs = new PDPageContentStream(doc, page); y = page.getMediaBox().getUpperRightY() - 50; }
                }
            }
        }

        cs.close();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        doc.save(baos);
        doc.close();
        return baos.toByteArray();
    }
}
