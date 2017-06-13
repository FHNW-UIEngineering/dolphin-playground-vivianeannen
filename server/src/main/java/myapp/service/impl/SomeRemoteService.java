package myapp.service.impl;

import java.util.List;
import java.util.Random;

import myapp.presentationmodel.canton.Canton;
import org.opendolphin.core.server.DTO;

import myapp.presentationmodel.canton.CantonAtt;
import myapp.service.SomeService;
import myapp.util.DTOMixin;

public class SomeRemoteService implements SomeService, DTOMixin {
    String[] cantons = {"Zürich", "Aargau", "Bern", "Luzern","Uri","Schwyz","Obwalden","Nidwalden",
            "Glarus","Zug","Freiburg","Solothurn","Basel-Stadt","Basel-Land","Schaffhausen",
            "Appenzell-Ausserrhoden","Appenzell-Innerrhoden","St.Gallen","Graubünden","Thurgau ",
            "Tessin","Waadt","Wallis","Neuenburg","Genf","Jura",};

    @Override
    public DTO loadSomeEntity() {
        long id = createNewId();

        Random r        = new Random();
        String name     = cantons[r.nextInt(cantons.length)];
        int    age      = r.nextInt(43);
        boolean isAdult = age >= 18;
        return new DTO(createSlot(CantonAtt.ID      , id     , id),
                       createSlot(CantonAtt.CANTON, name   , id),
                       createSlot(CantonAtt.AGE     , age    , id),
                       createSlot(CantonAtt.IS_ADULT, isAdult, id));
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
