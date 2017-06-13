package myapp.presentationmodel.canton;

import myapp.presentationmodel.PMDescription;
import myapp.util.AttributeDescription;
import myapp.util.ValueType;


public enum CantonAtt implements AttributeDescription{
    ID(ValueType.ID),
    CANTON(ValueType.STRING),
    AGE(ValueType.INT),
    IS_ADULT(ValueType.BOOLEAN);

    private final ValueType valueType;

    CantonAtt(ValueType type) {
        valueType = type;
    }

    public ValueType getValueType() {
        return valueType;
    }

    @Override
    public PMDescription getPMDescription() {
        return PMDescription.CANTON;
    }
}
