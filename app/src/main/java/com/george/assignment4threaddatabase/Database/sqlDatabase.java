


package com.george.assignment4threaddatabase.Database;

        import android.content.ContentValues;
        import android.content.Context;

        import android.database.Cursor;
        import android.database.MatrixCursor;
        import android.database.SQLException;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

        import android.util.Log;


        import java.util.ArrayList;
        import java.util.LinkedList;
        import java.util.List;
        import java.util.Objects;

/**
 * Created by George on 2016-02-10.
 */
public class sqlDatabase extends SQLiteOpenHelper {


    private static final int database_VERSION = 1;
    private static final String database_NAME = "PersonDB";
    private static final String table_Persons = "Person";
    private static final String Person_ID = "id";
    private static final String Person_Name = "Name";
    private static final String Person_PIC = "Picture";
    private static final String Person_BIO = "Description";
    private List<Person> local_person_list = new LinkedList<Person>();



    private static final String[] COLUMNS = { Person_ID, Person_Name, Person_PIC , Person_BIO };

    public sqlDatabase(Context context) {
        super(context, database_NAME, null, database_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_PERSON_TABLE = "CREATE TABLE Person ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "Name TEXT, "  +"Picture TEXT, "  + "Description TEXT )";
        db.execSQL(CREATE_PERSON_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Person");
        this.onCreate(db);
    }

    public void createPerson(Person person) {

        SQLiteDatabase db = this.getWritableDatabase();

        local_person_list = getAllPerson();

        for(int i=0; i< local_person_list.size(); i++){

            if(Objects.equals(person.getName(), local_person_list.get(i).getName())){

                return;
        }

        }


        Log.d("dossier", "adding person");

        ContentValues values = new ContentValues();
        values.put(Person_Name, person.getName());
        values.put(Person_PIC, person.getPicture());
        values.put(Person_BIO, person.getDescription());



        db.insert(table_Persons, null, values);


        db.close();
    }



    public Person readPerson(int index) {

        SQLiteDatabase db = this.getReadableDatabase();


        local_person_list = getAllPerson();

        Person person = new Person();

        person = local_person_list.get(index);




        return person;
    }

    public List<Person> getAllPerson() {
        List<Person> person_list = new LinkedList<Person>();


        String query = "SELECT  * FROM " + table_Persons;


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Person person = null;
        if (cursor.moveToFirst()) {
            do {
                person = new Person();
                person.setId(Integer.parseInt(cursor.getString(0)));
                person.setName(cursor.getString(1));
                person.setPicture(cursor.getString(2));
                person.setDescription(cursor.getString(3));

                person_list.add(person);
            } while (cursor.moveToNext());
        }
        return person_list;
    }

    public int updatePerson(Person person) {


        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Name", person.getName());
        values.put("Picture", person.getPicture());
        values.put("Description", person.getDescription());



        int i = db.update(table_Persons, values, Person_ID + " = ?", new String[] { String.valueOf(person.getId()) });

        db.close();
        return i;
    }


    public void deletePerson(Person deletedPerson) {


        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table_Persons, Person_ID + " = ?", new String[] { String.valueOf(deletedPerson.getId()) });
        db.close();


    }




}
