/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.radolyn.ayugram.ui.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.radolyn.ayugram.AyuConfig;
import com.radolyn.ayugram.AyuUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.*;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexFilterEditActivity extends BaseFragment {

    private final static int done_button = 1;

    private final int filterIdx;

    private EditTextBoldCursor editField;
    private View doneButton;
    private TextView helpTextView;
    private TextView errorTextView;

    public RegexFilterEditActivity() {
        filterIdx = -1;
    }

    public RegexFilterEditActivity(int filterIdx) {
        this.filterIdx = filterIdx; // use -1 to CREATE, not EDIT
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString(filterIdx == -1 ? R.string.RegexFiltersAdd : R.string.RegexFiltersEdit));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == done_button) {
                    var text = editField.getText().toString();

                    if (TextUtils.isEmpty(text)) {
                        // todo: show error toast
                        return;
                    }

                    try {
                        Pattern.compile(text);
                    } catch (PatternSyntaxException e) {
                        var errorText = e.getMessage();
                        if (!TextUtils.isEmpty(errorText)) {
                            errorText = errorText.replace(text, "");
                        }

                        errorTextView.setText(AyuUtils.htmlToString("<b>" + errorText + "</b>"));
                        BulletinFactory.of(RegexFilterEditActivity.this).createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.RegexFiltersAddError)).show();
                        return;
                    }

                    if (filterIdx == -1) {
                        AyuConfig.addFilter(text);
                    } else {
                        AyuConfig.editFilter(filterIdx, text);
                    }

                    finishFragment();
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();
        doneButton = menu.addItemWithWidth(done_button, R.drawable.ic_ab_done, AndroidUtilities.dp(56));
        doneButton.setContentDescription(LocaleController.getString("Done", R.string.Done));

        fragmentView = new LinearLayout(context);
        LinearLayout linearLayout = (LinearLayout) fragmentView;
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        fragmentView.setOnTouchListener((v, event) -> true);

        editField = new EditTextBoldCursor(context);

        editField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        editField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        // jaBBa для даунов
        editField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                doneButton.setEnabled(!TextUtils.isEmpty(s));

                if (errorTextView != null) {
                    errorTextView.setText("");
                }
            }
        });

        if (filterIdx != -1) {
            editField.setText(AyuConfig.getRegexFilters().get(filterIdx));
        }

        linearLayout.addView(editField, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 24, 24, 24, 0));

        helpTextView = new TextView(context);
        helpTextView.setFocusable(true);
        helpTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        helpTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
        helpTextView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        helpTextView.setText(AyuUtils.htmlToString(LocaleController.getString(R.string.RegexFiltersAddDescription)));
        linearLayout.addView(helpTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT, 24, 10, 24, 0));

        errorTextView = new TextView(context);
        errorTextView.setFocusable(true);
        errorTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        errorTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
        errorTextView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        errorTextView.setText("");
        linearLayout.addView(errorTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT, 24, 10, 24, 0));

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        boolean animations = preferences.getBoolean("view_animations", true);
        if (!animations) {
            editField.requestFocus();
            AndroidUtilities.showKeyboard(editField);
        }
    }

    @Override
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            editField.requestFocus();
            AndroidUtilities.showKeyboard(editField);
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));

        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));

        themeDescriptions.add(new ThemeDescription(editField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(editField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(editField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        themeDescriptions.add(new ThemeDescription(editField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));

        themeDescriptions.add(new ThemeDescription(helpTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText8));

        return themeDescriptions;
    }
}
