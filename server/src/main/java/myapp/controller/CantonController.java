package myapp.controller;

import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.SpecialPMMixin;
import myapp.presentationmodel.canton.Canton;
import myapp.presentationmodel.canton.CantonCommands;
import myapp.service.SomeService;
import myapp.util.Controller;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.comm.ActionRegistry;

import java.util.Collections;
import java.util.List;


class CantonController extends Controller implements SpecialPMMixin {

    private final SomeService service;
    private Canton cantonProxy;

    CantonController(SomeService service) {
        this.service = service;
    }

    @Override
    public void registerIn(ActionRegistry reception) {
        super.registerIn(reception);
        reception.register(CantonCommands.LOAD_SOME_CANTON, (command, response) -> loadCanton());
        reception.register(CantonCommands.SAVE            , (command, response) -> save());
        reception.register(CantonCommands.RESET           , (command, response) -> reset(PMDescription.CANTON, Collections.emptyList()));
    }

    @Override
    protected void initializeBasePMs() {
        ServerPresentationModel pm = createProxyPM(PMDescription.CANTON, CANTON_PROXY_ID);
        cantonProxy = new Canton(pm);
    }

    @Override
    protected void setDefaultValues() {
        cantonProxy.canton.setMandatory(true);
    }

    @Override
    protected void setupValueChangedListener() {
        getPresentationState().language.valueProperty().addListener((observable, oldValue, newValue) -> {
            translate(cantonProxy, newValue);
        });
    }

    ServerPresentationModel loadCanton() {
        DTO dto = service.loadSomeEntity();
        ServerPresentationModel pm = createPM(PMDescription.CANTON, dto);

        cantonProxy.getPresentationModel().syncWith(pm);

        return pm;
    }

    void save() {
        List<DTO> dtos = dirtyDTOs(PMDescription.CANTON, Collections.emptyList());
        service.save(dtos);
        rebase(PMDescription.CANTON, Collections.emptyList());
    }

    @Override
    public Dolphin getDolphin() {
        return getServerDolphin();
    }
}
