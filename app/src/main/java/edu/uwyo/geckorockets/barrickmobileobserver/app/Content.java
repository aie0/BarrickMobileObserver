package edu.uwyo.geckorockets.barrickmobileobserver.app;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.uwyo.geckorockets.barrickmobileobserver.MyApplication;
import edu.uwyo.geckorockets.barrickmobileobserver.R;

public class Content {

    public static int currentRow = 1;

    public static final List<Parameter> Items = new ArrayList<>();
    public static final Map<String, Parameter> ItemMap = new HashMap<>();

    private static Vector<Vector<String>> data = new Vector<>();
    private static InputStream inputFile = null;

    private static int parameterCount;

    static {
        inputFile = MyApplication.getAppContext().getResources().openRawResource(R.raw.demo_data);
        data = parseCsv(inputFile);

        parameterCount = data.elementAt(0).size();

        for (int i = 1; i < parameterCount; i++) {
            addItem(createItem(i));
        }
    }

    private static void addItem(Parameter item) {
        Items.add(item);
        ItemMap.put(item.id, item);
    }

    private static Parameter createItem(int position) {
        return new Parameter(data.elementAt(0).elementAt(position),
                data.elementAt(currentRow).elementAt(position),
                "Unit",
                getHistory(position));
    }

    private static String getHistory(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Last 100 Values: ");
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    private static Vector<Vector<String>> parseCsv(InputStream file) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file));
        try {
            Vector<Vector<String>> result = new Vector<>();
            String line;

            while ((line = reader.readLine()) != null) {
                result.add(new Vector<>(Arrays.asList(line.split(","))));
            }

            file.close();
            return result;
        } catch (IOException ex) {
            Log.e("parseCsv", ex.toString());
            return null;
        } finally {
            try {
                file.close();
            } catch (IOException e) {
                Log.e("parseCsv", e.toString());
            }
        }
    }


    public static class Parameter {
        public final String id;
        public final String content;
        public final String unit;
        public final String details;

        public Parameter(String id, String content, String unit, String details) {
            this.id = id;
            this.content = content;
            this.unit = unit;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
