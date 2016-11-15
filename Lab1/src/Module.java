import java.util.ArrayList;

/**
 * Created by rossfreeman on 9/8/16.
 */
public class Module {
    public ArrayList<Symbol> useList;
    public int[] memList;
    public static ArrayList<Symbol> defList;
    public static int totalSize;
    public int cumSize;
    public int memorySize;

    public Module() {
        useList = new ArrayList<Symbol>();

        if (Module.defList == null) {
            defList = new ArrayList<Symbol>();
        }
    }

    public void setMemorySize(int size) {
        memorySize = size;
        memList = new int[size];
    }

    public static int getVarLoc(Symbol s) {
        for (Symbol symbol : defList) {
            if (symbol.name.equals(s.name)) {
                symbol.isUsed = true;
                return symbol.value;
            }
        }

        return -1;
    }

    public void printUseErrors() {
        for (Symbol symbol : useList) {
            if (!symbol.isUsed) {
                System.out.println("Warning: In module " + symbol.moduleValue + " " + symbol.name + " is on use list but isn't used.");
            }
        }
    }

    public static void printErrors() {
        for (Symbol symbol : defList) {
            if (!symbol.isUsed) {
                System.out.println("Warning: " + symbol.name + " was defined in module " + symbol.moduleValue + " but was never used.");
            }
        }
    }

    public static void addDefSymbol(Symbol s) {
        for (Symbol symbol : defList) {
            if (symbol.name.equals(s.name)) {
                symbol.isMultiplyDefined = true;
                return;
            }
        }

        defList.add(s);
    }
}
