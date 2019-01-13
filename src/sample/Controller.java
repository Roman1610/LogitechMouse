package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.python.core.PyFunction;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.core.PyTuple;
import org.python.util.PythonInterpreter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller {

    private final String solidMode = "solid";
    private final String cycleMode = "cycle";
    private final String breatherMode = "breathe";

    String pythonScriptPath = "src/python/g203-led.py";
    String pythonTestScriptPath = "src/python/utils.py";

    private String solidToolTipText = "Solid color mode";
    private String cycleToolTipText = "Cycle through all colors";
    private String breatherToolTipText = "Single color breathing";

    private String introToolTipText = "Enable/disable startup effect";

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Slider rate_slider;

    @FXML
    private TextField rate_text;

    @FXML
    private ColorPicker color_picker;

    @FXML
    private RadioButton cycle_radio_button;

    @FXML
    private RadioButton solid_radio_button;

    @FXML
    private TextField brightness_text;

    @FXML
    private Slider brightness_slider;

    @FXML
    private Button reset_button;

    @FXML
    private CheckBox intro_checkbox;

    @FXML
    private Button apply_button;

    @FXML
    private RadioButton breathe_radio_button;

    ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    void initialize() {
        solid_radio_button.setTooltip(new Tooltip(solidToolTipText));
        cycle_radio_button.setTooltip(new Tooltip(cycleToolTipText));
        breathe_radio_button.setTooltip(new Tooltip(breatherToolTipText));

        solid_radio_button.setToggleGroup(toggleGroup);
        cycle_radio_button.setToggleGroup(toggleGroup);
        breathe_radio_button.setToggleGroup(toggleGroup);

        intro_checkbox.setTooltip(new Tooltip(introToolTipText));

        rate_slider.setMin(100);
        rate_slider.setMax(60000);

        brightness_slider.setMin(0);
        brightness_slider.setMax(100);

        brightness_slider.valueProperty().addListener(onBrightnessChangeValueScroll);
        rate_slider.valueProperty().addListener(onRateChangeValueScroll);

        brightness_slider.setSnapToTicks(true);
        rate_slider.setSnapToTicks(true);

        apply_button.setOnAction(onApplayButtonClick);
    }

    private EventHandler<MouseEvent> onResetButtonClick = mouseEvent -> {

    };

    private ChangeListener onRateChangeValueScroll = new ChangeListener() {
        @Override
        public void changed(ObservableValue observableValue, Object o, Object t1) {
            rate_text.setText(String.valueOf(observableValue.getValue()));
        }
    };

    private ChangeListener onBrightnessChangeValueScroll = new ChangeListener() {
        @Override
        public void changed(ObservableValue observableValue, Object o, Object t1) {
            brightness_text.setText(String.valueOf(observableValue.getValue()));
        }
    };

    private EventHandler<ActionEvent> onApplayButtonClick = actionEvent -> {
        PythonInterpreter pythonInterpreter = new PythonInterpreter();
        pythonInterpreter.execfile(pythonScriptPath);

        String colorHex = "";
        List<String> argv = new ArrayList<>();
        argv.add(pythonScriptPath);

        RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
        switch (selectedRadioButton.getText()) {
            case "Solid":
                argv.add("solid");
                colorHex = Integer.toHexString(color_picker.getValue().hashCode())
                        .substring(0, 6).toUpperCase();
                argv.add(colorHex);
                break;
            case "Cycle":
                argv.add("cycle");
                argv.add(rate_text.getText());
                argv.add(brightness_text.getText());
                break;
            case "Breathe":
                argv.add("breathe");
                colorHex = Integer.toHexString(color_picker.getValue().hashCode())
                        .substring(0, 6).toUpperCase();
                argv.add(colorHex);
                argv.add(rate_text.getText());
                argv.add(brightness_text.getText());
                break;
        }

        System.out.println(argv);

        PyFunction loadFileFunc = (PyFunction) pythonInterpreter.get("main", PyFunction.class);
        loadFileFunc.__call__(new PyList(argv));
    };
}
