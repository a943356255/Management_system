package com.example.demo.elasticsearch.repository;

import com.example.demo.elasticsearch.pojo.Entity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TestRepository extends ElasticsearchRepository<Entity, Integer> {

}
