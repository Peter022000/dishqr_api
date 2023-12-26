package com.example.DishQR_api.controller;

import com.example.DishQR_api.dto.ChangeOrderStatusRequest;
import com.example.DishQR_api.dto.QrCodeDto;
import com.example.DishQR_api.model.QrCode;
import com.example.DishQR_api.service.QrCodeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path ="/qrCode")
@AllArgsConstructor
public class QrCodeController {

    private final QrCodeService qrCodeService;

    @GetMapping(path = "/getValue/{id}")
    public ResponseEntity<QrCode> checkCode(@PathVariable String id){

        Optional<QrCode> qrCode = qrCodeService.checkCode(id);

        if(qrCode.isEmpty()){
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(qrCode.get());
        }
    }

    @GetMapping(path = "/getQRCodes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getQRCodes(){
        return qrCodeService.getQRCodes();
    }

    @PostMapping(path = "/updateQRCode")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateQRCode(@RequestBody QrCodeDto qrCodeDto){
        return qrCodeService.updateQRCode(qrCodeDto);
    }

    @PostMapping(path = "/addQRCode")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addQRCode(@RequestBody QrCodeDto qrCodeDto){
        return qrCodeService.addQRCode(qrCodeDto);
    }

    @DeleteMapping(path = "/deleteQRCode/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteQRCode(@PathVariable String id){
        return qrCodeService.deleteQRCode(id);
    }
}
