package myapp.presentationmodel.canton;

import myapp.util.veneer.*;
import org.opendolphin.core.BasePresentationModel;


public class Canton extends PresentationModelVeneer {
    public Canton(BasePresentationModel pm) {
        super(pm);
    }

    public final FX_LongAttribute    id      = new FX_LongAttribute(getPresentationModel()   , CantonAtt.ID);
    public final FX_StringAttribute  canton    = new FX_StringAttribute(getPresentationModel() , CantonAtt.CANTON);
    public final FX_IntegerAttribute age     = new FX_IntegerAttribute(getPresentationModel(), CantonAtt.AGE);
    public final FX_BooleanAttribute isAdult = new FX_BooleanAttribute(getPresentationModel(), CantonAtt.IS_ADULT);
}
