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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private MainActivity mainActivity;
    private SearchAdapter adapter;
    private ListView listView;
    private View root;
    private int more = 20;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mainActivity = null;
        if (getActivity() instanceof MainActivity)
            mainActivity = (MainActivity) getActivity();

    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.setSearchActive(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mainActivity.setSearchActive(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);
        if (mainActivity != null) {
            mainActivity.subscribeToMain(R.id.navigation_search, this);
        }

        if(mainActivity != null) {
            ImageButton imageButton = root.findViewById(R.id.button_more);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String query = mainActivity.getSearchView().getQuery().toString();
                    int kind = 1;
                    if(query.length() < 1) kind = 0;
                    more = more + 30;
                    new SearchQueryTask().execute(query,kind, more);
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d(TAG, symbole.get(i));
                    mainActivity.setSymbol_for_stock_fragment(symbole.get(i));
                    navController.navigate(R.id.navigation_stock);
                }
            });
        } else {
            Toast.makeText(getContext(),"Loading data error", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Error");
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);
        listView = root.findViewById(R.id.searchlist);
        String query = mainActivity.getSearchView().getQuery().toString();
        int kind = 1;
        if(query.length() < 1) kind = 0;
        new SearchQueryTask().execute(query,kind, more);

        Log.d(TAG, "Create Adapter");
        adapter = new SearchAdapter(this.getContext(), name, symbole, currency, exchange);
        listView.setAdapter(adapter);

        return root;
    }



    public void searchFor(String query){
        more = 20;
        Log.d(TAG, "searching for: " + query );
        new SearchQueryTask().execute(query, 1, more);
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
                if(objects != null && objects.length == 3 &&
                        objects[0] instanceof String &&
                        objects[1] instanceof Integer &&
                        objects[2] instanceof Integer)
                    if((Integer) objects[1] == 0)
                        output = mng.parseJSONData(mng.search(), (Integer) objects[1], (Integer) objects[2]);
                    else
                        output = mng.parseJSONData(mng.search((String) objects[0]),(Integer)objects[1], 40);
            } catch (Exception e){
                Toast.makeText(root.getContext(),"Loading data error", Toast.LENGTH_SHORT).show();
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

