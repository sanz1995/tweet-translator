package translator;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import org.json.JSONObject;
import translator.model.LanguageRepository;

@Component
public class Translator{

    private static RabbitTemplate rabbitTemplate;

    @Value( "${yandex.key}")
    private String key;

    @Value( "es")
    private String lang;

    @Autowired
    private LanguageRepository lr;

    public Translator(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }



    @Scheduled(fixedRate = 10000)
    public void reportCurrentTime() {
        lang = lr.findAll().iterator().next().getLanguage();
    }


    public void receiveMessage(String message) {


        JSONObject tweet = new JSONObject(message);

        String texto = tweet.get("text").toString();


        RestTemplate restTemplate = new RestTemplate();

        String url = "https://translate.yandex.net/api/v1.5/tr.json/translate";



        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("key", key);
        map.add("text", texto);
        map.add("lang", lang);


        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity( url, request , String.class );


        JSONObject translation = new JSONObject(response.getBody());

        tweet.put("text",translation.getJSONArray("text").get(0));

        rabbitTemplate.convertAndSend("translation",tweet.toString());



    }



}
