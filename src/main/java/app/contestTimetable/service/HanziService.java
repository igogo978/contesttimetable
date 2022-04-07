package app.contestTimetable.service;

import app.contestTimetable.model.Hanzi;
import app.contestTimetable.repository.HanziRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class HanziService {

    @Autowired
    HanziRepository hanziRepository;

    Logger logger = LoggerFactory.getLogger(HanziService.class);

    public void update(Hanzi hanzi) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        Optional<Hanzi> dbHanzi = hanziRepository.findById(hanzi.getId());

        if (dbHanzi.isEmpty()) {
            hanziRepository.save(hanzi);
        } else {
            dbHanzi.get().setFont(hanzi.getFont());
            hanziRepository.save(dbHanzi.get());
        }
    }


    public List<Hanzi> getAll() {
        List<Hanzi> hanziList = new ArrayList<>();
        hanziRepository.findAll().forEach(hanziList::add);
        return hanziList;
    }

    public void delete(Hanzi hanzi) {
        hanziRepository.delete(hanzi);

    }

    public Hanzi getHanzi(String hanzi) {
        return hanziRepository.findByHanzi(hanzi);
    }

    public Optional<Hanzi> getHanziById(String id) {
        return hanziRepository.findById(id);
    }


}
