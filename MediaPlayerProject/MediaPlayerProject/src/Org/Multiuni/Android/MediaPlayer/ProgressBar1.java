/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package Org.Multiuni.Android.MediaPlayer;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;



public class ProgressBar1 extends Activity {
	
	private ProgressBar progressHorizontal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    
        setContentView(R.layout.progressbar_1);
        
        progressHorizontal = (ProgressBar) findViewById(R.id.progress_horizontal);
        setProgress(progressHorizontal.getProgress() * 200);
        setProgress(50);
        progressHorizontal.incrementProgressBy(2);
        
    }
    
    Handler mHandler = new Handler() {
    	public void handleMessage(android.os.Message msg) {
    		
    		progressHorizontal.incrementProgressBy(1);
    	};
    };
    
}
