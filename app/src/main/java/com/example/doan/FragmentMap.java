package com.example.doan;


import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.addOnMapClickListener;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;
import static com.mapbox.navigation.base.extensions.RouteOptionsExtensions.applyDefaultNavigationOptions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.doan.interfaceFragment.OnMapFragmentInteractionListener;
import com.example.doan.model.Pothole;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;


import com.mapbox.api.directions.v5.models.Bearing;
import com.mapbox.api.geocoding.v5.models.CarmenContext;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.GeoJson;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;


import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteOptions;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.MultiPoint;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.MultiLineString;
import com.mapbox.geojson.Polygon;
import com.mapbox.geojson.MultiPolygon;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

import com.mapbox.geojson.utils.PolylineUtils;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.Layer;
import com.mapbox.maps.extension.style.layers.LayerUtils;
import com.mapbox.maps.extension.style.layers.generated.CircleLayer;
import com.mapbox.maps.extension.style.layers.generated.LineLayer;
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.extension.style.sources.SourceUtils;
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource;

import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.maps.plugin.locationcomponent.utils.BitmapUtils;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.base.route.NavigationRoute;
import com.mapbox.navigation.base.route.NavigationRouterCallback;
import com.mapbox.navigation.base.route.RouterFailure;
import com.mapbox.navigation.base.route.RouterOrigin;
import com.mapbox.navigation.core.MapboxNavigation;

import com.mapbox.search.autocomplete.PlaceAutocomplete;
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion;
import com.mapbox.search.ui.adapter.autocomplete.PlaceAutocompleteUiAdapter;
import com.mapbox.search.ui.view.CommonSearchViewConfiguration;
import com.mapbox.search.ui.view.SearchResultsView;
import com.mapbox.turf.TurfClassification;
import com.mapbox.turf.TurfMisc;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentMap extends Fragment
        implements PermissionsListener {
    private MapView mapView;
    private PermissionsManager permissionsManager;

    private List<Point> potholeList ;
    //Các thành phần của layout
    private FloatingActionButton navigationButton;
    private FloatingActionButton mylocationButton;
    private PlaceAutocomplete placeAutocomplete;
    private PlaceAutocompleteUiAdapter placeAutocompleteUiAdapter;
    private SearchResultsView searchResultsView;
    private TextInputEditText searchET;
    private boolean ignoreNextQueryUpdate = false;
    //Các thành phần trên map Mapbox
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private String sysbolIconId = "symbolIconId";
    private String geojsonSourceLayerId = "geojsonSourceLayerId";

    public FragmentMap() {
        super(R.layout.fragment_map);
    }

    private final OnIndicatorPositionChangedListener onIndicatorPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(@NonNull Point point) {
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(15.0).build());
            getGestures(mapView).setFocalPoint(mapView.getMapboxMap().pixelForCoordinate(point));
        }
    };
    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            getLocationComponent(mapView).removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            getGestures(mapView).removeOnMoveListener(onMoveListener);
            mylocationButton.show();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

        }
    };


    private PermissionsListener permissionsListener;
    //Lắng nghe các quyền cần để sử dụng cho ứng dụng
    @Override
    public void onExplanationNeeded(@NonNull List<String> list) {

    }

    //Kết quả cấp phép được gọi khi người dùng có cho phép hoặc từ chối cấp phép hay không, xử lí hành động sau khi ứng dụng yêu cầu cấp phép tiếp theo
    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {

        } else {

        }
    }



    public void setMylocationButton() {
        mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(10.0).build());
        LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
        locationComponentPlugin.setEnabled(true);
        locationComponentPlugin.setPulsingEnabled(true);
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
        getGestures(mapView).addOnMoveListener(onMoveListener);

        mylocationButton.setOnClickListener(viewButton -> {

            locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            getGestures(mapView).addOnMoveListener(onMoveListener);
            mylocationButton.hide();
        });
    }

    public void setSearchET() {
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ignoreNextQueryUpdate) {
                    ignoreNextQueryUpdate = false;
                } else {
                    placeAutocompleteUiAdapter.search(charSequence.toString(), new Continuation<Unit>() {
                        @NonNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }

                        @Override
                        public void resumeWith(@NonNull Object o) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    searchResultsView.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        placeAutocompleteUiAdapter.addSearchListener(new PlaceAutocompleteUiAdapter.SearchListener() {
            @Override
            public void onSuggestionsShown(@NonNull List<PlaceAutocompleteSuggestion> list) {

            }

            @Override
            public void onSuggestionSelected(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {
                ignoreNextQueryUpdate = true;
                searchET.setText(placeAutocompleteSuggestion.getName());
                searchResultsView.setVisibility(View.GONE);
            }

            @Override
            public void onPopulateQueryClick(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {

            }

            @Override
            public void onError(@NonNull Exception e) {
                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    LatLng LatLng;
    //Tạo các điểm pothole cố định trên map
    private void createPointPothole(){

        potholeList = new ArrayList<>();
        Point pothole1 = Point.fromLngLat( 106.8030533,10.8700083);
        Point pothole2 = Point.fromLngLat( 106.8041,10.87015);
        Point pothole3 = Point.fromLngLat( 106.8049,10.86918);
        Point pothole4 = Point.fromLngLat( 106.8024,10.86771);
        Point pothole5 = Point.fromLngLat( 106.8024,10.86672);
        Point pothole6 = Point.fromLngLat( 106.7993,10.86475);
        potholeList.add(pothole1);
        potholeList.add(pothole2);
        potholeList.add(pothole3);
        potholeList.add(pothole4);
        potholeList.add(pothole5);
        potholeList.add(pothole6);
    }

    private PointAnnotationManager pointPotholeAnnotationManager ;
    //Quản lí các điểm pothole trên map
    public void addPotholeToMap(){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_pothole_waning_map);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        pointPotholeAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);

        for ( Point potholePoint : potholeList) {
            PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                    .withTextAnchor(TextAnchor.CENTER)
                    .withIconImage(resizedBitmap)
                    .withPoint(potholePoint);
            pointPotholeAnnotationManager.create(pointAnnotationOptions);

//            Toast.makeText(getContext(), "Pothole", Toast.LENGTH_SHORT).show();
        }
    }

    private void setclickOnMap(){


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_pin);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 90, 90, true);
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);
        ViewAnnotationManager viewAnnotationManager = mapView.getViewAnnotationManager();

        addOnMapClickListener(mapView.getMapboxMap(), pointDestination -> {
            // Xóa tất cả các annotation
            pointAnnotationManager.deleteAll();

            // Tạo annotation mới tại vị trí click
            PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                    .withTextAnchor(TextAnchor.CENTER)
                    .withIconImage(resizedBitmap)
                    .withPoint(pointDestination);
            pointAnnotationManager.create(pointAnnotationOptions);

            // Gọi hàm getRoute với point
            getRoute(pointDestination);


            return false;
        });
    }


    private OnMapFragmentInteractionListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnMapFragmentInteractionListener){
            listener = (OnMapFragmentInteractionListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
        createPointPothole();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) view.findViewById(R.id.mapbox);
        mylocationButton = (FloatingActionButton) view.findViewById(R.id.mylocationButton);
        mylocationButton.hide();
        navigationButton = (FloatingActionButton) view.findViewById(R.id.navigationButton);
        searchET = (TextInputEditText) view.findViewById(R.id.searchET);
        searchResultsView = (SearchResultsView) view.findViewById(R.id.search_results_view);
        return view;
    }


    //để hiển thị vị trí hiện tại và các thành phần của vị trí Mapbox thông qua location component
    //thông qua thư viện location của mapbox
    //nhưng ở đây sử dụng thư viện LocationComponentPlugin để kích hoạt vị trí người dùng
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        placeAutocomplete = PlaceAutocomplete.create(getString(R.string.mapbox_access_token));
        searchResultsView.initialize(new SearchResultsView.Configuration(new CommonSearchViewConfiguration()));
        placeAutocompleteUiAdapter = new PlaceAutocompleteUiAdapter(searchResultsView, placeAutocomplete, LocationEngineProvider.getBestLocationEngine(getContext()));
        //Nhấp chuột vào đây sẽ thực hiện hiển thị navigation map
        navigationButton.setOnClickListener( viewButton -> {

        });

        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {


                navigationButton.setOnClickListener(viewButton ->{
                    listener.onMapButtonClicked(new FragmentMapNavigation());
                } );

                addPotholeToMap();

                setMylocationButton();

                setSearchET();

                setclickOnMap();

            }
        });

    }

    @SuppressLint("MissingPermission")
    private void getRoute(Point destination) {

        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(getContext());

        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();

                Point origin = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                Toast.makeText(getContext(), String.valueOf(location.getLongitude()) + "   "+String.valueOf(location.getLatitude()), Toast.LENGTH_SHORT).show();
                MapboxDirections.Builder builder = MapboxDirections.builder();
                RouteOptions routeOptions = RouteOptions.builder()
                        .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
                        .geometries(DirectionsCriteria.GEOMETRY_POLYLINE6)
                        .coordinatesList(Arrays.asList(origin, destination))
                        .waypointsPerRoute(true)
                        .alternatives(true)
                        .build();

                builder.routeOptions(routeOptions);
                builder.accessToken(getString(R.string.mapbox_access_token));

                builder.build().enqueueCall(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        DirectionsResponse directionsResponse = response.body();
                        DirectionsRoute currentRoute = directionsResponse.routes().get(0);


                        LineString lineString = LineString.fromPolyline(currentRoute.geometry(),PRECISION_6);

                        //Các điểm hình thành lên linestring
                        List<Point> pointList = lineString.coordinates();
                        List<Point> potholeonLineList;
//                        for (Point routePoint : pointList) {
//                            Log.d("LineStringPoint", "Longitude: " + routePoint.longitude() + ", Latitude: " + routePoint.latitude());
//                            Point nearestPothole = TurfClassification.nearestPoint(routePoint, potholeList);
//                            Log.d("LineStringPoint ","Các pothole gần với " + " " + + routePoint.longitude() + " " + routePoint.latitude()+"là ");
//
//                            Log.d("LineStringPoint ","Latitude: " + nearestPothole.latitude());
//                            Log.d("LineStringPoint ","Longitude: " + nearestPothole.longitude());
//                            Log.d("LineStringPoint ","khoảng cách từ đầu đến điểm này" + " " + haversine(origin,routePoint));
//                            Log.d("LineStringPoint ","");
//
//                        }
                        for (Point potholePoint : potholeList) {
                            // Tìm điểm gần nhất trên LineString
                            boolean check= booleanPointOnLine(potholePoint,lineString);
                            // In ra thông tin về điểm pothole gần nhất
                            Log.d("On line", "Pothole at: " + potholePoint.coordinates() + " on line: " +check) ;
                        }
                        Feature routeFeature = Feature.fromGeometry(lineString);
                        // Make a toast which displays the route's distance



                        Toast.makeText(getContext(), String.format("Route distance: %.2f meters", currentRoute.distance()), Toast.LENGTH_SHORT).show();
                        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS,style -> {
                            GeoJsonSource.Builder lineRoute = new GeoJsonSource.Builder(ROUTE_SOURCE_ID).feature(routeFeature);
                            GeoJsonSource.Builder pointRoute = new GeoJsonSource.Builder(ICON_SOURCE_ID).feature(routeFeature);

                            SourceUtils.addSource(style,lineRoute.build());
                            SourceUtils.addSource(style,pointRoute.build());
                            LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID)
                                    .lineColor("#3b9ddd")
                                    .lineWidth(3F);
                            LayerUtils.addLayer(style,routeLayer);
                            // Add a CircleLayer to display the points
                            CircleLayer pointLayer = new CircleLayer(ICON_LAYER_ID, ICON_SOURCE_ID)
                                    .circleRadius(8f)
                                    .circleColor("#3b9ddd");

                            LayerUtils.addLayer(style, pointLayer);
                        });


                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }
    public static double haversine(Point source , Point destination) {

        double lat1= source.latitude();
        double lon1= source.longitude();
        double lat2= destination.latitude();
        double lon2= destination.longitude();
        final int R = 6371; // Radius of the Earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Returns distance in kilometers
    }
    // Hàm kiểm tra điểm có nằm trên LineString hay không
    public static boolean booleanPointOnLine(Point point, LineString lineString) {
        // Duyệt qua tất cả các đoạn tuyến trong LineString
        for (int i = 0; i < lineString.coordinates().size() - 1; i++) {
            Point start = lineString.coordinates().get(i);
            Point end = lineString.coordinates().get(i + 1);

            // Kiểm tra khoảng cách từ điểm tới đoạn thẳng (tính bằng độ dài khoảng cách)
            if (isPointOnSegment(point, start, end)) {
                return true;
            }
        }
        return false;
    }

    // Hàm kiểm tra điểm có nằm trên đoạn thẳng giữa 2 điểm không
    private static boolean isPointOnSegment(Point point, Point start, Point end) {
        // Tính toán khoảng cách từ điểm tới đoạn thẳng
        double distance = distanceFromPointToSegment(point, start, end);
        // Đặt một ngưỡng độ chính xác (tolerance)
        return distance < 0.0001; // Bạn có thể thay đổi giá trị này để phù hợp với yêu cầu
    }

    // Hàm tính khoảng cách từ điểm đến đoạn thẳng
    private static double distanceFromPointToSegment(Point point, Point start, Point end) {
        double x0 = point.longitude();
        double y0 = point.latitude();
        double x1 = start.longitude();
        double y1 = start.latitude();
        double x2 = end.longitude();
        double y2 = end.latitude();

        // Tính chiều dài đoạn thẳng (x1, y1) đến (x2, y2)
        double dx = x2 - x1;
        double dy = y2 - y1;

        // Tính khoảng cách từ điểm (x0, y0) tới đoạn thẳng
        double dot = (x0 - x1) * dx + (y0 - y1) * dy;
        double lengthSquared = dx * dx + dy * dy;
        double param = -1.0;

        // Tính toán điểm gần nhất trên đoạn thẳng
        if (lengthSquared != 0) { // Tránh chia cho 0
            param = dot / lengthSquared;
        }

        double nearestX, nearestY;

        if (param < 0) {
            nearestX = x1;
            nearestY = y1;
        } else if (param > 1) {
            nearestX = x2;
            nearestY = y2;
        } else {
            nearestX = x1 + param * dx;
            nearestY = y1 + param * dy;
        }

        // Tính khoảng cách giữa điểm và điểm gần nhất trên đoạn thẳng
        double dx2 = x0 - nearestX;
        double dy2 = y0 - nearestY;
        return Math.sqrt(dx2 * dx2 + dy2 * dy2);
    }
}
