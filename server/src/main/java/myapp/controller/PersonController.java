package myapp.controller;

import java.beans.PropertyChangeListener;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.EventBus;
import org.opendolphin.core.server.ServerAttribute;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.Slot;
import org.opendolphin.core.server.comm.ActionRegistry;

import groovyx.gpars.dataflow.DataflowQueue;
import myapp.presentationmodel.BasePmMixin;
import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.person.Person;
import myapp.presentationmodel.person.PersonCommands;
import myapp.service.SomeService;
import myapp.util.Controller;

/**
 * This is an example for an application specific controller.
 * <p>
 * Controllers may have many actions that serve a common purpose.
 * <p>
 * Todo: Replace this with your Controller
 */
class PersonController extends Controller implements BasePmMixin {
    private final EventBus            personInfoBus;
    private final DataflowQueue<Slot> personQueue = new DataflowQueue<>();
    private       boolean             silent      = false;

    private final SomeService service;

    private Long currentEntityId = -1L;

    private Person personProxy;

    PersonController(Bus bus, SomeService service) {
        this.personInfoBus = bus.getEventBus();
        this.personInfoBus.subscribe(personQueue);

        this.service = service;
    }


    @Override
    public void registerCommands(ActionRegistry registry) {
        registry.register(PersonCommands.SHOW_NEXT, ($, $$) -> showNext());
        registry.register(PersonCommands.SHOW_LAST, ($, $$) -> showLast());
        registry.register(PersonCommands.SAVE     , ($, $$) -> save());
        registry.register(PersonCommands.RESET    , ($, $$) -> reset(PMDescription.PERSON));

        registry.register(PersonCommands.ON_PUSH   , ($, $$) -> processEventsFromQueue(60, TimeUnit.SECONDS));
        registry.register(PersonCommands.ON_RELEASE, ($, $$) -> personInfoBus.publish(null, new Slot("a", null)));
    }

    @Override
    protected void initializeBasePMs() {
        ServerPresentationModel personProxyPM = createProxyPM(PMDescription.PERSON, PERSON_PROXY_PM_ID);

        PropertyChangeListener proliferator = evt -> {
            if (silent) {
                return;
            }
            ServerAttribute attribute = (ServerAttribute) evt.getSource();
            if (attribute.getQualifier() != null) {
                Slot slot = new Slot(attribute.getPropertyName(), attribute.getValue(), attribute.getQualifier());
                personInfoBus.publish(personQueue, slot);
            }
        };

        personProxyPM.getAttributes().stream()
          .filter(attr -> attr.getTag().equals(Tag.VALUE))
          .forEach(attr -> attr.addPropertyChangeListener(Attribute.VALUE, proliferator));

        personProxy = new Person(personProxyPM);
    }

    private void processEventsFromQueue(int timeoutValue, TimeUnit timeoutUnit) {
        try {
            Slot slot = personQueue.getVal(timeoutValue, timeoutUnit);
            while (null != slot) {
                silent = true; // do not issue additional posts on the bus from value changes that come from the bus
                if ("toShow".equals(slot.getPropertyName())) {
                    show((Long) slot.getValue());
                } else {
                    List<ServerAttribute> attributes = getServerDolphin().findAllAttributesByQualifier(slot.getQualifier());
                    for (ServerAttribute attribute : attributes) {
                        PresentationModel pm = attribute.getPresentationModel();
                        if (personProxy.getPresentationModel().getPresentationModelType().equals(pm.getPresentationModelType()) &&
                            !attribute.getValue().equals(slot.getValue())) {
                            attribute.setValue(slot.getValue());
                        }
                    }
                }
                silent = false;

                slot = personQueue.getVal(20, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            // do nothing
        }
    }

    @Override
    protected void setDefaultValues() {
        personProxy.name.setMandatory(true);
    }

    @Override
    protected void setupValueChangedListener() {
        personProxy.age.valueProperty().addListener((observable, oldValue, newValue) -> {
            personProxy.isAdult.setValue(newValue.intValue() >= 18);
        });


        getApplicationState().language.valueProperty().addListener((observable, oldValue, newValue) -> translate(personProxy, newValue));
    }

    ServerPresentationModel showNext() {
        return show(currentEntityId + 1);
    }

    ServerPresentationModel showLast() {
        return show(currentEntityId - 1);
    }

    ServerPresentationModel show(long idToShow) {
        if(!silent){
            personInfoBus.publish(personQueue, new Slot("toShow", idToShow));
        }

        String                  pmId = PMDescription.PERSON.pmId(idToShow);
        ServerPresentationModel pm   = getServerDolphin().getAt(pmId);

        if (pm == null) {
            DTO dto = service.loadEntity(idToShow);
            pm = createPM(PMDescription.PERSON, dto);
        }

        currentEntityId = idToShow;

        personProxy.getPresentationModel().syncWith(pm);

        return pm;
    }


    void save() {
        List<DTO> dtos = dirtyDTOs(PMDescription.PERSON);
        service.save(dtos);
        rebase(PMDescription.PERSON);
    }


    @Override
    public Dolphin getDolphin() {
        return getServerDolphin();
    }
}
