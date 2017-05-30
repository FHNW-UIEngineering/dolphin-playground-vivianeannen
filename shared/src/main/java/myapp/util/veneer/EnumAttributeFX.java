package myapp.util.veneer;

import javafx.beans.property.ObjectProperty;

import org.opendolphin.core.PresentationModel;

import myapp.util.AttributeDescription;
import myapp.util.veneer.dolphinattributeadapter.EnumAttributeAdapter;

/**
 * @author Dieter Holz
 */
public class EnumAttributeFX<T extends Enum<T>> extends AttributeFX<ObjectProperty<T>, T> {

    private final Class<T> clazz;

    public EnumAttributeFX(PresentationModel pm, AttributeDescription attributeDescription, Class<T> clazz) {
        super(pm, attributeDescription,
              createRegex(clazz),
              new EnumAttributeAdapter<>(valueAttribute(pm, attributeDescription), clazz));
        this.clazz = clazz;
    }

    @Override
    protected String format(T value) {
        return value == null ? "" : value.name();
    }

    @Override
    protected T convertToValue(String string) {
        return Enum.valueOf(clazz, string);
    }

    public void setValue(T value) {
        valueProperty().setValue(value);
    }

    public T getValue() {
        return valueProperty().getValue();
    }

    static <E extends Enum<E>> String createRegex(Class<E> enumClass) {
        StringBuffer regex         = new StringBuffer();
        E[]          enumConstants = enumClass.getEnumConstants();
        for (int i = 0; i < enumConstants.length; i++) {
            regex.append("((?i)")
                 .append(enumConstants[i].name())
                 .append("){1}");
            if (i < enumConstants.length - 1) {
                regex.append("|");
            }
        }

        return regex.toString();
//        return Arrays.stream(enumClass.getEnumConstants())
//              .map(e -> "((?i)" + e.name() + "){1}")
//              .collect(Collectors.joining("|"));
    }
}
