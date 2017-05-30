package myapp.service.impl;

import java.util.List;
import java.util.Random;

import org.opendolphin.core.server.DTO;

import myapp.presentationmodel.person.PersonAtt;
import myapp.service.SomeService;
import myapp.util.DTOMixin;

public class SomeRemoteService implements SomeService, DTOMixin {
    String[] names = {"Neil Amstrong"  , "Michael Collins" , "Edwin Aldrin",      // Apollo 11
                      "Charles Conrad" , "Richard Gordon"  , "Alan Bean",         // Apollo 12
                      "James Lovell"   , "John Swigert"    , "Fred Haise",        // Apollo 13
                      "Alan Shepard"   , "Stuart Roosa"    , "Edgar Mitchell",    // Apollo 14
                      "David Scott"    , "Alfred Worden"   , "James Irwin",       // Apollo 15
                      "John Young"     , "Thomas Mattingly", "Charles Duke",      // Apollo 16
                      "Eugene Cernan"  , "Ronald Evans"    , "Harrison Schmitt"}; // Apollo 17

    @Override
    public DTO loadEntity(long entityId) {
        int index = Math.max(0, Math.min((int) entityId, names.length - 1));

        String  name    = names[index];
        int     age     = index + 22;
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
