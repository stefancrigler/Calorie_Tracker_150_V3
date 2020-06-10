package com.example.calorie_king_stefan_rob;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class ConfirmDeleteIngredientDialogFragment extends DialogFragment
{
   public static ConfirmDeleteIngredientDialogFragment newInstance(Bundle confirmDeleteIngredientDialogFragmentBundle)
   {
      ConfirmDeleteIngredientDialogFragment confirmDeleteIngredientDialogFragment =
              new ConfirmDeleteIngredientDialogFragment();
      Bundle args = confirmDeleteIngredientDialogFragmentBundle;
      confirmDeleteIngredientDialogFragment.setArguments(args);
      return confirmDeleteIngredientDialogFragment;
   }

   public interface ConfirmDeleteIngredientDialogFragmentDialogListener
   {
      public void onDeletionDialogPositiveClick(DialogFragment dialog);
      public void onDeletionDialogNegativeClick(DialogFragment dialog);
   }

   ConfirmDeleteIngredientDialogFragmentDialogListener confirmDeleteIngredientDialogFragmentDialogListener;

   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
      // Verify that the host activity implements the callback interface
      try {
         // Instantiate the NoticeDialogListener so we can send events to the host
         confirmDeleteIngredientDialogFragmentDialogListener =
                 (ConfirmDeleteIngredientDialogFragmentDialogListener) context;
      } catch (ClassCastException e) {
         // The activity doesn't implement the interface, throw exception
         throw new ClassCastException("Host activity must implement confirmDeleteIngredientDialogFragmentDialogListener");
      }
   }

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState)
   {
      String ingredientName = getArguments().getString("ingredientName");
      // Use the Builder class for convenient dialog construction
      AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
      String titleString;

      alertDialogBuilder.setMessage("Delete " + ingredientName + " from ingredient list?")
              .setPositiveButton("Yes", new DialogInterface.OnClickListener()
              {
                 public void onClick(DialogInterface dialog, int id)
                 {
                    confirmDeleteIngredientDialogFragmentDialogListener
                            .onDeletionDialogPositiveClick(ConfirmDeleteIngredientDialogFragment.this);
                 }
              })
              .setNegativeButton("No", new DialogInterface.OnClickListener()
              {
                 public void onClick(DialogInterface dialog, int id)
                 {
                    confirmDeleteIngredientDialogFragmentDialogListener
                            .onDeletionDialogNegativeClick(ConfirmDeleteIngredientDialogFragment.this);
                 }
              });
      // Create the AlertDialog object and return it
      return alertDialogBuilder.create();
   }
}
