package danil.example.SearchUnorderedFile.controller;

import danil.example.SearchUnorderedFile.service.NumberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/file")
@Tag(name = "Поиск по файлу")
public class NumberController {

    private final NumberService numberService;

    public NumberController(NumberService numberService) {
        this.numberService = numberService;
    }

    @Operation(summary = "Поиск N-е минимальное число из файла XLSX")
    @GetMapping("/findMin")
    public Long findNMinNumber(
            @Parameter(description = "Полный путь к файлу формата XLSX") @RequestParam String filePath,
            @Parameter(description = "Порядковый номер N", example = "10") @RequestParam Integer number)
            throws IOException  {
        return numberService.findNthMinNumber(filePath, number);
    }
}
