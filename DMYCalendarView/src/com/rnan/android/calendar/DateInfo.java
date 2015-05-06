/**************************************************************
 Copyright 2015 Anan Sriram

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 *****************************************************************/

package com.rnan.android.calendar;

import java.util.Calendar;

public class DateInfo {

    private Calendar _date ;
    private String _gregorianDate ;
    private String _moonDate;
    private boolean _isWanPra;//วันพระ
    private boolean _isWanRam;//วันแรม/ขึ้น
    private MoonType _moonType;

    public enum MoonType {
        FULL_MOON, FIRST_QUARTER_MOON,LAST_QUARTER_MOON, NEW_MOON , NONE
    }

    public DateInfo(){
        _moonType=MoonType.NONE;
    }

    public DateInfo(Calendar c, String g, String m,MoonType mt){
        _date =c;
        _gregorianDate = g;
        _moonDate=m;
        _moonType=mt;
    }

    public Boolean IsWanPra() {
        return _isWanPra;
    }

    public Boolean IsWanRam() {
        return _isWanRam;
    }

    public void SetWanPra(Boolean isWanpra) {
        _isWanPra=isWanpra;
    }

    public void SetWanRam(Boolean isWanRam) {
        _isWanRam=isWanRam;
    }

    public Calendar GetDate() {
        return _date;
    }

    public MoonType GetMoonType() {
        return _moonType;
    }

    public void SetDate(Calendar c) {
        _date=c;
    }

    public void SetMoonType(MoonType mt) {
        _moonType=mt;
    }

    public String GregorianDate() {
        return _gregorianDate;
    }

    public void SetGregorianDate(String g) {
        _gregorianDate=g;
    }

    public String GetMoonDate() {
        return _moonDate;
    }

    public void SetMoonDate(String m) {
        _moonDate=m;
    }
    //0 date:date [2005-03-28]
    //1 international-day:string [วันเสาร์ที่ 28 มีนาคม พ.ศ.2558]
    //2 moon-day [แรม 8 ค่ำ (ปักข์ขาด)]
}

