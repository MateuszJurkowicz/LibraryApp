package com.example.libraryapp;

import android.content.Context;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteCompat;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Book.class}, version = 1, exportSchema = false)
public abstract class BookDatabase extends RoomDatabase {
    private static BookDatabase databaseInstance;
    static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public abstract BookDao bookDao();
    private static final RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                BookDao dao = databaseInstance.bookDao();
                dao.insert(new Book("Clean code", "Robert C. Martin"));
                dao.insert(new Book("Władca pierścieni", "J.R.R. Tolkien"));
                dao.insert(new Book("Pan Tadeusz", "Adam Mickiewicz"));
                dao.insert(new Book("Ferdydurke", "Witold Gombrowicz"));
            });

        }
    };
    static BookDatabase getDatabase(final Context context) {
        if(databaseInstance == null) {
            databaseInstance = Room.databaseBuilder(context.getApplicationContext(), BookDatabase.class, "book_database")
                    .addCallback(roomDatabaseCallback)
                    .build();
        }
        return databaseInstance;
    }
}
