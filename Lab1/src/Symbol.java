/**
 * Created by rossfreeman on 9/8/16.
 */
public class Symbol {
    public String name;
    public int value;
    public boolean isUsed;
    public boolean isMultiplyDefined;
    public int moduleValue;

    public Symbol(String name, int value, int module) {
        this.name = name;
        this.value = value;
        isUsed = false;
        isMultiplyDefined = false;
        moduleValue = module;
    }

    public String toString() {
        String multiError = "Error: This variable is multiply defined; first value used.";

        String s = name + "=" + value;

        if (isMultiplyDefined) {
            s += " " + multiError;
        }

        return s;
    }
}
