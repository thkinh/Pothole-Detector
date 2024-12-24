package com.example.doan.map;


import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.maps.plugin.animation.CameraAnimationsUtils.getCamera;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.addOnMapClickListener;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;
import static com.mapbox.navigation.base.extensions.RouteOptionsExtensions.applyDefaultNavigationOptions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.doan.R;
import com.example.doan.api.potholes.PotholeManager;
import com.example.doan.model.Pothole;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import com.google.android.material.textfield.TextInputLayout;
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
import com.mapbox.api.directions.v5.models.VoiceInstructions;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.bindgen.Expected;
import com.mapbox.bindgen.ExpectedFactory;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;


import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;

import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.ViewAnnotationOptions;
import com.mapbox.maps.extension.style.layers.LayerUtils;
import com.mapbox.maps.extension.style.layers.generated.LineLayer;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.extension.style.sources.SourceUtils;
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource;

import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentConstants;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;
import com.mapbox.navigation.base.formatter.DistanceFormatterOptions;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.base.route.NavigationRoute;
import com.mapbox.navigation.base.route.NavigationRouterCallback;
import com.mapbox.navigation.base.route.RouterFailure;
import com.mapbox.navigation.base.route.RouterOrigin;
import com.mapbox.navigation.base.trip.model.RouteProgress;
import com.mapbox.navigation.core.MapboxNavigation;

import com.mapbox.navigation.core.directions.session.RoutesObserver;
import com.mapbox.navigation.core.directions.session.RoutesUpdatedResult;
import com.mapbox.navigation.core.formatter.MapboxDistanceFormatter;
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp;
import com.mapbox.navigation.core.trip.session.LocationMatcherResult;
import com.mapbox.navigation.core.trip.session.LocationObserver;
import com.mapbox.navigation.core.trip.session.RouteProgressObserver;
import com.mapbox.navigation.core.trip.session.VoiceInstructionsObserver;
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer;
import com.mapbox.navigation.ui.maneuver.api.MapboxManeuverApi;
import com.mapbox.navigation.ui.maneuver.model.Maneuver;
import com.mapbox.navigation.ui.maneuver.model.ManeuverError;
import com.mapbox.navigation.ui.maneuver.view.MapboxManeuverView;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi;
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView;
import com.mapbox.navigation.ui.maps.route.arrow.model.RouteArrowOptions;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView;
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineClearValue;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineError;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources;
import com.mapbox.navigation.ui.maps.route.line.model.RouteSetValue;
import com.mapbox.navigation.ui.voice.api.MapboxSpeechApi;
import com.mapbox.navigation.ui.voice.api.MapboxVoiceInstructionsPlayer;
import com.mapbox.navigation.ui.voice.model.SpeechAnnouncement;
import com.mapbox.navigation.ui.voice.model.SpeechError;
import com.mapbox.navigation.ui.voice.model.SpeechValue;
import com.mapbox.navigation.ui.voice.model.SpeechVolume;
import com.mapbox.navigation.ui.voice.view.MapboxSoundButton;
import com.mapbox.search.autocomplete.PlaceAutocomplete;
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion;
import com.mapbox.search.ui.adapter.autocomplete.PlaceAutocompleteUiAdapter;
import com.mapbox.search.ui.view.CommonSearchViewConfiguration;
import com.mapbox.search.ui.view.SearchResultsView;
import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfMeasurement;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentMap extends Fragment
        implements PermissionsListener {
    private MapView mapView;
    private View view;
    private PermissionsManager permissionsManager;

    //Các thành phần của layout
    private FloatingActionButton navigationButton;
    private FloatingActionButton mylocationButton;
    private FloatingActionButton mylocationNavigationButton;
    private FloatingActionButton directionButton;
    private MapboxSoundButton soundButton;

    private PlaceAutocomplete placeAutocomplete;
    private PlaceAutocompleteUiAdapter placeAutocompleteUiAdapter;
    private PlaceAutocompleteUiAdapter placeAutocompleteStartUiAdapter;
    private PlaceAutocompleteUiAdapter placeAutocompleteDestinationUiAdapter;
    private SearchResultsView searchResultsView;
    private SearchResultsView searchResultsViewDestination;
    private TextInputEditText searchET;
    private TextInputLayout searchETLayout;

    private TextInputEditText searchStartET;
    private TextInputEditText searchDestinationET;
    private Button findRouteButton;



    private MapboxManeuverView maneuverView;
    private CardView cardViewTrip;
    private CardView cardViewWarning;
    private ImageView imageView;

    private LinearLayout layoutStartDestination;

    //Các thành phần trên map Mapbox
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";



    // Hàm kiểm tra điểm có nằm trên LineString hay không
    public static boolean booleanPointOnLine(Point point, List<Point> line) {
        // Duyệt qua tất cả các đoạn tuyến trong LineString
        for (int i = 0; i < line.size() - 1; i++) {
            Point start = line.get(i);
            Point end = line.get(i + 1);

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
            mylocationButton.hide();
            locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            getGestures(mapView).addOnMoveListener(onMoveListener);

        });
    }

    //nếu như nhấp vào search start trước.

    private boolean ignoreNextQueryUpdate = false;
    public void setSearchET(PointAnnotationManager pointAnnotationManager,Bitmap resizedBitmap,TextInputEditText searchET) {
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
                searchResultsView.setVisibility(View.GONE);
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

                MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();

                CameraOptions cameraOptions = new CameraOptions.Builder().center(placeAutocompleteSuggestion.getCoordinate()).zoom(13.0)
                        .padding(new EdgeInsets(1000.0, 0.0, 0.0, 0.0)).build();

                getCamera(mapView).easeTo(cameraOptions, animationOptions);

                LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
                locationComponentPlugin.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);

                pointAnnotationManager.deleteAll();

                // Tạo annotation mới tại vị trí click
                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                        .withTextAnchor(TextAnchor.CENTER)
                        .withIconImage(resizedBitmap)
                        .withPoint(placeAutocompleteSuggestion.getCoordinate());
                pointAnnotationManager.create(pointAnnotationOptions);

                navigationButton.setOnClickListener(view->{
                    searchETLayout.setVisibility(View.GONE);
                    directionButton.hide();
                    setNavigationOnMap(placeAutocompleteSuggestion.getCoordinate());
                });
                Toast.makeText(getContext(), String.format("Show layout search 2 point", placeAutocompleteSuggestion.getCoordinate()), Toast.LENGTH_SHORT).show();



                directionButton.setOnClickListener(view->{
                    getDirectionWithMyLocationPoint(placeAutocompleteSuggestion.getCoordinate());
                    searchETLayout.setVisibility(View.GONE);
                    layoutStartDestination.setVisibility(View.VISIBLE);
                    destinationSearch=Point.fromLngLat(placeAutocompleteSuggestion.getCoordinate().longitude(),placeAutocompleteSuggestion.getCoordinate().latitude());
                    searchStartET.setText("Vị trí của tôi");
                    searchDestinationET.setText(placeAutocompleteSuggestion.getName());
                    getDirectionWithMyLocationPoint(destinationSearch);
                    directionButton.setVisibility(View.GONE);

                });
                findRouteButton.setOnClickListener(button->{
                    if(originSearch!=null && destinationSearch!= null){
                        getRouteTwoPoint(originSearch,destinationSearch);
                    }
                    if(originSearch==null){
                        getDirectionWithMyLocationPoint(destinationSearch);
                    }
                    searchResultsView.setVisibility(View.GONE);
                });
            }

            @Override
            public void onPopulateQueryClick(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {


            }

            @Override
            public void onError(@NonNull Exception e) {
            }
        });
    }

    Point originSearch;
    Point destinationSearch;
    private boolean FirstSearchOrigin=true;

    private boolean ignoreNextQueryUpdateStartorDestination = false;
    public void setSearcStartOrDestiantionhET(PointAnnotationManager pointAnnotationManager,Bitmap resizedBitmap,TextInputEditText searchETStartOrStart) {
        searchETStartOrStart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Toast.makeText(getContext(), "setSearchET beforeTextChanged", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ignoreNextQueryUpdateStartorDestination) {
                    ignoreNextQueryUpdateStartorDestination = false;
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
                searchResultsView.setVisibility(View.GONE);

            }
        });

        placeAutocompleteUiAdapter.addSearchListener(new PlaceAutocompleteUiAdapter.SearchListener() {
            @Override
            public void onSuggestionsShown(@NonNull List<PlaceAutocompleteSuggestion> list) {

            }

            @Override
            public void onSuggestionSelected(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {
                Toast.makeText(getContext(), String.format("FirstSearch: %s", FirstSearchOrigin), Toast.LENGTH_SHORT).show();

                ignoreNextQueryUpdateStartorDestination = true;
                if(FirstSearchOrigin==true) {
                    searchStartET.setText(placeAutocompleteSuggestion.getName());
                }else {
                    searchDestinationET.setText(placeAutocompleteSuggestion.getName());
                }
//                searchResultsView.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), String.format("", placeAutocompleteSuggestion.getCoordinate()), Toast.LENGTH_SHORT).show();

                MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();

                CameraOptions cameraOptions = new CameraOptions.Builder().center(placeAutocompleteSuggestion.getCoordinate()).zoom(13.0)
                        .padding(new EdgeInsets(1000.0, 0.0, 0.0, 0.0)).build();

                getCamera(mapView).easeTo(cameraOptions, animationOptions);

                LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
                locationComponentPlugin.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);

                if(FirstSearchOrigin==false){
                    pointAnnotationManager.deleteAll();
                }

                // Tạo annotation mới tại vị trí click
                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                        .withTextAnchor(TextAnchor.CENTER)
                        .withIconImage(resizedBitmap)
                        .withPoint(placeAutocompleteSuggestion.getCoordinate());
                pointAnnotationManager.create(pointAnnotationOptions);

                navigationButton.setOnClickListener(view->{
                    searchETLayout.setVisibility(View.GONE);
                    directionButton.hide();
                    setNavigationOnMap(placeAutocompleteSuggestion.getCoordinate());
                });

                if (FirstSearchOrigin==true){
                    originSearch = Point.fromLngLat(placeAutocompleteSuggestion.getCoordinate().longitude(),placeAutocompleteSuggestion.getCoordinate().latitude());
                }else {
                    destinationSearch = Point.fromLngLat(placeAutocompleteSuggestion.getCoordinate().longitude(),placeAutocompleteSuggestion.getCoordinate().latitude());
                }

                findRouteButton.setOnClickListener(view->{
                    if (originSearch==null && destinationSearch!=null){
                        getDirectionWithMyLocationPoint(destinationSearch);
                    }
                    if (destinationSearch==null ){
                        Toast.makeText(getContext(), String.format("Không có điểm đích", placeAutocompleteSuggestion.getCoordinate()), Toast.LENGTH_SHORT).show();
                    }
                    if(originSearch!=null && destinationSearch!=null){
                        getRouteTwoPoint(originSearch,destinationSearch);
                    }
                    searchResultsView.setVisibility(View.GONE);
                });
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


    //Tạo các điểm pothole cố định trên map
    private void createPointPothole(PointAnnotationManager pointAnnotationManager) {

        PotholeManager potholeManager= PotholeManager.getInstance();
        potholeManager.getALLPotholes(new PotholeManager.GetPotholeCallBack() {
            @Override
            public void onSuccess(List<Pothole> potholes) {
                addPotholeToMap(potholes,pointAnnotationManager);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }
    List<Pothole> potholesList;
    View potholeDetailLayout;
    private PointAnnotationManager pointPotholeAnnotationManager ;
    //Quản lí các điểm pothole trên map
    public void addPotholeToMap(List<Pothole> potholeList,PointAnnotationManager pointAnnotationManager){
        potholesList= potholeList;
        ViewAnnotationManager viewAnnotationManager = mapView.getViewAnnotationManager();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_pothole_waning_map);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        pointPotholeAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);

        for ( Pothole potholePoint : potholesList) {
            PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                    .withTextAnchor(TextAnchor.CENTER)
                    .withIconImage(resizedBitmap)
                    .withPoint(Point.fromLngLat(potholePoint.getLocation().getLongitude(),potholePoint.getLocation().getLatitude()));
            pointPotholeAnnotationManager.create(pointAnnotationOptions);
            if(potholePoint.getLocation().getCountry()==null && potholePoint.getLocation().getCity()==null){
                getPlaceFromPoint(Point.fromLngLat(potholePoint.getLocation().getLongitude(),potholePoint.getLocation().getLatitude()));
//                Toast.makeText(getContext(), place, Toast.LENGTH_SHORT).show();
//                Pothole.Location location = new Pothole.Location
//                potholePoint.setLocation();
            }
        }

        pointPotholeAnnotationManager.addClickListener(new OnPointAnnotationClickListener() {
            @Override
            public boolean onAnnotationClick(@NonNull PointAnnotation pointAnnotation) {
                pointAnnotationManager.deleteAll();
                double latitude = pointAnnotation.getPoint().latitude();
                double longitude = pointAnnotation.getPoint().longitude();
                potholeDetailLayout.setVisibility(View.VISIBLE);
                searchETLayout.setVisibility(View.GONE);
                ImageButton btnBack = potholeDetailLayout.findViewById(R.id.btnBack);
                btnBack.setOnClickListener(btn->{
                    potholeDetailLayout.setVisibility(View.INVISIBLE);
                    searchETLayout.setVisibility(View.VISIBLE);
                });
                Button btnSubmit = potholeDetailLayout.findViewById(R.id.btnSubmit);
                btnSubmit.setOnClickListener(btn->{
                });
                Button btnAddImage = potholeDetailLayout.findViewById(R.id.btnAddImage);
                btnAddImage.setOnClickListener(btn->{

                });
                TextView tvLocation = potholeDetailLayout.findViewById(R.id.tvLocation);
                getPlaceFromPoint(Point.fromLngLat(longitude,latitude));

                tvLocation.setText("Địa điểm: "+place);
                return true;
            }
        });

    }



    String place ;
    private void getPlaceFromPoint(Point point){
        MapboxGeocoding client = MapboxGeocoding.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .query(point)
                .geocodingTypes(GeocodingCriteria.TYPE_PLACE)
                .mode(GeocodingCriteria.MODE_PLACES)
                .build();


        client.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call,
                                   Response<GeocodingResponse> response) {
                if (response.body() != null) {
                    List<CarmenFeature> results = response.body().features();

                        // Get the first Feature from the successful geocoding response
                        CarmenFeature feature = results.get(0);
                        place = feature.placeName().toString();

                        Toast.makeText(getContext(), feature.placeName(),
                                Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {

                Toast.makeText(getContext(), "Fail Search Place",
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void setclickOnMap(PointAnnotationManager pointAnnotationManager,Bitmap resizedBitmap){

        addOnMapClickListener(mapView.getMapboxMap(), pointDestination -> {
            // Xóa tất cả các annotation
            pointAnnotationManager.deleteAll();
            searchStartET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    FirstSearchOrigin=true;
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    setSearcStartOrDestiantionhET(pointAnnotationManager,resizedBitmap,searchStartET);
                }
            });
            searchDestinationET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    FirstSearchOrigin=false;
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    setSearcStartOrDestiantionhET(pointAnnotationManager,resizedBitmap,searchDestinationET);
                }
            });
            // Tạo annotation mới tại vị trí click
            PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                    .withTextAnchor(TextAnchor.CENTER)
                    .withIconImage(resizedBitmap)
                    .withPoint(pointDestination);
            pointAnnotationManager.create(pointAnnotationOptions);

            navigationButton.setOnClickListener(navigation->{
                searchETLayout.setVisibility(View.GONE);
                directionButton.hide();
                setNavigationOnMap(pointDestination);
            });
            directionButton.setOnClickListener(direction->{
                getDirectionWithMyLocationPoint(pointDestination);
                searchETLayout.setVisibility(View.GONE);
                layoutStartDestination.setVisibility(View.VISIBLE);

                searchStartET.setText("Vị trí của tôi");
                searchDestinationET.setText(pointDestination.coordinates().toString());
                destinationSearch=pointDestination;
                findRouteButton.setOnClickListener(button->{
                    if(originSearch!=null && destinationSearch!= null){
                        getRouteTwoPoint(originSearch,destinationSearch);
                    }
                    searchResultsView.setVisibility(View.GONE);
                });

            });
            getPlaceFromPoint(pointDestination);

            return false;
        });
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }


    TextView warningText;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) view.findViewById(R.id.mapbox);
        mylocationButton = (FloatingActionButton) view.findViewById(R.id.mylocationButton);
        mylocationButton.hide();
        mylocationNavigationButton = (FloatingActionButton) view.findViewById(R.id.mylocationNavigationButton);
        mylocationNavigationButton.hide();
        navigationButton = (FloatingActionButton) view.findViewById(R.id.navigationButton);
        searchET = (TextInputEditText) view.findViewById(R.id.searchET);
        searchETLayout = (TextInputLayout) view.findViewById(R.id.searchLayout);
        searchResultsView = (SearchResultsView) view.findViewById(R.id.search_results_view);
        searchResultsViewDestination = (SearchResultsView) view.findViewById(R.id.search_results_view);
        cardViewTrip = view.findViewById(R.id.tripProgressCard);
        cardViewWarning = view.findViewById(R.id.NotificationWarning);
        imageView = view.findViewById(R.id.stop);
        maneuverView = view.findViewById(R.id.maneuverView);
        directionButton = view.findViewById(R.id.directionButton);
        soundButton = view.findViewById(R.id.soundButton);
        searchStartET= view.findViewById(R.id.searchStartET);
        searchDestinationET=view.findViewById(R.id.searchDestinationET);
        findRouteButton = view.findViewById(R.id.findRouteButton);
        layoutStartDestination=view.findViewById(R.id.searchStartETandDestination);
        warningText=view.findViewById(R.id.distancePothole);
        potholeDetailLayout=view.findViewById(R.id.potholeDetailLayout);
        return view;
    }


    //để hiển thị vị trí hiện tại và các thành phần của vị trí Mapbox thông qua location component
    //thông qua thư viện location của mapbox
    //nhưng ở đây sử dụng thư viện LocationComponentPlugin để kích hoạt vị trí người dùng
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_pin);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 90, 90, true);
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);

        placeAutocomplete = PlaceAutocomplete.create(getString(R.string.mapbox_access_token));
        searchResultsView.initialize(new SearchResultsView.Configuration(new CommonSearchViewConfiguration()));
        placeAutocompleteUiAdapter = new PlaceAutocompleteUiAdapter(searchResultsView, placeAutocomplete, LocationEngineProvider.getBestLocationEngine(getContext()));
        placeAutocompleteStartUiAdapter = new PlaceAutocompleteUiAdapter(searchResultsView, placeAutocomplete, LocationEngineProvider.getBestLocationEngine(getContext()));
        placeAutocompleteDestinationUiAdapter = new PlaceAutocompleteUiAdapter(searchResultsView, placeAutocomplete, LocationEngineProvider.getBestLocationEngine(getContext()));
        //Nhấp chuột vào đây sẽ thực hiện hiển thị navigation map

        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                createPointPothole(pointAnnotationManager);

                setMylocationButton();

                setclickOnMap(pointAnnotationManager,resizedBitmap);

                directionButton.setOnClickListener(direction->{
                    layoutStartDestination.setVisibility(View.VISIBLE);
                    searchETLayout.setVisibility(View.GONE);
                    directionButton.setVisibility(View.GONE);
                    searchStartET.setText("Vị Trí của bạn");
                });

                searchET.setOnClickListener(search->{
                    searchET.clearComposingText();
                });



                searchStartET.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        // If the event is a key-down event on the "enter" button
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                                (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            searchResultsView.setVisibility(View.GONE);
                            return true;
                        }
                        return false;
                    }
                });

                searchDestinationET.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        // If the event is a key-down event on the "enter" button
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                                (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            searchResultsView.setVisibility(View.GONE);
                            return true;
                        }
                        return false;
                    }
                });

                setSearchET(pointAnnotationManager,resizedBitmap,searchET);
                searchStartET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        FirstSearchOrigin=true;
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        setSearcStartOrDestiantionhET(pointAnnotationManager,resizedBitmap,searchStartET);


                    }
                });

                searchDestinationET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        FirstSearchOrigin=false;
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        setSearcStartOrDestiantionhET(pointAnnotationManager,resizedBitmap,searchDestinationET);
                        searchResultsView.setVisibility(View.GONE);

                    }
                });

            }
        });
        createPointPothole(pointAnnotationManager);

    }

    @SuppressLint("MissingPermission")
    private void getDirectionWithMyLocationPoint(Point destination) {
        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(getContext());

        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();

                Point origin = Point.fromLngLat(location.getLongitude(), location.getLatitude());

                getRouteTwoPoint(origin,destination);
            }

            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }

    private void getListPotholeOnLineRoute(LineString linestring){
        for ( Pothole potholePoint : potholesList) {

        }
    }
    private void getRouteTwoPoint(Point origin ,Point destination) {
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

                lineString = LineString.fromPolyline(currentRoute.geometry(),PRECISION_6);

                Feature routeFeature = Feature.fromGeometry(lineString);
                // Make a toast which displays the route's distance

                Toast.makeText(getContext(), String.format("Route distance: %.2f meters", currentRoute.distance()), Toast.LENGTH_SHORT).show();
                mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS,style -> {

                    GeoJsonSource.Builder lineRoute = new GeoJsonSource.Builder(ROUTE_SOURCE_ID).feature(routeFeature);

                    SourceUtils.addSource(style,lineRoute.build());

                    LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID)
                            .lineColor("#3b9ddd")
                            .lineWidth(8D);
                    LayerUtils.addLayer(style,routeLayer);


                });
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {

            }
        });
    }

    private final NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();
    private MapboxRouteLineView routeLineView;
    private MapboxRouteLineApi routeLineApi;
    boolean focusLocationNavigationMode = true;



    private void updateCamera(Point point, Double bearing) {
        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
        CameraOptions cameraOptions = new CameraOptions.Builder().center(point).zoom(14.0).bearing(bearing).pitch(0.0)
                .padding(new EdgeInsets(1000.0, 0.0, 0.0, 0.0)).build();

        getCamera(mapView).easeTo(cameraOptions, animationOptions);
    }


    private void updateCameraNavigation(Point point, Double bearing) {
        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
        CameraOptions cameraOptions = new CameraOptions.Builder().center(point).zoom(17.0).bearing(bearing).pitch(45.0)
                .padding(new EdgeInsets(1000.0, 0.0, 0.0, 0.0)).build();

        getCamera(mapView).easeTo(cameraOptions, animationOptions);
    }


    LineString lineString;
    Location myLocationNavigation;
    private final LocationObserver locationObserver = new LocationObserver() {
        @Override
        public void onNewRawLocation(@NonNull Location location) {

        }

        @Override
        public void onNewLocationMatcherResult(@NonNull LocationMatcherResult locationMatcherResult) {
            Location location = locationMatcherResult.getEnhancedLocation();
            navigationLocationProvider.changePosition(location, locationMatcherResult.getKeyPoints(), null, null);
            if (focusLocationNavigationMode) {
                updateCameraNavigation(Point.fromLngLat(location.getLongitude(), location.getLatitude()), (double) location.getBearing());
            }
            myLocationNavigation=location;
        }
    };
    private final RoutesObserver routesObserver = new RoutesObserver() {
        @Override
        public void onRoutesChanged(@NonNull RoutesUpdatedResult routesUpdatedResult) {
            routeLineApi.setNavigationRoutes(routesUpdatedResult.getNavigationRoutes(), new MapboxNavigationConsumer<Expected<RouteLineError, RouteSetValue>>() {
                @Override
                public void accept(Expected<RouteLineError, RouteSetValue> routeLineErrorRouteSetValueExpected) {
                    Style style = mapView.getMapboxMap().getStyle();
                    if (style != null) {
                        routeLineView.renderRouteDrawData(style, routeLineErrorRouteSetValueExpected);
                    }
                }
            });
//            Toast.makeText(getContext(), "Start logic warning", Toast.LENGTH_SHORT).show();
//
//            Point pointMyLocationNavigation = Point.fromLngLat(myLocationNavigation.getLongitude(),myLocationNavigation.getLatitude());
//
//            List<NavigationRoute> navigationRouteList = routesUpdatedResult.getNavigationRoutes();
//            NavigationRoute navigationRoute = navigationRouteList.get(navigationRouteList.size());
//
//            //Tọa độ các điểm trên đường route được cập nhập liên tục
//            List<Point> lineNavigation = navigationRoute.getDirectionsRoute().routeOptions().coordinatesList();
//            Map<Point,Double> potholeOnNavigation=findPotholeOnLineNavigationAnDistance(lineNavigation,pointMyLocationNavigation);
//
//            Point nearestPothole = null;
//            double minDistance = Double.MAX_VALUE;
//            // Duyệt qua các potholes và tìm pothole gần nhất
//            for (Map.Entry<Point, Double> entry : potholeOnNavigation.entrySet()) {
//                double distance = entry.getValue();
//                if (distance < minDistance) {
//                    minDistance = distance;
//                    nearestPothole = entry.getKey();
//                }
//            }
//            minDistance= Math.round(minDistance * 100.0) / 100.0;
//            Toast.makeText(getContext(), "Set warning", Toast.LENGTH_SHORT).show();
//            warningText.setText(String.format("%.2f", minDistance));
        }

    };

    //Tim các điểm pothole nằm trên tuyền đường này
    private Map<Point,Double> findPotholeOnLineNavigationAnDistance(List<Point> lineNavigation,Point pointMyLocationNavigation){
        Map<Point, Double> pointDistanceMap = new HashMap<>();
        for (Pothole pothole : potholesList){
            Point point=Point.fromLngLat(pothole.getLocation().getLongitude(),pothole.getLocation().getLatitude());
            if(booleanPointOnLine(point,lineNavigation)){
                double distance = TurfMeasurement.distance(pointMyLocationNavigation, point, TurfConstants.UNIT_METERS);
                pointDistanceMap.put(point,distance);
            };
        }
        return pointDistanceMap;
    }


    private final OnMoveListener onMoveListenerNavigation = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            focusLocationNavigationMode = false;
            getGestures(mapView).removeOnMoveListener(this);
            mylocationButton.hide();
            mylocationNavigationButton.show();

        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

        }
    };

    private MapboxNavigation mapboxNavigation;

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                Toast.makeText(getContext(), "Permission granted! Restart this app", Toast.LENGTH_SHORT).show();
            }
        }
    });

    private MapboxSpeechApi speechApi;
    private MapboxVoiceInstructionsPlayer mapboxVoiceInstructionsPlayer;

    private MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>> speechCallback = new MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>>() {
        @Override
        public void accept(Expected<SpeechError, SpeechValue> speechErrorSpeechValueExpected) {
            speechErrorSpeechValueExpected.fold(new Expected.Transformer<SpeechError, Unit>() {
                @NonNull
                @Override
                public Unit invoke(@NonNull SpeechError input) {
                    mapboxVoiceInstructionsPlayer.play(input.getFallback(), voiceInstructionsPlayerCallback);
                    return Unit.INSTANCE;
                }
            }, new Expected.Transformer<SpeechValue, Unit>() {
                @NonNull
                @Override
                public Unit invoke(@NonNull SpeechValue input) {
                    mapboxVoiceInstructionsPlayer.play(input.getAnnouncement(), voiceInstructionsPlayerCallback);
                    return Unit.INSTANCE;
                }
            });
        }
    };

    private MapboxNavigationConsumer<SpeechAnnouncement> voiceInstructionsPlayerCallback = new MapboxNavigationConsumer<SpeechAnnouncement>() {
        @Override
        public void accept(SpeechAnnouncement speechAnnouncement) {
            speechApi.clean(speechAnnouncement);
        }
    };

    VoiceInstructionsObserver voiceInstructionsObserver = new VoiceInstructionsObserver() {
        @Override
        public void onNewVoiceInstructions(@NonNull VoiceInstructions voiceInstructions) {
            speechApi.generate(voiceInstructions, speechCallback);
        }
    };

    private boolean isVoiceInstructionsMuted = false;

    private MapboxManeuverApi maneuverApi;
    private MapboxRouteArrowView routeArrowView;
    private MapboxRouteArrowApi routeArrowApi = new MapboxRouteArrowApi();
    private RouteProgressObserver routeProgressObserver = new RouteProgressObserver() {
        @Override
        public void onRouteProgressChanged(@NonNull RouteProgress routeProgress) {
            Style style = mapView.getMapboxMap().getStyle();
            if (style != null) {
                routeArrowView.renderManeuverUpdate(style, routeArrowApi.addUpcomingManeuverArrow(routeProgress));
            }

            maneuverApi.getManeuvers(routeProgress).fold(new Expected.Transformer<ManeuverError, Object>() {
                @NonNull
                @Override
                public Object invoke(@NonNull ManeuverError input) {
                    return new Object();
                }
            }, new Expected.Transformer<List<Maneuver>, Object>() {
                @NonNull
                @Override
                public Object invoke(@NonNull List<Maneuver> input) {
                    maneuverView.setVisibility(View.VISIBLE);
                    cardViewTrip.setVisibility(View.VISIBLE);

                    maneuverView.renderManeuvers(maneuverApi.getManeuvers(routeProgress));
                    return new Object();
                }
            });

        }
    };

    public void setNavigationOnMap(Point destination){

        layoutStartDestination.setVisibility(View.GONE);

        //không cho phép nhấp chuột vào map khi chuyển sang navigation
        addOnMapClickListener(mapView.getMapboxMap(), new OnMapClickListener() {
            @Override
            public boolean onMapClick(@NonNull Point point) {
                return false;
            }
        });
        navigationButton.hide();
        mylocationButton.hide();
//            directionButton.hide();
        maneuverApi = new MapboxManeuverApi(new MapboxDistanceFormatter(new DistanceFormatterOptions.Builder(getActivity().getApplication()).build()));
        routeArrowView = new MapboxRouteArrowView(new RouteArrowOptions.Builder(getContext()).build());

        MapboxRouteLineOptions options = new MapboxRouteLineOptions.Builder(getContext()).withRouteLineResources(new RouteLineResources.Builder().build())
                .withRouteLineBelowLayerId(LocationComponentConstants.LOCATION_INDICATOR_LAYER).build();

        routeLineView = new MapboxRouteLineView(options);
        routeLineApi = new MapboxRouteLineApi(options);

        speechApi = new MapboxSpeechApi(getContext(), getString(R.string.mapbox_access_token), Locale.US.toLanguageTag());
        mapboxVoiceInstructionsPlayer = new MapboxVoiceInstructionsPlayer(getContext(), Locale.US.toLanguageTag());

        NavigationOptions navigationOptions = new NavigationOptions.Builder(getContext()).accessToken(getString(R.string.mapbox_access_token)).build();

        MapboxNavigationApp.setup(navigationOptions);
        mapboxNavigation = new MapboxNavigation(navigationOptions);

        mapboxNavigation.registerRoutesObserver(routesObserver);
        mapboxNavigation.registerLocationObserver(locationObserver);
        mapboxNavigation.registerVoiceInstructionsObserver(voiceInstructionsObserver);
        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver);

        imageView.setOnClickListener(view -> {
            mapboxNavigation.stopTripSession();
            mapboxNavigation.onDestroy();
            mapboxNavigation.unregisterRoutesObserver(routesObserver);
            mapboxNavigation.unregisterLocationObserver(locationObserver);
            updateCamera(Point.fromLngLat(myLocationNavigation.getLongitude(), myLocationNavigation.getLatitude()),(double) myLocationNavigation.getBearing());
            endTrip();
        });

        soundButton.unmute();
        soundButton.setOnClickListener(view -> {
            isVoiceInstructionsMuted = !isVoiceInstructionsMuted;
            if (isVoiceInstructionsMuted) {
                soundButton.muteAndExtend(1500L);
                mapboxVoiceInstructionsPlayer.volume(new SpeechVolume(0f));
            } else {
                soundButton.unmuteAndExtend(1500L);
                mapboxVoiceInstructionsPlayer.volume(new SpeechVolume(1f));
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            activityResultLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        } else {
            mapboxNavigation.startTripSession();
        }

        navigationRoute(destination);

        LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
        getGestures(mapView).addOnMoveListener(onMoveListenerNavigation);
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(20.0).build());
                locationComponentPlugin.setEnabled(true);
                locationComponentPlugin.setLocationProvider(navigationLocationProvider);
                getLocationComponent(mapView).removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                getGestures(mapView).addOnMoveListener(onMoveListenerNavigation);
                locationComponentPlugin.updateSettings(new Function1<LocationComponentSettings, Unit>() {
                    @Override
                    public Unit invoke(LocationComponentSettings locationComponentSettings) {
                        locationComponentSettings.setEnabled(true);
                        locationComponentSettings.setPulsingEnabled(true);
                        return null;
                    }
                });

                mylocationNavigationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        focusLocationNavigationMode = true;
                        getGestures(mapView).addOnMoveListener(onMoveListenerNavigation);
                        getLocationComponent(mapView).removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                        mylocationNavigationButton.hide();
                    }
                });
            }
        });
    }

    private void endTrip(){
        cardViewWarning.setVisibility(View.GONE);
        cardViewTrip.setVisibility(View.GONE);
        searchET.setVisibility(View.VISIBLE);
        soundButton.setVisibility(View.GONE);
        maneuverView.setVisibility(View.GONE);

        routeLineApi.clearRouteLine(new MapboxNavigationConsumer<Expected<RouteLineError, RouteLineClearValue>>() {
            @Override
            public void accept(Expected<RouteLineError, RouteLineClearValue> routeLineErrorRouteLineClearValueExpected) {
                // Handle potential errors during route line clearing

            }
        });
    }

    @SuppressLint("MissingPermission")
    private void navigationRoute(Point point) {

        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(getContext());
        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                Point origin = Point.fromLngLat(Objects.requireNonNull(location).getLongitude(), location.getLatitude());
                RouteOptions.Builder builder = RouteOptions.builder()
                        .coordinatesList(Arrays.asList(origin, point))
                        .alternatives(false)
                        .profile(DirectionsCriteria.PROFILE_DRIVING)
                        .bearingsList(Arrays.asList(Bearing.builder().angle(location.getBearing()).build(), null));
                applyDefaultNavigationOptions(builder);

                mapboxNavigation.requestRoutes(builder.build(), new NavigationRouterCallback() {
                    @Override
                    public void onRoutesReady(@NonNull List<NavigationRoute> route, @NonNull RouterOrigin routerOrigin) {
                        cardViewWarning.setVisibility(View.VISIBLE);
                        cardViewTrip.setVisibility(View.VISIBLE);
                        soundButton.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "navigationRoute", Toast.LENGTH_SHORT).show();

                        mapboxNavigation.setNavigationRoutes(route);
                        mylocationNavigationButton.performClick();


                    }
                    @Override
                    public void onFailure(@NonNull List<RouterFailure> list, @NonNull RouteOptions routeOptions) {
                        Toast.makeText(getContext(), "Route request failed", Toast.LENGTH_SHORT).show();
                    };
                    @Override
                    public void onCanceled(@NonNull RouteOptions routeOptions, @NonNull RouterOrigin routerOrigin) {

                    }
                });
            }

            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }
}
