package com.olivadevelop.knittingcounter.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.olivadevelop.knittingcounter.MainActivity;
import com.olivadevelop.knittingcounter.R;

import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private MainActivity mainActivity;
    private View root;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        this.root = inflater.inflate(R.layout.fragment_gallery, container, false);
        this.mainActivity = (MainActivity) this.getActivity();

        if (this.mainActivity != null) {
            Toolbar toolbar = this.mainActivity.findViewById(R.id.toolbar);
            //toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_gallery));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.onBackPressed();
                }
            });
        }
        return this.root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            // Get the NavController for your NavHostFragment
            if (this.mainActivity != null) {
                NavController navController = Navigation.findNavController(view);
                // Set up the ActionBar to stay in sync with the NavController
                setupActionBarWithNavController(this.mainActivity, navController);
            }
            TextView textView = root.findViewById(R.id.text_gallery);
            textView.setText("Project selected: " + getArguments().getLong("idProjectSelected"));
        }
    }

}