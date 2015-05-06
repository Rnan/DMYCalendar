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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TYCalendar {
    private long _jd_pkn = 0;
    private int _daysInMonth = 0;
    private int _day = 0;
    private int _month = 0;
    private int _actualMonth = 0;
    private int _year = 0;

    public TYCalendar(){
        _day = Calendar.getInstance().get(Calendar.DATE);
        _month = Calendar.getInstance().get(Calendar.MONTH); //month is an order start 0 to 11
        _actualMonth = _month+1;
        _year = Calendar.getInstance().get(Calendar.YEAR);
        initDaysInMonth();
    }

    private void initDaysInMonth(){
        Calendar calendar= new GregorianCalendar(_year,_month,_day);
        _daysInMonth=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        System.out.println("_daysInMonth:"+_daysInMonth);
    }

    public void initDaysInMonth(int y,int m,int d){
        _day = d;
        _month = m; //month is an order start 0 to 11
        _actualMonth = m+1;
        _year = y;
        initDaysInMonth();
    }

    public ArrayList ProcessDaysInMonth(){
        ArrayList allDateInfoInMonth = new ArrayList();
        DateInfo.MoonType moonType = DateInfo.MoonType.NONE;

        for (int iDay=1;iDay<=_daysInMonth;iDay++) {
            Calendar c = new GregorianCalendar(_year,_month,iDay);
            long jd_pkn=Gregorian2JD( _actualMonth, iDay, _year, 12,0,0);
            ArrayList list = JD2PKN((int) jd_pkn);
            if (list != null) {
                String days_text=FindDate(jd_pkn);
                String moonDayText= list.get(17)+ " (" + list.get(18) +")";
                //Calendar c = new GregorianCalendar(_year,_month,_day);
                //System.out.println(days_text + " " + moonDayText);
                boolean isWanPra=false;
                boolean isWanRam=true;
                if(list.get(21).toString().equals("1"))
                    isWanRam=false;

                if(list.get(15).toString().equals("1"))
                    isWanPra=true;

                int pak=Integer.parseInt(list.get(11).toString());

                switch (pak){
                    case 8:
                        if(isWanRam) {
                            moonType = DateInfo.MoonType.LAST_QUARTER_MOON;
                        }else{
                            moonType = DateInfo.MoonType.FIRST_QUARTER_MOON;
                        }
                        break;
                    case 14:
                    case 15:
                        if(isWanRam){
                            moonType = DateInfo.MoonType.NEW_MOON;
                        }else{
                            moonType = DateInfo.MoonType.FULL_MOON;
                        }
                        break;
                }

                DateInfo d = new DateInfo(c,days_text,moonDayText, moonType);
                d.SetWanPra(isWanPra);
                d.SetWanRam(isWanRam);
                if(d.IsWanPra())
                    allDateInfoInMonth.add(d);
            }
        }

        return allDateInfoInMonth;
    }
/*
    private void init(){
        Date tDate = new Date();
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        System.out.println("tDate:"+tDate);
        _jd_pkn = Gregorian2JD( month+1, day, year, 12,0,0);
        System.out.println("jd_pkn:"+_jd_pkn);
    }

    public void ProcessDay(){
        ArrayList list = JD2PKN((int)_jd_pkn);
        if(list!=null) {
            for (int i = 0; i < list.size(); i++) {
                System.out.println("ArrayList[" + i + "]" + list.get(i));
            }
            CalDate(list);
        }
    }
    */

    private String FindDate(long jd_pkn){
        ArrayList cal_date = JD2Gregorian(jd_pkn);
        String[] thai_month_name = {"", "มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"};
        String[] thai_dow_name = {"อาทิตย์", "จันทร์", "อังคาร", "พุธ", "พฤหัสบดี", "ศุกร์", "เสาร์"};
        String th_dow = thai_dow_name[jddayofweek(jd_pkn)];

        int indexTHmonth= Integer.parseInt(cal_date.get(1).toString());
        String cal_text = "วัน"+th_dow+"ที่ "+cal_date.get(0)+" "+thai_month_name[indexTHmonth]+" พ.ศ."+cal_date.get(2);

        //System.out.print("cal_text:"+cal_text);

        return cal_text;
    }

    private int jddayofweek(long jd) {
        return (int)(jd+1)%7;
    }

    private int jd2thdayofweek(long jd) {
        return jddayofweek(jd)+1;
    }

    private long mFrac(long r) {
        //
        // Return the fractional part of a real number.
        //
        return r - mFloor(r);
    }

    private ArrayList JD2Gregorian(long jd ) {

        //var	j1, j2, j3, j4, j5;			//scratch

        //
        // get the date from the Julian day number
        //

        long intgr   = mFloor(jd);
        long frac    = mFrac(jd);
        long gregjd  = 2299161; // julian day of 15 Oct 1582 in Gregorian Calendar


        long tmp = mFloor( ( (intgr - 1867216) - 0.25 ) / 36524.25d );
        long j1  = intgr + 1 + tmp - mFloor(0.25*tmp);

        //correction for half day offset
        double dayfrac = frac + 0.5;
        if( dayfrac >= 1.0 ) {
            dayfrac -= 1.0;
            j1++;
        }

        long j2 = j1 + 1524;
        long j3 = mFloor( 6680.0 + ( (j2 - 2439870) - 122.1 )/365.25 );
        long j4 = mFloor( j3*365.25 );
        long j5 = mFloor( (j2 - j4)/30.6001 );

        long d = mFloor(j2 - j4 - mFloor(j5*30.6001));
        long m = mFloor(j5 - 1);
        if( m > 12 ) {
            m -= 12;
        }

        long y = mFloor(j3 - 4715);

        if( m > 2 ) { y--; }
        if( y <= 0 ) { y--; }

        //
        // get time of day from day fraction
        //
        long hr  = mFloor(dayfrac * 24.0);
        long mn  = mFloor((dayfrac*24.0 - hr)*60.0);

        double f  = ((dayfrac*24.0 - hr)*60.0 - mn)*60.0;
        long sc  = mFloor(f);

        f -= sc;
        if( f > 0.5 ) { sc++; }

        if(sc >= 60) { sc -=60; mn++; }

        //return d+"/"+m+"/"+y+" "+hr+":"+mn+":"+sc;
        //var th_month_name = ["", "มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"];
        //return d+"/"+m+"/"+y;
        long buddhist_era = y + 543;
        //return d+" "+th_month_name[m]+" พ.ศ. "+buddhist_era+" (ค.ศ. "+y+")";

        ArrayList sdate = new ArrayList();
        sdate.add(0,d);
        sdate.add(1,m);
        sdate.add(2,buddhist_era);
        sdate.add(3,y);
        return sdate;
    }

    private long Gregorian2JD(int m,int d, int y, int h, int mn, int s ) {
        /*
        System.out.println("d:"+d);
        System.out.println("m:"+m);
        System.out.println("y:"+y);
        */
        if( y == 0 ) { return 0; }
        if( y < 0 ) { y++; }
        int jy=0;
        int jm=0;
        if( m > 2 ) {
            jy = y;
            jm = m + 1;
        } else {
            jy = y - 1;
            jm = m + 13;
        }

        // integer part of Julian Day (from Gregorian Calendar)
        long intgr = mFloor( mFloor(365.25* jy) + mFloor(30.6001* jm) + d + 1720995 );

        long ja = mFloor(0.01*jy);
        intgr += 2 - ja + mFloor(0.25*ja);

        //correct for half-day offset
        double dayfrac = h/24.0 - 0.5;

        if( dayfrac < 0.0 ) {
            dayfrac += 1.0;
            intgr--;
        }

        //now set the fraction of a day
        double frac = dayfrac + (mn + s/60.0)/60.0/24.0;
        //round to nearest second
        double jd0 = (intgr + frac)*100000;
        long jd  = mFloor(jd0);

        if( (jd0 - jd) > 0.5 ) {
            jd++;
        }

        long result=jd/100000;

        //System.out.println("Gregorian2JD:"+result);
        return result;
    }

    /*
    function jddayofweek(jd) {
        return (jd+1)%7;
    }

    function jd2thdayofweek(jd) {
        return jddayofweek(jd)+1;
    }
    */

    private int mRound(double num, int position) {
        int ten=10;
        return  (int)(Math.round(num * Math.pow(ten, position)) / Math.pow(ten, position));
    }
    private long mFloor(double num) {
        long numx=(long)num;
        //System.out.print("numx:"+numx);
        return Long.parseLong(String.valueOf(numx), 10);  // INT function (like TRUNC).
    }
    private long mCeil(double num) {
        //System.out.println("num:" + num);
        if(num == mFloor(num) ) {
            return (long)num;
        }
        else { return mRound(num+0.5,0); }
    }

    private int JD2PD(int jd) {
        // วันเริ่มต้นปักขคณนา ตรงกับ วันเสาร์ แรม 1 ค่ำ เดือน 3 พ.ศ. 2278 **
        // หรือ วันที่ 28 มกราคม พ.ศ. 2279 ตามปฏิทินสากล (แบบ gregorian)
        // สิ้นสุดในวันที่ 28 พฤศจิกายน พ.ศ. 3071 (gregorian) เป็นปักขคณนาครบ 1 รอบ
        // ** ( ศักราชไทยสมัยก่อนจะเปลี่ยนเมื่อ ขึ้น 1 ค่ำ เดือน 5 )

        int jd_start = 2355148; // 28 มกราคม พ.ศ. 2279 (gregorian)
        int jd_r = mRound(jd,0);
        if( jd_r < 2355148) { return jd; }

        int days = mRound(jd,0) - jd_start + 1; // นับจำนวนวันทั้งหมดตั้งแต่วันที่ 28 ไป (วันที่ 28 เริ่มนับเป็นลำดับที่ 1...)

        return days;
    }


    private ArrayList JD2PKN(int jd) {
        int days = JD2PD(jd);
        //System.out.println("days:" + days);
        double input =days/289577d;
        //System.out.println("input:" + input);
        long pp = mCeil(input); // ปักขคณนารอบที่
        //System.out.println("pp:" + pp);
        days = days % 289577; days += (days == 0)? 289577 : 0; // ปักคณนา 1 รอบ ใช้เวลาทั้งหมด 289577 วัน



        String[] A  = {"", "B1", "B1", "B1", "B1", "B1", "B1", "B1", "B1", "B1", "B1", "B1", "B1", "B1", "B1", "B1", "B1", "B1", "B2"};
        String[] B1 = {"", "C2", "C2", "C2", "C2", "C2", "C2", "C2", "C2", "C2", "C2", "C1"};
        String[] B2 = {"", "C2", "C2", "C2", "C2", "C2", "C2", "C2", "C2", "C2", "C1"};
        String[] C1 = {"", "D1", "D1", "D1", "D1", "D1", "D1", "D2"};
        String[] C2 = {"", "D1", "D1", "D1", "D1", "D1", "D2"};
        String[] D1 = {"", "E2", "E2", "E2", "E1"};
        String[] D2 = {"", "E2", "E2", "E1"};
        String[] E1 = {"", "F1", "F1", "F1", "F1", "F2" };
        String[] E2 = {"", "F1", "F1", "F1", "F2"};
        int F1 = 15;
        int F2 = 14;

        // สมการความสัมพันธ์ทั่วไปของปักขคณนาวิธี (จ.น.วัน)
        // $days = (pp-1)*A1 + ($a-1)*B1 + ($b-1)*C2 + ($c-1)*D1 + ($d-1)*E2 + ($e-1)*F1 + $f
        // A1 has 289577 days  ปักขคณนา 1 รอบ
        // B1 has 16168 days    มหาสัมพยุหะ
        // C2 has 1447 days      จุลสัมพยุหะ
        // D1 has 251 days        มหาสมุหะ
        // E2 has 59 days           จุลวรรค
        // F1 has 15 days           มหาปักษ์
        // โดย $a , $b, $c, $d, $e, $f เป็นสมาชิกของจำนวนเต็ม

        long a = mCeil( days/16168d ); // Position ใน มัชฌิมคติ
        //System.out.println("a:" + a);
        if( a > 18) { a = 18; }
        String AA = A[(int)a]; // ประเภท สัมพยุหะ

        String BB="";
        long b = mCeil(( days- (a-1)*16168)/1447d); // Position ใน สัมพยุหะ
        if(  AA == "B1") { if(  b > 11) {  b = 11; }   BB = B1[(int)b]; }
        else { if( b > 10) {  b = 10; }  BB = B2[(int)b];} // ประเภท พยุหะ

        String CC="";
        long c = mCeil((days - (a-1)*16168 - (b-1)*1447)/251d); // Position ใน พยุหะ
        if( BB == "C1") { if( c > 7) { c = 7; } CC = C1[(int)c]; }
        else { if( c > 6) { c=6; } CC = C2[(int)c]; } // ประเภท สมุหะ

        String DD="";
        long d = mCeil((days - (a-1)*16168 - (b-1)*1447 - (c-1)*251)/59d); // Position ใน สมุหะ
        if( CC == "D1" ) { if( d >4) { d = 4; }  DD = D1[(int)d]; }
        else { if( d >3) { d = 3; } DD = D2[(int)d]; } // ประเภท วรรค

        String EE="";
        long e = mCeil((days - (a-1)*16168 - (b-1)*1447 - (c-1)*251 - (d-1)*59)/15d); // Position ใน วรรค
        if( DD == "E1") { if ( e > 5) { e = 5; } EE = E1[(int)e]; }
        else { if( e > 4) { e = 4; } EE = E2[(int)e]; } // ประเภทปักษ์

        String f_type="";
        int f_max=0;
        long f = (days - (a-1)*16168 - (b-1)*1447 - (c-1)*251 - (d-1)*59 - (e-1)*15); // ดิถี
        if( EE == "F1" ) { f_max = F1;  f_type = "ปักข์ถ้วน"; }
        else { f_max = F2;  f_type = "ปักข์ขาด"; } // จ.น.วันในปักษ์ปัจจุบัน
        //if( f==0 || f > f_max) { return null; } //{print "PKN Bug!!!\n"; } --> หากต้องการจะทดสอบ BUG ให้เปลี่ยนเป็นอันหลังนี้ ซึ่งเท่าที่ทดสอบไม่พบ Bug


        // สมการความสัมพันธ์ทั่วไปของปักขคณนาวิธี (จ.น.ปักษ์)
        // สูตรนี้เป็น สูตรเพิ่มเติม ได้คิดใหม่ไม่มีมาแต่เดิม
        // ใช้สำหรับหาข้างขึ้นข้างแรม เทียบประกอบ
        long pak_total = (pp-1)*19612 + (a-1)*1095 + (b-1)*98 + (c-1)*17 + (d-1)*4 + e;
        String pak_type  = ( pak_total % 2 )==0 ? "ขึ้น" : "แรม";
        int pak_type2 = ( pak_total % 2 )==0 ? 1 : 0;

        long img_num=0;
        if( pak_type2==1) { // ข้างขึ้น
            img_num = ( f_max == 14 )? f+1: f;
        } else { // ข้างแรม
            img_num = ( f_max == 14 )? 15+f : 14+f;
        }
        String img_id = (img_num < 10)? "im0"+img_num : "im"+img_num;

        //pkn_text = "ปักขคณนารอบที่ "+pp+" "+AA+" "+a+" "+BB+" "+b+" "+CC+" "+c+" "+DD+" "+d+" "+EE+" "+e+" "+pak_type+" "+f+" ค่ำ ("+f_type+")";
        String pkn_text = AA+" "+a+" "+BB+" "+b+" "+CC+" "+c+" "+DD+" "+d+" "+EE+" "+e+" "+pak_type+" "+f+" ค่ำ ("+f_type+")";
        //System.out.println("pkn_text:" + pkn_text);
        pkn_text = pkn_text.replace("B1", "มหาสัมพยุหะ");
        pkn_text = pkn_text.replace("B2", "จุลสัมพยุหะ");
        pkn_text = pkn_text.replace("C1", "มหาพยุหะ");
        pkn_text = pkn_text.replace("C2", "จุลพยุหะ");
        pkn_text = pkn_text.replace("D1", "มหาสมุหะ");
        pkn_text = pkn_text.replace("D2", "จุลสมุหะ");
        pkn_text = pkn_text.replace("E1", "มหาวรรค");
        pkn_text = pkn_text.replace("E2", "จุลวรรค");
        pkn_text = pkn_text.replace("F1", "มหาปักข์");
        pkn_text = pkn_text.replace("F2", "จุลปักข์");

        String pkn_text2 =	"<a title='แสดงตาม \"ปักษ์คณนาสำเร็จ\" ของ พระเจ้าบรมวงษเธอ กรมสมเด็จพระปวเรศวริยาลงกรณ์'><table CELLSPACING=0 CELLPADDING=0 style='font-size:15;' bgcolor=lightgreen><tr>"+
                "<td>"+	((AA == "B1")?	( (a>9)? MAHA_transform(mFloor(a/10)) : MAHA_transform(a)) :
                ( (a>9)? JULA_transform(mFloor(a/10)) : JULA_transform(a)) ) + "</td>"+
                "<td>"+	((BB == "C1")?	( (b>9)? MAHA_transform(mFloor(b/10)) : MAHA_transform(b)) :
                ( (b>9)? JULA_transform(mFloor(b/10)) : JULA_transform(b)) ) + "</td>"+
                "<td>"+	((CC == "D1")?	( (c>9)? MAHA_transform(mFloor(c/10)) : MAHA_transform(c)) :
                ( (c>9)? JULA_transform(mFloor(c/10)) : JULA_transform(c)) ) + "</td>"+
                "<td>"+	((DD == "E1")?	( (d>9)? MAHA_transform(mFloor(d/10)) : MAHA_transform(d)) :
                ( (d>9)? JULA_transform(mFloor(d/10)) : JULA_transform(d)) ) + "</td>"+
                "<td>"+	((EE == "F1")?	( (e>9)? MAHA_transform(mFloor(e/10)) : MAHA_transform(e)) :
                ( (e>9)? JULA_transform(mFloor(e/10)) : JULA_transform(e)) ) + "</td>"+"</tr>"+
                "<tr>"+
                "<td>"+	((AA == "B1")?	( (a>9)? MAHA_transform(a %10) : "" ) :
                ( (a>9)? JULA_transform(a %10) : "" ) ) + "</td>"+
                "<td>"+	((BB == "C1")?	( (b>9)? MAHA_transform(b %10) : "" ) :
                ( (b>9)? JULA_transform(b %10) : "" ) ) + "</td>"+
                "<td>"+	((CC == "D1")?	( (c>9)? MAHA_transform(c %10) : "" ) :
                ( (c>9)? JULA_transform(c %10) : "" ) ) + "</td>"+
                "<td>"+	((DD == "E1")?	( (d>9)? MAHA_transform(d %10) : "" ) :
                ( (d>9)? JULA_transform(d %10) : "" ) ) + "</td>"+
                "<td>"+	((EE == "F1")?	( (e>9)? MAHA_transform(e %10) : "" ) :
                ( (e>9)? JULA_transform(e %10) : "" ) ) + "</td>"+
                "</tr></table></a>";


        ArrayList pkn = new ArrayList();
        pkn.add(0,pkn_text);
        pkn.add(1,1);
        pkn.add(2, AA.replace("B", ""));
        pkn.add(3, BB.replace("C", ""));
        pkn.add(4, CC.replace("D", ""));
        pkn.add(5, DD.replace("E", ""));
        pkn.add(6,a);
        pkn.add(7,b);
        pkn.add(8,c);
        pkn.add(9,d);
        pkn.add(10,e);
        pkn.add(11,f);
        pkn.add(12,f_max);
        pkn.add(13,pp);
        pkn.add(14,jd);
        //** convert f_max to double for check value of หาร
        double double_f_max= f_max;
        pkn.add(15,( f/double_f_max ==  4/7d || f/double_f_max == 8/15d || f/double_f_max == 1 )? 1 : 0);  // วันพระธรรมยุติ
        pkn.add(16, img_id);
        pkn.add(17, pak_type+" "+f+" ค่ำ");
        pkn.add(18,f_type);
        pkn.add(19,pak_total);
        pkn.add(20,pkn_text2);
        pkn.add(21,pak_type2); //0=ขึ้น 1=แรม

        //return a+","+b+","+c+","+d+","+e+","+f;
        return pkn;
    }


    private String MAHA_transform(long n ) {
        String result="";
        switch ((int)n){
            case 1: result="๑";
                break;
            case 2: result="๒";
                break;
            case 3: result="๓";
                break;
            case 4: result="๔";
                break;
            case 5: result="๕";
                break;
            case 6: result="๖";
                break;
            case 7: result="๗";
                break;
            case 8: result="๘";
                break;
            case 9: result="๙";
                break;
            case 0: result="๐";
                break;
        }
        return result;
    }

    private String JULA_transform(long n ) {
        String result="";
        switch ((int)n){
            case 1: result="ก";
                break;
            case 2: result="ข";
                break;
            case 3: result="ฅ";
                break;
            case 4: result="จ";
                break;
            case 5: result="ห";
                break;
            case 6: result="ฉ";
                break;
            case 7: result="ษ";
                break;
            case 8: result="ฐ";
                break;
            case 9: result="ฬ";
                break;
            case 0: result="ฮ";
                break;
        }
        return result;
    }
}
