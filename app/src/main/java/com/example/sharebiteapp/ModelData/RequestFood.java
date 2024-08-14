package com.example.sharebiteapp.ModelData;

import com.example.sharebiteapp.Utility.Utils;

public class RequestFood {
    public String requestId ;
    public String requestforId ;
    public String  requestedBy ;
    public String requestedon;

    public RequestFood() {
    }
    public RequestFood(String requestforId, String requestedBy) {
        this.requestforId = requestforId;
        this.requestedBy = requestedBy;
        this.requestedon = Utils.getCurrentDatetime();
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getRequestedon() {
        return requestedon;
    }

    public void setRequestedon(String requestedon) {
        this.requestedon = requestedon;
    }
}
