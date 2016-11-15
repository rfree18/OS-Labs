import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by rossfreeman on 9/7/16.
 */
public class Lab1 {
    public static void main(String [] args) {
        try (Scanner in = new Scanner(new File(args[0])); ) {
            ArrayList<Module> moduleList = new ArrayList<Module>();

            int numModule = in.nextInt();
            int memSum = 0;
            int currentModule = 0;

            while (currentModule < numModule) {
                Module module = new Module();

                int defCount = 0;
                int numDef = in.nextInt();
                while (defCount < numDef) {
                    String key = in.next();
                    int value = in.nextInt();
                    value += Module.totalSize; // Relative -> absolute

                    Symbol symbol = new Symbol(key, value, currentModule);
                    Module.addDefSymbol(symbol);

                    defCount++;
                }

                int useCount = 0;
                int numUse = in.nextInt();

                while (useCount < numUse) {
                    String key = in.next();
                    Symbol symbol = new Symbol(key, -1, currentModule);

                    module.useList.add(symbol);

                    useCount++;
                }

                int memCount = 0;
                int numMem = in.nextInt();
                module.setMemorySize(numMem);

                while (memCount < numMem) {
                    module.memList[memCount] = in.nextInt();
                    memCount++;
                }
                module.cumSize = Module.totalSize;
                Module.totalSize += numMem;

                moduleList.add(module);


                currentModule++;
            }

            System.out.println("Symbol Table");
            for (Symbol symbol : Module.defList) {
                System.out.println(symbol);
            }
            int counter = 0;

            System.out.println("\nMemory Map");
            for (Module module : moduleList) {
                for (int i = 0; i < module.memList.length; i++) {
                    int adr = module.memList[i];

                    printAddress(counter, adr, module);
                    counter++;
                }
            }
            System.out.println();
            Module.printErrors();
            for (Module module : moduleList) {
                module.printUseErrors();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isInvalidAdr(int adr) {
        // Remove thounsand place to get memory address
        int memLoc = Integer.parseInt(Integer.toString(adr).substring(1));

        if (memLoc > 600) {
            return true;
        }

        return false;
    }

    public static boolean isInvalidRelAdr(int adr, Module module) {
        int memLoc = Integer.parseInt(Integer.toString(adr).substring(1));

        if (memLoc > module.memorySize) {
            return true;
        }

        return false;
    }

    public static void printAddress(int counter, int adr, Module module) {
        int type = adr % 10;
        adr = adr / 10;
        String missingSymError = "";

        if (type == 3) {
            if (isInvalidRelAdr(adr, module)) {
                String relError = " Error: Relative address exceeds module size; zero used.";
                adr = adr / 1000 * 1000;
                System.out.println(counter + ":" + "\t" + adr + relError);
                return;
            }
            else {
                adr += module.cumSize;
                System.out.println(counter + ":" + "\t" + adr);
                return;
            }

        } else if (type == 4) {
            int varNum = adr % 10;

            if (varNum > module.useList.size()) {
                String extError = " Error: External address exceeds length of use list; treated as immediate.";
                System.out.println(counter + ":" + "\t" + adr + extError);
                return;
            }

            Symbol sym = module.useList.get(varNum);
            sym.isUsed = true;

            adr = (adr / 1000) % 10 * 1000;

            int varLoc = Module.getVarLoc(sym);

            if (varLoc > 0) {
                adr += varLoc;
            } else {
                missingSymError = " Error: " + sym.name + " is not defined; zero used.";
            }
        }


        if (isInvalidAdr(adr) && type == 2) {
            adr = adr / 1000 * 1000; // Normalize to 0
            System.out.println(counter + ":" + "\t" + adr + " Error: Absolute address exceeds machine size; zero used.");
        } else {
            System.out.println(counter + ":" + "\t" + adr + missingSymError);
        }
    }
}