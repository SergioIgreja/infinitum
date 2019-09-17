package fyi.repository

import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import fyi.utils.ApplicationContext

/**
 * Uses the [applicationContext] to get the Android application Context. This Context will then be used
 * to create an AndroidSqlDriver with the [schema] and [dbName].
 */
actual fun getSqlDriver(
    applicationContext: ApplicationContext,
    schema: SqlDriver.Schema,
    dbName: String
): SqlDriver {
    return AndroidSqliteDriver(schema, applicationContext.context, dbName)
}