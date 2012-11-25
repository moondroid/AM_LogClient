package eu.areamobile_android_apps.logclient.database;

import java.util.ArrayList;
import java.util.List;

import eu.areamobile_android_apps.logclient.bean.Comments;
import eu.areamobile_android_apps.logclient.bean.SingleComment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class CommentsDataSource {

	private Context mContext;
	
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_TIMESTAMP,
			MySQLiteHelper.COLUMN_SENDER,
			MySQLiteHelper.COLUMN_COMMENT };

	public CommentsDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
		mContext = context;
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		Log.d("CommentsDataSource" , "open");
	}

	public void close() {
		dbHelper.close();
	}

	public void createComment(SingleComment comment) {

		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_ID, Integer.parseInt(comment.getId()));
		values.put(MySQLiteHelper.COLUMN_TIMESTAMP, comment.getTimestamp());
		values.put(MySQLiteHelper.COLUMN_SENDER, comment.getSender());
		values.put(MySQLiteHelper.COLUMN_COMMENT, comment.getComment());
		
		long insertId = database.insert(MySQLiteHelper.TABLE_COMMENTS, null,
				values);
		
		System.out.println("Comment inserted with id: " + Long.toString(insertId));

//		Log.d("insertID", Long.toString(insertId));
		
//		Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
//				allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
//				null, null, null);
//		cursor.moveToFirst();
//		SingleComment newComment = cursorToComment(cursor);
//		cursor.close();
//		return newComment;
	}

	public void  addCommentList(Comments comments) {
		for(int i=0; i<comments.size(); i++){			
			createComment(comments.get(i));
		}
		
	}
	
	
	public void deleteComment(SingleComment comment) {
		String id = comment.getId();
		System.out.println("Comment deleted with id: " + id);

		database.delete(MySQLiteHelper.TABLE_COMMENTS, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public void deleteAllComments() {
		
//		database.execSQL("DROP TABLE IF EXISTS " + MySQLiteHelper.TABLE_COMMENTS);
//		database = dbHelper.getWritableDatabase();
	
		Comments comments = getAllComments();
		for(int i=0; i<comments.size(); i++){			
			deleteComment(comments.get(i));
		}
	}
	
	
	public Comments getAllComments() {
		Comments comments = new Comments();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			SingleComment comment = cursorToComment(cursor);
			comments.add(comment);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return comments;
	}

	public boolean isEmpty(){
		
		Comments comments = getAllComments();
		if (comments.size()==0){
			return true;
		}
		return false;
	}
	
	private SingleComment cursorToComment(Cursor cursor) {
		SingleComment comment = new SingleComment();
		comment.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
		comment.setTimestamp(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_TIMESTAMP)));
		comment.setSender(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_SENDER)));
		comment.setComment(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COMMENT)));
		return comment;
	}
	
	
	public void testProvider(){
		ContentResolver cr = mContext.getContentResolver();
		
//		Cursor c = cr.query(Uri.parse("content://eu.areamobile.logclient/comments"), projection, selection, selectionArgs, sortOrder)
		
//		cr.registerContentObserver(null, true, null);
	}
}
