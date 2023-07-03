package tools;

import java.util.*;

public class ExternalCodeShortTableGetter {

    final Properties props;

    public ExternalCodeShortTableGetter(Properties properties) {
        props = properties;
    }

    private static <T extends Enum<? extends WritableShortValueHolder> & WritableShortValueHolder> T valueOf(final String name, T[] values) {
        for (T val : values) {
            if (val.name().equals(name)) {
                return val;
            }
        }
        return null;
    }

    public final static <T extends Enum<? extends WritableShortValueHolder> & WritableShortValueHolder> String getOpcodeTable(T[] enumeration) {
        StringBuilder enumVals = new StringBuilder();
        List<T> all = new ArrayList<>(); // need a mutable list plawks
        all.addAll(Arrays.asList(enumeration));
        Collections.sort(all, new Comparator<WritableShortValueHolder>() {
            @Override
            public int compare(WritableShortValueHolder o1, WritableShortValueHolder o2) {
                return Short.valueOf(o1.get()).compareTo(o2.get());
            }
        });
        for (T code : all) {
            enumVals.append(code.name());
            enumVals.append(" = ");
            enumVals.append("0x");
            enumVals.append(HexTool.toString(code.get()));
            enumVals.append(" (");
            enumVals.append(code.get());
            enumVals.append(")\n");
        }
        return enumVals.toString();
    }

    public final static <T extends Enum<? extends WritableShortValueHolder> & WritableShortValueHolder> void populateValues(Properties properties, T[] values) {
        ExternalCodeShortTableGetter exc = new ExternalCodeShortTableGetter(properties);
        for (T code : values) {
            code.set(exc.getValue(code.name(), values, (short) -2));
        }
    }

    private <T extends Enum<? extends WritableShortValueHolder> & WritableShortValueHolder> short getValue(final String name, T[] values, final short def) {
        String prop = props.getProperty(name);
        if (prop != null && prop.length() > 0) {
            String trimmed = prop.trim();
            String[] args = trimmed.split(" ");
            int base = 0;
            String offset;
            if (args.length == 2) {
                base = valueOf(args[0], values).get();
                if (base == def) {
                    base = getValue(args[0], values, def);
                }
                offset = args[1];
            } else {
                offset = args[0];
            }
            if (offset.length() > 2 && offset.substring(0, 2).equals("0x")) {
                return (short) (Short.parseShort(offset.substring(2), 16) + base);
            } else {
                return (short) (Short.parseShort(offset) + base);
            }
        }
        return def;
    }
}
