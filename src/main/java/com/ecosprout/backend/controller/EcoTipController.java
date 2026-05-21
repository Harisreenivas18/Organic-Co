package com.ecosprout.backend.controller;

import com.ecosprout.backend.model.EcoTip;
import com.ecosprout.backend.repository.EcoTipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tips")
@CrossOrigin(origins = "*")
public class EcoTipController {

    @Autowired
    private EcoTipRepository ecoTipRepository;

    @GetMapping("/random")
    public ResponseEntity<EcoTip> getRandomTip() {
        return ecoTipRepository.findRandomTip()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}