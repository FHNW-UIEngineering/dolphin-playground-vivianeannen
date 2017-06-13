package myapp.presentationmodel;

import myapp.presentationmodel.canton.Canton;
import org.opendolphin.core.BasePresentationModel;
import org.opendolphin.core.Dolphin;


import myapp.presentationmodel.presentationstate.PresentationState;

/**
 * @author Dieter Holz
 */
public interface SpecialPMMixin {
    long   PRESENTATION_STATE_ID = -888L;
    String PRESENTATION_STATE_PM_ID = PMDescription.PRESENTATION_STATE.pmId(PRESENTATION_STATE_ID);

    long   CANTON_PROXY_ID       = -777L;
    String CANTON_PROXY_PM_ID    = PMDescription.CANTON.pmId(CANTON_PROXY_ID);

    default PresentationState getPresentationState() {
        return new PresentationState(getPresentationStatePM());
    }

    default BasePresentationModel getPresentationStatePM() {
        return (BasePresentationModel) getDolphin().getAt(PRESENTATION_STATE_PM_ID);
    }

    default Canton getCantonProxy() {
        return new Canton(getCantonProxyPM());
    }

    default BasePresentationModel getCantonProxyPM() {
        return (BasePresentationModel) getDolphin().getAt(CANTON_PROXY_PM_ID);
    }

    Dolphin getDolphin();

}
