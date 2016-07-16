package com.byids.hy.smarttable.view;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by hy on 2016/7/13.
 */
public class MyCustomDialog extends Dialog{
    public MyCustomDialog(Context context) {
        super(context);
    }

    public MyCustomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MyCustomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
