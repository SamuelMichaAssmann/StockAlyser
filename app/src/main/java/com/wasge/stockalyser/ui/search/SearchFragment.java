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

import com.wasge.stockalyser.MainActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.wasge.stockalyser.R;
import com.wasge.stockalyser.util.ApiManager;

import java.util.ArrayList;

public class SearchFragment extends Fragment  {

    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<String> symbole = new ArrayList<>();
    private ArrayList<String> currency = new ArrayList<>();
    private ArrayList<String> exchange = new ArrayList<>();

    private String TAG = "SearchFragment";
    private SearchAdapter adapter;
    private ListView listView;
    private View root;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.subscribeToMain(R.id.navigation_search,this);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        if(getActivity() instanceof MainActivity) {
            final MainActivity sender = (MainActivity) getActivity();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d(TAG, symbole.get(i));
                    navController.navigate(R.id.navigation_stock);
                    sender.sendToStockFragment(new Object[]{symbole.get(i)});
                }
            });
        } else {
            Log.d(TAG, "Error");
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);
        listView = root.findViewById(R.id.searchlist);

        new SearchQueryTask().execute("null",0);

        Log.d(TAG, "Create Adapter");
        adapter = new SearchAdapter(this.getContext(), name, symbole, currency, exchange);
        listView.setAdapter(adapter);

        return root;
    }



    public void searchFor(String query){
        Log.d(TAG, "searching for: " + query );
        new SearchQueryTask().execute(query,1);
    }

    private void update_list(ArrayList<String[]> table){
        symbole.clear();
        name.clear();
        currency.clear();
        exchange.clear();
        //String debug = "\ngot:\n";
        for (String[] s : table) {
            symbole.add(s[0]);
            //debug+="   " + s[0] + "\n";
            name.add(s[1]);
            currency.add(s[2]);
            exchange.add(s[3]);
        }
        //Log.d(TAG, debug);
        adapter.notifyDataSetChanged();
        Log.d(TAG, "Notify changes Adapter");

    }


    private class SearchQueryTask extends AsyncTask<Object,Integer,Integer> {


        private ArrayList<String[]> output = new ArrayList<>();

        @Override
        protected void onPostExecute(Integer strings) {
            update_list(output);
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
            try {
                ApiManager mng = new ApiManager(getContext());
                if(objects != null && objects.length == 2 &&
                        objects[0] instanceof String &&
                        objects[1] instanceof Integer)
                    if((Integer) objects[1] == 0)
                        output = mng.parseJSONData(mng.search(),(Integer)objects[1]);
                    else
                        output = mng.parseJSONData(mng.search((String) objects[0]),(Integer)objects[1]);
            } catch (Exception e){
                Log.e("searchFragment","BackgroundTask failed: " + e.getMessage());
            }
            return output.size();
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
        this.name = name;
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

