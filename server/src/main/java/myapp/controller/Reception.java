package myapp.controller;

import myapp.service.SomeService;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;


public class Reception extends DolphinServerAction {
    private SomeService myService;

    public Reception(SomeService myService) {
        this.myService = myService;
    }

    public void registerIn(ActionRegistry registry) {
        // todo register all your controllers here.
        getServerDolphin().register(new CantonController(myService));

        //always needed
        getServerDolphin().register(new PresentationStateController());
    }
}
