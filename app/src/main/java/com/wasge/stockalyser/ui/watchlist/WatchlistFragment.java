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
import com.wasge.stockalyser.util.DatabaseManager;
import com.yabu.livechart.model.DataPoint;
import com.yabu.livechart.model.Dataset;
import com.yabu.livechart.view.LiveChart;
import java.util.ArrayList;

public class WatchlistFragment extends Fragment {

    ListView listView;
    ArrayList<String> symbole = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> date = new ArrayList<>();
    ArrayList<String> value = new ArrayList<>();
    ArrayList<float[]> data = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        if(getActivity() instanceof MainActivity) {
            final MainActivity sender = (MainActivity) getActivity();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d("Stock", symbole.get(i));
                    navController.navigate(R.id.navigation_stock);
                    sender.sendToStockFragment(new Object[]{symbole.get(i)});
                }
            });
        } else {
            Log.d("Stock", "Error");
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_watchlist, container, false);
        listView = root.findViewById(R.id.listview);
        getData();
        Log.d("Listview", "Create Adapter");
        WatchlistAdapter adapter = new WatchlistAdapter(this.getContext(), name, date, value, data);
        listView.setAdapter(adapter);

        return root;
    }

    private void getData(){
        ArrayList<String[]> watchData = DatabaseManager.getWatchlistStockIDs();
        for (String[] d : watchData) {
            symbole.add(d[0]);
            name.add(d[1]);
            date.add(d[5]);
            value.add(d[4]);
        }
        for (String s : symbole) {
            data.add(DatabaseManager.getTenDayData(s));
        }
    }
}


// Adapterclass for Listview
class WatchlistAdapter extends ArrayAdapter<String> {

    Context context;
    ArrayList<String> name;
    ArrayList<String> date;
    ArrayList<String> value;
    ArrayList<float[]> data;

    WatchlistAdapter (Context context, ArrayList<String> name, ArrayList<String> date, ArrayList<String> value, ArrayList<float[]> data) {
        super(context, R.layout.listview_watchlist, R.id.watch_name, name);
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
        View root = layoutInflater.inflate(R.layout.listview_watchlist, parent, false);

        ArrayList<DataPoint> dataPoints = new ArrayList<>();

        LiveChart liveChart = root.findViewById(R.id.pre_chart);
        TextView watch_name = root.findViewById(R.id.watch_name);
        TextView watch_date = root.findViewById(R.id.watch_date);
        TextView watch_value = root.findViewById(R.id.watch_value);

        try {
            watch_name.setText(name.get(position));
            watch_date.setText(date.get(position));
            watch_value.setText(value.get(position));
            int i = 0;
            for (float d : data.get(position)) {
                dataPoints.add(new DataPoint(i,d));
                i++;
            }

            Dataset dataset = new Dataset(dataPoints);

            liveChart.setDataset(dataset)
                    .disableTouchOverlay()
                    .drawBaselineFromFirstPoint()
                    .drawDataset();
        }catch (Exception ignored){}

        return root;
    }
}