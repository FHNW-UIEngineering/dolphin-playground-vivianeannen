package myapp;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import myapp.presentationmodel.canton.CantonAtt;
import myapp.presentationmodel.canton.Canton;
import myapp.presentationmodel.canton.CantonCommands;
import org.opendolphin.binding.Converter;
import org.opendolphin.binding.JFXBinder;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;

import myapp.presentationmodel.SpecialPMMixin;
import myapp.presentationmodel.presentationstate.PresentationState;
import myapp.presentationmodel.presentationstate.PresentationStateAtt;
import myapp.util.AdditionalTag;
import myapp.util.Language;
import myapp.util.ViewMixin;
import myapp.util.veneer.FX_Attribute;
import myapp.util.veneer.FX_BooleanAttribute;


class RootPane extends GridPane implements ViewMixin, SpecialPMMixin {

    private static final String DIRTY_STYLE     = "dirty";
    private static final String INVALID_STYLE   = "invalid";
    private static final String MANDATORY_STYLE = "mandatory";

    private final ClientDolphin clientDolphin;

    private Label headerLabel;

    private Label idLabel;
    private Label idField;

    private Label     nameLabel;
    private TextField nameField;

    private Label populationLabel;
    private TextField populationField;

    private Label    isAdultLabel;
    private CheckBox isAdultCheckBox;

    private Button saveButton;
    private Button resetButton;
    private Button nextButton;
    private Button germanButton;
    private Button englishButton;

    private final PresentationState ps;
    private final Canton cantonProxy;

    RootPane(ClientDolphin clientDolphin) {
        this.clientDolphin = clientDolphin;
        ps = getPresentationState();
        cantonProxy = getCantonProxy();


        init();
    }

    @Override
    public Dolphin getDolphin() {
        return clientDolphin;
    }

    @Override
    public void initializeSelf() {
        addStylesheetFiles("/fonts/fonts.css", "/myapp/myApp.css");
        getStyleClass().add("rootPane");
    }

    @Override
    public void initializeParts() {
        headerLabel = new Label();
        headerLabel.getStyleClass().add("heading");

        idLabel = new Label();
        idField = new Label();

        nameLabel = new Label();
        nameField = new TextField();

        populationLabel = new Label();
        populationField = new TextField();

        isAdultLabel    = new Label();
        isAdultCheckBox = new CheckBox();

        saveButton    = new Button("Save");
        resetButton   = new Button("Reset");
        nextButton    = new Button("Next");
        germanButton  = new Button("German");
        englishButton = new Button("English");
    }

    @Override
    public void layoutParts() {
        ColumnConstraints grow = new ColumnConstraints();
        grow.setHgrow(Priority.ALWAYS);

        getColumnConstraints().setAll(new ColumnConstraints(), grow);
        setVgrow(headerLabel, Priority.ALWAYS);

        add(headerLabel    , 0, 0, 5, 1);
        add(idLabel        , 0, 1);
        add(idField        , 1, 1, 4, 1);
        add(nameLabel      , 0, 2);
        add(nameField      , 1, 2, 4, 1);
        add(populationLabel, 0, 3);
        add(populationField, 1, 3, 4, 1);
        add(isAdultLabel   , 0, 4);
        add(isAdultCheckBox, 1, 4, 4, 1);
        add(new HBox(5, saveButton, resetButton, nextButton, germanButton, englishButton), 0, 5, 5, 1);
    }

    @Override
    public void setupEventHandlers() {
        // all events either send a command (needs to be registered in a controller on the server side)
        // or set a value on an Attribute

        PresentationState ps = getPresentationState();
        saveButton.setOnAction(   $ -> clientDolphin.send(CantonCommands.SAVE));
        resetButton.setOnAction(  $ -> clientDolphin.send(CantonCommands.RESET));
        nextButton.setOnAction(   $ -> clientDolphin.send(CantonCommands.LOAD_SOME_CANTON));

        germanButton.setOnAction( $ -> ps.language.setValue(Language.GERMAN));
        englishButton.setOnAction($ -> ps.language.setValue(Language.ENGLISH));
    }

    @Override
    public void setupValueChangedListeners() {
        cantonProxy.canton.dirtyProperty().addListener((observable, oldValue, newValue)    -> updateStyle(nameField      , DIRTY_STYLE, newValue));
        cantonProxy.age.dirtyProperty().addListener((observable, oldValue, newValue)     -> updateStyle(populationField, DIRTY_STYLE, newValue));
        cantonProxy.isAdult.dirtyProperty().addListener((observable, oldValue, newValue) -> updateStyle(isAdultCheckBox, DIRTY_STYLE, newValue));

        cantonProxy.canton.validProperty().addListener((observable, oldValue, newValue)    -> updateStyle(nameField      , INVALID_STYLE, !newValue));
        cantonProxy.age.validProperty().addListener((observable, oldValue, newValue)     -> updateStyle(populationField, INVALID_STYLE, !newValue));
        cantonProxy.isAdult.validProperty().addListener((observable, oldValue, newValue) -> updateStyle(isAdultCheckBox, INVALID_STYLE, !newValue));

        cantonProxy.canton.mandatoryProperty().addListener((observable, oldValue, newValue)    -> updateStyle(nameField      , MANDATORY_STYLE, newValue));
        cantonProxy.age.mandatoryProperty().addListener((observable, oldValue, newValue)     -> updateStyle(
                populationField, MANDATORY_STYLE, newValue));
        cantonProxy.isAdult.mandatoryProperty().addListener((observable, oldValue, newValue) -> updateStyle(isAdultCheckBox, MANDATORY_STYLE, newValue));
    }

    @Override
    public void setupBindings() {
        setupBindings_DolphinBased();
        //setupBindings_VeneerBased();
    }

    private void setupBindings_DolphinBased() {
        // you can fetch all existing PMs from the modelstore via clientDolphin
        ClientPresentationModel cantonProxyPM = clientDolphin.getAt(SpecialPMMixin.CANTON_PROXY_PM_ID);

        //JFXBinder is ui toolkit agnostic. We have to use Strings
        JFXBinder.bind(CantonAtt.CANTON.name())
                 .of(cantonProxyPM)
                 .using(value -> value + ", " + cantonProxyPM.getAt(CantonAtt.AGE.name()).getValue())
                 .to("text")
                 .of(headerLabel);

        JFXBinder.bind(CantonAtt.AGE.name())
                 .of(cantonProxyPM)
                 .using(value -> cantonProxyPM.getAt(CantonAtt.CANTON.name()).getValue() + ", " + value)
                 .to("text")
                 .of(headerLabel);

        JFXBinder.bind(CantonAtt.CANTON.name(), Tag.LABEL).of(cantonProxyPM).to("text").of(nameLabel);
        JFXBinder.bind(CantonAtt.CANTON.name()).of(cantonProxyPM).to("text").of(nameField);
        JFXBinder.bind("text").of(nameField).to(CantonAtt.CANTON.name()).of(cantonProxyPM);

        JFXBinder.bind(CantonAtt.AGE.name(), Tag.LABEL).of(cantonProxyPM).to("text").of(populationLabel);
        JFXBinder.bind(CantonAtt.AGE.name()).of(cantonProxyPM).to("text").of(populationField);
        Converter toIntConverter = value -> {
            try {
                int newValue = Integer.parseInt(value.toString());
                cantonProxyPM.getAt(CantonAtt.AGE.name(), AdditionalTag.VALID).setValue(true);
                cantonProxyPM.getAt(CantonAtt.AGE.name(), AdditionalTag.VALIDATION_MESSAGE).setValue("OK");

                return newValue;
            } catch (NumberFormatException e) {
                cantonProxyPM.getAt(CantonAtt.AGE.name(), AdditionalTag.VALID).setValue(false);
                cantonProxyPM.getAt(CantonAtt.AGE.name(), AdditionalTag.VALIDATION_MESSAGE).setValue("Not a number");
                return cantonProxyPM.getAt(CantonAtt.AGE.name()).getValue();
            }
        };
        JFXBinder.bind("text").of(populationField).using(toIntConverter).to(CantonAtt.AGE.name()).of(cantonProxyPM);

        JFXBinder.bind(CantonAtt.IS_ADULT.name(), Tag.LABEL).of(cantonProxyPM).to("text").of(isAdultLabel);
        JFXBinder.bind(CantonAtt.IS_ADULT.name()).of(cantonProxyPM).to("selected").of(isAdultCheckBox);
        JFXBinder.bind("selected").of(isAdultCheckBox).to(CantonAtt.IS_ADULT.name()).of(cantonProxyPM);

        Converter not = value -> !(boolean) value;
        JFXBinder.bindInfo(Attribute.DIRTY_PROPERTY).of(cantonProxyPM).using(not).to("disable").of(saveButton);
        JFXBinder.bindInfo(Attribute.DIRTY_PROPERTY).of(cantonProxyPM).using(not).to("disable").of(resetButton);

        PresentationModel presentationStatePM = clientDolphin.getAt(SpecialPMMixin.PRESENTATION_STATE_PM_ID);

        JFXBinder.bind(PresentationStateAtt.LANGUAGE.name()).of(presentationStatePM).using(value -> value.equals(Language.GERMAN.name())).to("disable").of(germanButton);
        JFXBinder.bind(PresentationStateAtt.LANGUAGE.name()).of(presentationStatePM).using(value -> value.equals(Language.ENGLISH.name())).to("disable").of(englishButton);
    }

    private void setupBindings_VeneerBased(){
        headerLabel.textProperty().bind(cantonProxy.canton.valueProperty().concat(", ").concat(cantonProxy.age.valueProperty()));

        idLabel.textProperty().bind(cantonProxy.id.labelProperty());
        idField.textProperty().bind(cantonProxy.id.valueProperty().asString());

        setupBinding(nameLabel   , nameField      , cantonProxy.canton);
        setupBinding(populationLabel, populationField, cantonProxy.age);
        setupBinding(isAdultLabel, isAdultCheckBox, cantonProxy.isAdult);

        germanButton.disableProperty().bind(Bindings.createBooleanBinding(() -> Language.GERMAN.equals(ps.language.getValue()), ps.language.valueProperty()));
        englishButton.disableProperty().bind(Bindings.createBooleanBinding(() -> Language.ENGLISH.equals(ps.language.getValue()), ps.language.valueProperty()));

        saveButton.disableProperty().bind(cantonProxy.dirtyProperty().not());
        resetButton.disableProperty().bind(cantonProxy.dirtyProperty().not());
    }

    private void setupBinding(Label label, TextField field, FX_Attribute attribute) {
        setupBinding(label, attribute);

        field.textProperty().bindBidirectional(attribute.userFacingStringProperty());
        field.tooltipProperty().bind(Bindings.createObjectBinding(() -> new Tooltip(attribute.getValidationMessage()),
                                                                  attribute.validationMessageProperty()
                                                                 ));
    }

    private void setupBinding(Label label, CheckBox checkBox, FX_BooleanAttribute attribute) {
        setupBinding(label, attribute);
        checkBox.selectedProperty().bindBidirectional(attribute.valueProperty());
    }

    private void setupBinding(Label label, FX_Attribute attribute){
        label.textProperty().bind(Bindings.createStringBinding(() -> attribute.getLabel() + (attribute.isMandatory() ? " *" : "  "),
                                                               attribute.labelProperty(),
                                                               attribute.mandatoryProperty()));
    }

    private void updateStyle(Node node, String style, boolean value){
        if(value){
            node.getStyleClass().add(style);
        }
        else {
            node.getStyleClass().remove(style);
        }
    }
}
