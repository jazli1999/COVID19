package com.bupt.sse.group7.covid19;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class DrawMarker {
    private List<OverlayOptions> optionsList;//存访标志的集合
    BaiduMap baiduMap;
    BitmapDescriptor bitmap;


    public DrawMarker(BaiduMap baiduMap){
        bitmap= BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
        this.baiduMap=baiduMap;
    }

    //展示所有人详细的轨迹
    public void drawAllDetail(List<JsonArray> tracklist){
        baiduMap.clear();
        if(tracklist==null||tracklist.size()==0)
            return;
        optionsList=new ArrayList<>();
        for(int j=0;j<tracklist.size();j++){
            JsonArray track=tracklist.get(j);
           
            Bundle bundle=null;
            List<LatLng> points=new ArrayList<>();

            for(int i=0;i<track.size();i++){

                JsonObject object=track.get(i).getAsJsonObject();
                int id=object.get("p_id").getAsInt();
                double curLng= object.get("longitude").getAsDouble();
                double curLan= object.get("latitude").getAsDouble();
                String date=object.get("date_time").getAsString();
                String descrip = "";
                JsonElement descObj = object.get("description");
                if (!descObj.isJsonNull()) {
                    descrip = descObj.getAsString();
                }

                LatLng currLatLng=new LatLng(curLan,curLng);
                //添加文字
                OverlayOptions textOption = new TextOptions()
                        //                    .bgColor(0xAAFFFF00)
                        .fontSize(36)
                        .fontColor(Color.BLACK)
                        .text(date+" "+descrip)
                        .position(currLatLng);
                optionsList.add(textOption);

                //添加Marker
                OverlayOptions option=new MarkerOptions().position(currLatLng).icon(bitmap);
                Marker marker= (Marker) baiduMap.addOverlay(option);
                //Marker上绑定id信息用于界面跳转
                bundle=new Bundle();
                bundle.putInt("p_id",id);
                marker.setExtraInfo(bundle);
                points.add(currLatLng);
            }
            //添加line
            if(points.size()>1) {
                OverlayOptions ooPolyline = new PolylineOptions().width(10).color(0xAAFF0000).points(points);
                Polyline polyline= (Polyline) baiduMap.addOverlay(ooPolyline);
                polyline.setExtraInfo(bundle);
            }
            points=new ArrayList<>();
        }
        baiduMap.addOverlays(optionsList);
    }

    //展示所有人的粗略轨迹
    public void drawAllRough(List<JsonArray> tracklist){
        baiduMap.clear();

        if(tracklist==null||tracklist.size()==0)
            return;
        optionsList=new ArrayList<>();
        List<Marker> markerList=new ArrayList<>();
        for(int j=0;j<tracklist.size();j++){
            JsonArray track=tracklist.get(j);
            if(track.size()==0)
                return;
            Bundle bundle=null;
            JsonObject object=track.get(0).getAsJsonObject();//获取起点
            int id=object.get("p_id").getAsInt();
            double curLng= object.get("longitude").getAsDouble();
            double curLan= object.get("latitude").getAsDouble();
            String date=object.get("date_time").getAsString();
            String descrip = "";
            JsonElement descObj = object.get("description");
            if (!descObj.isJsonNull()) {
                descrip = descObj.getAsString();
            }
            LatLng currLatLng=new LatLng(curLan,curLng);
            //添加文字
            OverlayOptions textOption = new TextOptions()
                    //                    .bgColor(0xAAFFFF00)
                    .fontSize(36)
                    .fontColor(Color.BLACK)
                    .text(date+" "+descrip)
                    .position(currLatLng);
            optionsList.add(textOption);

            //添加Marker
            OverlayOptions option=new MarkerOptions().position(currLatLng).icon(bitmap);
            Marker marker= (Marker) baiduMap.addOverlay(option);

            //Marker上绑定id信息用于界面跳转
            bundle=new Bundle();
            bundle.putInt("p_id",id);
            marker.setExtraInfo(bundle);
        }
        baiduMap.addOverlays(optionsList);
    }

}
