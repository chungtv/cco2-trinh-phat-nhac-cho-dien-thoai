package Org.Multiuni.Android.MediaPlayer;


import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class MediaPlayerActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_palyer);
        
        initializingComponent();
    }
	
	private void initializingComponent() {//khoi tao bien
		
        mBtnNext 		=	(ImageButton)findViewById(R.id.next);
        mBtnPrevious 	= 	(ImageButton)findViewById(R.id.previous);
        mBtnPlayPause 	= 	(ImageButton)findViewById(R.id.play_pause);
        mTVArtist		=	(TextView)findViewById(R.id.singer);
        mTVAlbum		= 	(TextView)findViewById(R.id.album);
        mTVYear			= 	(TextView)findViewById(R.id.year);
        mTVTitle		= 	(TextView)findViewById(R.id.nameOfSong);
        mTVDuration		= 	(TextView)findViewById(R.id.duration);
        mTVElapsedTime	= 	(TextView)findViewById(R.id.elapsedTime);
        mSeekBar 		= 	(SeekBar)findViewById(R.id.seekbar1);
        
        mAmountOfSong	= 	0;
        mCurrentPosOfSeek = 0;
        mMediaDuration = 0;
        mIsFirst = false;
        
        setOnClick(mBtnPrevious);
        setOnClick(mBtnPlayPause);
        setOnClick(mBtnNext);
        setOnClick(mSeekBar);
        
        mCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
        		MediaStore.Audio.Media.TITLE + " ASC");
        mAmountOfSong = mCursor.getCount();
        
        newCommer();
        
	}
	
	private void newCommer() {
		if(!mIsFirst) {
			Bundle b = getIntent().getExtras();
			mCurSongId = b.getInt("ItemId");
			
			createMediaPlayerFromId(mCurSongId);
	        playMedia();
	        displayData(mCurSongId);
	        bindService();
		}
	}
	
	private String[] createRecord(int _recordId) {
		String[] _res = new String[4];
		mCursor.moveToPosition(_recordId);
		_res[0] = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
		_res[1] = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
		_res[2] = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.YEAR));
		_res[3] = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
		
		return _res;
	}
	
	private void displayData(int _recordId) {//hien thi thong tin bai hat len giao dien
		String[] _record = createRecord(_recordId);
		mTVAlbum.setText(_record[1]);
		mTVTitle.setText(_record[0]);
		MediaService.mTitleofSong = _record[0];
		mTVYear.setText(_record[2]);
		mTVArtist.setText(_record[3]);
		mTVDuration.setText(Utility.createHMFormatfromM((mMediaDuration)));
	}
	
	
	
	
	private void setOnClick(final View v) {//thao tac tren giao dien chinh
				v.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						switch (v.getId()) {
						case R.id.previous:
							//PREVIOUS BUTTON HERE
							
							if(mTick <= 3000) {//neu bai hat dang hat duoi 3s thi` lui` ve 1 bai
								mIsFirst = true;
								if(mCurSongId == 0) {
									mCurSongId = mAmountOfSong-1;
								} else {
									mCurSongId--;
								}
								
								createMediaPlayerFromId(mCurSongId);
								displayData(mCurSongId);
								playMedia();
							}
							else {
								mTick = 0;
								mMediaPlayer.seekTo(0);
								mCurrentPosOfSeek = 0;
							}
							break;
							
						case R.id.play_pause:	
							//PLAY BUTTON DOSE HERE
							if(mIsPlay) {
								pauseMedia();
								stopService(new Intent(getApplicationContext(),MediaService.class));
							} else {
								playMedia();
							}
							
							break;
							
						case R.id.next:
							//NEXT BUTTON HERE
							mIsFirst = true;
						
							if(mCurSongId == mAmountOfSong-1) {
								mCurSongId = 0;
							} else {
								mCurSongId++;
							}
							
							createMediaPlayerFromId(mCurSongId);
							displayData(mCurSongId);
							playMedia();
							
							displayData(mCurSongId);
							
							break;

						default:
							break;
						}
					}
				});
				
				if(v.getId() == R.id.seekbar1) {
					v.setOnTouchListener(new OnTouchListener() {
						
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							switch (event.getAction()) {
							case MotionEvent.ACTION_UP:
								mCurrentPosOfSeek = mSeekBar.getProgress();
								mTick = mCurrentPosOfSeek*Utility.MILISECOND;
								mMediaPlayer.seekTo(mTick);
								break;

							default:
								break;
							}
							return false;
						}
					});
				}
		
	}// end of setOnclick
	
	Handler mHandler = new Handler() {//tANG  gia tri cua seekbar (da nhiem, da luong)
		public void handleMessage(Message msg) {
			
			if((mTick < mMediaDuration) &&(mIsPlay)) {
				mSeekBar.setProgress(mCurrentPosOfSeek);
				mTVElapsedTime.setText(Utility.createHMFormatfromM(mTick));
				mTick += Utility.MILISECOND;
				mCurrentPosOfSeek++;
				sendEmptyMessageDelayed(0, Utility.MILISECOND);
			}
			
			if(mTick >= mMediaDuration) {
				mBtnPlayPause.setImageResource(R.drawable.play);
			}
		}
	};
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		releaseMedia();
	}
	
	private void createMediaPlayerFromId(int _id) {//lay thong tin bai nhac tu list view
		releaseMedia();
		
		mCursor.moveToPosition(_id);
		Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		Long.valueOf(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media._ID))));
		mMediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
		mMediaDuration = mMediaPlayer.getDuration();
		mSeekBar.setMax(mMediaDuration/Utility.MILISECOND);
		mTick = 0;
		mCurrentPosOfSeek = 0;
	}
	
	private void releaseMedia() {//chuan bi de choi nhac
		if(mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
		}
		
		mIsPlay = false;
	}
	private void playMedia() {//dang hat button hinh ||
		if(!mMediaPlayer.isPlaying()) {
			mBtnPlayPause.setImageResource(R.drawable.pause);
			mIsPlay = true;
			mMediaPlayer.start();
		}
		if(!mIsFirst) {
			mHandler.sendEmptyMessage(0);
		}
	}
	
	private void pauseMedia() {//dang hat button hinh tam giac
		if(mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
			mBtnPlayPause.setImageResource(R.drawable.play);
			mIsPlay = false;
		}
	}
	
	private boolean mIsBound;
    private MediaService mBoundService;
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
        	
            mBoundService = ((MediaService.MediaBinder)service).getService();
            
          
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    };
    
    public void bindService() { 
    	bindService(new Intent(getApplicationContext(), 
            MediaService.class), mConnection, Context.BIND_AUTO_CREATE);
    	mIsBound = true;
    }
    
    public void UnbindService() {
        if (mIsBound) {
          
            unbindService(mConnection);
            mIsBound = false;
        }
    }
	
	private MediaPlayer mMediaPlayer;
	private ImageButton mBtnNext;
	private	ImageButton mBtnPrevious;
	private ImageButton mBtnPlayPause;
	private TextView	mTVTitle;
	private TextView	mTVArtist;
	private TextView	mTVYear;
	private TextView	mTVAlbum;
	private TextView	mTVDuration;
	private TextView	mTVElapsedTime;
	private SeekBar 	mSeekBar;
	private int 		mCurrentPosOfSeek;
	private Cursor		mCursor;
	private int mTick;
	private int mAmountOfSong;
	private int mCurSongId;
	private Boolean mIsPlay;
	private Boolean mIsFirst;
	
	private int 		mMediaDuration;
}