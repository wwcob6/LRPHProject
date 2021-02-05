package com.hengyi.wheelpicker.ppw;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hengyi.wheelpicker.R;
import com.hengyi.wheelpicker.listener.OnCityWheelComfirmListener;
import com.hengyi.wheelpicker.listener.OnWheelChangedListener;
import com.hengyi.wheelpicker.weight.WheelView;
import com.hengyi.wheelpicker.weight.adapters.ArrayWheelAdapter;

/**
 * Created by Administrator on 2018/1/3.
 */

public class CityWheelPickerBottomSheetDialogFragment extends BaseBottomSheetDialogFragment implements View.OnClickListener, OnWheelChangedListener {
    private View mView;
    private TextView btn_cancel, btn_confirm;
    private WheelView mViewProvince, mViewCity, mViewDistrict;
    private OnCityWheelComfirmListener listener = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.ppw_wheel_picker_view, null, false);
        btn_cancel = mView.findViewById(R.id.btn_cancel);
        btn_confirm = mView.findViewById(R.id.btn_confirm);
        mViewProvince = mView.findViewById(R.id.id_province);
        mViewCity = mView.findViewById(R.id.id_city);
        mViewDistrict = mView.findViewById(R.id.id_district);

        btn_confirm.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        mViewProvince.addChangingListener(this);
        mViewCity.addChangingListener(this);
        mViewDistrict.addChangingListener(this);

        initProvinceData(getActivity());
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<>(getContext(), mProvinceDatas));
        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        mViewDistrict.setVisibleItems(7);
        updateCities();
        updateAreas();
        return mView;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        close();
    }

    public void close() {
        releaseProvinceData();
        dismiss();
    }

    public void setListener(OnCityWheelComfirmListener wheelPickerComfirmListener) {
        this.listener = wheelPickerComfirmListener;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cancel) {
            dismiss();
        } else if (i == R.id.btn_confirm) {
            if (listener != null) {
                listener.onSelected(mCurrentProviceName, mCurrentCityName, mCurrentDistrictName, mCurrentZipCode);
            }
            dismiss();
        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        } else if (wheel == mViewDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
        }
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[]{""};
        }
        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<>(getContext(), areas));
        mViewDistrict.setCurrentItem(0);
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        mViewCity.setViewAdapter(new ArrayWheelAdapter<>(getContext(), cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }
}
