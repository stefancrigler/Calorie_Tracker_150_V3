//package com.example.calorie_king_stefan_rob;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.os.Bundle;
//
//import androidx.fragment.app.DialogFragment;
//
//public class ConfirmAddMealDialogFragment
//{
//}


package com.example.calorie_king_stefan_rob;

        import android.app.AlertDialog;
        import android.app.Dialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.widget.EditText;

        import androidx.fragment.app.DialogFragment;

public class ConfirmAddMealDialogFragment extends DialogFragment
{
   public EditText mealNameEditText;

   public static ConfirmAddMealDialogFragment newInstance()
   {
      ConfirmAddMealDialogFragment confirmAddMealDialogFragment =
              new ConfirmAddMealDialogFragment();
//      Bundle args = confirmAddMealDialogFragmentBundle;
//      confirmAddMealDialogFragment.setArguments(args);
      return confirmAddMealDialogFragment;
   }

   public interface ConfirmAddMealDialogFragmentDialogListener
   {
      public void onConfirmAddMealDialogPositiveClick(DialogFragment dialog);
      public void onConfirmAddMealDialogNegativeClick(DialogFragment dialog);
   }

   ConfirmAddMealDialogFragmentDialogListener confirmAddMealDialogFragmentDialogListener;

   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
      // Verify that the host activity implements the callback interface
      try {
         // Instantiate the NoticeDialogListener so we can send events to the host
         confirmAddMealDialogFragmentDialogListener =
                 (ConfirmAddMealDialogFragmentDialogListener) context;
      } catch (ClassCastException e) {
         // The activity doesn't implement the interface, throw exception
         throw new ClassCastException("Host activity must implement ConfirmAddMealDialogFragmentDialogListener");
      }
   }

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState)
   {
      // Use the Builder class for convenient dialog construction
      AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
      LayoutInflater inflater = requireActivity().getLayoutInflater();
      alertDialogBuilder.setMessage("Enter meal name: ")
              .setView(inflater.inflate(R.layout.dialog_confirm_delete_ingredient, null))
              .setPositiveButton("Yes", new DialogInterface.OnClickListener()
              {
                 public void onClick(DialogInterface dialog, int id)
                 {
                    confirmAddMealDialogFragmentDialogListener
                            .onConfirmAddMealDialogPositiveClick(ConfirmAddMealDialogFragment.this);
                 }
              })
              .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
              {
                 public void onClick(DialogInterface dialog, int id)
                 {
                    confirmAddMealDialogFragmentDialogListener
                            .onConfirmAddMealDialogNegativeClick(ConfirmAddMealDialogFragment.this);
                 }
              });
      // Create the AlertDialog object and return it
      return alertDialogBuilder.create();
   }
}

