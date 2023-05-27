package com.example.educheck.Controleur.DashboardAdmin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.educheck.Modele.Interface.AsyncTaskcallback;
import com.example.educheck.Modele.University;
import com.example.educheck.R;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManagerUniversityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManagerUniversityFragment extends Fragment implements AsyncTaskcallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TOKEN = "token";
    private static final String UNIVERSITY = "university";

    // TODO: Rename and change types of parameters
    private String token;
    private University university;
    private ImageView logoRight;

    public ManagerUniversityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param token Token de session.
     * @param university University of Admin.
     * @return A new instance of fragment ManagerUniversityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManagerUniversityFragment newInstance(String token, University university) {
        ManagerUniversityFragment fragment = new ManagerUniversityFragment();
        Bundle args = new Bundle();
        args.putString(TOKEN, token);
        args.putSerializable(UNIVERSITY, university);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            token = getArguments().getString(TOKEN);
            university = (University) getArguments().getSerializable(UNIVERSITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewInflate = inflater.inflate(R.layout.fragment_manager_university, container, false);
        logoRight = viewInflate.findViewById(R.id.logoRight);
        return viewInflate;
    }

    @Override
    public void onTaskCompleted(JSONArray items) throws JSONException {

    }

    public void setUniversity(University university) {
        this.university = university;
    }
}