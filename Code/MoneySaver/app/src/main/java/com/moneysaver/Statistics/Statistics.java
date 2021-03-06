package com.moneysaver.Statistics;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.moneysaver.Config;
import com.moneysaver.R;
import com.moneysaver.SQLite;
import com.moneysaver.Settings.Category;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.util.ArrayList;

public class Statistics extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener {

    private PieChart chart;

    private TextView textView;

    private SeekBar seekBar;

    private ArrayList<Category> categories;

    private double sum;

    private final int defaultCount = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        int count = 0;
        categories = SQLite.getCategoryList(getBaseContext(), "Category");
        for (Category category: categories) {
            sum += category.getSpent();
            if (!(category.getSpent() < Config.EPS))
                count++;
        }

        Shimmer shimmer = new Shimmer();
        ShimmerTextView shimmerTextView = findViewById(R.id.shimmer);
        shimmer.start(shimmerTextView);

        textView = findViewById(R.id.tvXMax);
        seekBar = findViewById(R.id.seekBar);

        seekBar.setMax(count);

        seekBar.setOnSeekBarChangeListener(this);

        chart = findViewById(R.id.chart);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        chart.setOnChartValueSelectedListener(this);

        chart.animateY(1400, Easing.EaseInOutQuad);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTextSize(12f);

        if (count > 5)
            setData(5);
        else
            setData(count);
    }

    private void setData(int count) {
        textView.setText(Integer.toString(count));
        seekBar.setProgress(count);
        int counter = 0;
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < categories.size() && counter < count; i++) {
            Category category = categories.get(i);
            if (category.getSpent() < Config.EPS)
                continue;
            entries.add(new PieEntry(getPercent(category),category.getName()));
            counter++;
        }

        PieDataSet dataSet = new PieDataSet(entries, "Категории");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLUE);
        chart.setData(data);
        chart.highlightValues(null);

        chart.invalidate();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        textView.setText(String.valueOf(seekBar.getProgress()));
        setData(seekBar.getProgress());
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        PieEntry pieEntry = (PieEntry)e;
        for (Category category: categories) {
            if (category.getName().equals(pieEntry.getLabel())) {
                chart.setCenterText(generateCenterSpannableTextSelected(category));
                return;
            }
        }
    }

    @Override
    public void onNothingSelected() {
        chart.setCenterText(generateCenterSpannableTextNothingSelected());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    private float getPercent(Category category) {
        return (float)Math.round(category.getSpent() * 1000 / sum) / 10;
    }

    private SpannableString generateCenterSpannableTextNothingSelected() {
        SpannableString s = new SpannableString("Всего потрачено\n" + sum + " руб.");
        int spentLen = Double.toString(sum).length();
        s.setSpan(new StyleSpan(Typeface.ITALIC), 0, 16, 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 0, 16, 0);
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 16, 0);
        s.setSpan(new RelativeSizeSpan(2f), 16, 16 + spentLen, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), 16 + spentLen, 16 + spentLen + 5, 0);
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#065535")), 16 + spentLen, 16 + spentLen + 5, 0);
        return s;
    }

    private SpannableString generateCenterSpannableTextSelected(Category category) {
        SpannableString s = new SpannableString("Потрачено\n" + category.getSpent() +
                " руб.\nОстаток\n" + category.getBalance() + " руб.");
        int spentLen = Double.toString(category.getSpent()).length();
        int balanceLen = Double.toString(category.getBalance()).length();

        s.setSpan(new StyleSpan(Typeface.ITALIC), 0, 9, 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 0, 9, 0);
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 9, 0);

        s.setSpan(new RelativeSizeSpan(2f), 9, 10 + spentLen, 0);

        s.setSpan(new StyleSpan(Typeface.ITALIC), 10 + spentLen, 15 + spentLen, 0);
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#065535")), 10 + spentLen, 15 + spentLen, 0);

        s.setSpan(new StyleSpan(Typeface.ITALIC), 15 + spentLen, 24 + spentLen, 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 15 + spentLen, 24 + spentLen, 0);
        s.setSpan(new RelativeSizeSpan(1.5f), 15 + spentLen, 24 + spentLen, 0);

        s.setSpan(new RelativeSizeSpan(2f), 24 + spentLen, 24 + spentLen + balanceLen, 0);

        s.setSpan(new StyleSpan(Typeface.ITALIC), 24 + spentLen + balanceLen, 29 + spentLen + balanceLen, 0);
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#065535")), 24 + spentLen + balanceLen, 29 + spentLen + balanceLen, 0);

        return s;
    }
}

