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

    private String TAG = "WatchistFragment";
    private MainActivity mainActivity;
    private DatabaseManager dbManager;
    private ListView listView;
    private ArrayList<String> symbole = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<String> date = new ArrayList<>();
    private ArrayList<String> value = new ArrayList<>();
    private ArrayList<float[]> data = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = null;
        if (getActivity() instanceof MainActivity)
            mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            dbManager = mainActivity.getDatabaseManager();
            mainActivity.subscribeToMain(R.id.navigation_stock, this);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_watchlist, container, false);
        listView = root.findViewById(R.id.listview);
        getData();
        Log.d(TAG, "Create Adapter");
        WatchlistAdapter adapter = new WatchlistAdapter(this.getContext(), name, date, value, data);
        listView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        if(mainActivity != null) {

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d(TAG, symbole.get(i));
                    Log.d("Stock", symbole.get(i));
                    mainActivity.setSymbol_for_stock_fragment(symbole.get(i));
                    navController.navigate(R.id.navigation_stock);
                }
            });
        } else {
            Log.d(TAG, "Error");
        }

    }


    private void getData(){
        ArrayList<String[]> watchData = dbManager.getWatchlistStock();

        for (String[] d : watchData) {
            symbole.add(d[0]);
            name.add(d[1]);
            date.add(d[5]);
            value.add(d[4]);
        }
        data.clear();
        for (String s : symbole) {
            data.add(dbManager.getTenDayData(s));
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