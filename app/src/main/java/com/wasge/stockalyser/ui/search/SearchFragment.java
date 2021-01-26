package com.wasge.stockalyser.ui.search;

import android.content.Context;
import android.os.AsyncTask;
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

import com.wasge.stockalyser.R;
import com.wasge.stockalyser.util.ApiManager;
import com.wasge.stockalyser.util.FragmentReciever;
import com.wasge.stockalyser.util.FragmentSender;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements FragmentReciever {

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        if(getActivity() instanceof FragmentSender) {
            final FragmentSender sender = (FragmentSender) getActivity();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d("Stock", symbole.get(i));
                    navController.navigate(R.id.navigation_stock);
                    sender.sendToFragment("fragment_stock", new Object[]{symbole.get(i)});
                }
            });
        } else {
            Log.d("Stock", "Error");
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);
        listView = root.findViewById(R.id.searchlist);

        new SearchQueryTask().execute("null",0);

        Log.d("Listview", "Create Adapter");
        adapter = new SearchAdapter(this.getContext(), name, symbole, currency, exchange);
        listView.setAdapter(adapter);

        return root;
    }

    @Override
    public void recieveData(Object[] data) {
        if (data[0] instanceof String) {
            Log.d("Data", "Data received");
            new SearchQueryTask().execute(data[0],1);
        }
    }

    private void update_list(ArrayList<String[]> table){
        symbole.clear();
        name.clear();
        currency.clear();
        exchange.clear();
        for (String[] s : table) {
            symbole.add(s[0]);
            name.add(s[1]);
            currency.add(s[2]);
            exchange.add(s[3]);
            Log.d("Listview", s[0]);
        }
        Log.d("Listview", "Notify changes Adapter");
        adapter.notifyDataSetChanged();
        listView.invalidateViews();
    }


    private class SearchQueryTask extends AsyncTask<Object,Integer,Integer> {


        private ArrayList<String[]> test = new ArrayList<>();

        @Override
        protected void onPostExecute(Integer strings) {
            update_list(test);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        /**
         * @param objects Url, kind if kind == 0 -> Url will be ignored
         * **/
        @Override
        protected Integer doInBackground(Object... objects) {
            ApiManager mng = new ApiManager(getContext());
            if(objects != null && objects.length == 2 &&
                    objects[0] instanceof String &&
                    objects[1] instanceof Integer)
                if((Integer) objects[1] == 0)
                    test = mng.parseJSONData(mng.search(),(Integer)objects[1]);
                    else
                    test = mng.parseJSONData(mng.search((String) objects[0]),(Integer)objects[1]);

            return test.size();
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
        super(context, R.layout.listview_search, R.id.search_name, name);
        this.context = context;
        this.symbol = symbol;
        this.name = name;
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
        }catch (Exception e){
            e.printStackTrace();
        }

        return root;
    }
}

