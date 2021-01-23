package com.wasge.stockalyser.ui.watchlist;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.wasge.stockalyser.MainActivity;
import com.wasge.stockalyser.R;
import com.yabu.livechart.model.DataPoint;
import com.yabu.livechart.model.Dataset;
import com.yabu.livechart.view.LiveChart;
import java.util.ArrayList;

public class WatchlistFragment extends Fragment {

    ListView listView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("test", ""+ i);

                navController.navigate(R.id.navigation_settings);
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_watchlist, container, false);
        listView = root.findViewById(R.id.listview);



        String[] name = {"Apple Inc.", "Airbus", "DB Systel"};
        String[] date = {"20-12-2020", "20-12-2020", "20-12-2020"};
        String[] value = {"16520.34", "34.54"};
        float[][] data = {{3f, 5f, 7f, 1f, 4f, 7f, 10f, 6f, 3f,5f},{7f, 3f, 4f, 3.5f,5f, 7f, 10f, 12f, 7f, 10f}};


        WatchlistAdapter adapter = new WatchlistAdapter(this.getContext(), name, date, value, data);
        listView.setAdapter(adapter);


        return root;
    }

}

class WatchlistAdapter extends ArrayAdapter<String> {

    Context context;
    String[] name;
    String[] date;
    String[] value;
    float[][] data;

    WatchlistAdapter (Context context, String[] name, String[] date, String[] value, float[][] data) {
        super(context, R.layout.fragment_listview, R.id.watch_name, name);
        this.context = context;
        this.name = name;
        this.date = date;
        this.value = value;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = layoutInflater.inflate(R.layout.fragment_listview, parent, false);

        ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();

        LiveChart liveChart = root.findViewById(R.id.pre_chart);
        TextView watch_name = root.findViewById(R.id.watch_name);
        TextView watch_date = root.findViewById(R.id.watch_date);
        TextView watch_value = root.findViewById(R.id.watch_value);

        try {
            watch_name.setText(name[position]);
            watch_date.setText(date[position]);
            watch_value.setText(value[position]);
            int i = 0;
            for (float d : data[position]) {
                dataPoints.add(new DataPoint(i,d));
                i++;
            }

            Dataset dataset = new Dataset(dataPoints);

            liveChart.setDataset(dataset)
                    .disableTouchOverlay()
                    .drawBaselineFromFirstPoint()
                    .drawDataset();
        }catch (Exception e){
            e.printStackTrace();
        }


        return root;
    }
}