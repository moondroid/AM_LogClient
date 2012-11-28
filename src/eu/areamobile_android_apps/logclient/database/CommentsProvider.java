package eu.areamobile_android_apps.logclient.database;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class CommentsProvider extends ContentProvider {

	// database
	private MySQLiteHelper database;

	// Used for the UriMacher
	private static final int TOKEN_COMMENTS = 10;
	private static final int TOKEN_COMMENT_ID = 20;

	private static final String AUTHORITY = "eu.areamobile_android_apps.logclient.contentprovider";
	private static final String BASE_PATH = "comments";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/comments";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/comment";

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, TOKEN_COMMENTS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TOKEN_COMMENT_ID);
	}

	@Override
	public boolean onCreate() {
		database = new MySQLiteHelper(getContext());
		return true;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsDeleted = 0;
	    switch (uriType) {
	    case TOKEN_COMMENTS:
	      rowsDeleted = sqlDB.delete(MySQLiteHelper.TABLE_COMMENTS, selection,
	          selectionArgs);
	      break;
	    case TOKEN_COMMENT_ID:
	      String id = uri.getLastPathSegment();
	      if (TextUtils.isEmpty(selection)) {
	        rowsDeleted = sqlDB.delete(MySQLiteHelper.TABLE_COMMENTS,
	        		MySQLiteHelper.COLUMN_ID + "=" + id, 
	        		selectionArgs);
	      } else {
	        rowsDeleted = sqlDB.delete(MySQLiteHelper.TABLE_COMMENTS,
	        		MySQLiteHelper.COLUMN_ID + "=" + id 
	            + " and " + selection,
	            selectionArgs);
	      }
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsDeleted;

	}

	@Override
	public String getType(Uri uri) {
		
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	   
	    long id = 0;
	    switch (uriType) {
	    case TOKEN_COMMENTS:
	      id = sqlDB.insert(MySQLiteHelper.TABLE_COMMENTS, null, values);
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return Uri.parse(BASE_PATH + "/" + id);
		
	}

	

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// Using SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Check if the caller has requested a column which does not exists
		checkColumns(projection);

		// Set the table
		queryBuilder.setTables(MySQLiteHelper.TABLE_COMMENTS);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case TOKEN_COMMENTS:
			break;
		case TOKEN_COMMENT_ID:
			// Adding the ID to the original query
			queryBuilder.appendWhere(MySQLiteHelper.COLUMN_ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsUpdated = 0;
	    switch (uriType) {
	    case TOKEN_COMMENTS:
	      rowsUpdated = sqlDB.update(MySQLiteHelper.TABLE_COMMENTS, 
	          values, 
	          selection,
	          selectionArgs);
	      break;
	    case TOKEN_COMMENT_ID:
	      String id = uri.getLastPathSegment();
	      if (TextUtils.isEmpty(selection)) {
	        rowsUpdated = sqlDB.update(MySQLiteHelper.TABLE_COMMENTS, 
	            values,
	            MySQLiteHelper.COLUMN_ID + "=" + id, 
	            selectionArgs);
	      } else {
	        rowsUpdated = sqlDB.update(MySQLiteHelper.TABLE_COMMENTS, 
	            values,
	            MySQLiteHelper.COLUMN_ID + "=" + id 
	            + " and " 
	            + selection,
	            selectionArgs);
	      }
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsUpdated;
	    
	}

	
	private void checkColumns(String[] projection) {
		String[] available = { MySQLiteHelper.COLUMN_ID,
				MySQLiteHelper.COLUMN_TIMESTAMP, MySQLiteHelper.COLUMN_SENDER,
				MySQLiteHelper.COLUMN_COMMENT };
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(
					Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(
					Arrays.asList(available));
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}

}
