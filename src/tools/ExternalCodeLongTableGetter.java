package tools;

import java.util.*;


public class ExternalCodeLongTableGetter {

    final Properties props;

    public ExternalCodeLongTableGetter(Properties properties) {
        props = properties;
    }

    private static <T extends Enum<? extends WritableLongValueHolder> & WritableLongValueHolder> T valueOf(final String name, T[] values) {
        for (T val : values) {
            if (val.name().equals(name)) {
                return val;
            }
        }
        return null;
    }

    public final static <T extends Enum<? extends WritableLongValueHolder> & WritableLongValueHolder> String getOpcodeTable(T[] enumeration) {
        StringBuilder enumVals = new StringBuilder();
        List<T> all = new ArrayList<>(); // need a mutable list plawks
        all.addAll(Arrays.asList(enumeration));
        Collections.sort(all, new Comparator<WritableLongValueHolder>() {
            @Override
            public int compare(WritableLongValueHolder o1, WritableLongValueHolder o2) {
                return Long.valueOf(o1.get()).compareTo(o2.get());
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

    public final static <T extends Enum<? extends WritableLongValueHolder> & WritableLongValueHolder> void populateValues(Properties properties, T[] values) {
        ExternalCodeLongTableGetter exc = new ExternalCodeLongTableGetter(properties);
        for (T code : values) {
            code.set(exc.getValue(code.name(), values, (long) -2));
        }
    }

    private <T extends Enum<? extends WritableLongValueHolder> & WritableLongValueHolder> long getValue(final String name, T[] values, final long def) {
        String prop = props.getProperty(name);
        if (prop != null && prop.length() > 0) {
            String trimmed = prop.trim();
            String[] args = trimmed.split(" ");
            long base = 0;
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
                return (long) (Long.parseLong(offset.substring(2), 16) + base);
            } else {
                return (long) (Long.parseLong(offset) + base);
            }
        }
        return def;
    }
}
