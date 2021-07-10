package com.alert.vaccinefinder;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SlotAdapter<w> extends ArrayAdapter<Slot> {
    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param translations A List of AndroidFlavor objects to display in a list
     */
    public SlotAdapter(Activity context, ArrayList<Slot> translations) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, translations);
    }
    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the
     *                 list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        Slot current_info = getItem(position);


                // Find the TextView in the list_item.xml layout with the ID version_name
                TextView nameTextView = (TextView) listItemView.findViewById(R.id.hospital_name);
                nameTextView.setText(current_info.get_hospital_name());

                TextView capacityTextView = (TextView) listItemView.findViewById(R.id.capacity);
                capacityTextView.setText("Capacity: "+current_info.get_capacity());
                capacityTextView.setTextColor(Color.parseColor("#3cb371"));

                TextView minAgeTextView = (TextView) listItemView.findViewById(R.id.min_age_limit);
                minAgeTextView.setText("Minimum age limit: " + current_info.get_min_age());


                TextView vaccineTextView = (TextView) listItemView.findViewById(R.id.vaccine_name);
                vaccineTextView.setText("Vaccine name: " + current_info.get_Vaccine_name());

                TextView feeTypeTextView = (TextView) listItemView.findViewById(R.id.fee_type);
                feeTypeTextView.setText("Fee type: " + current_info.get_fee_type());
                if (current_info.get_fee_type().equals("Paid")) {
                    feeTypeTextView.setTextColor(Color.parseColor("#FF0000"));
                } else {
                    feeTypeTextView.setTextColor(Color.parseColor("#3cb371"));
                }

                TextView feeTextView = (TextView) listItemView.findViewById(R.id.fee);
                feeTextView.setText("Fee: " + current_info.get_fee());

                TextView fromTextView = (TextView) listItemView.findViewById(R.id.from);
                fromTextView.setText("From: " + current_info.get_from());

                TextView toTextView = (TextView) listItemView.findViewById(R.id.to);
                toTextView.setText("To: " + current_info.get_to());

                TextView pincodeTextView = (TextView) listItemView.findViewById(R.id.pincode);
                pincodeTextView.setText("Pincode: " + current_info.get_pincode());

                TextView blockTextView = (TextView) listItemView.findViewById(R.id.block);
                blockTextView.setText("Block: " + current_info.get_block());

                TextView districtTextView = (TextView) listItemView.findViewById(R.id.district);
                districtTextView.setText("District: " + current_info.get_district());


        return listItemView;
    }
}
