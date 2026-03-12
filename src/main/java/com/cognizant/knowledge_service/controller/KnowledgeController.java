package com.cognizant.knowledge_service.controller;

import com.cognizant.knowledge_service.domain.KnowledgeArticle;
import com.cognizant.knowledge_service.repository.KnowledgeArticleRepository;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class KnowledgeController {
    @Autowired
    private KnowledgeArticleRepository knowledgeArticleRepository;


    @GetMapping(path = "/hello")
    public String reqestParam(@RequestParam(name="name") String name){
        return "hi " + name ;
    }

    @GetMapping(path = "/hello/{name}")
    public String pathVariable(@PathVariable(name="name") String name){
        return "hi " + name ;
    }

    @PostMapping (path = "/save")
    public String postVariable(@RequestBody() KnowledgeArticle knowledgeArticle){
        knowledgeArticle = knowledgeArticleRepository.save(knowledgeArticle);
        return knowledgeArticle.toString();
    }

    @PostMapping (path = "/article/{id}")
    public String findArticle(@PathParam("id") UUID id){
        return knowledgeArticleRepository.findById(id).toString();
    }

    @GetMapping (path = "/article/{name}")
    public String findByName(@PathParam("name") String name){
        return knowledgeArticleRepository.findByName(name).toString();
    }


}
