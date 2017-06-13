package myapp;

import java.util.List;
import java.util.Random;

import myapp.presentationmodel.canton.CantonAtt;
import org.opendolphin.core.server.DTO;

import myapp.service.SomeService;
import myapp.util.DTOMixin;

public class SomeCombinedService implements SomeService, DTOMixin {

    String[] cantons = {"Zürich", "Aargau", "Bern", "Luzern","Uri","Schwyz","Obwalden","Nidwalden",
            "Glarus","Zug","Freiburg","Solothurn","Basel-Stadt","Basel-Land","Schaffhausen",
            "Appenzell-Ausserrhoden","Appenzell-Innerrhoden","St.Gallen","Graubünden","Thurgau ",
            "Tessin","Waadt","Wallis","Neuenburg","Genf","Jura",};

    @Override
    public DTO loadSomeEntity() {
        long id = createNewId();

        Random r        = new Random();
        String canton     = cantons[r.nextInt(cantons.length)];
        int    age      = r.nextInt(43);
        return new DTO(createSlot(CantonAtt.ID      , id     , id),
                       createSlot(CantonAtt.CANTON, canton   , id),
                       createSlot(CantonAtt.AGE     , age    , id));
                       //createSlot(CantonAtt.IS_ADULT, isAdult, id));
    }

    @Override
    public void save(List<DTO> dtos) {
        System.out.println(" Data to be saved");
        dtos.stream()
            .flatMap(dto -> dto.getSlots().stream())
            .map(slot -> String.join(", ", slot.getPropertyName(), slot.getValue().toString(), slot.getQualifier()))
            .forEach(System.out::println);
    }
}
