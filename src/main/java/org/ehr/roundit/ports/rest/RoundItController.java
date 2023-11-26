package org.ehr.roundit.ports.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ehr.roundit.domain.RoundItService;
import org.ehr.roundit.domain.entities.RoundItPortData;
import org.ehr.roundit.ports.rest.entities.RoundItPortDataImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@Slf4j
@RestController
@RequestMapping("v1/round-it")
@RequiredArgsConstructor
public class RoundItController {
    @Autowired
    private RoundItService roundItService;

    // just a very rough localhost executor, of course token should go through Authorisation
    @GetMapping()
    public ResponseEntity<String> roundIt(@RequestParam String accessToken,
                                          @RequestParam String accountUid, @RequestParam String saverType) {
        RoundItPortData portData = RoundItPortDataImpl.builder()
                .accessToken(accessToken)
                .accountUid(accountUid)
                .saverType(saverType)
                .build();

        try {
            BigInteger savings = roundItService.roundItAndSave(portData);
            return ResponseEntity.ok(savings.toString());
        } catch (Exception e) {
            log.error("Failed to process roundIt request: ", e);
        }
        return ResponseEntity.unprocessableEntity().build();
    }
}
