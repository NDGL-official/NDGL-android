package com.yapp.ndgl.feature.travel.model

import androidx.annotation.StringRes
import com.yapp.ndgl.core.ui.R

enum class PlaceDetailTab(@get:StringRes val titleRes: Int) {
    INFO(R.string.place_detail_tab_info),
    PHOTO(R.string.place_detail_tab_photo),
}
