package com.sparta.springresttemplateclient.service;

import com.sparta.springresttemplateclient.dto.ItemDto;
import com.sparta.springresttemplateclient.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RestTemplateService {

    private final RestTemplate restTemplate;

    public RestTemplateService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public ItemDto getCallObject(String query) {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:7070")
                .path("/api/server/get-call-obj")
                .queryParam("query", query) //검색어를 파람으로 보냄
                .encode()
                .build()
                .toUri();
        log.info("uri = " + uri);

        ResponseEntity<ItemDto> responseEntity = restTemplate.getForEntity(uri, ItemDto.class);
        // getForEntity = 해당 url에 get방식으로 요청을 보냄
        // 두번째 파라미터로 받을 때 쓸 클래스 타입을 지정 해 주면 객체 형태로 담김

        log.info("statusCode = " + responseEntity.getStatusCode());

        return responseEntity.getBody();
    }


    //여러개의 리스트를 받아 올 때
    //String으로 받음!!
    public List<ItemDto> getCallList() {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:7070")
                .path("/api/server/get-call-list")
                .encode()
                .build()
                .toUri();
        log.info("uri = " + uri);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class); //스트링으로 받아서 다시 변환

        log.info("statusCode = " + responseEntity.getStatusCode());
        log.info("Body = " + responseEntity.getBody());

        return fromJSONtoItems(responseEntity.getBody());
    }

    public ItemDto postCall(String query) {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:7070")
                .path("/api/server/post-call/{query}")
                .encode()
                .build()
                .expand(query)
                .toUri();
        log.info("uri = " + uri);

        User user = new User("Robbie", "1234");

        ResponseEntity<ItemDto> responseEntity = restTemplate.postForEntity(uri, user, ItemDto.class);


        log.info("statusCode = " + responseEntity.getStatusCode());

        return responseEntity.getBody();
    }

    //헤더에 데이터 넘기기
    public List<ItemDto> exchangeCall(String token) {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:7070")
                .path("/api/server/exchange-call")
                .encode()
                .build()
                .toUri();
        log.info("uri = " + uri);

        User user = new User("Robbie", "1234");

        RequestEntity<User> requestEntity = RequestEntity
                .post(uri)
                .header("X-Authorization", token)
                .body(user);

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        return fromJSONtoItems(responseEntity.getBody());
    }

    //스트링으로 받은 데이터를 json 으로 변환
    public List<ItemDto> fromJSONtoItems(String responseEntity) {
        JSONObject jsonObject = new JSONObject(responseEntity);
        JSONArray items  = jsonObject.getJSONArray("items"); //key값으로 items를 받아 올 것
        List<ItemDto> itemDtoList = new ArrayList<>();

        //데이터를 한줄씩 뽑음
        for (Object item : items) {
            ItemDto itemDto = new ItemDto((JSONObject) item); //itemDto로 변환
            itemDtoList.add(itemDto);
        }

        return itemDtoList;
    }


}