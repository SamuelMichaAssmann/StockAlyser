package com.wasge.stockalyser.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.wasge.stockalyser.R;
import com.wasge.stockalyser.util.ApiManager;
import com.wasge.stockalyser.util.FragmentReciever;
import com.yabu.livechart.model.DataPoint;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements FragmentReciever {

    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> symbole = new ArrayList<>();
    ArrayList<String> currency = new ArrayList<>();
    ArrayList<String> exchange = new ArrayList<>();

    SearchAdapter adapter;
    ListView listView;
    View root;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);
        listView = root.findViewById(R.id.searchlist);

        ApiManager apiManager = new ApiManager(root.getContext());
        ArrayList<String[]> stocklist = apiManager.parseJSONData(apiManager.search(), 0);

        for (String[] s : stocklist) {
            symbole.add(s[0]);
            name.add(s[1]);
            currency.add(s[2]);
            exchange.add(s[3]);
        }
        Log.d("Listview", "Create Adapter");
        adapter = new SearchAdapter(this.getContext(), name, symbole, currency, exchange);
        listView.setAdapter(adapter);

        return root;
    }

    @Override
    public void recieveData(Object[] data) {
        if (data[0] instanceof String) {
            Log.d("Data", "Data received");
            symbole.clear();
            name.clear();
            currency.clear();
            exchange.clear();

            ApiManager apiManager = new ApiManager(root.getContext());
            ArrayList<String[]> stocklist = apiManager.parseJSONData(apiManager.search((String) data[0]), 1);

            for (String[] s : stocklist) {
                symbole.add(s[0]);
                name.add(s[1]);
                currency.add(s[2]);
                exchange.add(s[3]);
            }
            Log.d("Listview", "Notify changes Adapter");
            adapter.notifyDataSetChanged();
            listView.invalidateViews();
        }
    }
}

// Adapterclass for Listview
class SearchAdapter extends ArrayAdapter<String> {

    Context context;
    ArrayList<String> name;
    ArrayList<String> symbol;
    ArrayList<String> currency;
    ArrayList<String> exchange;

    SearchAdapter (Context context, ArrayList<String> name, ArrayList<String> symbol, ArrayList<String> currency, ArrayList<String> exchange) {
        super(context, R.layout.listview_watchlist, R.id.search_name, name);
        this.context = context;
        this.symbol = symbol;
        this.currency = currency;
        this.exchange = exchange;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = layoutInflater.inflate(R.layout.listview_search, parent, false);

        TextView search_name = root.findViewById(R.id.search_name);
        TextView search_symbol = root.findViewById(R.id.search_symbol);
        TextView search_currency = root.findViewById(R.id.search_currency);
        TextView search_exchange = root.findViewById(R.id.search_exchange);

        try {
            search_name.setText(name.get(position));
            search_symbol.setText(symbol.get(position));
            search_currency.setText(currency.get(position));
            search_exchange.setText(exchange.get(position));
        }catch (Exception ignored){}

        return root;
    }
}