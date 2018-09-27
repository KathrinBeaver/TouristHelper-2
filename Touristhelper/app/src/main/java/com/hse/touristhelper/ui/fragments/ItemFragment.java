package com.hse.touristhelper.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hse.touristhelper.R;
import com.hse.touristhelper.qr.storage.QRCodeObject;
import com.hse.touristhelper.ui.FragmentsCallback;
import com.hse.touristhelper.ui.fragments.adapter.RealmQrCodeAdapter;
import com.hse.touristhelper.ui.fragments.adapter.RecyclerQrCodeAdapter;

import io.realm.Realm;
import io.realm.RealmResults;

public class ItemFragment extends Fragment {

    private RecyclerQrCodeAdapter mAdapter;
    private FragmentsCallback mListener;

    public ItemFragment() {
    }

    public static ItemFragment newInstance() {
        ItemFragment fragment = new ItemFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qr_code_item_list, container, false);

        mAdapter = new RecyclerQrCodeAdapter(mListener);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.list);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // ... calling super.onResume(), etc...
        // Perform the Realm database query
        Realm realm = Realm.getDefaultInstance();
        RealmResults<QRCodeObject> events = realm.where(QRCodeObject.class).findAll();
        RealmQrCodeAdapter realmAdapter = new RealmQrCodeAdapter(getActivity().getApplicationContext(), events, true);
        // Set the data and tell the RecyclerView to draw
        mAdapter.setRealmAdapter(realmAdapter);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentsCallback) {
            mListener = (FragmentsCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
