package com.hse.touristhelper.ui.fragments.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.hse.touristhelper.App;
import com.hse.touristhelper.R;
import com.hse.touristhelper.qr.storage.QRCodeObject;
import com.hse.touristhelper.ui.FragmentsCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Alex on 10.05.2016.
 */
public class RecyclerQrCodeAdapter extends RealmRecyclerViewAdapter<QRCodeObject> {
    private FragmentsCallback mCallbacks;

    private class RecyclerQrCodeHolder extends RecyclerView.ViewHolder {
        public TextView data;
        public TextView content;
        public ImageView type;

        public RecyclerQrCodeHolder(View view) {
            super(view);
            data = (TextView) view.findViewById(R.id.qr_data);
            content = (TextView) view.findViewById(R.id.content_text);
            type = (ImageView) view.findViewById(R.id.image_type);
        }
    }

    public RecyclerQrCodeAdapter(FragmentsCallback callback) {
        mCallbacks = callback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.qr_code_history_item, parent, false);
        return new RecyclerQrCodeHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        RecyclerQrCodeHolder evh = (RecyclerQrCodeHolder) viewHolder;
        final QRCodeObject object = getItem(i);
        evh.data.setText(getDate(object.getTime(), "dd/MM/yyyy"));
        evh.content.setText(object.getContent());
        evh.type.setImageResource(getResByQrType(object.getType()));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.openTextFragment(object.getContent());
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) App.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("qr content", object.getContent());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(App.getAppContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    /* The inner RealmBaseAdapter
     * view count is applied here.
     * 
     * getRealmAdapter is defined in RealmRecyclerViewAdapter.
     */
    @Override
    public int getItemCount() {
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private int getResByQrType(int type) {
        switch (type) {
            case Barcode.SMS:
                return R.drawable.ic_block_black_24dp;
            case Barcode.WIFI:
                return R.drawable.ic_network_wifi_black_24dp;
            case Barcode.CALENDAR_EVENT:
                return R.drawable.ic_event_black_24dp;
            case Barcode.CONTACT_INFO:
                return R.drawable.ic_contacts_black_24dp;
            case Barcode.TEXT:
                return R.drawable.ic_note_black_24dp;
            case Barcode.URL:
                return R.drawable.ic_language_black_24dp;
            case Barcode.GEO:
                return R.drawable.ic_location_on_black_24dp;
            case Barcode.EMAIL:
                return R.drawable.ic_mail_black_24dp;
            case Barcode.PHONE:
                return R.drawable.ic_block_black_24dp;
            default:
                return R.drawable.ic_block_black_24dp;

        }
    }
}
