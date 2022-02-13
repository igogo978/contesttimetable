package app.contestTimetable.service;

import app.contestTimetable.model.InformComments;
import app.contestTimetable.repository.InformCommentsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InformCommentsService {

    Logger logger = LoggerFactory.getLogger(InformCommentsService.class);

    @Autowired
    InformCommentsRepository informCommentsRepository;

    public InformComments getInformComments() {


        InformComments informComments = new InformComments();

        if (informCommentsRepository.findById(1).isPresent()) {
            logger.info("get informcomments");
            informComments = informCommentsRepository.findById(1).get();
        } else {
            informComments.setId(1);
            informComments.setHeader("臺中市OOO年度中小學資訊網路應用競賽決賽");
            List<String> comments = new ArrayList<>();
            comments.add("1.「所有競賽項目」的題目於競賽時間開始時自動轉址公布，請記得重新整理網頁，就可看到題目；「所有競賽項目」的作品內容都不得出現姓名及學校等相關資訊。");
            comments.add("2、競賽時間開始後，遲到30分鐘以上不得入場。競賽開始超過40分鐘後，參賽學生始得離開試場。");
            comments.add("3、競賽時間內不得自行攜帶使用任何形式之可攜式儲存媒體及通訊設備，一經發現立即取消參賽資格。（競賽主辦單位借給每位參賽者空白隨身碟一支，供暫存使用）。");
            informComments.getComments().addAll(comments);

            informCommentsRepository.save(informComments);
        }
        return informComments;
    }

    public InformComments update(String payload) throws JsonProcessingException {
        InformComments informComments = new InformComments();
        informComments.setId(1);
        ObjectMapper mapper = new ObjectMapper();
        informComments = mapper.readValue(payload, InformComments.class);
        informComments.getComments().removeIf(comment -> comment.strip().length() == 0);
        informCommentsRepository.save(informComments);

        return informCommentsRepository.findById(1).get();
    }


}
