package app.contestTimetable.api;


import app.contestTimetable.model.Hanzi;
import app.contestTimetable.service.HanziService;
import app.contestTimetable.service.PdfService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
public class HanziApiController {


    Logger logger = LoggerFactory.getLogger(HanziApiController.class);

    @Autowired
    HanziService hanziService;

    @Autowired
    PdfService pdfService;


    @GetMapping(value = "/api/hanzi")
    public List<Hanzi> getHanzi() {
        return hanziService.getAll();
    }


    @PutMapping(value = "/api/hanzi")
    public List<Hanzi> updateHanzi(@RequestBody String payload) throws JsonProcessingException, JsonProcessingException {
        logger.info(payload);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(payload);
        JsonNode hanziNode = root.get("hanzi");
        Hanzi hanzi = new Hanzi();

        hanzi.setHanzi(hanziNode.get("hanzi").asText().trim());
        hanzi.setFont(hanziNode.get("font").asText());

        String id = Base64.getEncoder().encodeToString(hanzi.getHanzi().getBytes(StandardCharsets.UTF_8));
        logger.info(id);
        hanzi.setId(id);


        hanziService.update(hanzi);

        return hanziService.getAll();
    }

    @DeleteMapping(value = "/api/hanzi")
    public List<Hanzi> deleteHanzi(@RequestBody String payload) throws JsonProcessingException {
        logger.info(payload);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(payload);
        String hanziStr = root.get("delete").asText();

        Hanzi hanzi = hanziService.getHanzi(hanziStr);

        if (hanzi != null) {
            hanziService.delete(hanzi);
        }

        return hanziService.getAll();
    }


    @GetMapping(value = "/public/api/hanzi/print/{id}")
    public ResponseEntity<Resource> print(@PathVariable(value = "id") String id) throws IOException {

        Optional<Hanzi> hanzi = hanziService.getHanziById(id);
        if (hanzi.isPresent()) {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(byteArrayOutputStream));
            Document document = new Document(pdfDoc);

            PdfFont twKaiExtFont = pdfService.getFont(hanzi.get());

            Paragraph paragraph = new Paragraph();
            paragraph.add(String.format("%s", hanzi.get().getHanzi())).setFont(twKaiExtFont).setBold().setFontSize(50).setTextAlignment(TextAlignment.CENTER);
            document.add(paragraph);
            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("charset", "utf-8");
            headers.setContentDispositionFormData("attachment", String.format("%s", "hanzi.pdf"));
            headers.setContentType(MediaType.APPLICATION_PDF);
            Resource resource = new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            return ResponseEntity.ok().headers(headers).body(resource);

        }
        return null;


    }

}