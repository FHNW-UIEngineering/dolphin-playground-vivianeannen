package myapp;

import java.util.List;
import java.util.Random;

import org.opendolphin.core.server.DTO;

import myapp.presentationmodel.person.PersonAtt;
import myapp.service.SomeService;
import myapp.util.DTOMixin;

public class SomeCombinedService implements SomeService, DTOMixin {

    String[] names = {"Virgil Grissom", "Edward White", "Roger Chaffee",      // Apollo 1
                      "Walter Schirra", "Donn Eisele" , "Walter Cunningham",  // Apollo 7
                      "Frank Borman"  , "James Lovell", "William Anders",     // Apollo 8
                      "James McDivitt", "David Scott" , "Russel Schweickart", // Apollo 9
                      "Tom Stafford"  , "John Young"  , "Eugene Cernan"};     // Apollo 10

    @Override
    public DTO loadEntity(long entityId) {
        int index = Math.max(0, Math.min((int) entityId, names.length - 1));
        String name     = names[index];
        int    age      = index + 22;
        boolean isAdult = age >= 18;
        return new DTO(createSlot(PersonAtt.ID      , entityId, entityId),
                       createSlot(PersonAtt.NAME    , name    , entityId),
                       createSlot(PersonAtt.AGE     , age     , entityId),
                       createSlot(PersonAtt.IS_ADULT, isAdult , entityId));
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
