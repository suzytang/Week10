package au.edu.unsw.infs3634.cryptobag.Entities;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CoinDao {

    @Query("SELECT * FROM Coin")
    List<Coin> getCoins();

    @Insert
    void insertCoins(List<Coin> coins);

    @Query("DELETE FROM Coin")
    void deleteCoins();

    @Query("SELECT * FROM Coin WHERE id LIKE :search")
    List<Coin> searchCoins(String search);
}
