package Org.Multiuni.Android.MediaPlayer;


public class Utility {
	
	public static String createHMFormatfromM(long _value) {//doi do dai bai hat ra gio phut giay
		
		String _res, _strMinute, _strSecond = null;
		long _tempsecond, _second,_minute = 0;
		
		_tempsecond = _value/MILISECOND;
		_second = _tempsecond%MINUTE;//_second= (_value/1000)%60;
		_minute = _tempsecond/MINUTE;//_minute= (_value/1000)/60;
		
		if(_minute < 10) {
			_strMinute = "0" + String.valueOf(_minute);
		} else {
			_strMinute = String.valueOf(_minute);
		}
		
		if(_second < 10) {
			_strSecond = "0" + String.valueOf(_second);
		} else {
			_strSecond = String.valueOf(_second);
		}
		
		_res = _strMinute + ":" + _strSecond; 
		
		return _res;
	}

	public static final int MILISECOND = 1000;
	public static final int MINUTE = 60;
}
