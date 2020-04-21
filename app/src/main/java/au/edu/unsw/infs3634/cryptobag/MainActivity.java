package au.edu.unsw.infs3634.cryptobag;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.edu.unsw.infs3634.cryptobag.Entities.Coin;
import au.edu.unsw.infs3634.cryptobag.Entities.CoinDatabase;
import au.edu.unsw.infs3634.cryptobag.Entities.CoinLoreResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private boolean mTwoPane;
    private CoinAdapter mAdapter;
    private CoinDatabase coinDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.detail_container) != null) {
            mTwoPane = true;
        }

        RecyclerView mRecyclerView = findViewById(R.id.rvList);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CoinAdapter(this, new ArrayList<Coin>(), mTwoPane);
        mRecyclerView.setAdapter(mAdapter);
        new InsertCoinTask().execute();
        new SetCoinTask().execute();


    }

    public class InsertCoinTask extends AsyncTask<List<Coin>, Void, Void> {
        @Override
        protected Void doInBackground(List<Coin>... lists) {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.coinlore.com")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                CoinService service = retrofit.create(CoinService.class);
                Call<CoinLoreResponse> coinsCall = service.getCoins();
                Response<CoinLoreResponse> coinsResponse = coinsCall.execute();
                List<Coin> coins = coinsResponse.body().getData();
                coinDatabase = Room.databaseBuilder(getApplicationContext(), CoinDatabase.class, "myDB").build();
                coinDatabase.coinDao().deleteCoins();
                coinDatabase.coinDao().insertCoins(coins);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

    }

    public class SetCoinTask extends AsyncTask<Void, Void, List<Coin>> {
        @Override
        protected List<Coin> doInBackground(Void... voids) {
            coinDatabase = Room.databaseBuilder(getApplicationContext(), CoinDatabase.class, "myDB").build();
            List<Coin> coins = coinDatabase.coinDao().getCoins();
            return coins;

        }
        @Override
        protected void onPostExecute(List<Coin> coins) {
            mAdapter.setCoins(coins);
        }
    }
}
