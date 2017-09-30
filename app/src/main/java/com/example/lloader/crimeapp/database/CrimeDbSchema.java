package com.example.lloader.crimeapp.database;

/**
 * Created by Alexander Garkavenko
 */

public class CrimeDbSchema {
    public static final class CrimeTable {
        public static final String NAME = "CrimeTable";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "isSolved";
            public static final String SUSPECT = "suspect";
            public static final String PHONE = "phone";
        }
    }
}
