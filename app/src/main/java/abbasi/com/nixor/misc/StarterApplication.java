/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package abbasi.com.nixor.misc;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;


public class StarterApplication extends Application {




  @Override
  public void onCreate() {
    super.onCreate();
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    // Add your initialization code here

    android.content.Context context = this.getApplicationContext();



  }



}
