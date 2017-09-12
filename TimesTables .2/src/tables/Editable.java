package tables;

import java.util.Map;

import javafx.beans.value.WritableValue;

public interface Editable {

	Map<String, WritableValue<Number>> getPropertyMap();
}
