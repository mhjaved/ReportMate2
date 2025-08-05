
package com.hasanjaved.reportmate.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Companies {

    @SerializedName("30")
    @Expose
    private String _30;

    public String get30() {
        return _30;
    }

    public void set30(String _30) {
        this._30 = _30;
    }

}
