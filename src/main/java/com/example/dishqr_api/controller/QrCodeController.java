package com.example.dishqr_api.controller;

import com.example.dishqr_api.model.QrCode;
import com.example.dishqr_api.service.QrCodeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path ="/qrCode")
@AllArgsConstructor
public class QrCodeController {

    private final QrCodeService qrCodeService;

    //todo zabezpieczyć przez złym kodem qr
    @GetMapping(path = "/getValue/{id}")
    public ResponseEntity<QrCode> checkCode(@PathVariable String id){

        Optional<QrCode> qrCode = qrCodeService.checkCode(id);

        if(qrCode.isEmpty()){
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(qrCode.get());
        }
    }
}
