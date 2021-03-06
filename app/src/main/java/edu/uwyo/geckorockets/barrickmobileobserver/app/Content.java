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

    private static int currentRow = 2;

    public static List<Parameter> Items = new ArrayList<>();
    public static Map<String, Parameter> ItemMap = new HashMap<>();

    private static Vector<Vector<String>> data = new Vector<>();
    private static InputStream inputFile = null;

    private enum statuses {OK, ALERT, WARNING, DOWN, UNKNOWN}

    private static HashMap<statuses, Integer> lightColorMap = new HashMap<>();

    private static int parameterCount;

    private static Vector<Double> averages = new Vector<>();
    private static Vector<Double> stdDevs = new Vector<>();
    private static Vector<Integer> topAvgIndexes = new Vector<>();

    private static final double sentinel = -9999.99;

    static {
        lightColorMap.put(statuses.OK, R.color.colorOkLight);
        lightColorMap.put(statuses.ALERT, R.color.colorAlertLight);
        lightColorMap.put(statuses.WARNING, R.color.colorWarnLight);
        lightColorMap.put(statuses.DOWN, R.color.colorDownLight);
        lightColorMap.put(statuses.UNKNOWN, R.color.colorUnknownLight);

        inputFile = MyApplication.getAppContext().getResources().openRawResource(R.raw.demo_data);
        data = parseCsv(inputFile);

        parameterCount = data.elementAt(0).size();

        for (int i = 0; i < parameterCount; i++) {
            topAvgIndexes.add(currentRow);

            try {
                averages.add(Double.parseDouble(data.elementAt(currentRow).elementAt(i)));
            } catch (Exception ex) {
                averages.add(sentinel);
            }

            stdDevs.add(averages.elementAt(i)/2);
        }

        populate();
    }

    public static void nextRow() {
        currentRow++;
        Items = new ArrayList<>();
        ItemMap = new HashMap<>();
        populate();
        calculateStats();
    }

    private static void calculateStats() {
        for(int i = 0; i < topAvgIndexes.size(); i++){
            if(currentRow - topAvgIndexes.elementAt(i) > 24)
                topAvgIndexes.set(i, topAvgIndexes.elementAt(i) + 1);
        }

        for(int i = 0; i < averages.size(); i++) {
            averages.set(i, getAverage(i));
        }

        for(int i = 0; i < stdDevs.size(); i++) {
            stdDevs.set(i, stDev(getDataAverageSubArray(i)));
        }
    }

    private static Vector<Double> getDataAverageSubArray(int position) {
        Vector<Double> sub = new Vector<>();
        for(int i = currentRow; i >= topAvgIndexes.elementAt(position); i--) {
            try {
                sub.add(Double.parseDouble(data.elementAt(i).elementAt(position)));
            } catch (Exception ex) {
                sub.add(sentinel);
            }
        }
        return sub;
    }

    private static double stDev(Vector<Double> numbers) {
        double sd = 0.0;

        double sum = 0.0;
        for (int i = 0; i < numbers.size(); i++) {
            sum += numbers.elementAt(i);
        }
        double mean = sum / numbers.size();

        for (int i = 0; i < numbers.size(); i++)
        {
            sd += ((numbers.elementAt(i) - mean) * (numbers.elementAt(i) - mean)) / numbers.size();
        }
        return Math.sqrt(sd);
    }

    private static double getAverage(int position) {
        double sum = 0.0;
        for (int i = currentRow; i >= topAvgIndexes.elementAt(position); i--) {
            try {
                sum += Double.parseDouble(data.elementAt(i).elementAt(position));
            } catch (Exception ex) {
                topAvgIndexes.set(position, i);
            }
        }
        return sum / (currentRow - topAvgIndexes.elementAt(position) + 1);
    }

    private static void populate() {
        for (int i = 0; i < parameterCount; i++) {
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
                data.elementAt(1).elementAt(position),
                getHistory(position),
                getColor(position));
    }

    private static String getHistory(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Average: ");
        builder.append(averages.elementAt(position));
        builder.append("\nStandard Deviation: ");
        builder.append(stdDevs.elementAt(position));
        return builder.toString();
    }

    private static int getColor(int position) {
        String stringValue = data.elementAt(currentRow).elementAt(position);
        double value;
        try {
            value = Double.parseDouble(stringValue);
        } catch (Exception ex){
            value = sentinel;
        }

        double alertUpper = averages.elementAt(position) + (2 * stdDevs.elementAt(position));
        double alertLower = averages.elementAt(position) - (2 * stdDevs.elementAt(position));

        double warnUpper = averages.elementAt(position) + (3 * stdDevs.elementAt(position));
        double warnLower = averages.elementAt(position) - (3 * stdDevs.elementAt(position));

        if (value == sentinel)
            return lightColorMap.get(statuses.UNKNOWN);
        else if (value == 0.0)
            return lightColorMap.get(statuses.DOWN);
        else if (value > warnUpper || value < warnLower)
            return lightColorMap.get(statuses.WARNING);
        else if (value > alertUpper || value < alertLower)
            return lightColorMap.get(statuses.ALERT);
        else
            return lightColorMap.get(statuses.OK);
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
        public final int color;

        public Parameter(String id, String content, String unit, String details, int color) {
            this.id = id;
            this.content = content;
            this.unit = unit;
            this.details = details;
            this.color = color;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
