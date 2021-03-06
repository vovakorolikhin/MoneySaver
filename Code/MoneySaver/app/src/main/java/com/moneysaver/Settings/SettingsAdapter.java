package com.moneysaver.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.moneysaver.Config;
import com.moneysaver.R;

import java.util.ArrayList;

class SettingsAdapter extends ArrayAdapter<Category> {
    private LayoutInflater inflater;

    private int layout;

    private VariableFields vFields;

    private Container container;

    SettingsAdapter(Context context, int resource, VariableFields vFields, Container container) {
        super(context, resource, container.getCommonCategories());
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.vFields = vFields;
        this.container = container;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        convertView = inflater.inflate(R.layout.list_categories, parent, false);
        viewHolder = new ViewHolder(convertView, position);
            //convertView.setTag(viewHolder);

        final Category category = container.getCommonCategories().get(position);

        viewHolder.nameView.setText(category.getName());
        viewHolder.miniMaxSum.setText(Integer.toString(category.getMaxSum()));

        viewHolder.change.b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = category.changed;
                if (!flag) {
                    startDialog(position, viewHolder);
                }
                else {
                    container.abortChanges(position);
                    container.getCommonCategories().get(position).changed = false;

                    viewHolder.setActualChange();
                    viewHolder.miniMaxSum.setText(Integer.toString(category.getMaxSum()));
                    viewHolder.nameView.setText(category.getName());
                    vFields.setSum(container.getSum());
                    vFields.recountBalance();
                }
            }
        });

        viewHolder.delete.b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDefault(container.getCommonCategories().get(position))) {
                    return;
                }
                container.getCommonCategories().get(position).deleted = !container.getCommonCategories().get(position).deleted;
                viewHolder.setActualDelete();
                vFields.setSum(container.getSum());
                vFields.recountBalance();
            }
        });

        return convertView;
    }

    private class ViewHolder {
        private MyButton change;
        private MyButton delete;
        private final TextView nameView;
        private final TextView miniMaxSum;
        private int position;

        ViewHolder(View view, int position){
            this.position = position;
            change = new MyButton(view, true);
            setActualChange();

            delete = new MyButton(view, false);
            setActualDelete();
            nameView = view.findViewById(R.id.nameCategory);
            miniMaxSum = view.findViewById(R.id.miniMaxSum);
        }

        public void setActualChange() {
            Category category = container.getCommonCategories().get(position);
            if (category.changed) {
                change.b.setText("Изменено");
                change.b.setBackgroundColor(Color.parseColor("#3b6b87"));
                change.b.setTextSize(13);
            }
            else {
                change.b.setText("Изменить");
                //change.b.setBackgroundColor(Color.parseColor("#359bd7"));
                change.b.setTextSize(13);
            }
        }

        public void setActualDelete() {
            Category category = container.getCommonCategories().get(position);

            if (!category.deleted) {
                delete.b.setText("Удалить");
                change.b.setEnabled(true);
                delete.b.setEnabled(true);
                //delete.b.setBackgroundColor(Color.parseColor("#359bd7"));
                delete.b.setTextSize(13);
            }
            else {
                delete.b.setText("Удалено");
                change.b.setEnabled(false);
                delete.b.setEnabled(false);
                delete.b.setBackgroundColor(Color.parseColor("#3b6b87"));
                change.b.setBackgroundColor(Color.parseColor("#3b6b87"));
                delete.b.setTextSize(13);
            }
        }
    }

    private class MyButton {
        private Button b;
        MyButton(View view, boolean flag) {
            if (flag)
                b = view.findViewById(R.id.circularButton1);
            else
                b = view.findViewById(R.id.circularButton2);
        }
    }

    private void startDialog(final int position, final ViewHolder viewHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.dialog_category, null);
        final EditText newCategoryName = view.findViewById(R.id.categoryNameChange);
        final EditText newBalance = view.findViewById(R.id.categoryChangeMaxSum);

        final Category category = container.getCommonCategories().get(position);
        newCategoryName.setText(category.getName());
        newBalance.setText(Integer.toString(container.getCommonCategories().get(position).getMaxSum()));

        builder.setView(view)
                .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String name = newCategoryName.getText().toString();
                        int newSum = getUserBalance(newBalance.getText().toString());

                        if (!container.updateCategory(position, name, newSum)) {
                            Toast.makeText(getContext(), "Ошибка!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            viewHolder.setActualChange();
                            viewHolder.miniMaxSum.setText(Integer.toString(category.getMaxSum()));
                            viewHolder.nameView.setText(category.getName());
                            vFields.setSum(container.getSum());
                            vFields.recountBalance();
                        }
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create();
        builder.show();
    }

    private int getUserBalance(String data){
        try{
            int b = Integer.parseInt(data);
            if (b < 0)
                return -2;
            else
                return b;
        }catch(NumberFormatException e){
            return -2;
        }
    }

    private boolean isDefault(Category category) {
        for (String name: Config.defaultCategories) {
            if (name.equals(category.getName())) {
                return true;
            }
        }
        return false;
    }
}
