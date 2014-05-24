package fr.tvbarthel.apps.billing;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Inventory;

import fr.tvbarthel.apps.billing.utils.SupportUtils;
import fr.tvbarthel.apps.simpleweatherforcast.R;


/**
 * Use to check donation state
 */
public class DonateCheckActivity extends Activity {

    /**
     * used to know if user as donate
     */
    private boolean mHasDonate;

    /**
     * use to manage Toast well
     */
    private Toast mToast;

    @Override
    protected void onResume() {
        super.onResume();

        //check donation history
        checkDonation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideToast();
    }

    /**
     * @return true if user has already donate
     */
    public boolean hasDonate() {
        return mHasDonate;
    }

    /**
     * avoid Toast queue, only one Toast for this activity
     *
     * @param text toast message
     */
    protected void makeToast(int text) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        mToast.show();
    }


    /**
     * Hide the current toast if needed.
     */
    protected void hideToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

    /**
     * Unlock small advantages for supporter, at least greet them
     */
    protected void checkDonation() {
        mHasDonate = false;
        final SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final int donateState = sharedPreferences.getInt(SupportUtils.SUPPORT_SHARED_KEY,
                SupportUtils.SUPPORT_UNSET);
        switch (donateState) {
            case SupportUtils.SUPPORT_UNSET:
                //retrieve info
                final IabHelper helper = new IabHelper(getApplicationContext(),
                        getResources().getString(R.string.support_key));
                helper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                    @Override
                    public void onIabSetupFinished(IabResult result) {
                        if (result.isSuccess()) {
                            helper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
                                @Override
                                public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                                    if (result.isSuccess()) {
                                        if (SupportUtils.hasPurchased(inv)) {
                                            mHasDonate = true;
                                            makeToast(R.string.support_has_supported_us);

                                            //save it
                                            final SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putInt(SupportUtils.SUPPORT_SHARED_KEY, SupportUtils.SUPPORT_DONATE);
                                            editor.commit();

                                            //release resources
                                            helper.dispose();
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
                break;
            case SupportUtils.SUPPORT_DONATE:
                //user already support us
                mHasDonate = true;
                makeToast(R.string.support_has_supported_us);
                break;
            case SupportUtils.SUPPORT_NOT_YET:
                //user doesn't support us yet
                break;
        }
    }
}
