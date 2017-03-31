package com.taccardi.zak.library.db

import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.annotation.VisibleForTesting

/**
 * Database helper.
 */
class DatabaseHelper : SQLiteOpenHelper {
    constructor(context: Context, name: String, factory: SQLiteDatabase.CursorFactory, version: Int) : super(context, name, factory, version)

    constructor(context: Context, name: String, factory: SQLiteDatabase.CursorFactory, version: Int, errorHandler: DatabaseErrorHandler) : super(context, name, factory, version, errorHandler)

    override fun onCreate(db: SQLiteDatabase) {
        db.applyCreate()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.applyUpgrade(oldVersion, newVersion)
    }


    companion object {
        val SCHEMA_VERSION = 1
    }

}


@VisibleForTesting
fun SQLiteDatabase.applyCreate() {
    throw UnsupportedOperationException("not implemented")
}

@VisibleForTesting
fun SQLiteDatabase.applyUpgrade(oldVersion: Int, newVersion: Int) {
    throw UnsupportedOperationException("not implemented")
}

