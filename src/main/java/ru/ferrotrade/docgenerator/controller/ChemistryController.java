package ru.ferrotrade.docgenerator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ferrotrade.docgenerator.service.chemistryService.ChemistryService;
import ru.ferrotrade.docgenerator.view.PlavInfoSaveView;

@RestController
@RequestMapping("/chemistry")
@RequiredArgsConstructor
public class ChemistryController {

    private final ChemistryService chemistryService;

    @GetMapping("{id}")
    public @ResponseBody
    ResponseEntity<PlavInfoSaveView> getChemistry(@PathVariable String id) {
        PlavInfoSaveView view = chemistryService.getPlavView(id);
        return view == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).build() : ResponseEntity.ok(view);
    }

    @PutMapping
    public void saveChemistry(@RequestBody PlavInfoSaveView plavInfo) {
        chemistryService.savePlavInfo(plavInfo);
    }

}
