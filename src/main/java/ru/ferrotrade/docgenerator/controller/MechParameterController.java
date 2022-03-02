package ru.ferrotrade.docgenerator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.ferrotrade.docgenerator.service.mechService.MechService;
import ru.ferrotrade.docgenerator.view.PartInfoSaveView;

@RestController
@RequestMapping("/mech")
@RequiredArgsConstructor
public class MechParameterController {

    private final MechService mechService;

    @GetMapping("{id}")
    public @ResponseBody
    PartInfoSaveView getChemistry(@PathVariable String id) {
        return mechService.getPartView(id);
    }

    @PutMapping
    public void saveChemistry(@RequestBody PartInfoSaveView plavInfo) {
        mechService.savePartInfo(plavInfo);
    }

}
