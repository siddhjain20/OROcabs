package com.orocab.customer;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by admin on 29-08-2016.
 */
public class TouchableWrapper extends FrameLayout {

    private long lastTouched = 0;
    private static final long SCROLL_TIME = 200L; // 200 Milliseconds, but you can adjust that to your liking
    private UpdateMapAfterUserInterection updateMapAfterUserInterection;

    public TouchableWrapper(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {

        if (MapActivity.receiver.isConnected)
        {
            switch (ev.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    if (MapActivity.Book.isEnabled())
                    {
                        MapActivity.mMapIsTouched = false;
                        MapActivity.ed_auto_pick.setText("Pin Location");
                        MapActivity.timeoutHandler.removeCallbacks(MapActivity.finalizer);
                        MapActivity.toolbar.setVisibility(GONE);
                        MapActivity.Book.setVisibility(GONE);
                        MapActivity.BookingLayout.setVisibility(GONE);
                        MapActivity.FAB.setVisibility(GONE);
                        MapActivity.cross_img.setVisibility(VISIBLE);
                        MapActivity.mark_img.setPadding(0, 0, 0, 70);
                        MapActivity.cross_img.setPadding(0, 0, 0, 70);

                        if (MapActivity.ed_auto_pick.isEnabled())
                        {
                            MapActivity.pick_arrow.setVisibility(GONE);
                        }
                        else if (MapActivity.ed_auto_drop.isEnabled())
                        {
                            MapActivity.drop_arrow.setVisibility(GONE);
                        }
                    }
//                MapActivity.FAB.setVisibility(GONE);
                    Log.i("ACTION_DOWN", "ACTION_DOWN");
                    break;

                case MotionEvent.ACTION_UP:
                    if (MapActivity.Book.isEnabled())
                    {
                        MapActivity.mMapIsTouched = true;
                        MapActivity.cross_img.setVisibility(GONE);
                        MapActivity.toolbar.setVisibility(VISIBLE);
                        MapActivity.BookingLayout.setVisibility(VISIBLE);
                        MapActivity.FAB.setVisibility(VISIBLE);
                        MapActivity.mark_img.setPadding(0, 0, 0, 0);
                        MapActivity.cross_img.setPadding(0, 0, 0, 0);

                        if (MapActivity.ed_auto_pick.isEnabled())
                        {
                            MapActivity.pick_arrow.setVisibility(VISIBLE);
                        }
                        else if (MapActivity.ed_auto_drop.isEnabled())
                        {
                            MapActivity.drop_arrow.setVisibility(VISIBLE);
                        }
                    }
//                MapActivity.FAB.setVisibility(VISIBLE);
                    Log.i("ACTION_UP", "ACTION_UP");
                    break;
            }
            return super.dispatchTouchEvent(ev);
        }
        else
        {
            return true;
        }
    }

    // Map Activity must implement this interface
    public interface UpdateMapAfterUserInterection {
        public void onUpdateMapAfterUserInterection();
    }
}