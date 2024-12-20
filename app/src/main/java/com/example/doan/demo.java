//package com.example.doan;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//
//import com.google.android.gms.maps.model.LatLng;
//import com.mapbox.geojson.LineString;
//import com.mapbox.geojson.Point;
//import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
//import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
//import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
//import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
//import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
//import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
//
//import java.util.ArrayList;
//
//public class demo {
//    com.google.android.gms.maps.model.LatLng LatLng;
//    //Tạo các điểm pothole cố định trên map
//    private void createPointPothole(){
//
//        potholeList = new ArrayList<>();
//        Point pothole2 = Point.fromLngLat( 106.8041,10.87015);
//        Point pothole3 = Point.fromLngLat( 106.8049,10.86918);
//        Point pothole4 = Point.fromLngLat( 106.8024,10.86771);
//        Point pothole5 = Point.fromLngLat( 106.8024,10.86672);
//        Point pothole6 = Point.fromLngLat( 106.7993,10.86475);
//        potholeList.add(pothole2);
//        potholeList.add(pothole3);
//        potholeList.add(pothole4);
//        potholeList.add(pothole5);
//        potholeList.add(pothole6);
//    }
//
//    private PointAnnotationManager pointPotholeAnnotationManager ;
//    //Quản lí các điểm pothole trên map
//    public void addPotholeToMap(){
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_pothole_waning_map);
//        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
//        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
//        pointPotholeAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);
//
//        for ( Point potholePoint : potholeList) {
//            PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
//                    .withTextAnchor(TextAnchor.CENTER)
//                    .withIconImage(resizedBitmap)
//                    .withPoint(potholePoint);
//            pointPotholeAnnotationManager.create(pointAnnotationOptions);
//
////            Toast.makeText(getContext(), "Pothole", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    // Hàm kiểm tra điểm có nằm trên LineString hay không
//    public static boolean booleanPointOnLine(Point point, LineString lineString) {
//        // Duyệt qua tất cả các đoạn tuyến trong LineString
//        for (int i = 0; i < lineString.coordinates().size() - 1; i++) {
//            Point start = lineString.coordinates().get(i);
//            Point end = lineString.coordinates().get(i + 1);
//
//            // Kiểm tra khoảng cách từ điểm tới đoạn thẳng (tính bằng độ dài khoảng cách)
//            if (isPointOnSegment(point, start, end)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    // Hàm kiểm tra điểm có nằm trên đoạn thẳng giữa 2 điểm không
//    private static boolean isPointOnSegment(Point point, Point start, Point end) {
//        // Tính toán khoảng cách từ điểm tới đoạn thẳng
//        double distance = distanceFromPointToSegment(point, start, end);
//        // Đặt một ngưỡng độ chính xác (tolerance)
//        return distance < 0.0001; // Bạn có thể thay đổi giá trị này để phù hợp với yêu cầu
//    }
//
//    // Hàm tính khoảng cách từ điểm đến đoạn thẳng
//    private static double distanceFromPointToSegment(Point point, Point start, Point end) {
//        double x0 = point.longitude();
//        double y0 = point.latitude();
//        double x1 = start.longitude();
//        double y1 = start.latitude();
//        double x2 = end.longitude();
//        double y2 = end.latitude();
//
//        // Tính chiều dài đoạn thẳng (x1, y1) đến (x2, y2)
//        double dx = x2 - x1;
//        double dy = y2 - y1;
//
//        // Tính khoảng cách từ điểm (x0, y0) tới đoạn thẳng
//        double dot = (x0 - x1) * dx + (y0 - y1) * dy;
//        double lengthSquared = dx * dx + dy * dy;
//        double param = -1.0;
//
//        // Tính toán điểm gần nhất trên đoạn thẳng
//        if (lengthSquared != 0) { // Tránh chia cho 0
//            param = dot / lengthSquared;
//        }
//
//        double nearestX, nearestY;
//
//        if (param < 0) {
//            nearestX = x1;
//            nearestY = y1;
//        } else if (param > 1) {
//            nearestX = x2;
//            nearestY = y2;
//        } else {
//            nearestX = x1 + param * dx;
//            nearestY = y1 + param * dy;
//        }
//
//        // Tính khoảng cách giữa điểm và điểm gần nhất trên đoạn thẳng
//        double dx2 = x0 - nearestX;
//        double dy2 = y0 - nearestY;
//        return Math.sqrt(dx2 * dx2 + dy2 * dy2);
//    }
//}

//
//import com.mapbox.geojson.Point;
//
//import java.util.List;//Các điểm hình thành lên linestring
//List<Point> pointList = lineString.coordinates();
//                        for (Point potholePoint : potholeList) {
//// Tìm điểm gần nhất trên LineString
//boolean check= booleanPointOnLine(potholePoint,lineString);
//// In ra thông tin về điểm pothole gần nhất
//                            Log.d("On line", "Pothole at: " + potholePoint.coordinates() + " on line: " +check) ;
//        }