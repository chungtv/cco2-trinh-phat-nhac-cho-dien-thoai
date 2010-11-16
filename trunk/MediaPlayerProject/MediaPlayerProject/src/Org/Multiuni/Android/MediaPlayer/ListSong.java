package Org.Multiuni.Android.MediaPlayer;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ListView;

public class ListSong extends ListActivity{
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initializeComponent();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {//click vao bai thi hat
		super.onListItemClick(l, v, position, id);
		
		launchMediaPlayer(position);
	}
	
	private void initializeComponent() {
		mCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				null, null, null, MediaStore.Audio.Media.TITLE + " ASC");
		adapter = new ListSongAdapter(this, mCursor);
		setListAdapter(adapter);
		
	}
	
	private void launchMediaPlayer(int _position) {//hat
		Intent mediaPlayerIntent = new Intent(getApplicationContext(), MediaPlayerActivity.class);
		mediaPlayerIntent.putExtra("ItemId",_position);
		startActivity(mediaPlayerIntent);
	}
	
	private Cursor mCursor;
	private ListSongAdapter adapter;
	
    
}